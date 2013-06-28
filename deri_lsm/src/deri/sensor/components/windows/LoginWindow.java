package deri.sensor.components.windows;


import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import deri.sensor.database.ServiceLocator;
import deri.sensor.javabeans.User;
import deri.sensor.manager.UserActiveManager;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class LoginWindow extends Window {
	private static final long serialVersionUID = 8576537856924626725L;
	
	public LoginWindow() {
		super();
	}
	
	public LoginWindow(String title) {
		this();
		this.setTitle(title);
	}
	
	public void init(){
		this.setId("loginWindow");
		this.setBorder("normal");
		this.setWidth("400px");
		this.setPosition("top,center");
		this.setClosable(true);
		
		Grid grid = new Grid();
		grid.setParent(this);
		
		Columns columns = new Columns();
		columns.setParent(grid);
		
		Column column1 = new Column();
		column1.setLabel("Type");
		column1.setParent(columns);
		
		Column column2 = new Column();
		column2.setLabel("Content");
		column2.setParent(columns);
		
		Rows rows = new Rows();
		rows.setParent(grid);
		
		addContent(rows);
	}
	

	public void addContent(Rows rows){
		addUserNameRow(rows);
		addPasswordRow(rows);
		addRegisterLoginRow(rows);
	}
	
	public void addUserNameRow(Rows rows){
		Row row = new Row();
		row.setParent(rows);
		
		Label label = new Label("UserName");
		label.setParent(row);
		
		Textbox input = new Textbox();
		input.setParent(row);
	}
	
	public void addPasswordRow(Rows rows){
		Row row = new Row();
		row.setParent(rows);
		
		Label label = new Label("Password");
		label.setParent(row);
		
		Textbox input = new Textbox();
		input.setParent(row);
		input.setType("password");
		input.addEventListener(Events.ON_OK, new EventListener(){
			@Override
			public void onEvent(Event event) throws Exception {
				Row row = ((Row)event.getTarget().getParent());
				Grid grid = row.getGrid();
				login(grid);
			}
		});
	}
	
	public void addRegisterLoginRow(Rows rows){
		Row row = new Row();
		row.setParent(rows);
		
		Button register = new Button("Register");
		register.setParent(row);
		register.addEventListener(Events.ON_CLICK, new EventListener(){
			@Override
			public void onEvent(Event event) throws Exception {
				RegisterWindow registerWindow = new RegisterWindow("Register");
				registerWindow.setParent(getPage().getFirstRoot());
				onClose();
				registerWindow.doModal();
			}
		});
		
		
		Button login = new Button("Login");
		login.setParent(row);
		login.addEventListener(Events.ON_CLICK, new EventListener(){
			@Override
			public void onEvent(Event event) throws Exception {
				Row row = ((Row)event.getTarget().getParent());
				Grid grid = row.getGrid();
				login(grid);
			}
		});
	}
	
	public void login(Grid grid){
		Textbox usernamebox = (Textbox)grid.getCell(0, 1);
		String username = usernamebox.getValue().trim();
		Textbox passwordbox = (Textbox)grid.getCell(1, 1);
		String password = passwordbox.getValue().trim();
		
		User user = new User();
		user.setUsername(username);
		user.setPass(password);
		UserActiveManager userActiveManager = ServiceLocator.getUserActiveManager();
		if(userActiveManager.isUser(user)){
			user = userActiveManager.getUser(username);
			Session session = getDesktop().getSession();
			session.setAttribute("user", user);
			
			MainWindow mainWindow = (MainWindow)getPage().getFirstRoot().getFellowIfAny("win");
			mainWindow.resetCaptionWithSession();
			mainWindow.showUserPanel(true);
			showMessage("Login Successfully.", "Login Dialogue", Messagebox.OK, Messagebox.INFORMATION);
			onClose();
		}else{
			showMessage("Login unsuccessfully.", "Login Dialogue", Messagebox.OK, Messagebox.ERROR);
		}
	}
	
	@Override
	public void onClose(){
		this.detach();
	}
	
	public void showMessage(String message, String title, int buttons, String icon){
		try {
			Messagebox.show(message, title, buttons, icon);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
