package uk.co.aspian.health.devices.scales.data.importer;

import uk.co.aspian.health.devices.scales.data.ScaleData;

public abstract class ScaleDataImporter
{
	public abstract ScaleData getScaleData(byte[] raw);
}
