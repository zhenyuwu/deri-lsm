package ssc.controller;

import java.sql.SQLException;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ssc.beans.SensorManager;


@Controller
public class UserViewController {
	@RequestMapping(value = "/userview_save", method = RequestMethod.GET)
	public @ResponseBody
	String saveBlocks(@RequestParam("blocks") String object) throws SQLException {
		try{
	//		System.out.println(object);
			JSONObject json = (JSONObject) JSONSerializer.toJSON( object );
		
			SensorManager sensorManager = new SensorManager();
			boolean isSave = sensorManager.addSSCView(json);						
			JSONObject js = new JSONObject();
			js.put("success", isSave);
			return js.toString();
		}catch(Exception e){
			e.printStackTrace();
		}
		System.out.println("error");
		return null;
	}
	
	@RequestMapping(value = "/userview_load", method = RequestMethod.GET)
	public @ResponseBody
	String loadBlocks(@RequestParam("view") String object) throws SQLException {
		try{
	//		System.out.println(object);
			JSONObject json = (JSONObject) JSONSerializer.toJSON( object );		
			SensorManager sensorManager = new SensorManager();
			JSONObject jLoad = sensorManager.loadSSCView(json);
			return jLoad.toString();
		}catch(Exception e){
			e.printStackTrace();
		}
		System.out.println("error");
		return null;
	}
	
	@RequestMapping(value = "/loadAllViewName", method = RequestMethod.GET)
	public @ResponseBody
	String loadViewName(@RequestParam("userId") String object) throws SQLException {
		try{			
			String userId = object.toString();
			SensorManager sensorManager = new SensorManager();
			JSONObject jViews = sensorManager.loadSSCViewNameWithSpecifiedUserId(userId);
			return jViews.toString();
		}catch(Exception e){
			e.printStackTrace();
		}
		System.out.println("error");
		return null;
	}
	
	@RequestMapping(value = "/usersocket_save", method = RequestMethod.GET)
	public @ResponseBody
	String saveSocketURL(@RequestParam("info") String object) throws SQLException {
		try{
	//		System.out.println(object);
			object.trim().replaceAll("(\\r|\\n)", "");
			JSONObject json = (JSONObject) JSONSerializer.toJSON( object );		
			SensorManager sensorManager = new SensorManager();
			boolean isSave = sensorManager.addSSCSocket(json);						
			JSONObject js = new JSONObject();
			js.put("success", isSave);
			return js.toString();
		}catch(Exception e){
			e.printStackTrace();
		}
		System.out.println("error");
		return null;
	}
}


