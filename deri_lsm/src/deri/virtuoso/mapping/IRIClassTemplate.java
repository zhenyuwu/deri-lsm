package deri.virtuoso.mapping;

import deri.sensor.database.ServiceLocator;
import deri.sensor.hashmaps.util.VirtuosoConstantUtil;
import deri.sensor.manager.SensorManager;

public class IRIClassTemplate {
	private StringBuffer IRI_SQL;
	private StringBuffer subIRI_SQL;
	private RDTableTemplate tbl;	
	private String IRIClassName;
	private String graphDataURI;
	private String IRIClassURI;
	
	String databaseName = "DERI.DBA";

	
	public IRIClassTemplate(RDTableTemplate tb){
		IRI_SQL = new StringBuffer();
		subIRI_SQL = new StringBuffer();
		IRI_SQL.append("sparql ");
		tbl = tb;		
	}
	
	public IRIClassTemplate(RDFSchemaTemplate sctp, RDTableTemplate tbl){
		IRI_SQL = new StringBuffer();
		subIRI_SQL = new StringBuffer();
		setIRIClassName(sctp.getClassLocalName());
		setGraphDataURI(sctp.getClassLocalName().toLowerCase());
		
		IRI_SQL.append("sparql\n");
		addCreateIRI(sctp.getSchemaURI(), tbl.getTblKey(), tbl.getTblKeyDataType());
	}
	
	public void setIRIClassName(String tableName){
		IRIClassName=tableName+"_iri";
	}
	
	public String getIRIClassName(){
		return IRIClassName;
	}
	
	public void setIRIClassURI(RDFSchemaTemplate sctp){
		IRIClassURI= sctp.getSchemaURI()+IRIClassName;
	}
	
	public String getIRIClassURI(){
		return IRIClassURI;
	}
	
	public void setGraphDataURI(String tableName){
		//if(tableName.equals("sensor")||(tableName.equals("place")))
			graphDataURI=VirtuosoConstantUtil.sensorObjectDataPrefix;
		//else graphDataURI=VirtuosoConstantUtil.instanceDataPrefix;
	}
	
	public String getGraphDataURI(){
		return graphDataURI; 
	}
	
	
	private void addCreateIRI(String sURI,String tblKey,String keyType){
		subIRI_SQL.append("create iri class ").append("<"+sURI)
				.append(IRIClassName+"> ").append("\"" +graphDataURI + "%s\"")
				.append("(in ").append(tblKey + " ")
				.append("VARCHAR")
				.append(" not null).");
	}
	
	public String getIRI_SQL(){
		return IRI_SQL.append(subIRI_SQL).toString();
	}
	
	public void excuteIRISQL(){
		SensorManager sensorManager = ServiceLocator.getSensorManager();
		sensorManager.excuteSQL(IRI_SQL.toString());
	}
	
	
	public static void main(String[] args) {		
		RDFSchemaTemplate rs = new RDFSchemaTemplate();		
		
		RDTableTemplate tp = new RDTableTemplate("snowfall");
		//tp.createTable();
		
		IRIClassTemplate iri = new IRIClassTemplate(rs, tp);
		System.out.println(iri.getIRI_SQL());
	}
}
