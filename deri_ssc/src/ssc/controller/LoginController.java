package ssc.controller;

import java.io.OutputStream;
import java.sql.SQLException;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ssc.beans.User;
import ssc.beans.UserActiveManager;


@Controller
public class LoginController {

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public @ResponseBody
	String getSignIn(@RequestParam("user") String object) throws SQLException {
		try{
	//		System.out.println(object);
			JSONObject json = (JSONObject) JSONSerializer.toJSON( object );
			String username = json.getString("user");
			String pass = json.getString("pass");			
			
			UserActiveManager userManager = new UserActiveManager();
			User user = userManager.userAuthentication(username, pass);			
			JSONObject jUser = new JSONObject();
			if(user!=null){
				jUser.put("find", true);
				jUser.put("username", username);
				jUser.put("userId", user.getId());
			}
			else jUser.put("find", false);
			return jUser.toString();
		}catch(Exception e){
			e.printStackTrace();
		}
		System.out.println("error");
		return null;
	}
	
	@RequestMapping(value = "/signup", method = RequestMethod.GET)
	public @ResponseBody
	String getSignUp(@RequestParam("user") String object) throws SQLException {
		try{
	//		System.out.println(object);
			JSONObject json = (JSONObject) JSONSerializer.toJSON( object );
			String username = json.getString("user");
			String pass = json.getString("pass");	
			String email = json.getString("email");
			
			UserActiveManager userManager = new UserActiveManager();
			User user = userManager.userAuthentication(username, pass);			
			JSONObject jUser = new JSONObject();
			if(user!=null){
				jUser.put("success", false);				
			}
			else{
				userManager.addNewUser(username,email,pass);
				user = userManager.userAuthentication(username, pass);
				jUser.put("success", true);			
				jUser.put("userId", user.getId());
			}
			return jUser.toString();
		}catch(Exception e){
			e.printStackTrace();
		}
		System.out.println("error");
		return null;
	}
	
}
