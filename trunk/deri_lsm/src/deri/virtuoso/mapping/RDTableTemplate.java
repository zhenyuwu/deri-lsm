package deri.virtuoso.mapping;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.*;
import java.util.HashMap;

import deri.sensor.database.ServiceLocator;
import deri.sensor.manager.SensorManager;

public class RDTableTemplate {
	//private LinkedHashMap field_LMap;
	
	private Connection conn;
	private String url = "jdbc:virtuoso://localhost:1111/";
	private String user = "dba";
	private String pass = "dba";
	private String tblName = "";
	private String tblKey ="id";
	private String tblKeyDataType = null;
	private SensorManager sensorManager = ServiceLocator.getSensorManager();
	public String getTblKeyDataType() {
		return tblKeyDataType;
	}

	public void setTblKeyDataType(String tblKeyDataType) {
		this.tblKeyDataType = tblKeyDataType;
	}

	private HashMap tblForeignKey = new HashMap();
	//private String tbl_Qua_prefix;	
	
	public RDTableTemplate(String tblName){
		setTblName(tblName);
		setTblKey(this.getTblKeyFromDatabase());
	}
	
	public void setTblKey(String k){
		tblKey = k;
	}
	
	public String getTblKey(){return tblKey;}
	
	public void setTblName(String n){
		tblName = n;
	}
	
	public String getTblName(){return tblName;}
	
	public String getTblKeyFromDatabase(){
		//return sensorManager.getPrimaryKeyForTable(tblName);
		return "id";		
	}
		
	public HashMap<String,String> getForeignKey(){
		return tblForeignKey;
	}
	
	public void setForeignKey() throws SQLException{
		Statement st = conn.createStatement();
		ResultSet rs = null;
	    DatabaseMetaData meta = conn.getMetaData();
	    rs = meta.getExportedKeys(null, null, tblName);
	    //rs = meta.getImportedKeys(null, null, "sensormeta.dba.sensor");
	    while (rs.next()) {
	    	
	       String fkTableName = rs.getString("FKTABLE_NAME");
	       String fkColumnName = rs.getString("FKCOLUMN_NAME");
	       tblForeignKey.put(fkTableName, fkColumnName);
	       int fkSequence = rs.getInt("KEY_SEQ");
	       System.out.println("getExportedKeys(): fkTableName="+fkTableName);
	       System.out.println("getExportedKeys(): fkColumnName="+fkColumnName);
	       System.out.println("getExportedKeys(): fkSequence="+fkSequence);
	    }
	}
	
	
	private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        return dateFormat.format(date);
    }
	
	public static void main(String[] args) {		
		RDTableTemplate rl = new RDTableTemplate("datasource");
	}
}

