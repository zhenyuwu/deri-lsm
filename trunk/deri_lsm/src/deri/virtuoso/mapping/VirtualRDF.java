package deri.virtuoso.mapping;

import java.util.HashMap;

import deri.sensor.hashmaps.util.MappingMap;
import deri.sensor.meta.SensorType;



/**
 * @author Hoan Nguyen Mau Quoc
 *
 */
public class VirtualRDF {	
	
	private RDFSchemaTemplate rs;
	private RDTableTemplate tp;
	private IRIClassTemplate iri;
	private QuadStorageTemplate qd;
	private String VirtualRDFName;
	
	private String ontURI;
	private String className;
	private String dirPath =  "/root/Sensor Ontology/Sensormasher ont/";
	private SensorType type;
	private String tblName;
	private String userID;
	private HashMap hsPropertiesMapping;
	/**	 
	 *  this hashMap will storage all quad storages were created by user.
	 *  the template is <table name,quad storage>
	 */
	
	
	public VirtualRDF(){		
	}
	
	public VirtualRDF(String filePath,String classURI,String ontURI){
		rs = new RDFSchemaTemplate(ontURI);
		rs.setClassURI(classURI);
	}
	
		
	public void setRDFSchemaTemplate(String ontURI,String classURI){		
		rs = new RDFSchemaTemplate(ontURI);
		rs.setClassURI(classURI);
		
		//rs.setBaseOnt(rs.LoadMutilOntologyFromFile());
//		rs.setBaseOnt(rs.LoadOntologyFromFile(filePath, null));
//		rs.getBaseOnt().write(System.out,"RDF/XML");
//		rs.setPropertiesLinkedList();
//		System.out.println(rs.getPropertiesLinkedList().toString());
//		rs.exportSchemaToVirtGraph(rs.getBaseOnt());
	}
	
	public RDFSchemaTemplate getRDFSchemaTemplate(){
		return this.rs;
	}
	
	public void setRDTable(String tblName){
		tp = new RDTableTemplate(tblName);		
	}
	
	public RDTableTemplate getRDTable(){
		return tp;
	}
	
	public void setIRIClass(){
		iri = new IRIClassTemplate(rs, tp);
		System.out.println(iri.getIRI_SQL());
		//iri.excuteIRISQL();
//		rdfViewGraphURI.put(tp.getTblName(), iri.getGraphDataURI());
	}
	
	public IRIClassTemplate getIRIClass(){
		return iri;
	}
	
	public void setQuadStorage(){
		qd = new QuadStorageTemplate(rs, tp, iri);
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
		  rs.setClassLocalName();		  
		  
		  tp = new RDTableTemplate(tblName);		  
		  iri = new IRIClassTemplate(rs, tp);
		  System.out.println(iri.getIRI_SQL());
		  iri.excuteIRISQL();		  		  
		  
		  qd = new QuadStorageTemplate(rs, tp, iri);
		  qd.createQuadStorageWithSignMap(rs, MappingMap.rdfViewMapSensor, tp, iri);
		  System.out.println(qd.getQs_SQL());
		  qd.excuteQuadSQL();
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
	}

}
