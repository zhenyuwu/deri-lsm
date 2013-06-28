package deri.sensor.components.Map;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.zkoss.gmaps.Gmaps;
import org.zkoss.gmaps.Gmarker;
import org.zkoss.gmaps.Gpolygon;
import org.zkoss.gmaps.event.MapMouseEvent;
import org.zkoss.zul.East;
import org.zkoss.zul.Messagebox;

import deri.sensor.components.search.DataSearchTabPannel;
import deri.sensor.components.search.MapSearchTabPanel;
import deri.sensor.components.search.SearchTabbox;
import deri.sensor.components.user.FilterRegion;
import deri.sensor.components.user.FilterSaveView;
import deri.sensor.components.user.datamanagent.UserDataMashupTab;
import deri.sensor.components.user.datamanagent.UserLiveDataTab;
import deri.sensor.components.windows.AdvanceSearchWindow;
import deri.sensor.components.windows.SensorWithSourceWindow;
import deri.sensor.database.ServiceLocator;
import deri.sensor.javabeans.CallSign;
import deri.sensor.javabeans.SensorServer;
import deri.sensor.javabeans.User;
import deri.sensor.manager.SensorManager;
import deri.sensor.manager.UserActiveManager;
import deri.sensor.meta.SensorType;
import deri.sensor.meta.SensorTypeEnum;
import deri.sensor.utils.ConstantsUtil;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class GMaps extends Gmaps {
	private static final long serialVersionUID = -7261148124568614950L;
	private int zoom_before = ConstantsUtil.map_default_zoom;
	private Gpolygon mypoly;
	private double circleLat;
	private double circleLng;
	private double circleRadius;
	private int generalzoom;

	private SensorServer svPush;
	public GMaps() {
		super();
	}
	
	public void init(double lat, double lng){
		this.setShowTypeCtrl(true);
		this.setScrollWheelZoom(true);
		this.setShowSmallCtrl(true);
		this.setShowScaleCtrl(true);
		this.setMapType("hybrid");
		this.setShowLargeCtrl(true);
		this.setZoom(ConstantsUtil.map_default_zoom);
		this.setCenter(lat, lng);
//		String repSrc = Sessions.getCurrent().getWebApp().getRealPath("/WEB-INF/Data/LinkImages/");
//        System.out.println(repSrc);
	}
	
	
	public Gpolygon getMypoly() {
		return mypoly;
	}

	public void setMypoly(Gpolygon mypoly) {
		this.mypoly = mypoly;
	}

	public double getCircleRadius() {
		return circleRadius;
	}

	public void setCircleRadius(double circleRadius) {
		this.circleRadius = circleRadius;
	}

	public double getCircleLat() {
		return circleLat;
	}

	public void setCircleLat(double circleLat) {
		this.circleLat = circleLat;
	}

	public double getCircleLng() {
		return circleLng;
	}

	public void setCircleLng(double circleLng) {
		this.circleLng = circleLng;
	}

	public void detachCircle(){
		if(mypoly!=null) this.mypoly.detach();
	}
	
	public void drawCircle(){
		try{
			double lat1 = circleLat * Math.PI / 180;
			double lng1= circleLng * Math.PI / 180;			
			int level = MapZoomUtil.calculateLevel(this.getZoom());
			
			double R=6371;
			double d=this.circleRadius*3990/6371;
			if(mypoly!=null) mypoly.detach();
			mypoly = new Gpolygon();
			for(int i=0;i<360;i++)
			{
			double brng=i * Math.PI / 180;
			double lat2 = Math.asin( Math.sin(lat1)*Math.cos(d/R) +
			Math.cos(lat1)*Math.sin(d/R)*Math.cos(brng) );
			double lon2 = lng1 + Math.atan2(Math.sin(brng)*Math.sin(d/R)*Math.cos(lat1),
			Math.cos(d/R)-Math.sin(lat1)*Math.sin(lat2));
			lon2 = (lon2+Math.PI)%(2*Math.PI) - Math.PI;
	
			double finallat2=lat2* 180 / Math.PI;
			double finallon2= lon2* 180 / Math.PI;			
			mypoly.addPoint(finallat2, finallon2, 3);
			}
			mypoly.setParent(this);
			mypoly.setColor("#FF0000");
			mypoly.setWeight(5);			
		}catch(Exception e){
			
		}
	
	}
	
	
	/**
	 * This is called when the sensor.zul is created.
	 * 
	 */
	public void onCreate(){
		this.init(ConstantsUtil.map_default_lat, ConstantsUtil.map_default_lng);
	}
	
	/* ----------------------------- map functions ----------------------------- */
	
	/**
	 * the neLat, neLng, swLat, swLng have already processed(the version of gmaps integrated in zkoss will get the right part of the map
	 * if the -180 lng appears in the map to avoid the condition that the neLng <= swLng. So I processed it).
	 * @param type
	 * @param level
	 * @param regionType
	 * @param region
	 * @param neLat
	 * @param neLng
	 * @param swLat
	 * @param swLng
	 */
	private void showMarkers(String type, int level, String regionType, List<String> region, double neLat, double neLng, double swLat, double swLng,String userId){
		SensorManager sensorManager = ServiceLocator.getSensorManager();
		if(!type.equals(SensorTypeEnum.ADSBHub.toString())){
			List<Object[]> obj_array_list =  sensorManager.getMarkersShown(type, level, regionType, region, neLat, neLng, swLat, swLng,userId);
			if(obj_array_list != null){
				int zoom = this.getZoom();
				for(Object[] obj_array : obj_array_list){
					try{
						int markersNearby = Integer.parseInt(obj_array[0].toString());				
						double lat = Double.parseDouble(obj_array[1].toString());
						double lng = Double.parseDouble(obj_array[2].toString());
						SensorMarker marker = new SensorMarker(type,markersNearby,lat,lng,zoom,neLat,neLng,swLat,swLng);
						marker.setParent(this);
						marker.init();
					}catch(Exception e){
						e.printStackTrace();
						continue;
					}
				}
			}
		}else{
			List<Object[]> obj_array_list =  sensorManager.getFlightMarkersShown(level, regionType, region, neLat, neLng, swLat, swLng,userId);
			if(obj_array_list != null){
				int zoom = this.getZoom();
				for(Object[] obj_array : obj_array_list){
					int markersNearby = ((Integer)obj_array[0]).intValue();				
					double lat = Double.parseDouble(obj_array[1].toString());
					double lng = Double.parseDouble(obj_array[2].toString());					
					SensorMarker marker = new SensorMarker(type,markersNearby,lat,lng,zoom,neLat,neLng,swLat,swLng);
					CallSign callSign = sensorManager.getFlightCallSignWithOneLocation(marker.getLat(),marker.getLng());
					if(callSign!=null)
						marker.setCallSign(callSign.getValue());
					marker.setParent(this);
					marker.init();
				}
			}
		}
			
	}
	
	/**
	 *
	 * @param type all can not be used
	 */
	public void deleteSpecifiedSensorTypeMarkersOrAll(String type){
		SensorMarker[] specifiedMarkers = this.getSpecifiedSensorTypeMarker(type);
		if(specifiedMarkers != null && specifiedMarkers.length != 0){
			for(SensorMarker marker : specifiedMarkers){
				marker.detach();
			}
		}
	}

	/**
	 * 1. get the ofiginal neLat, neLng, swLat, swLng
	 * 2. get the region filters
	 * 3. show it
	 * @param type all can not be used
	 */
	public void addSpecifiedSensorTypeMarkers(String type,String userId){
		int level = MapZoomUtil.calculateLevel(this.getZoom());		
		double neLat = this.getNeLat();
		double neLng = this.getNeLng();
		double swLat = this.getSwLat();
		double swLng = this.getSwLng();
		
		double[] bounds = this.toOriginalBounds(neLat, neLng, swLat, swLng);
		neLat = bounds[0];
		neLng = bounds[1];
		swLat = bounds[2];
		swLng = bounds[3];
		
		List<String> countries = this.getSelectedRegionAfterProcess();
		if(countries.size() == 7){// all the 7 continents checked
			this.showMarkers(type, level, "all", null, neLat, neLng, swLat, swLng,userId);
		}else{
			List<String> continents = Country_Continent_Util.RemoveContinents(countries);
			if(continents != null && continents.size() > 0){
				this.showMarkers(type, level, "continent", continents, neLat, neLng, swLat, swLng,userId);
			}
			
			if(countries != null && countries.size() > 0){
				this.showMarkers(type, level, "country", countries, neLat, neLng, swLat, swLng,userId);
			}
		}
	}
	
	/**
	 * 
	 * @param type all can not be used
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private SensorMarker[] getSpecifiedSensorTypeMarker(String type){
		try{
			//List<SensorMarker> markers = this.getChildren();
			//List<Object> markers = this.getChildren();
			List<SensorMarker> markers = getSensorMakersFromChilds();
			List<SensorMarker> specifiedMarkers = new ArrayList<SensorMarker>();
			if(markers != null){
				SensorMarker[] markers_array = markers.toArray(new SensorMarker[markers.size()]); 
				if(type == "all"){
					return markers_array;
				}else{
					for(SensorMarker marker : markers_array){
						if(marker.getType() == type){
							specifiedMarkers.add(marker);
						}
					}
				}
				return specifiedMarkers.toArray(new SensorMarker[specifiedMarkers.size()]);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	private List<SensorMarker> getSensorMakersFromChilds(){
		List<SensorMarker> markers = new ArrayList<SensorMarker>();
		List<Object> allChilds = this.getChildren();
		for(int i=0;i<allChilds.size();i++){
			try{
				markers.add((SensorMarker)allChilds.get(i));
			}catch(Exception e){
				continue;
			}
		}
		return markers.size()>0?markers:null;
	}
	/**
	 * this event will be activated when user clicks the marker.
	 * @param event
	 */
	public void onMapClick(MapMouseEvent event){		
		try{		
			setMashupLocation(event.getLat(), event.getLng());
			setDataSearchLocation(event.getLat(), event.getLng());
			setLiveInforLocation(event.getLat(), event.getLng());
			SensorMarker sensorMarker = (SensorMarker)event.getGmarker();
			if(sensorMarker != null){				
				if(sensorMarker.getMarkersNearby() <= ConstantsUtil.max_markers_nearby){// this can be changed to decide the maximum callsign of sensors that can be shown when user clicks one marker.
					String sensorType = sensorMarker.getType();
					int generateZoom = sensorMarker.getGenerateZoom();
					this.generalzoom = generateZoom;
					double lat = sensorMarker.getLat();
					double lng = sensorMarker.getLng();
					double neLat = sensorMarker.getNeLat();
					double neLng = sensorMarker.getNeLng();
					double swLat = sensorMarker.getSwLat();
					double swLng = sensorMarker.getSwLng();
					if(this.getRoot().getFellowIfAny("sensorWithSourceWindow") != null){
						this.getRoot().getFellowIfAny("sensorWithSourceWindow").detach();
					}
					//int le = MapZoomUtil.calculateLevel(generateZoom);				
					SensorWithSourceWindow sensorWithSourceWindow = new SensorWithSourceWindow(sensorType,"Sensor Detail");
					sensorWithSourceWindow.setParent(this.getRoot());
					sensorWithSourceWindow.init();
					
					if(sensorMarker.getMarkersNearby() == 1){						
						sensorWithSourceWindow.addContent(lat, lng);
						sensorWithSourceWindow.doOverlapped();
					}else{
						SensorManager sensorManager = ServiceLocator.getSensorManager();
						List<String> countries = this.getSelectedRegionAfterProcess();
						List<String> continents = Country_Continent_Util.RemoveContinents(countries);
						int level = MapZoomUtil.calculateLevel(generateZoom);
						
						/*
						 * The following is to get the sensor stations nearby.
						 * 1. all
						 * 2. continent
						 * 3. country
						 * */
						if(continents.size() == 7){
							List<Object[]> list = null;
							if(sensorType.equals(SensorTypeEnum.ADSBHub.toString())){
								list = sensorManager.getFlightMarkersNearby(level, "all", null, lat, lng, neLat, neLng, swLat, swLng);// lat, lng
							}else
								list = sensorManager.getMarkersNearby(sensorType, level, "all", null, lat, lng, neLat, neLng, swLat, swLng);// lat, lng
							for(Object[] obj_array : list){
								double lat_nearby = Double.parseDouble(obj_array[0].toString());
								double lng_nearby = Double.parseDouble(obj_array[1].toString());
								//System.out.println(lat_nearby +"," +lng_nearby);
								sensorWithSourceWindow.addContent(lat_nearby, lng_nearby);
							}
							sensorWithSourceWindow.doOverlapped();
						}else{
							boolean found = false;
							List<Object[]> list= null;
							if(sensorType.equals(SensorTypeEnum.ADSBHub.toString())){
								list = sensorManager.getFlightMarkersNearby(level, "all", null, lat, lng, neLat, neLng, swLat, swLng);// lat, lng
							}else
								list = sensorManager.getMarkersNearby(sensorType, level, "continent", continents, lat, lng, neLat, neLng, swLat, swLng);
							if(list != null){
								found = true;
							}
							
							if(found == false){
								if(sensorType.equals(SensorTypeEnum.ADSBHub.toString())){
									list = sensorManager.getFlightMarkersNearby(level, "all", null, lat, lng, neLat, neLng, swLat, swLng);// lat, lng
								}else
									list = sensorManager.getMarkersNearby(sensorType, level, "country", countries, lat, lng, neLat, neLng, swLat, swLng);
							}
							
							if(list != null){
								for(Object[] obj_array : list){
									double lat_nearby = Double.parseDouble(obj_array[0].toString());
									double lng_nearby = Double.parseDouble(obj_array[1].toString());
									sensorWithSourceWindow.addContent(lat_nearby, lng_nearby);
								}
								sensorWithSourceWindow.doOverlapped();
							}
						}
					}
				}else{
					this.showMessage(
							"Too Many Weather Sensors Nearby. You'd better zoom in.",
							"Information", Messagebox.OK,
							Messagebox.INFORMATION);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void onMapZoom(){		
		this.setZoom(this.getZoom());
	}
	public void onMapRightClick(MapMouseEvent event){
		try{
			SensorServer.stop();
			SensorMarker sensorMarker = (SensorMarker)event.getGmarker();
			String label_content="";
			if(sensorMarker != null){
				sensorMarker.setStyle("font-size:10px;color:#F25252;text-align:left");				
				
				SensorManager sensorManager = ServiceLocator.getSensorManager();
				if(sensorMarker.getMarkersNearby() <= ConstantsUtil.max_markers_nearby){// this can be changed to decide the maximum callsign of sensors that can be shown when user clicks one marker.
					String sensorType = sensorMarker.getType();
					int generateZoom = sensorMarker.getGenerateZoom();
					this.generalzoom = generateZoom;
					double lat = sensorMarker.getLat();
					double lng = sensorMarker.getLng();
					double neLat = sensorMarker.getNeLat();
					double neLng = sensorMarker.getNeLng();
					double swLat = sensorMarker.getSwLat();
					double swLng = sensorMarker.getSwLng();
					
					if(sensorMarker.getMarkersNearby() == 1){						
						svPush = new SensorServer();
						SensorServer.start(sensorMarker,lat,lng);
						sensorMarker.setOpen(true);
						
					}else{
						List<String> countries = this.getSelectedRegionAfterProcess();
						List<String> continents = Country_Continent_Util.RemoveContinents(countries);
						int level = MapZoomUtil.calculateLevel(generateZoom);
						
						/*
						 * The following is to get the sensor stations nearby.
						 * 1. all
						 * 2. continent
						 * 3. country
						 * */
						if(continents.size() == 7){
							List<Object[]> list = sensorManager.getMarkersNearby(sensorType, level, "all", null, lat, lng, neLat, neLng, swLat, swLng);// lat, lng
							
							for(Object[] obj_array : list){
								double lat_nearby = (Double)obj_array[0];
								double lng_nearby = (Double)obj_array[1];
								System.out.println(lat_nearby +"," +lng_nearby);
								try{
									label_content = ServiceLocator.getPlaceManager().getPlaceWithSpecifiedLatLng(lat_nearby, lng_nearby).getSimpleContent();
									svPush = new SensorServer();
									SensorServer.start(sensorMarker,lat_nearby,lng_nearby);
									sensorMarker.setOpen(true);
									return;
								}catch(Exception e){
									continue;
								}
							}						
						}else{
							boolean found = false;
							List<Object[]> list= null;
							
							list = sensorManager.getMarkersNearby(sensorType, level, "continent", continents, lat, lng, neLat, neLng, swLat, swLng);
							if(list != null){
								found = true;
							}
							
							if(found == false){
								list = sensorManager.getMarkersNearby(sensorType, level, "country", countries, lat, lng, neLat, neLng, swLat, swLng);
							}
							
							if(list != null){
								for(Object[] obj_array : list){
									double lat_nearby = (Double)obj_array[0];
									double lng_nearby = (Double)obj_array[1];
									try{
										label_content = ServiceLocator.getPlaceManager().getPlaceWithSpecifiedLatLng(lat_nearby, lng_nearby).getSimpleContent();
										svPush = new SensorServer();
										SensorServer.start(sensorMarker,lat_nearby,lng_nearby);
										sensorMarker.setOpen(true);
										return;
									}catch(Exception e){
										continue;
									}
								}							
							}
						}
					}
				}else{
					this.showMessage(
							"Too Many Weather Sensors Nearby. You'd better zoom in.",
							"Information", Messagebox.OK,
							Messagebox.INFORMATION);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * In order to upgrade the performance, I have done some process before reshowMarkersWith3Filters();
	 * 1. search is enabled and search only checkbox is checked
	 * 2. search is not enabled and no sensor type filter or region filter item selected
	 * 3. if just zoom, no action, just zoom_before = zoom_now;
	 * 4. reshowMarkersWith3Filters();
	 */
	public void onMapMove(){		
		if(this.isSearchEnabled() && this.isSearchOnlyChecked()){
			this.deleteSpecifiedSensorTypeMarkersOrAll(SensorType.all);
			this.showSearchedCities();
		}else if(!this.isSearchEnabled() && ( this.getAllTypesShownOnTheMap() == null || this.getSelectedRegionAfterProcess() == null || this.getAllTypesShownOnTheMap().size() == 0 || this.getSelectedRegionAfterProcess().size() == 0)){
			this.deleteSpecifiedSensorTypeMarkersOrAll(SensorType.all);
		}else {
			int zoom_now = this.getZoom();
			if(zoom_now != zoom_before){
				zoom_before = zoom_now;
			}else{
				reshowMarkersWith3Filters();
			}
		}
	}
	
	/**
	 * 3 Filters include:
	 * search filter,
	 * sensor type filter,
	 * region filter.
	 * 
	 */
	public void reshowMarkersWith3Filters(){
		this.deleteSpecifiedSensorTypeMarkersOrAll(SensorType.all);
		
		if(!this.isSearchEnabled()){
			this.reshowMarkersWith2Filters();
		}else{
			//this.showSearchedCities();
			if(!this.isSearchOnlyChecked()){
				this.reshowMarkersWith2Filters();
			}
		}
	}
	
	/**
	 * 2 Filters include:
	 * sensor type filter,
	 * region filter.
	 */
	private void reshowMarkersWith2Filters(){		
		UserActiveManager userManager = ServiceLocator.getUserActiveManager();
		List<String> allTypesShownOnTheMap = null;
		User user = null;
		if(isOnlyShowMySensorOnMap()){
			user = (User)this.getDesktop().getSession().getAttribute("user");
			allTypesShownOnTheMap = SensorType.getTypeList();
			//allTypesShownOnTheMap = SensorType.getSystemTypeList();
			allTypesShownOnTheMap.remove("all");
			//this.getDesktop().setAttribute("selectedSensorTypes", allTypesShownOnTheMap);
		}
		else{
			user = userManager.getUser("admin");
			allTypesShownOnTheMap= this.getAllTypesShownOnTheMap();
		}
		for(String type : allTypesShownOnTheMap){
			this.addSpecifiedSensorTypeMarkers(type,user.getId());
		}
	}
	
	@SuppressWarnings("unchecked")
	public void deleteOneMarkerWithLatLng(double lat, double lng){
		List<Gmarker> markers = this.getChildren();
		if(markers != null){
			Gmarker[] markers_array = markers.toArray(new Gmarker[markers.size()]);
			for(Gmarker marker : markers_array){
				double lat_exist = marker.getLat();
				double lng_exist = marker.getLng();
				if(lat_exist == lat && lng_exist == lng){
					marker.detach();
				}
			}
		}
	}
	
	
	/**
	 * This is for the case:
	 * If there are more than one station for one position. For it includes itself, so if there are more than one 
	 * station for one position, the marker count for that position should be more than 2.
	 * @param lat
	 * @param lng
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public int getMarkerCountWithSpecifiedLatLng(double lat, double lng){
		int count = 0;
		try{
			//List<Gmarker> markers = this.getChildren();
			List<Object> markers = this.getChildren();
			if(markers != null){
				//for(Gmarker marker : markers){				
				for(int i=0;i<markers.size();i++){
					try{
						Gmarker marker = (Gmarker) markers.get(i);
						double lat_exist = marker.getLat();
						double lng_exist = marker.getLng();
						if(lat_exist == lat && lng_exist == lng){
							count++;
						}
					}catch(Exception e){
						continue;
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return count;
	}
	
	public void showMessage(String message, String title, int buttons, String icon){
		try {
			Messagebox.show(message, title, buttons, icon);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * convert the gmap integrated into zk 3.6.3 bounds got to the real original one.
	 * @param neLat
	 * @param neLng
	 * @param swLat
	 * @param swLng
	 * @return
	 */
	private double[] toOriginalBounds(double neLat, double neLng, double swLat, double swLng){
		int zoom = this.getZoom();
		boolean isCollapsed = ((East)this.getFellowIfAny("east")).isOpen();
		double lngWidth = 0;
		if(swLng == -180 && (lngWidth = MapZoomUtil.getCurrentMapLngWidth(zoom, isCollapsed)) != 360){
			double leftLngWidth = lngWidth - (180 - Math.abs(neLng));
			swLng = 180 - leftLngWidth;
		}
		
		return new double[]{neLat, neLng, swLat, swLng};
	}

	/* ----------------------------- Circle search results on the map ----------------------------- */
	public boolean isShowOnMapChecked(){
		try{
			SearchTabbox stb = (SearchTabbox)this.getFellowIfAny("south").getFellowIfAny("searchTab");
			return stb.isShowOnMap();
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		
	}	
	
	/* ----------------------------- only show my sensor on the map ----------------------------- */
	/**
	 * show or unshow user's sensor on the map
	 * 
	 * 
	 */
	public boolean isOnlyShowMySensorOnMap(){
		try{			
			UserFisheyeBar userF = (UserFisheyeBar)this.getFellowIfAny("west").getFellowIfAny("westDiv").getFellowIfAny("userFishEyeBar");			
			return userF.isOnlyShowMySensor();
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		
	}
	/* ----------------------------- SensorType Filter functions ----------------------------- */
	
	/**
	 * for the toolbar on the top of the map
	 * @param label
	 * @param isChecked
	 */
//	public void setFirstLevelSelected(String label, boolean isChecked){
//		FilterSensor sensorFilter = (FilterSensor)this.getFellowIfAny("filterWindow").getFellowIfAny("sensorFilter");
//		sensorFilter.setFirstLevelSelected_Label(label, isChecked);
//	}
//	
	/**
	 * for the FilterSaveView Use
	 * @param signLabels
	 */
	public void setSensorTypeItemsSelected(List<String> signLabels, boolean isChecked){
		MarkerToolBar markerToolbar = (MarkerToolBar)this.getParent().getFellowIfAny("markerbar");
		markerToolbar.setItemsSelected(signLabels, isChecked);
	}

	/**
	 * for the FilterSaveView Use
	 * @return
	 */
	public List<String> getSelectedItemLabels(){
		MarkerToolBar markerToolbar = (MarkerToolBar)this.getParent().getParent().getFellowIfAny("markerbar");		
		List<String> signs = markerToolbar.getSelectedItemLabels();
		return signs;
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getAllTypesShownOnTheMap(){
		List<String> allTypesShownOnTheMap= (List<String>)this.getDesktop().getAttribute("selectedSensorTypes");
		if(allTypesShownOnTheMap == null){
			allTypesShownOnTheMap = new ArrayList<String>();
		}
		return allTypesShownOnTheMap;
	}
	
	
	
	/* ----------------------------- Region Filter functions ----------------------------- */
	/**
	 * for the userview to use
	 */
	public List<String> getSelectedRegionAfterProcess(){
		FilterRegion filterRegion = (FilterRegion)this.getFellowIfAny("filterWindow").getFellowIfAny("filterRegion");
		List<String> countries = filterRegion.getSelectedRegionAfterProcess();
		return countries;
	}
	
	/**
	 * for the userview to use
	 * @param countries
	 */
	public void setCountryItemsSelected(List<String> countries){
		FilterRegion filterRegion = (FilterRegion)this.getFellowIfAny("filterWindow").getFellowIfAny("filterRegion");
		filterRegion.setItemsSelected(countries);
	}
	
	
	/* ----------------------------- Search Filter functions ----------------------------- */
	public boolean isSearchEnabled(){
		MapSearchTabPanel search = ((SearchTabbox)this.getFellowIfAny("searchTab")).getMapSearchpnl();
		if(search!=null) return search.isSearchEnabled();
		return false;
	}
	
	public String getSearchedCitiesString(){
		MapSearchTabPanel search = ((SearchTabbox)this.getFellowIfAny("searchTab")).getMapSearchpnl();
		Set<String> set = search.getSearchedCities();
		String result = "";
		if(set != null && set.size() > 0){
			for(String city : set){
				result += city + ",";
			}
		}
		return result;
	}
	
	public Set<String> getSearchedCities(){
		MapSearchTabPanel search = ((SearchTabbox)this.getFellowIfAny("searchTab")).getMapSearchpnl();
		return search.getSearchedCities();
	}
	
	public void setSearchedCities(Set<String> cities){
		MapSearchTabPanel search = ((SearchTabbox)this.getFellowIfAny("searchTab")).getMapSearchpnl();		
		search.setSearchedCities(cities);
	}
	
	/**
	 * the cities is divided by ,
	 * @param cities
	 */
	public void setSearchedCities(String cities){
		MapSearchTabPanel search = ((SearchTabbox)this.getFellowIfAny("searchTab")).getMapSearchpnl();
		search.setSearchedCities(cities);
	}
	
//	public void openSearchFilter(boolean isOpen){
//		MapSearchTabPanel search = (MapSearchTabPanel)this.getFellowIfAny("searchTab").getFellowIfAny("searchFilter");
//		search.setOpen(isOpen);
//	}
	
	public void setSearchText(String cities){
		MapSearchTabPanel search = ((SearchTabbox)this.getFellowIfAny("searchTab")).getMapSearchpnl();
		search.setSearchText(cities);
	}
	
	public void setSearchOnly(boolean isOnly){
		MapSearchTabPanel search = ((SearchTabbox)this.getFellowIfAny("searchTab")).getMapSearchpnl();
		search.setSearchOnly(isOnly);
	}
	
	public boolean isSearchOnlyChecked(){
		MapSearchTabPanel search = ((SearchTabbox)this.getFellowIfAny("searchTab")).getMapSearchpnl();
		return search.isSearchOnly();
		
	}
	
	public void showSearchedCities(){
		MapSearchTabPanel search = ((SearchTabbox)this.getFellowIfAny("searchTab")).getMapSearchpnl();
		search.showSearchResult();
	}
	
	/* ----------------------------- UserView Filter functions ----------------------------- */
	
	public void closeSaveViewFilter(){
		FilterSaveView filterSaveView = (FilterSaveView)this.getFellowIfAny("filterWindow").getFellowIfAny("filterSaveView");
		filterSaveView.setOpen(false);
	}
	
/* ----------------------------- User mashup data functions ----------------------------- */
	
	public void setMashupLocation(double lat,double lng){
		try{
			UserDataMashupTab mashupData = (UserDataMashupTab)this.getFellowIfAny("west").getFellowIfAny("westDiv").getFellowIfAny("userFishEyeBar")
										.getFellowIfAny("DataManagementPanel").getFellowIfAny("DataMashupTabpanel");
			if(mashupData!=null)			
				mashupData.setLocation(lat, lng);
		}catch(Exception e){
			
		}
	}
	
/* ----------------------------- Advance search functions ----------------------------- */
	public void setDataSearchLocation(double lat,double lng){
		try{
			DataSearchTabPannel dataSearch = ((SearchTabbox)this.getFellowIfAny("searchTab")).getDataSearchpnl();
			dataSearch.setLocation(lat, lng);
			AdvanceSearchWindow advSearch = (AdvanceSearchWindow)getFellowIfAny("AdvanceSearchWindow");
			if(advSearch!=null)			
				advSearch.setLocation(lat, lng);
		}catch(Exception e){
			
		}
	}
	
/* ----------------------------- Advance search functions ----------------------------- */
	public void setLiveInforLocation(double lat,double lng){
		try{
			UserLiveDataTab liveInforSearch = (UserLiveDataTab)this.getFellowIfAny("west")
					.getFellowIfAny("westDiv").getFellowIfAny("userFishEyeBar").getFellowIfAny("DataManagementPanel").getFellowIfAny("UserLiveData");
			if(liveInforSearch!=null)			
				liveInforSearch.setLocation(lat, lng);
		}catch(Exception e){
			
		}
	}
}
