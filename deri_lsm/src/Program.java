import java.lang.reflect.*;
import java.util.Date;

public class Program
{
    public String str = "Hello";
    private Date date = new Date();
    protected int i = 0;
    
	public Program(){
    }
    public String sayHello(){
        return "Hello!";
    }
    public void setStr(String str){
        this.str = str;
    }
    private void setDate(Date date){
        this.date = date;
    }
    private void setI(int i){
        this.i = i;
    }

    public static void main(String[] args)
    {
        try
        {
            // Get the Class object associated with this class.
            //Program program = new Program();
            Class progClass = Class.forName("Program");

            // Get the method named sayHello.
            Method helloMethod = progClass.getDeclaredMethod(
                "sayHello", null);
            System.out.println("Method found: " +
                helloMethod.toString());

            // Get the method named setStr.
            Class[] args1 = new Class[1];
            args1[0] = String.class;
            Method strMethod = progClass.getDeclaredMethod(
                "setStr", args1);
            System.out.println("Method found: " + strMethod.toString());
           
            // Get the method named setDate.
            Class[] args2 = new Class[1];
            args2[0] = Date.class;
            Method dateMethod = progClass.getDeclaredMethod(
                "setDate", args2);
            System.out.println("Method found: " + dateMethod.toString());

            // Get the method named setI.
            Class[] args3 = new Class[1];
            args3[0] = Integer.TYPE;
            Method iMethod = progClass.getDeclaredMethod(
                "setI", args3);
            System.out.println("Method found: " + iMethod.toString());
        	//getFieldForCalss();
        	
            
        }
        catch (Exception ex)
        {
            System.out.println(ex.toString());
        }
               
    }

    public static void getFieldForCalss() throws ClassNotFoundException, InstantiationException, IllegalAccessException{
    	Class cls = Class.forName("deri.sensor.beans.Weather");
    	Field fieldlist[] = cls.getDeclaredFields();
	      for (int i 
	        = 0; i < fieldlist.length; i++) {
	         Field fld = fieldlist[i];
	         System.out.println("name = " + fld.getName());
	         System.out.println("decl class = " + fld.getDeclaringClass());
	         System.out.println("type = " + fld.getType());
         	 int mod = fld.getModifiers();
         	 System.out.println("modifiers = " + Modifier.toString(mod));         	 
         	 System.out.println("-----");
         	 Object i1 = fld.getType().newInstance();
         	 System.out.println("i1 la:"+i1.getClass());
           }	      
    }
}