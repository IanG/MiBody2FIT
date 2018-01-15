package uk.co.aspian.fit;

import java.io.File;
import java.util.Date;

import com.garmin.fit.DateTime;
import com.garmin.fit.FileEncoder;
import com.garmin.fit.WeightScaleMesg;

public class WeightScaleFitCreator
{

	public static void main(String[] args)
	{
		// --------------------------------------------------------------------------------
		// Scale values we'll need to write into the .FIT file
		// --------------------------------------------------------------------------------
		
		float weight_scale_weight_kg = 113.398f; // 86.1826=190lbs, 113.398=250lbs
		float weight_scale_pcnt_fat = 40.4f; 
		float weight_scale_pcnt_hydration = 65.4f;
		short weight_scale_visceral_fat_rating = 11;

		// --------------------------------------------------------------------------------
		// Create the WeightScale object (As an ANT+ compatible scale would)
		// NOTE: We don't have to set all the fields - only Weight(kg) is mandatory
		// --------------------------------------------------------------------------------
	
		WeightScaleMesg msg = new WeightScaleMesg();
				
		msg.setTimestamp(new DateTime(new Date()));
		//msg.setActiveMet();
		//msg.setBasalMet();
		//msg.setBoneMass();
		//msg.setMetabolicAge();
		//msg.setMuscleMass();
		msg.setPercentFat(weight_scale_pcnt_fat);
		msg.setPercentHydration(weight_scale_pcnt_hydration);
		//msg.setPhysiqueRating();
		msg.setUserProfileIndex(1);		// User Profile Id from Scales
		//msg.setVisceralFatMass();
		msg.setVisceralFatRating(weight_scale_visceral_fat_rating);
		msg.setWeight(weight_scale_weight_kg);
		

		// --------------------------------------------------------------------------------
		// Now encode the ANT+ message into a .FIT file
		// --------------------------------------------------------------------------------
		
		File outputFile = new File("./fit/idg_weight_scale_test_pm.fit");
		
		if(!outputFile.exists())
		{
			FileEncoder encoder = new FileEncoder(outputFile);
			encoder.write(msg);
			encoder.close();
			
			System.out.println("Output File \'" + outputFile.getAbsolutePath() + "\' written");
		}
		else
		{
			System.out.println("Output File \'" + outputFile.getAbsolutePath() + "\' already exists");
		}
	}
}
