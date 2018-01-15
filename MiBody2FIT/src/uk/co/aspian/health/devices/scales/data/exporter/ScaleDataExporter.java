package uk.co.aspian.health.devices.scales.data.exporter;

import uk.co.aspian.health.devices.scales.data.ScaleData;

public abstract class ScaleDataExporter
{
	protected ScaleData scaleData;
	
	public ScaleDataExporter(ScaleData scaleData)
	{
		this.scaleData = scaleData;
	}
}
