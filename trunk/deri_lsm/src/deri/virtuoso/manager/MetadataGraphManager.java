package deri.virtuoso.manager;

import java.io.File;
import java.io.FileOutputStream;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import deri.sensor.hashmaps.util.VirtuosoConstantUtil;
import virtuoso.jena.driver.VirtGraph;

public class MetadataGraphManager {
	private final String urlHost ="jdbc:virtuoso://localhost:1111";
	private String username = "dba";
	private String password = "dba";
	private VirtGraph graph;
	private String type;
	
	public void exportGraphToTriple(){
		try{
			VirtGraph virt = new VirtGraph(VirtuosoConstantUtil.sensormasherMetadataGraphURI,urlHost,username,password);
					Model model = ModelFactory.createModelForGraph(virt);
			//ciao.write(System.out,"N3");	
			File fi = new File("/root/sensormashermeta.n3");
			FileOutputStream out = null;
			out = new FileOutputStream(fi);
			model.write(out,"N3");
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("File not found");
		}
	}
	
	public static void main(String[] args){
		MetadataGraphManager metaManager = new MetadataGraphManager();
		metaManager.exportGraphToTriple();
	}
}
