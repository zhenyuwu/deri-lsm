package deri.sensor.components.windows;

import org.zkoss.zul.Html;
import org.zkoss.zul.Window;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class ManualWindow extends Window {
	
	public void initContent(){
		this.setId("manualWindow");
		this.setTitle("User manual");
		this.setBorder("normal");
		this.setSizable(true);
		this.setWidth("25%");
		this.setHeight("20%");
		this.setPosition("center,center");
		this.setClosable(true);

		Html html = new Html();
		html.setParent(this);
		html.setStyle("padding-top:35%");
		String content="";
		content+="You can visit our wiki page <a href=\"http://deri-lsm.googlecode.com/\">here</a> ";
		content+="<p>or you can download LSM User manual <a href=\"http://deri-lsm.googlecode.com/files/lsmmanual_v1.1.pdf\">here</a></p> ";
		html.setContent(content);
	}
}
