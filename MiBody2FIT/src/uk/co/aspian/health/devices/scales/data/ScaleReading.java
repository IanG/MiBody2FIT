package uk.co.aspian.health.devices.scales.data;

import java.util.Date;

/**
 * A class that represents a reading from weight scales
 * @author idg
 */

public class ScaleReading
{
	ScaleProfile scaleProfile;
	
	String id;
	Date date;
	double weightKg;	
	double bodyFatPcnt;
	double muscleMassPcnt;
	int visceralFat;
	double bodyMassIndex;
	double basalMetabolicRate;
	double bodyWaterPcnt;
	short physiqueRating;
	
	public ScaleReading() {}
	
	public ScaleReading(ScaleProfile profile)
	{
		this.scaleProfile = profile;
	}

	public ScaleProfile getScaleProfile() { return scaleProfile; }
	public void setScaleProfile(ScaleProfile scaleProfile) { this.scaleProfile = scaleProfile; }

	public String getId() { return id; }
	public void setId(String id) { this.id = id; }
	
	public Date getDate() { return date; }
	public void setDate(Date date) { this.date = date; }

	public double getWeightKg() { return weightKg; }
	public void setWeightKg(double weightKg) { this.weightKg = weightKg; }

	public double getBodyFatPcnt() { return bodyFatPcnt; }
	public void setBodyFatPcnt(double bodyFatPcnt) { this.bodyFatPcnt = bodyFatPcnt; }

	public double getMuscleMassPcnt() { return muscleMassPcnt; }
	public void setMuscleMassPcnt(double muscleMassPcnt) { this.muscleMassPcnt = muscleMassPcnt; }

	public int getVisceralFat() { return visceralFat; }
	public void setVisceralFat(int visceralFatPcnt) { this.visceralFat = visceralFatPcnt; }

	public double getBodyMassIndex() { return bodyMassIndex; }
 	public void setBodyMassIndex(double bodyMassIndex) { this.bodyMassIndex = bodyMassIndex; }

	public double getBasalMetabolicRate() { return basalMetabolicRate; }
 	public void setBasalMetabolicRate(double basalMetabolicRate) { this.basalMetabolicRate = basalMetabolicRate; }

	public double getBodyWaterPcnt() { return bodyWaterPcnt; }
	public void setBodyWaterPcnt(double bodyWaterPcnt) { this.bodyWaterPcnt = bodyWaterPcnt; }

	public short getPhysiqueRating() { return physiqueRating; }
	public void setPhysiqueRating(short physiqueRating) { this.physiqueRating = physiqueRating; }
}

