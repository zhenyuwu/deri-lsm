package ssc.beans;

import java.util.ArrayList;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class Block {
	private String id;
	private JSONObject data;
	private ArrayList<Block> childs;
	private Block parent;
	private String type;
	private String url;
	private String sparql;
	private JSONArray filterArray; 
	
	public Block(){
		childs = new ArrayList<>();
		data = new JSONObject();
		filterArray = new JSONArray();
		sparql="";
		url="";
	}
	
	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}

	
	public JSONArray getFilterArray() {
		return filterArray;
	}

	public void setFilterArray(JSONArray filterArray) {
		this.filterArray = filterArray;
	}

	public Block getParent() {
		return parent;
	}

	public void setParent(Block parent) {
		this.parent = parent;
	}

	
	public JSONObject getData() {
		return data;
	}

	public void setData(JSONObject data) {
		this.data = data;
	}

	public ArrayList<Block> getChilds() {
		return childs;
	}


	public void setChilds(ArrayList<Block> childs) {
		this.childs = childs;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getUrl() {
		return url;
	}


	public void setUrl(String url) {
		this.url = url;
	}


	public String getSparql() {
		return sparql;
	}


	public void setSparql(String sparql) {
		this.sparql = sparql;
	}

	public void addChild(Block block){
		childs.add(block);
	}
	
	public void removeChild(Block block){
		childs.remove(block);
	}
}
