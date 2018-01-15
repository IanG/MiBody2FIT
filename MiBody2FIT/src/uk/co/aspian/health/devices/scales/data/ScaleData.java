package uk.co.aspian.health.devices.scales.data;

import java.util.List;

/**
 * A Class to represent a data reading from a weight scale
 * @author idg
 */

public class ScaleData
{
	String scaleMake;
	String scaleModel;
	
	List<ScaleProfile> scaleProfiles;
	
	public String getScaleMake() { return scaleMake; }
	public void setScaleMake(String scaleMake) { this.scaleMake = scaleMake; }
	
	public String getScaleModel() { return scaleModel; }
	public void setScaleModel(String scaleModel) { this.scaleModel = scaleModel; }
	
	public List<ScaleProfile> getScaleProfiles() { return scaleProfiles; }
	public void setScaleProfiles(List<ScaleProfile> scaleProfiles)  { this.scaleProfiles = scaleProfiles; }
}
