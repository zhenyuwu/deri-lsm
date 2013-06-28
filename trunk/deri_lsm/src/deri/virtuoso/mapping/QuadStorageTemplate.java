package deri.virtuoso.mapping;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import deri.sensor.database.ServiceLocator;
import deri.sensor.hashmaps.util.MappingMap;
import deri.sensor.hashmaps.util.VirtuosoConstantUtil;
import deri.sensor.manager.SensorManager;
import deri.sensor.utils.ConstantsUtil;

public class QuadStorageTemplate {
	private StringBuffer qs_SQL;
	private StringBuffer subQs_SQL;
	private RDTableTemplate tbl;	
	private String quadStURIDefault = "http://lsm.deri.ie/";	
	private String tableAlias;	
	private boolean isAlter=false;

	public QuadStorageTemplate(RDFSchemaTemplate rdfSchema, RDTableTemplate tbl, IRIClassTemplate iri){		
		this.setQuadStURIDefault(tbl.getTblName());
		qs_SQL = new StringBuffer();
		subQs_SQL = new StringBuffer();		
		qs_SQL.append("sparql \n");
		this.tbl = tbl;
		tableAlias = tbl.getTblName().toLowerCase() +"_tbl";
	}
	
	public QuadStorageTemplate(RDFSchemaTemplate rdfSchema, RDTableTemplate tbl, IRIClassTemplate iri,String userID){		
		this.setQuadStURIDefault(tbl.getTblName());
		qs_SQL = new StringBuffer();
		subQs_SQL = new StringBuffer();		
		qs_SQL.append("sparql \n");
		this.tbl = tbl;
		tableAlias = tbl.getTblName().toLowerCase() +"_tbl";
	}
	
	public QuadStorageTemplate(){		
		qs_SQL = new StringBuffer();
		subQs_SQL = new StringBuffer();		
		qs_SQL.append("sparql \n");
	}
	
	public void setQuadStURIDefault(String st){	
		quadStURIDefault+=st+"/quad_storage/";
	}
	
	public String getQuadStURIDefault(){
		return quadStURIDefault;
	}
	
	/*----------------------------------update or create quad storage-------------------------------*/
		//for sensor quad and place quad
	public void createQuadStorageWithSignMap(RDFSchemaTemplate rdfSchema,HashMap hm,RDTableTemplate tbl, IRIClassTemplate iri){
		subQs_SQL.append("create").append(" quad storage ").append("<"+quadStURIDefault).append("default>\n ")
		.append("from ").append(ConstantsUtil.databaseName).append(tbl.getTblName())
		.append(" as ").append(tableAlias +"\n{")
		.append("  create ").append("<"+quadStURIDefault).append(tbl.getTblName()+">\n")
		.append("        as graph ").append("<"+VirtuosoConstantUtil.sensorObjectDataPrefix+tbl.getTblName()+">\n  {\n")
		.append("   <"+rdfSchema.getSchemaURI()).append(iri.getIRIClassName()+">(")
		.append(tableAlias).append("."+tbl.getTblKey()+")").append(" a ")
		.append("<"+rdfSchema.getSchemaURI()).append(rdfSchema.getClassLocalName()+">\n")
		.append("      as ").append("<"+quadStURIDefault)
		.append(":"+"dba-"+rdfSchema.getClassLocalName()+">;\n");

		Set set = hm.entrySet();
		Iterator i = set.iterator();
		while(i.hasNext()){
			Map.Entry me = (Map.Entry)i.next();
			if(me.getKey().toString().equals(tbl.getTblKey())){
				System.out.println("true");
				continue;
			}
			if(i.hasNext()){
				if(me.getKey().toString().equals("placeID")||me.getKey().toString().equals("userID")){
					subQs_SQL.append("     <")
					.append(me.getValue() + ">  ")
					.append("<"+rdfSchema.getSchemaURI()).append(iri.getIRIClassName()+">("+ tableAlias + "." + me.getKey().toString()+")") 
					.append("   as ").append("<"+quadStURIDefault)
					.append(":"+"dba-"+me.getKey()+">;\n");
				}else		
					subQs_SQL.append("     <")
					 .append(me.getValue() + ">  ")
					 .append(tableAlias + "." + me.getKey().toString()) 
					 .append("   as ").append("<"+quadStURIDefault)
					 .append(":"+"dba-"+me.getKey()+">;\n");
			}
			else
				if(me.getKey().toString().equals("placeID")||me.getKey().toString().equals("userID")){
					subQs_SQL.append("     <")
					.append(me.getValue() + ">  ")
					.append("<"+rdfSchema.getSchemaURI()).append(iri.getIRIClassName()+">("+ tableAlias + "." + me.getKey().toString()+")") 
					.append("   as ").append("<"+quadStURIDefault)
					.append(":"+"dba-"+me.getKey()+">.\n");
				}else		
					subQs_SQL.append("     <")
					 .append(me.getValue() + ">  ")
					 .append(tableAlias + "." + me.getKey().toString()) 
					 .append("   as ").append("<"+quadStURIDefault)
					 .append(":"+"dba-"+me.getKey()+">.\n");			
				
		}
		subQs_SQL.append("  }.\n}.");
		
		qs_SQL.append(subQs_SQL);
	}
		
	public void alterQuadStorageWithSignMap(RDFSchemaTemplate rdfSchema,HashMap hm,RDTableTemplate tbl, IRIClassTemplate iri,
											boolean isAlter){
		if(isAlter)
			subQs_SQL.append("alter").append(" quad storage ").append(VirtuosoConstantUtil.sensormasherDataQuadURI+"\n ");		
		else
			subQs_SQL.append("create").append(" quad storage ").append(VirtuosoConstantUtil.sensormasherDataQuadURI+"\n ")	;
		
		subQs_SQL.append("from ").append(ConstantsUtil.databaseName).append(tbl.getTblName())
		.append(" as ").append(tableAlias +"\n{")
		.append("  create ").append("<"+quadStURIDefault).append(tbl.getTblName()+">\n")		
		.append("        as graph ").append("<"+VirtuosoConstantUtil.sensorObjectDataPrefix+tbl.getTblName()+">\n  {\n")		
		.append("   <"+rdfSchema.getSchemaURI()).append(iri.getIRIClassName()+">(")
		.append(tableAlias).append("."+tbl.getTblKey()+")").append(" a ")
		.append("<"+rdfSchema.getSchemaURI()).append(rdfSchema.getClassLocalName()+">\n")
		.append("      as ").append("<"+quadStURIDefault)
		.append(":"+"dba-"+rdfSchema.getClassLocalName()+">;\n");

		Set set = hm.entrySet();
		Iterator i = set.iterator();
		while(i.hasNext()){
			Map.Entry me = (Map.Entry)i.next();
			if(me.getKey().toString().equals(tbl.getTblKey())){
				System.out.println("true");
				continue;
			}
			if(i.hasNext()){
				if(me.getKey().toString().equals("observationID")||me.getKey().toString().equals("sensorID")){
					subQs_SQL.append("     <")
					 .append(me.getValue() + ">  ")
					 .append("<"+rdfSchema.getSchemaURI()).append(iri.getIRIClassName()+">("+ tableAlias + "." + me.getKey().toString()+")") 
					 .append("   as ").append("<"+quadStURIDefault)
					 .append(":"+"dba-"+me.getKey()+">;\n");
				}else
					subQs_SQL.append("     <")
							 .append(me.getValue() + ">  ")
							 .append(tableAlias + "." + me.getKey().toString()) 
							 .append("   as ").append("<"+quadStURIDefault)
							 .append(":"+"dba-"+me.getKey()+">;\n");
			}
			else
				if(me.getKey().toString().equals("observationID")||me.getKey().toString().equals("sensorID")){
					subQs_SQL.append("     <")
					 .append(me.getValue() + ">  ")
					 .append("<"+rdfSchema.getSchemaURI()).append(iri.getIRIClassName()+">("+ tableAlias + "." + me.getKey().toString()+")") 
					 .append("   as ").append("<"+quadStURIDefault)
					 .append(":"+"dba-"+me.getKey()+">.\n");
				}else
					subQs_SQL.append("     <")
							 .append(me.getValue() + ">  ")
							 .append(tableAlias + "." + me.getKey().toString()) 
							 .append("   as ").append("<"+quadStURIDefault)
							 .append(":"+"dba-"+me.getKey()+">.\n");			
		}
		subQs_SQL.append("  }.\n}.");
		
		qs_SQL.append(subQs_SQL);

	}
	
	public String getQs_SQL(){
		return qs_SQL.toString();
	}
	
	
	public void excuteQuadSQL(){
		SensorManager sensorManager = ServiceLocator.getSensorManager();
		sensorManager.excuteSQLUpdate(qs_SQL.toString());
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String classPath = "/root/Sensor Ontology/Sensormasher ont/PlaceSnowFallOnt.owl";		
		String ontURI = "http://purl.oclc.org/NET/ssnx/ssn#";
		String classURI = ontURI+"Snow";
		
		RDFSchemaTemplate rs = new RDFSchemaTemplate(ontURI);
		rs.setClassURI(classURI);
		rs.setBaseOnt(rs.loadOntologyFromFile(classPath,null));	
		
		RDTableTemplate tp = new RDTableTemplate("snowfall");
		
		IRIClassTemplate iri = new IRIClassTemplate(rs, tp);
		//iri.excuteIRISQL();
		System.out.println(iri.getIRI_SQL());
		
		QuadStorageTemplate qd = new QuadStorageTemplate(rs, tp, iri);
		qd.createQuadStorageWithSignMap(rs, MappingMap.rdfViewMapSnowFall, tp, iri);
		//qd.excuteQuadSQL();
		System.out.println(qd.getQs_SQL());		
	}
}
