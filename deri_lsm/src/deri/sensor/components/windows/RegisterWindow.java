package deri.sensor.components.windows;

import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Window;
import org.zkoss.zul.Textbox;

import deri.sensor.database.ServiceLocator;
import deri.sensor.javabeans.User;
import deri.sensor.manager.UserActiveManager;
import deri.sensor.utils.PasswordConstraint;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class RegisterWindow extends Window {
	private static final long serialVersionUID = 2834623352418543010L;
	
	public RegisterWindow() {
		super();
		init();
	}
	
	public RegisterWindow(String title) {
		this();
		this.setTitle(title);
	}
	
	public void init(){
		this.setId("registerWindow");
		this.setBorder("normal");
		this.setWidth("600px");
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
		addNickNameRow(rows);
		addUserNameRow(rows);
		addPasswordRow(rows);
		addRePasswordRow(rows);
		addResetSubmitRow(rows);
	}
	
	public void addNickNameRow(Rows rows){
		Row row = new Row();
		row.setParent(rows);
		
		Label label = new Label("NickName");
		label.setParent(row);
		
		Textbox input = new Textbox();
		input.setParent(row);
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
		input.addEventListener(Events.ON_CHANGE, new EventListener(){
			@Override
			public void onEvent(Event event) throws Exception {
				Textbox password = (Textbox)event.getTarget();
				Grid grid = ((Row)password.getParent()).getGrid();
				Textbox repassword = (Textbox)grid.getCell(3, 1);
				if(password != null && !password.getValue().trim().equals("")){
					repassword.setConstraint(new PasswordConstraint(password.getValue().trim(),"input should be the same with password input"));
				}
			}
		});
	}
	
	public void addRePasswordRow(Rows rows){
		Row row = new Row();
		row.setParent(rows);
		
		Label label = new Label("RePassword");
		label.setParent(row);
		
		Textbox input = new Textbox();
		input.setParent(row);
		input.setType("password");
		input.addEventListener(Events.ON_CHANGE, new EventListener(){
			@Override
			public void onEvent(Event event) throws Exception {
				Textbox repassword = (Textbox)event.getTarget();
				Grid grid = ((Row)repassword.getParent()).getGrid();
				Textbox password = (Textbox)grid.getCell(2, 1);
				if(repassword != null && !repassword.getValue().trim().equals("")){
					password.setConstraint(new PasswordConstraint(repassword.getValue().trim(),"input should be the same with repassword input"));
				}
			}
		});
	}
	
	public void addResetSubmitRow(final Rows rows){
		Row row = new Row();
		row.setParent(rows);
		
		Button reset = new Button("Reset");
		reset.setParent(row);
		reset.addEventListener(Events.ON_CLICK, new EventListener(){
			@Override
			public void onEvent(Event event) throws Exception {
				Grid grid = rows.getGrid();
				Textbox nickname = (Textbox)grid.getCell(0, 1);
				nickname.setValue("");
				Textbox username = (Textbox)grid.getCell(1, 1);
				username.setValue("");
				Textbox password = (Textbox)grid.getCell(2, 1);
				password.setValue("");
				Textbox repassword = (Textbox)grid.getCell(3, 1);
				repassword.setValue("");
			}
		});
		
		Button submit = new Button("Submit");
		submit.setParent(row);
		submit.addEventListener(Events.ON_CLICK, new EventListener(){
			@Override
			public void onEvent(Event event) throws Exception {
				Grid grid = rows.getGrid();
				Textbox nicknamebox = (Textbox)grid.getCell(0, 1);
				String nickname = nicknamebox.getValue().trim();
				Textbox usernamebox = (Textbox)grid.getCell(1, 1);
				String username = usernamebox.getValue().trim();
				Textbox passwordbox = (Textbox)grid.getCell(2, 1);
				String password = passwordbox.getValue().trim();
				Textbox repasswordbox = (Textbox)grid.getCell(3, 1);
				String repassword = repasswordbox.getValue().trim();
				
				if(username.trim().equals("") || password.trim().equals("") || repassword.trim().equals("")){
					showMessage("username or password or repassword empty","Empty",Messagebox.OK,Messagebox.ERROR);
				}else{
					if(!password.equals(repassword)){
						showMessage("Password and Repassword should be the same","Error",Messagebox.OK,Messagebox.ERROR);
					}else{
						UserActiveManager userActiveManager = ServiceLocator.getUserActiveManager();
						User user_in_db = userActiveManager.getUser(username);
						if(user_in_db != null){
							showMessage("UserName has existed already.\n Please Try another one.","Error",Messagebox.OK,Messagebox.ERROR);
						}else{
							User user = new User(nickname,username,password);
							userActiveManager.addUser(user);
							showMessage("Register Successfully","Success",Messagebox.OK,Messagebox.INFORMATION);
							Session session = getDesktop().getSession();
							session.setAttribute("user", user);
							
							MainWindow mainWindow = (MainWindow)getPage().getFirstRoot().getFellowIfAny("win");
							Caption caption = (Caption)mainWindow.getFellowIfAny("caption");
							mainWindow.clearCaption(caption);
							mainWindow.resetCaptionWithSession();
							onClose();
						}
					}
				}
			}
		});
		
	}
	
	public void showMessage(String message, String title, int buttons, String icon){
		try {
			Messagebox.show(message, title, buttons, icon);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void onClose(){
		this.detach();
	}
	
}
