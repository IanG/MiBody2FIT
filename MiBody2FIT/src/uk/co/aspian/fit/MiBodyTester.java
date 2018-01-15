package uk.co.aspian.fit;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

import uk.co.aspian.health.devices.scales.data.ScaleData;
import uk.co.aspian.health.devices.scales.data.ScaleProfile;
import uk.co.aspian.health.devices.scales.data.ScaleReading;
import uk.co.aspian.health.devices.scales.data.exporter.ScaleDataCSVExporter;
import uk.co.aspian.health.devices.scales.data.exporter.ScaleDataFITExporter;
import uk.co.aspian.health.devices.scales.data.importer.salter.MiBody9119Importer;


public class MiBodyTester
{
	static final int USER_COUNT = 12;
	static final int SLOTS_PER_USER = 35;
	static final int BYTES_PER_SLOT = 18;
	
	public static void main(String[] args)
    {
		File inputFile = new File("." + File.separator + "mibody" + File.separator + "BODYDATA-20140201.TXT");
		File csvOutputFile = new File("." + File.separator + "csv" + File.separator + "BODYDATA-20140516.CSV");
		File fitOutputFile = new File("." + File.separator + "fit" + File.separator + "BODYDATA-20140516.FIT");
		
		byte[] rawData = getMiBodyData(inputFile);
		
		MiBody9119Importer importer = new MiBody9119Importer();
		
		ScaleData scaleData = importer.getScaleData(rawData);
		
		showScaleData(scaleData);
		
		try
		{
			// --------------------------------------------------------------------------------
	        // Export to FIT format
	        // --------------------------------------------------------------------------------
			
			ScaleDataFITExporter fitExporter = new ScaleDataFITExporter(scaleData);
			fitExporter.exportProfileToFile(scaleData.getScaleProfiles().get(0), fitOutputFile);
			
			// --------------------------------------------------------------------------------
	        // Export to CSV Format
	        // --------------------------------------------------------------------------------
			
			//ScaleDataCSVExporter csvExporter = new ScaleDataCSVExporter(scaleData);
			//csvExporter.exportToFile(csvOutputFile);
		}
		catch(IOException e)
		{
			System.err.println("Failed to export data");
			e.printStackTrace();
		}
		
		
    }
	
	private static void showScaleData(ScaleData scaleData)
	{
		System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------");
		System.out.println("User Gender Age Height(cm) Fitness Level Reading Date       Time     weight(kg) Body Fat(%) Body Water(%) Muscle Mass(%) V.Fat  BMI    BMR PR");
		System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------");
		
		for(ScaleProfile profile : scaleData.getScaleProfiles())
		{
			for(ScaleReading reading : profile.getReadings())
			{
				System.out.println(String.format("%4s %6s %3d     %.2f %13S %7s %10s %8s      %.2f       %.2f         %.2f          %.2f    %2d %.1f %.1f  %1d", 
												 reading.getScaleProfile().getId(),
												 reading.getScaleProfile().getGender().getDescription(),
												 reading.getScaleProfile().getAge(),
												 reading.getScaleProfile().getHeightCm(),
												 reading.getScaleProfile().getFitnessLevel().getDescription(),
												 reading.getId(),
												 new SimpleDateFormat("yyyy-MM-dd").format(reading.getDate()),
												 new SimpleDateFormat("hh:mm:ss").format(reading.getDate()),
												 reading.getWeightKg(),
												 reading.getBodyFatPcnt(),
												 reading.getBodyWaterPcnt(),
												 reading.getMuscleMassPcnt(),
												 reading.getVisceralFat(),
												 reading.getBodyMassIndex(),
												 reading.getBasalMetabolicRate(),
												 reading.getPhysiqueRating()
												 ));
			}
		}
	}
	
	private static byte[] getMiBodyData(File dataFile)
	{
		byte[] data = null;
		
		try
		{
			if(dataFile.exists())
			{
				//System.out.println("File \'" + dataFile.getAbsolutePath() + "\' exists");
				
				FileInputStream fis = new FileInputStream(dataFile);
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				
				byte[] buf = new byte[1024];
				for(int bytesRead; (bytesRead = fis.read(buf)) != -1;)
				{
					bos.write(buf, 0, bytesRead);
				}
				
				data = bos.toByteArray();
				
				fis.close();
				fis = null;
				
				bos.close();
				bos = null;
			}
			else
			{
				System.out.println("It doesn't");
			}
    	}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		return data;
	}
}
