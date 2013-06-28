package deri.virtuoso.mapping;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import deri.sensor.hashmaps.util.MappingMap;
import deri.sensor.meta.SensorType;

public class VirtualRDFView {
	private RDFSchemaTemplate rs;
	private RDTableTemplate tp;
	private IRIClassTemplate iri;
	private QuadStorageTemplate qd;
	
	private String ontURI = "http://purl.oclc.org/NET/ssnx/ssn#";
	private String className;
	private String dirPath =  "";
	private SensorType type;
	private String tblName;
	private String userID;
	private HashMap hsPropertiesMapping;
	private boolean isAlter=false;
	/**	 
	 *  this hashMap will storage all quad storages were created by user.
	 *  the template is <table name,quad storage>
	 */
	
	
	public VirtualRDFView(){		
	}
	public String getClassName() {
		return className;
	}


	public void setClassName(String className) {
		this.className = className;
	}


	public SensorType getType() {
		return type;
	}


	public void setType(SensorType type) {
		this.type = type;
	}

	
	public boolean isAlter() {
		return isAlter;
	}
	public void setAlter(boolean isAlter) {
		this.isAlter = isAlter;
	}
	public String getTblName() {
		return tblName;
	}


	public void setTblName(String tblName) {
		this.tblName = tblName;
	}


	public String getUserID() {
		return userID;
	}


	public void setUserID(String userID) {
		this.userID = userID;
	}
	
	public String getOntURI() {
		return ontURI;
	}
	public void setOntURI(String ontURI) {
		this.ontURI = ontURI;
	}
	public HashMap getHsPropertiesMapping() {
		return hsPropertiesMapping;
	}


	public void setHsPropertiesMapping(HashMap hsPropertiesMapping) {
		this.hsPropertiesMapping = hsPropertiesMapping;
	}
				
	public void removeQuadStorage(){
		
	}
	
	public QuadStorageTemplate getQuadStorage(){
		return qd;
	}
	
	public void createRDFView(){
		  String classURI = ontURI+className;		  
		  
		  rs = new RDFSchemaTemplate(ontURI);
		  rs.setClassURI(classURI);		  
		  rs.setBaseOnt(rs.loadMutilOntologyFromFile(dirPath));
		  rs.setClassLocalName(className);
		  
		  tp = new RDTableTemplate(tblName.toLowerCase());
		  
		  iri = new IRIClassTemplate(rs, tp);
		  System.out.println(iri.getIRI_SQL());
		  iri.excuteIRISQL();		  		  
		  
		  qd = new QuadStorageTemplate(rs, tp, iri,userID);
		  if(tblName.equals("sensor")||tblName.equals("place"))
			  qd.createQuadStorageWithSignMap(rs, hsPropertiesMapping, tp, iri);
		  else
			  qd.alterQuadStorageWithSignMap(rs, hsPropertiesMapping, tp, iri,isAlter);		  
		  System.out.println(qd.getQs_SQL());
		  qd.excuteQuadSQL();
	}
	
	public static void createElementsPropertyRDFView(){
		VirtualRDFView virRDF = new VirtualRDFView();
		Set set = MappingMap.class2Table.entrySet();
		Iterator iter = set.iterator();
		while(iter.hasNext()){
			Map.Entry me = (Map.Entry) iter.next();			
			virRDF.setUserID("");
			virRDF.setTblName(me.getValue().toString());
			virRDF.setClassName(me.getKey().toString());
			virRDF.setHsPropertiesMapping(MappingMap.class2MappingMap.get(me.getKey()));
			virRDF.createRDFView();
			virRDF.setAlter(true);
		}
	}
	
	public static void createSensorRDFView(){
		VirtualRDFView virRDF = new VirtualRDFView();
		virRDF.setTblName("sensor");
		virRDF.setClassName("Sensor");
		virRDF.setOntURI("http://purl.oclc.org/NET/ssnx/ssn#");
		virRDF.setHsPropertiesMapping(MappingMap.rdfViewMapSensor);
		virRDF.createRDFView();
	}
	
	public static void createPlaceRDFView(){
		VirtualRDFView virRDF = new VirtualRDFView();
		virRDF.setTblName("place");
		virRDF.setClassName("Place");
		virRDF.setOntURI("http://www.loa-cnr.it/ontologies/DUL.owl#");
		virRDF.setHsPropertiesMapping(MappingMap.rdfViewMapPlace);
		virRDF.createRDFView();
	}
	
	public static void createObservationRDFView(){
		VirtualRDFView virRDF = new VirtualRDFView();
		virRDF.setTblName("observation");
		virRDF.setClassName("Observation");
		virRDF.setOntURI("http://purl.oclc.org/NET/ssnx/ssn#");
		virRDF.setHsPropertiesMapping(MappingMap.rdfViewMapObservation);
		virRDF.createRDFView();
	}
	
	public static void createTemp(String st){
		VirtualRDFView virRDF = new VirtualRDFView();
		virRDF.setAlter(true);
		String classname = st;
		virRDF.setTblName(MappingMap.class2Table.get(classname));
		virRDF.setClassName(classname);
		virRDF.setHsPropertiesMapping(MappingMap.class2MappingMap.get(classname));
		virRDF.createRDFView();
	}
	
	public static void main(String[] args) {
		//createObservationRDFView();
//		createSensorRDFView();
//		createPlaceRDFView();
//		createElementsPropertyRDFView();
		
//		createTemp("Temperature");
		createTemp("TimeInterval");
		createTemp("Description");		
		createTemp("Direction");
		createTemp("District");
		createTemp("Duration");
		createTemp("Elevation");
		createTemp("InformationEntity");		
		createTemp("WebcamSnapShot");
		createTemp("Station");
		createTemp("Status");
		createTemp("UserContact");
		createTemp("WindSpeed");
		createTemp("WindChill");
		createTemp("Observation");		
		createTemp("InformationObject");
		createTemp("Comment");
		createTemp("DoubleValue");
		createTemp("StringValue");
		createTemp("IntegerValue");
		createTemp("BikeAvailability");
		createTemp("BikeDockAvailability");
		createTemp("StationPlatform");
		createTemp("TrainNumber");
		createTemp("TimeToStation");
		createTemp("SecondToStation");
		createTemp("Destination");
	}
}

