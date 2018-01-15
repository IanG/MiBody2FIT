package uk.co.aspian.health.devices.scales.data;

/**
 * An enumeration to represent the persons gender
 * @author idg
 */

public enum ScaleProfileGender
{
	MALE("Male"),
	FEMALE("Female");
				
	private String description;

	ScaleProfileGender(String description) { this.description = description; }
	public String getDescription() { return description; }
	public String getShortDescription() { return description.substring(0, 1); }
}
