package uk.co.aspian.health.devices.scales.data;

/**
 * An enumeration to represent the persons fitness level.
 * @author idg
 */

public enum ScaleProfileFitnessLevel
{
	LOW("Low"),
	MEDIUM("Medium"),
	HIGH("High"),
	UNKNOWN("Unknown");
	
	private String description;

	ScaleProfileFitnessLevel(String description) { this.description = description; }
	public String getDescription() { return description; }	
}