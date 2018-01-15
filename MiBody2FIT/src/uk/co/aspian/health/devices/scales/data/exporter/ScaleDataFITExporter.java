package uk.co.aspian.health.devices.scales.data.exporter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import uk.co.aspian.health.devices.scales.data.ScaleData;
import uk.co.aspian.health.devices.scales.data.ScaleProfile;
import uk.co.aspian.health.devices.scales.data.ScaleReading;

import com.garmin.fit.DateTime;
import com.garmin.fit.FileEncoder;
import com.garmin.fit.Mesg;
import com.garmin.fit.WeightScaleMesg;

public class ScaleDataFITExporter extends ScaleDataExporter
{

	public ScaleDataFITExporter(ScaleData scaleData)
    {
	    super(scaleData);
    }
	
	public void exportProfileToFile(ScaleProfile profile, File file) throws IOException
	{		
		// --------------------------------------------------------------------------------
		// Create the list to house our readings
		// --------------------------------------------------------------------------------		
		
		List<Mesg> messages = new ArrayList<Mesg>();
		
		for(ScaleReading reading : profile.getReadings())
		{
			// --------------------------------------------------------------------------------
			// Create the WeightScale object (As an ANT+ compatible scale would)
			// NOTE: We don't have to set all the fields - only Weight(kg) is mandatory
			// --------------------------------------------------------------------------------
		
			WeightScaleMesg msg = new WeightScaleMesg();
					
			msg.setUserProfileIndex(profile.getId());
			msg.setPhysiqueRating(reading.getPhysiqueRating());
			msg.setTimestamp(new DateTime(reading.getDate()));
			msg.setWeight((float)reading.getWeightKg());
			msg.setPercentFat((float)reading.getBodyFatPcnt());
			msg.setPercentHydration((float)reading.getBodyWaterPcnt());
			msg.setVisceralFatRating((short)reading.getVisceralFat());
			msg.setBasalMet((float)reading.getBasalMetabolicRate());
			msg.setMuscleMass((float)reading.getMuscleMassPcnt());
						
			messages.add(msg);
		}
		
		// --------------------------------------------------------------------------------
		// Now encode the ANT+ message into a .FIT file
		// --------------------------------------------------------------------------------
		
		FileEncoder encoder = new FileEncoder(file);
		encoder.write(messages);
		encoder.close();	
	}
}
