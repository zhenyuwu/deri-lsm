package deri.sensor.components.Map;


import java.util.HashMap;
import java.util.Map;

import deri.sensor.utils.ConstantsUtil;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class MapZoomUtil {
	public static Map<Integer,Double> level2divide_map = new HashMap<Integer,Double>(5);
	public static Map<Integer,Double> zoom2MapWidthNotCollapse = new HashMap<Integer,Double>(20);
	public static Map<Integer,Double> zoom2MapWidthCollapse = new HashMap<Integer,Double>(20);
	private static final int divide_by = 3;
	
	static{
		initializelevel2divide_map();
		initializezoom2MapWidthNotCollapse();
		initializezoom2MapWidthCollapse();
	}
	
	private static void initializelevel2divide_map(){
		level2divide_map.put(ConstantsUtil.marker_show_level_0, 100d);
		level2divide_map.put(ConstantsUtil.marker_show_level_1, 10d);
		level2divide_map.put(ConstantsUtil.marker_show_level_2, 1d);
	}
	
	private static void initializezoom2MapWidthNotCollapse(){
		zoom2MapWidthNotCollapse.put(0, 360.0);
		zoom2MapWidthNotCollapse.put(1, 360.0);
		zoom2MapWidthNotCollapse.put(2, 360.0);
		zoom2MapWidthNotCollapse.put(3, 177.5390625);
		zoom2MapWidthNotCollapse.put(4, 88.76953125);
		zoom2MapWidthNotCollapse.put(5, 44.384765625);
		zoom2MapWidthNotCollapse.put(6, 22.1923828125);
		zoom2MapWidthNotCollapse.put(7, 11.09619140625);
		zoom2MapWidthNotCollapse.put(8, 5.548095703125);
		zoom2MapWidthNotCollapse.put(9, 2.7740478515625);
		zoom2MapWidthNotCollapse.put(10, 1.38702392578125);
		zoom2MapWidthNotCollapse.put(11, 0.693511962890625);
		zoom2MapWidthNotCollapse.put(12, 0.3467559814453125);
		zoom2MapWidthNotCollapse.put(13, 0.17337799072265625);
		zoom2MapWidthNotCollapse.put(14, 0.08668899536132812);
		zoom2MapWidthNotCollapse.put(15, 0.04334449768066406);
		zoom2MapWidthNotCollapse.put(16, 0.02167224884033203);
		zoom2MapWidthNotCollapse.put(17, 0.010836124420166016);
		zoom2MapWidthNotCollapse.put(18, 0.005418062210083008);
		zoom2MapWidthNotCollapse.put(19, 0.002709031105041504);
	}
	
	private static void initializezoom2MapWidthCollapse(){
		zoom2MapWidthCollapse.put(0, 360.0);
		zoom2MapWidthCollapse.put(1, 360.0);
		zoom2MapWidthCollapse.put(2, 360.0);
		zoom2MapWidthCollapse.put(3, 215.859375);
		zoom2MapWidthCollapse.put(4, 107.9296875);
		zoom2MapWidthCollapse.put(5, 53.96484375);
		zoom2MapWidthCollapse.put(6, 26.982421875);
		zoom2MapWidthCollapse.put(7, 13.491210937500004);
		zoom2MapWidthCollapse.put(8, 6.74560546875);
		zoom2MapWidthCollapse.put(9, 3.372802734375);
		zoom2MapWidthCollapse.put(10, 1.6864013671875);
		zoom2MapWidthCollapse.put(11, 0.84320068359375);
		zoom2MapWidthCollapse.put(12, 0.421600341796875);
		zoom2MapWidthCollapse.put(14, 0.10540008544921875);
		zoom2MapWidthCollapse.put(15, 0.052700042724609375);
		zoom2MapWidthCollapse.put(16, 0.026350021362304688);
		zoom2MapWidthCollapse.put(17, 0.013175010681152344);
		zoom2MapWidthCollapse.put(18, 0.006587505340576172);
		zoom2MapWidthCollapse.put(19, 0.003293752670288086);
	}
	
	/**
	 * to get the level. Level means the group level. 
	 * level2divide_map map stored the necessary data.
	 * @param zoom
	 * @return
	 */
	public static int calculateLevel(int zoom){
		if(zoom <= 1){
			return 0;
		}
		int level = (zoom - 2) / divide_by;
		return level;
	}
	
	public static double getDivide(int level){
		if(level>2) return 1d;
		return level2divide_map.get(level);
	}
	
	/**
	 * I get the map widths with each zoom with the filter collapsed or not
	 * @param zoom
	 * @param isCollapsed
	 * @return
	 */
	public static double getCurrentMapLngWidth(int zoom, boolean isCollapsed){
		if(isCollapsed){
			return zoom2MapWidthCollapse.get(zoom);
		}else{
			return zoom2MapWidthNotCollapse.get(zoom);
		}
	}
	
}
