package deri.sensor.components.Map;

import java.util.HashSet;
import java.util.Set;
/**
 * @author Hoan Nguyen Mau Quoc
 */
public class MarkersShownNotFromDataSource {
	public static final Set<String> not_from_datasource_place_directly = new HashSet<String>();
	public static final Set<String> not_from_datasource_source = new HashSet<String>();
	public static final Set<String> from_snowsource = new HashSet<String>();
	public static final Set<String> from_trafficsource = new HashSet<String>();
	public static final Set<String> from_roadsource = new HashSet<String>();
	
	static{
		not_from_datasource_place_directly.add("sealevel");		
		not_from_datasource_source.add("webcam");
		not_from_datasource_source.add("radar");
		not_from_datasource_source.add("satellite");
		not_from_datasource_source.add("bikehire");
		
		from_snowsource.add("snowdepth");
		from_snowsource.add("snowfall");
		
		from_trafficsource.add("traffic");
		from_roadsource.add("roadactivity");
	}
}
