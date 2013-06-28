package deri.sensor.components.user;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Auxhead;
import org.zkoss.zul.Auxheader;
import org.zkoss.zul.Button;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

import deri.sensor.database.ServiceLocator;
import deri.sensor.javabeans.User;
import deri.sensor.manager.UserActiveManager;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class UserProfile extends Window {
	private Grid profileGrid = new Grid();
	private Rows rows = new Rows();
	private Button cmdSave;
	private Button cmdCancel;
	private Vbox vbox;	
	private User user;
	private Textbox txtUsername;
	private Textbox txtPass;
	private Textbox txtNickname;
	private Textbox txtNewPass;
	private Textbox txtConfirmPass;

	
	public UserProfile(){
		super();
	}

	public User getUser() {
		return user;
	}


	public void setUser(User user) {
		this.user = user;
	}


	public void init(){
		this.setTitle("User profile");
		this.setId("UserProfile");
		this.setPosition("center,center");
		this.setContentStyle("overflow:auto");
		this.setClosable(true);
		this.setSizable(true);
		this.setStyle("border-top: 1px solid #AAB");
		this.setWidth("40%");
		this.setHeight("30%");
		vbox = new Vbox();
		vbox.setParent(this);
		vbox.setHeight("100%");
		vbox.setWidth("100%");

		initProfileGrid();
		
		Hbox hboxWButton = new Hbox();		
		hboxWButton.setParent(vbox);
		hboxWButton.setSpacing("10px");
		hboxWButton.setStyle("padding-left:40%");
		hboxWButton.setHeight("100%");				
		
		cmdSave = new Button("Apply","/imgs/Button/button_save.png");
		cmdSave.setParent(hboxWButton);		
		cmdSave.addEventListener(Events.ON_CLICK, new EventListener() {			
			@Override
			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub
				save();
			}
		});
		cmdCancel = new Button("Cancel","/imgs/Button/button_cancel.png");
		cmdCancel.setParent(hboxWButton);		
		cmdCancel.addEventListener(Events.ON_CLICK, new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub
				UserProfile.this.detach();
			}
		});
	}

	protected void save() throws InterruptedException {
		// TODO Auto-generated method stub
		UserActiveManager userManager = ServiceLocator.getUserActiveManager();
		if(txtNewPass.getValue().equals(txtConfirmPass.getValue())){
			user.setPass(txtNewPass.getValue());
			userManager.updateUser(user);
			Messagebox.show("Successful!.","Successful",
					Messagebox.OK,Messagebox.INFORMATION);
		}else
			Messagebox.show("Your new password does not match. Please input again.","Error",
						Messagebox.OK,Messagebox.ERROR);
					
	}

	private void initProfileGrid(){
		
		Auxhead aux = new Auxhead();
		aux.setParent(profileGrid);
		Auxheader auxhder = new Auxheader("Update user information");
		auxhder.setColspan(2);
		auxhder.setStyle("color:red");
		auxhder.setParent(aux);
		
		profileGrid.setParent(vbox);
		profileGrid.setId("profileGrid");
		profileGrid.setMold("paging");
		profileGrid.setPageSize(10);
		profileGrid.setParent(vbox);
		profileGrid.setWidth("100%");
		
				
		Columns columns = new Columns();
		columns.setParent(profileGrid);
		columns.setSizable(true);
		
		Column column = new Column();
		column.setParent(columns);
		column.setWidth("30%");
		
		column = new Column();
		column.setWidth("70%");
		column.setParent(columns);
		
		
		rows.setParent(profileGrid);
		
		Row r1 = new Row();
		r1.appendChild(new Label("User name"));		
		txtUsername = new Textbox();
		txtUsername.setParent(r1);
		txtUsername.setValue(user.getUsername());
		txtUsername.setReadonly(true);
		rows.appendChild(r1);
		
		Row r2 = new Row();
		r2.appendChild(new Label("nick name"));		
		txtNickname = new Textbox();
		txtNickname.setParent(r2);
		txtNickname.setValue(user.getNickname());
		txtNickname.setReadonly(true);
		rows.appendChild(r2);
		
		Row r3 = new Row();
		r3.appendChild(new Label("Current password"));
		txtPass = new Textbox();
		txtPass.setParent(r3);
		txtPass.setValue(user.getPass());
		txtPass.setType("password");
		rows.appendChild(r3);
		
		Row r4 = new Row();
		r4.appendChild(new Label("New password"));
		txtNewPass = new Textbox();
		txtNewPass.setParent(r4);
		txtNewPass.setType("password");
		rows.appendChild(r4);
		
		Row r5 = new Row();
		r5.appendChild(new Label("Confirm password"));
		txtConfirmPass = new Textbox();
		txtConfirmPass.setParent(r5);
		txtConfirmPass.setType("password");
		rows.appendChild(r5);
		
	}	
}
