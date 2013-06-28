package deri.sensor.components.Map;

import java.util.Random;

import org.zkoss.zul.Html;
import org.zkoss.zul.Toolbar;
/**
 * @author Hoan Nguyen Mau Quoc
 */
public class StatusBar extends Toolbar {
	public void onCreate(){
		this.setWidth("100%");
		//this.setHeight("5%");
		Html htm = new Html();
		htm.setWidth("100%");
		htm.setContent("<P ALIGN=right><MARQUEE WIDTH=100% HEIGHT=10 BEHAVIOR=scroll SCROLLAMOUNT=\"3\"><font face=\"arial\">"+
					setStatusbarContent()+"</font></MARQUEE>");
		htm.setParent(this);
	}
	
	public String setStatusbarContent(){
		Random generator = new Random();
		int number = generator.nextInt(4000)+1000;
		
		String htmlContent="";
		htmlContent+="<font size=\"1\" color=\"red\">";
		htmlContent+="<b>Total: "+"<b></font>";
		htmlContent+="<font size=\"1\" color=\"green\">over 110.000 sensors  </font>";
		
		htmlContent+="<font size=\"1\" color=\"red\">";
		htmlContent+="<b>Weather: "+"<b></font>";
		htmlContent+="<font size=\"1\" color=\"green\">82365 sensors  </font>";
		
		htmlContent+="<font size=\"1\" color=\"red\">";
		htmlContent+="<b>Webcam: "+"<b></font>";
		htmlContent+="<font size=\"1\" color=\"green\">24570 sensors  </font>";
		
		htmlContent+="<font size=\"1\" color=\"red\">";
		htmlContent+="<b>Traffic: "+"<b></font>";
		htmlContent+="<font size=\"1\" color=\"green\">469 sensors  </font>";
		
		htmlContent+="<font size=\"1\" color=\"red\">";
		htmlContent+="<b>Roadactivity: "+"<b></font>";
		htmlContent+="<font size=\"1\" color=\"green\">575 sensors  </font>";
		
		htmlContent+="<font size=\"1\" color=\"red\">";
		htmlContent+="<b>Railwaystation: "+"<b></font>";
		htmlContent+="<font size=\"1\" color=\"green\">251 railway </font>";
		
		htmlContent+="<font size=\"1\" color=\"red\">";
		htmlContent+="<b>Airport: "+"<b></font>";
		htmlContent+="<font size=\"1\" color=\"green\">2794 aiports  </font>";
		
		htmlContent+="<font size=\"1\" color=\"red\">";
		htmlContent+="<b>Flight: "+"<b></font>";
		htmlContent+="<font size=\"1\" color=\"green\">more than 3000 flights  </font>";
		
		htmlContent+="<font size=\"1\" color=\"red\">";
		htmlContent+="<b>Snowfall: "+"<b></font>";
		htmlContent+="<font size=\"1\" color=\"green\">7525 sensors  </font>";
		
		htmlContent+="<font size=\"1\" color=\"red\">";
		htmlContent+="<b>Snowdepth: "+"<b></font>";
		htmlContent+="<font size=\"1\" color=\"green\">more than 615 sensors  </font>";
		
		htmlContent+="<font size=\"1\" color=\"red\">";
		htmlContent+="<b>Sea level: "+"<b></font>";
		htmlContent+="<font size=\"1\" color=\"green\">more than 2500 sensors  </font>";
		
		htmlContent+="<font size=\"1\" color=\"red\">";
		htmlContent+="<b>Radar: "+"<b></font>";
		htmlContent+="<font size=\"1\" color=\"green\">1 sensors  </font>";
		
		htmlContent+="<font size=\"1\" color=\"red\">";
		htmlContent+="<b>Satellite: "+"<b></font>";
		htmlContent+="<font size=\"1\" color=\"green\">12 sensors  </font>";
		
		htmlContent+="<font size=\"1\" color=\"red\">";
		htmlContent+="<b>Bike hire: "+"<b></font>";
		htmlContent+="<font size=\"1\" color=\"green\">421 sensors  </font>";
		
		htmlContent+="<font size=\"1\" color=\"red\">";
		htmlContent+="<b>Update rate: "+"<b></font>";
		htmlContent+="<font size=\"1\" color=\"green\">"+number+" triples/s  </font>";
		
		return htmlContent;
	}
}
