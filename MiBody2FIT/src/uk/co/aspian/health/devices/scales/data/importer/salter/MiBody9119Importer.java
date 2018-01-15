package uk.co.aspian.health.devices.scales.data.importer.salter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import uk.co.aspian.health.devices.scales.data.ScaleData;
import uk.co.aspian.health.devices.scales.data.ScaleProfile;
import uk.co.aspian.health.devices.scales.data.ScaleProfileFitnessLevel;
import uk.co.aspian.health.devices.scales.data.ScaleProfileGender;
import uk.co.aspian.health.devices.scales.data.ScaleReading;
import uk.co.aspian.health.devices.scales.data.importer.ScaleDataImporter;

/**
 * @author idg
 *
 * This class imports scale data from the Salter MiBody 9119 scales.
 * 
 * The format of this file was documented by the people who run the OpenSalterMiBody project
 * over at: https://code.google.com/p/opensaltermibody/ who have a .NET implementation for working
 * with this data.
 * 
 * Note: Primitive type 'byte' in Java is 8-bit singled allowing values from -128 to 127.
 * The data in this file is easier to work with if its converted to unsigned integers by performing 'byte & 0xff'
 * 
 * This scale supports 12 user profiles which have 35 weight readings each
 * 
 * The USB output from the scales produces a 7,560 byte *BINARY* file called BODYDATA.TXT.  
 * The file contains 420 18 byte data slots
 * 
 * Each sequence of 35 slots represents a user profile and its associated readings 
 * (i.e Profile 1 = slots 0-34, Profile 2 = slots 35-69 etc.)
 * 
 * Typically an 18 byte slot would look like this:
 * 
 *  0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 
 * -----------------------------------------------------  
 * 07 DE 02 17 08 29 3B A8 B7 02 03 55 00 87 00 01 93 0B
 * 
 * The bytes of the file are as follows from the above example:
 * 
 * Byte	Hex Decimal Description
 * -------------------------------------------------------------------------------------------------------------------------
 * 0	07	7		1st byte of Year	
 * 1	DE	222		2nd byte of Year 
 *                   
 *                  Year can be calculated by shifting data[0] 8 bytes left with bitwise inclusive OR of data[1] 
 *                  (i.e. data[0] << 8 | data[1] giving 7 << 8 | 222 = 2014)
 *                  
 * 2	02	2		Month (2)
 * 3	17	23		Day (23)
 * 4	08	8		Hour (24h time) (8)
 * 5	29	41		Minute  (41)
 * 6	3B	59		Seconds (59)
 * 7	A8	168		Gender and Age
 * 
 * 					Gender is determined from the 8th bit (1 = Male, 0 = Female) 
 * 					(i.e. data[7] >> 7 giving '1' = Male)
 * 
 * 					Age is determined by zeroing the 8th bit (only 7 bits for age giving a maximum of 127)
 * 					(i.e. data[7] &= ~(1 << 7) = 40)
 * 
 * 8	B7	183		Height in whole Centimetres (183)
 * 9	02	2		Fitness Level (0=Low, 1=Medium, 2=High)
 * 
 * 10	03	3		1st byte of Weight (kg)
 * 11	55	85		2nd byte of Weight (kg)
 * 
 * 					Weight can be calculated by shifting data[10] 8 bytes left and adding data[11] and dividing by 10 
 * 					(i.e. (double)((data[10] << 8) + data[11]) / 10 = 85.3kg)
 * 
 * 12	00	0		1st byte of Body Fat %
 * 13	87	135		2nd byte of Body Fat %
 * 
 * 					Body Fat % can be calculated by shifting data[12] 8 bytes left and adding data[13] and dividing by 10
 * 					(i.e. (double)((data[12] << 8) + data[13]) /10 = 13.5%)
 * 
 * 14	00	0		Unused or Reserved for future changes
 * 
 * 15	01	1		1st byte of Muscle Mass %
 * 16	93	147		2nd byte of Muscle Mass %
 * 
 * 					Muscle Mass % can be calculated by shifting data[15] 8 bytes left and adding data[16] and dividing by 10
 * 					(i.e. (double)((data[15] << 8) + data[16]) /10 = 40.3%)
 * 
 * 17	0B	11		Visceral Fat (11)
 * -------------------------------------------------------------------------------------------------------------------------
 *  
 * An Empty Reading slot will look like this:
 * 
 *  0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 
 * -----------------------------------------------------  
 * 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
 * 
 * A configured user profile slot (slots 0, 35, 70, 105, etc.) without a reading (i.e. User has configured profile but not 
 * taken a weight reading) will look like this (XX indicates a populated bit)
 * 
 *  0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 
 * -----------------------------------------------------  
 * 00 00 00 00 00 00 00 XX XX XX 00 00 00 00 00 00 00 00
 * 
 * 
 */

public class MiBody9119Importer extends ScaleDataImporter
{
	// --------------------------------------------------------------------------------
    // Constants that describe the makeup of the raw data
    // --------------------------------------------------------------------------------
	
	static final int USER_COUNT = 12;
	static final int DATA_SLOTS_PER_USER = 35;
	static final int BYTES_PER_DATA_SLOT = 18;
	static final int RAW_DATA_SIZE = USER_COUNT * (DATA_SLOTS_PER_USER * BYTES_PER_DATA_SLOT);
	
	@Override
    public ScaleData getScaleData(byte[] rawData)
    {
		ScaleData scaleData = new ScaleData();
		List<ScaleProfile> profiles = new ArrayList<ScaleProfile>();
		
		ScaleProfile profile = null;
		
		// --------------------------------------------------------------------------------
        // Set the Make and Model
        // -------------------------------------------------------------------------------- 
		
		scaleData.setScaleMake("Salter");
		scaleData.setScaleModel("MiBody 9119");
	    
		// --------------------------------------------------------------------------------
        // Process the raw data from the scales
        // --------------------------------------------------------------------------------

		int profileId = 0;
		int reading = 0;
		
		int slotNumber = 1;
		int offset = 0;
		
		while(offset < rawData.length)
		{
			// --------------------------------------------------------------------------------
            // Get the data for the next slot
            // --------------------------------------------------------------------------------
			
			int[] slotData = getNextSlotData(rawData, offset);
			
			if(!isEmptySlot(slotData))
			{
				showSlotData(slotNumber, slotData);
				
				// --------------------------------------------------------------------------------
	            // Figure out if this is a new scale profile
	            // --------------------------------------------------------------------------------
				
				if(offset % (DATA_SLOTS_PER_USER * BYTES_PER_DATA_SLOT) == 0)
				{
					profileId++;
					reading = 1;
					
					if(!isEmptyProfileSlot(slotData))
					{
						// --------------------------------------------------------------------------------
                        // Create a new profile
                        // --------------------------------------------------------------------------------
						
						profile = new ScaleProfile(scaleData);
						
						profile.setId(profileId);
						profile.setGender(getGender(slotData));
						profile.setAge(getAge(slotData));
						profile.setHeightCm(getHeightCm(slotData));
						profile.setFitnessLevel(getFitnessLevel(slotData));
										        
				        // --------------------------------------------------------------------------------
                        // Create a new list for readings associated with this profile
                        // --------------------------------------------------------------------------------
				        
				        profile.setReadings(new ArrayList<ScaleReading>());
				        
				        // --------------------------------------------------------------------------------
                        // Add this profile to the list of profiles
                        // --------------------------------------------------------------------------------
				        
				        profiles.add(profile);
					}
					else
					{
						// --------------------------------------------------------------------------------
                        // This profile has not been configured in the scales
                        // --------------------------------------------------------------------------------
						
						profile = null;
					}
				}
				
				if(!isEmptyReadingSlot(slotData))
				{
					// --------------------------------------------------------------------------------
                    // Create a new scale reading and add it to the profile
                    // --------------------------------------------------------------------------------
					
					ScaleReading scaleReading = new ScaleReading(profile);
					
					scaleReading.setId(Integer.toString(reading));
					scaleReading.setDate(getSlotDate(slotData));
			        scaleReading.setWeightKg(getSlotWeighKg(slotData));
			        scaleReading.setBodyFatPcnt(getSlotBodyFatPcnt(slotData));
			        scaleReading.setMuscleMassPcnt(getMuscleMassPcnt(slotData));
			        scaleReading.setVisceralFat(getVisceralFat(slotData));
			        scaleReading.setBodyMassIndex(calculateBMIFromReading(scaleReading));
			        scaleReading.setBasalMetabolicRate(calculateBMRFromReading(scaleReading));
			        scaleReading.setBodyWaterPcnt(calculateBodyWaterPercent(scaleReading));
			        scaleReading.setPhysiqueRating(calculatePhysiqueRating(scaleReading));
			        
			        profile.getReadings().add(scaleReading);
				}
			}

			offset += slotData.length;
	        slotNumber++;
	        reading++;
		}
		
		scaleData.setScaleProfiles(profiles);
		
		return scaleData;
    }
	
	/**
	 * Obtains the Date the scale reading was taken on from within the raw slot data
	 * @param slot the raw slot data
	 * @return a Date representing the date within the slot
	 */
	
	private static Date getSlotDate(int[] slot)
	{
		Calendar c = Calendar.getInstance();
        
		// --------------------------------------------------------------------------------
        // Get Year from byte 1 and byte 2
		// --------------------------------------------------------------------------------
		
        c.set(Calendar.YEAR, (slot[0] << 8 | slot[1]));
        
        // --------------------------------------------------------------------------------
        // Get Month from byte 3
        // --------------------------------------------------------------------------------
        
        c.set(Calendar.MONTH, (slot[2] - 1));
        
        // --------------------------------------------------------------------------------
        // Get Day from byte 4		        
        // --------------------------------------------------------------------------------
        
        c.set(Calendar.DAY_OF_MONTH, slot[3]);
        
        // --------------------------------------------------------------------------------
        // Get hour from byte 5
        // --------------------------------------------------------------------------------
        
        c.set(Calendar.HOUR, slot[4]);
        
        // --------------------------------------------------------------------------------
        // Get minute from byte 6
        // --------------------------------------------------------------------------------
        
        c.set(Calendar.MINUTE, slot[5]);
        
        // --------------------------------------------------------------------------------
        // Step 6: Get seconds from byte 7		        
        // --------------------------------------------------------------------------------
        
        c.set(Calendar.SECOND, slot[6]);
        
        return c.getTime();
	}
	
	private static int getHeightCm(int[] slot)
	{
		// --------------------------------------------------------------------------------
		// Set Height in cm from byte 9
		// --------------------------------------------------------------------------------
		
		return slot[8];
	}
	
	private static int getAge(int[] slot)
	{
		// --------------------------------------------------------------------------------
		// Set Age from byte 8 (Unset the 1st bit from byte 8 for age only)
		// --------------------------------------------------------------------------------
		
		return slot[7] &= ~(1 << 7);
	}
	
	private static ScaleProfileFitnessLevel getFitnessLevel(int[] slot)
	{
		// --------------------------------------------------------------------------------
		// Set Fitness Level from byte 10
		// --------------------------------------------------------------------------------
		
        int fitnessLevel = slot[9];
        
        switch(fitnessLevel)
        {
        	case 0: 
        		return ScaleProfileFitnessLevel.LOW;
        	case 1:
        		return ScaleProfileFitnessLevel.MEDIUM;
        	case 2:
        		return ScaleProfileFitnessLevel.HIGH;
        }
        
        return ScaleProfileFitnessLevel.LOW;
	}

	/**
	 * Obtains the gender reading from within the raw slot data
	 * @param slot the raw slot data
	 * @return the gender
	 */
	
	private static ScaleProfileGender getGender(int[] slot)
	{
		// --------------------------------------------------------------------------------
		// Set the Gender from the 1st bit of byte 8
		// --------------------------------------------------------------------------------
		
		return ((slot[7] >> 7) == 1) ? ScaleProfileGender.MALE : ScaleProfileGender.FEMALE;
	}
	
	/**
	 * Obtains the weight in kg reading from within the raw slot data
	 * @param slot the raw slot data
	 * @return the weight in kg
	 */
	
	private static double getSlotWeighKg(int[] slot)
	{
		// --------------------------------------------------------------------------------
		// Get weight (kg) from bytes 11 and 12
		// --------------------------------------------------------------------------------
		
		return (double)((slot[10] << 8) + slot[11]) / 10;
	}
	
	/**
	 * Obtains the body fat % reading from within the raw slot data
	 * @param slot the raw slot data
	 * @return the body fat %
	 */
	
	private static double getSlotBodyFatPcnt(int[] slot)
	{
		// --------------------------------------------------------------------------------
		// Get body fat (%) from bytes 13 and 14
		// --------------------------------------------------------------------------------
		
		return (double)((slot[12] << 8) + slot[13]) / 10;
	}
	
	/**
	 * Obtains the muscle mass % reading from within the raw slot data
	 * @param slot the raw slot data
	 * @return the muscle mass %
	 */
	
	private static double getMuscleMassPcnt(int[] slot)
	{
		// --------------------------------------------------------------------------------
		// Set Get Muscle Mass (%) from bytes 16 and 17
		// --------------------------------------------------------------------------------
		
		return (double)((slot[15] << 8) + slot[16]) / 10;
	}

	/**
	 * Obtains the visceral fat reading from within the raw slot data
	 * @param slot the raw slot data
	 * @return the visceral fat
	 */
	
	private static int getVisceralFat(int[] slot)
	{
		// --------------------------------------------------------------------------------
		// SetVisceral Fat from byte 18
		// --------------------------------------------------------------------------------
		
		return slot[17];
	}
	
	private short calculatePhysiqueRating(ScaleReading reading)
	{
		// --------------------------------------------------------------------------------
        // Calculate Physique Rating
		//
		// PR = Height(cm) / (WeightKg / 100 * MuscleMassPercent)
        // --------------------------------------------------------------------------------
		
		double physiquerating = reading.getScaleProfile().getHeightCm() / ((reading.getWeightKg() / 100) * reading.getMuscleMassPcnt());
		return (short)Math.round(physiquerating);
	}
	
	private static double calculateBodyWaterPercent(ScaleReading reading) 
	{
		DecimalFormat formatter = new DecimalFormat("#.#");
		
		double muscleMass = reading.getMuscleMassPcnt() * reading.getWeightKg() / 100;
		double fatMass = reading.getBodyFatPcnt() * reading.getWeightKg() / 100;
		double restOfFluids = reading.getWeightKg() - muscleMass - fatMass;
		double waterMass = muscleMass * 0.83 + restOfFluids * 0.62;
		double waterPercent = waterMass / reading.getWeightKg() * 100;

		return Double.valueOf(formatter.format(waterPercent));			
	}
	
	private static double calculateBMIFromReading(ScaleReading reading)
	{
		DecimalFormat formatter = new DecimalFormat("#.#");
		
		// --------------------------------------------------------------------------------
		// convert height from cm to metres
		// --------------------------------------------------------------------------------
		
		double heightInMetres = reading.getScaleProfile().getHeightCm() * 0.01;
		double bmi = reading.getWeightKg() / (heightInMetres * heightInMetres);

		return Double.valueOf(formatter.format(bmi));		
	}
	
	private static double calculateBMRFromReading(ScaleReading reading) 
	{
		// --------------------------------------------------------------------------------
		// Calculate the Basal Metabolic Rate based upon this formula
		// http://www.bmi-calculator.net/bmr-calculator/bmr-formula.php
		//
		// Women: BMR = 655 + ( 9.6 x weight in kilos ) + ( 1.8 x height in cm ) - ( 4.7 x age in years )
		// Men: BMR = 66 + ( 13.7 x weight in kilos ) + ( 5 x height in cm ) - ( 6.8 x age in years )
		// --------------------------------------------------------------------------------
		
		double bmr = 0;
		
		switch(reading.getScaleProfile().getGender())
		{
			case MALE:
				bmr = 66 + (13.7 * reading.getWeightKg()) + (5 * reading.getScaleProfile().getHeightCm()) - (6.8 * reading.getScaleProfile().getAge());
				break;
				
			case FEMALE:
				bmr = 655 + (9.6 * reading.getWeightKg()) + (1.8 * reading.getScaleProfile().getHeightCm()) - (4.7 * reading.getScaleProfile().getAge());
				break;
		}
		
		return bmr;
	}
	
	/**
	 * Gets the next slot of raw data
	 * @param data the raw data
	 * @param position offset into the raw data
	 * @return the raw slot data
	 */
	
	private static int[] getNextSlotData(byte[] data, int position)
	{
		int[] slotData = new int[BYTES_PER_DATA_SLOT];
		
		// --------------------------------------------------------------------------------
        // Get the next slots data converting each signed (-127 to 128) byte to an int
        // --------------------------------------------------------------------------------
		
		for(int b = position; b < (position + BYTES_PER_DATA_SLOT); b++)
		{	
			slotData[b - position] = data[b] & 0xff;
		}
		
		return slotData;
	}
	
	/**
	 * Pretty prints the Hex of the slot data
	 * @param slot
	 * @param scaleProfile
	 * @param reading
	 * @param data
	 */
	
	private static void showSlotData(int slot, int[] data)
	{
		StringBuffer slotData = new StringBuffer();
		
		for(int d = 0; d < data.length; d++)
		{
			slotData.append(String.format("%02x ", data[d]));
		}
		
		System.out.println(String.format("Slot %03d : %s", slot, slotData.toString().trim()));
	}
	
	/**
	 * Determines if a slot is completely empty (i.e. all zeros)
	 * @param slot the data representing the slot
	 * @return true if the slot is empty, false if its not
	 */
	
	private static boolean isEmptySlot(int[] slot)
	{
		for(int i = 0; i < slot.length; i++)
		{
			if(slot[i] != 0) return false;
		}
		return true;
	}
	
	/**
	 * Determines if a slot is an empty profile slot.  The 8th, 9th and 10th bytes
	 * are zero if the slot has not been configured within the weight scale
	 * @param slot the data representing the slot
	 * @return true if the profile elements within the slot are empty
	 */
	
	private static boolean isEmptyProfileSlot(int[] slot)
	{
		for(int i = 7; i < 10; i++)
		{
			if(slot[i] != 0) return false;
		}
		return true;
	}
	
	/**
	 * Determines if a slot contains an empty reading.  Elements 11 onwards contain
	 * core reading data and will be empty if the slot doesn't contain a reading
	 * @param slot the data representing the slot
	 * @return true if the reading elements within the slot are empty
	 */
	
	private static boolean isEmptyReadingSlot(int[] slot)
	{
		for(int i = 10; i < slot.length; i++)
		{
			if(slot[i] != 0) return false;
		}
		return true;
	}
}
