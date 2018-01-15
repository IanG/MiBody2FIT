package uk.co.aspian.health.devices.scales.data;

import java.util.List;

/**
 * A class to represent a person's profile from a weight scale
 * @author idg
 */

public class ScaleProfile
{
	ScaleData scaleData;
	int id;
	ScaleProfileGender gender;
	int age;
	float heightCm;
	ScaleProfileFitnessLevel fitnessLevel;
	List<ScaleReading> readings;
	
	public ScaleProfile(ScaleData scaleData)
	{
		this.scaleData = scaleData;
	}
	
	public ScaleData getScaleData() { return scaleData; }
	public void setScaleDate(ScaleData scaleData) { this.scaleData = scaleData; }
	
	public int getId() { return id; }
	public void setId(int id) { this.id = id; }

	public ScaleProfileGender getGender() { return gender; }
	public void setGender(ScaleProfileGender gender) { this.gender = gender; }
	
	public int getAge() { return age; }
	public void setAge(int age) { this.age = age; }

	public float getHeightCm() { return heightCm; }
	public void setHeightCm(float heightCm) { this.heightCm = heightCm; }

	public ScaleProfileFitnessLevel getFitnessLevel() { return fitnessLevel; }
	public void setFitnessLevel(ScaleProfileFitnessLevel fitnessLevel) { this.fitnessLevel = fitnessLevel; }
	
	public List<ScaleReading> getReadings() { return readings; }
	public void setReadings(List<ScaleReading> readings) { this.readings = readings; }	
}
