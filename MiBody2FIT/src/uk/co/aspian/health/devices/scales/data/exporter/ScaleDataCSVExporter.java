package uk.co.aspian.health.devices.scales.data.exporter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;

import uk.co.aspian.health.devices.scales.data.ScaleData;
import uk.co.aspian.health.devices.scales.data.ScaleProfile;
import uk.co.aspian.health.devices.scales.data.ScaleReading;

public class ScaleDataCSVExporter extends ScaleDataExporter
{
	public ScaleDataCSVExporter(ScaleData scaleData)
    {
	    super(scaleData);
    }
	
	public void exportToFile(File file) throws IOException
	{		
		FileWriter writer = new FileWriter(file, false);
		writer.write(getExportAsString());
		writer.flush();
		writer.close();
	}
	
	public String getExportAsString()
	{
		StringBuffer buffer = new StringBuffer();
		
		// --------------------------------------------------------------------------------
        // Write out the file header based upon the fields
        // --------------------------------------------------------------------------------
		
		buffer.append("User,Gender,Age,Height(cm),Fitness Level,Reading,Date,weight(kg),Body Fat(%),Body Water(%),Muscle Mass(%),Visceral Fat,BMI,BMR\n");

		// --------------------------------------------------------------------------------
        // Now write out each reading from the scale data
        // --------------------------------------------------------------------------------
		
		for(ScaleProfile profile : scaleData.getScaleProfiles())
		{
			for(ScaleReading reading : profile.getReadings())
			{
				buffer.append(String.format("%s,%s,%d,%.2f,%S,%s,%s,%.2f,%.2f,%.2f,%.2f,%2d,%.1f,%.1f\n", 
												 reading.getScaleProfile().getId(),
												 reading.getScaleProfile().getGender().getDescription(),
												 reading.getScaleProfile().getAge(),
												 reading.getScaleProfile().getHeightCm(),
												 reading.getScaleProfile().getFitnessLevel().getDescription(),
												 reading.getId(),
												 new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(reading.getDate()),
												 reading.getWeightKg(),
												 reading.getBodyFatPcnt(),
												 reading.getBodyWaterPcnt(),
												 reading.getMuscleMassPcnt(),
												 reading.getVisceralFat(),
												 reading.getBodyMassIndex(),
												 reading.getBasalMetabolicRate()
												 ));
			}
		}

		return buffer.toString();
	}

}
