package uk.co.aspian.fit;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.garmin.fit.DateTime;
import com.garmin.fit.FileEncoder;
import com.garmin.fit.Mesg;
import com.garmin.fit.WeightScaleMesg;

public class MultiEntryWeightScaleFitCreator
{

	public static void main(String[] args)
	{
		// --------------------------------------------------------------------------------
		// Set the base values
		// --------------------------------------------------------------------------------
		
		float weight_scale_weight_kg = 113.398f; // 86.1826=190lbs, 113.398=250lbs
		float weight_scale_pcnt_fat = 40.4f; 
		float weight_scale_pcnt_hydration = 65.4f;
		short weight_scale_visceral_fat_rating = 11;
		Date baseDate = new Date();		
		
		List<Mesg> messages = new ArrayList<Mesg>();
				
		for(int i = 0; i < 5; i++)
		{
			Calendar cal = GregorianCalendar.getInstance();
			cal.setTime(baseDate);
			cal.add(Calendar.DATE, i);
			
			// --------------------------------------------------------------------------------
			// Create the WeightScale object (As an ANT+ compatible scale would)
			// NOTE: We don't have to set all the fields - only Weight(kg) is mandatory
			// --------------------------------------------------------------------------------
		
			WeightScaleMesg msg = new WeightScaleMesg();
					
			msg.setTimestamp(new DateTime(cal.getTime()));
			msg.setPercentFat(weight_scale_pcnt_fat);
			msg.setPercentHydration(weight_scale_pcnt_hydration);
			msg.setUserProfileIndex(1);		// User Profile Id from Scales
			msg.setVisceralFatRating(weight_scale_visceral_fat_rating);
			msg.setWeight(weight_scale_weight_kg);
						
			messages.add(msg);
			
			weight_scale_weight_kg += weight_scale_weight_kg / 10; 
		}
		
		// --------------------------------------------------------------------------------
		// Now encode the ANT+ message into a .FIT file
		// --------------------------------------------------------------------------------
		
		File outputFile = new File(".\\fit\\idg_multi_weight_scale_test.fit");
		
		if(!outputFile.exists())
		{
			FileEncoder encoder = new FileEncoder(outputFile);
			encoder.write(messages);
			encoder.close();
			
			System.out.println("Output File \'" + outputFile.getAbsolutePath() + "\' written");
		}
		else
		{
			System.out.println("Output File \'" + outputFile.getAbsolutePath() + "\' already exists");
		}


	}

}
