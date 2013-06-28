package deri.sensor.components.user.notification;

import javax.mail.Authenticator; 
import javax.mail.PasswordAuthentication; 
 
/**
 * @author Hoan Nguyen Mau Quoc
 */
public class MyAuth extends Authenticator{ 
    private String emailId, password;    
     
    public MyAuth(String emailId, String password)  
    { 
        this.emailId = emailId; 
        this.password = password;         
    } 
 
   @Override
public PasswordAuthentication getPasswordAuthentication() 
    {  
	   return new PasswordAuthentication(emailId,password); 
    } 
}   
