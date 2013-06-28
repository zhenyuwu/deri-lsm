package deri.virtuoso.mapping;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;


public class RDFSchemaTemplate {
	private Model rdfsModel = ModelFactory.createDefaultModel();
	private LinkedHashMap propertiesLinkedMap;
	private String schemaURI;	
	private String classURI;
	private String classLocalName;
	private OntModel baseOnt;


	
	public RDFSchemaTemplate(){
	}
		
	
	public RDFSchemaTemplate(String filePath,String ontoURI){
		propertiesLinkedMap = new LinkedHashMap();
		this.schemaURI = ontoURI;
	}
	
	public RDFSchemaTemplate(String ontoURI){
		propertiesLinkedMap = new LinkedHashMap();
		this.schemaURI = ontoURI;
	}
	
	public void setClassURI(String cn){
		this.classURI = cn;
	}
	
	public String getClassURI() {
		return classURI;
	}


	public void setClassLocalName(){
		OntClass c = baseOnt.getOntClass(classURI);		
		this.classLocalName = c.getLocalName();
	}
	public void setClassLocalName(String st){
		this.classLocalName = st;
	}
	public String getClassLocalName(){
		return classLocalName;
	}
	
	public void setBaseOnt(OntModel m){
		baseOnt = m;
	}
	
	public OntModel getBaseOnt(){
		return baseOnt;
	}
	
	public void setSchemaURI(String schemaURI) {
		this.schemaURI = schemaURI;
	}
	
	public OntModel loadOntologyFromFile(String filePath,String fileFormatType){
		OntModel m = null;
		BufferedReader reader;
		try {			 
			reader = new BufferedReader(new FileReader(filePath));
			m = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);						
			m.read(reader,null,fileFormatType);
		}catch(Exception e){
			System.out.println("File not found");
		}
		return m;
	}
	
	public OntModel loadMutilOntologyFromFile(String dirPath){
		OntModel ontmodel = null;
		try{
			ontmodel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);			
			dirPath = "file:/usr/local/tomcat7/webapps/SensorMiddlewareData/Document/Ontology/";
			OntDocumentManager dm = ontmodel.getDocumentManager();
			dm.addAltEntry("http://lsm.deri.ie/ont/lsm.owl#",dirPath+ "lsm.owl");
			ontmodel.read("http://lsm.deri.ie/ont/lsm.owl#");
			
			dm.addAltEntry("http://purl.oclc.org/NET/ssnx/ssn.owl#",dirPath+ "ssn.owl");
			ontmodel.read("http://purl.oclc.org/NET/ssnx/ssn.owl#");		
			
		    dm.addAltEntry("http://www.loa-cnr.it/ontologies/DUL.owl#",
		    		dirPath + "DUL.owl");
			ontmodel.read("http://www.loa-cnr.it/ontologies/DUL.owl#");
			//ontmodel.write(System.out,"N3");
		}catch(Exception e){
			System.out.println("File not found");
		}
		return ontmodel;
	}
	
	public OntModel loadOntologyFromURL(String ontURL){
		OntModel ontmodel = null;
		try{
			ontmodel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
			ontmodel.read(ontURL);			
		}catch(Exception e){
			System.out.println("File not found");
		}
		return ontmodel;
	}
	

	@SuppressWarnings("unchecked")
	public void setPropertiesLinkedList(){
		try {	 
			propertiesLinkedMap = new LinkedHashMap();			
			//-----create class of rdfs ciao----------------------------
			OntClass c = baseOnt.getOntClass(classURI);			
			this.classLocalName = c.getLocalName();
			ExtendedIterator extIter = c.listDeclaredProperties();
			while(extIter.hasNext()){				
				OntProperty stm = (OntProperty)extIter.next();
				System.out.println(stm.toString());				
				if(stm.getRange()!=null)
					propertiesLinkedMap.put(stm,stm.getRange()
						.getLocalName().toUpperCase());
			}
		}catch(Exception e){
			System.out.println(e.toString());
		}
	}
	public LinkedHashMap getPropertiesLinkedList(){
		return propertiesLinkedMap;
	}
	
	public String getNsURIPrefix(){
		return rdfsModel.getNsURIPrefix(this.schemaURI);
	}
	
	public String getSchemaURI(){
		return schemaURI;
	}
	
	
	@SuppressWarnings("unchecked")
	private void parseToSQLDataType(){
		LinkedHashMap sqlDType = new LinkedHashMap();
		sqlDType.put("DECIMAL", "DECIMAL");
		sqlDType.put("STRING", "VARCHAR");
		sqlDType.put("INTERGER", "INTERGER");
		sqlDType.put("FLOAT", "FLOAT");
		sqlDType.put("DOUBLE", "DECIMAL");
		sqlDType.put("DATETIME", "TIMESTAMP");
		
		
		Set set = propertiesLinkedMap.entrySet();
		Iterator i = set.iterator();
		while(i.hasNext()){
			Map.Entry me = (Map.Entry)i.next();			
			if(sqlDType.containsKey(me.getValue())){
				propertiesLinkedMap.put(me.getKey(),sqlDType.get(me.getValue()));
			}
		}
	}
	
	
	public static void main(String[] args){
		OntModel ontmodel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
		ontmodel.read("http://www.loa-cnr.it/ontologies/DUL.owl#");
		ontmodel.write(System.out,"RDF/XML");
	}	

}
