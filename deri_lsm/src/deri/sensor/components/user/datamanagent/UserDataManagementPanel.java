package deri.sensor.components.user.datamanagent;

import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Window;
import deri.sensor.javabeans.User;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class UserDataManagementPanel extends Window {
		private Tabbox tbbDataManagement;
		private Tab deleteTab;
		private Tab mashupTab;		
		private Tab feedTab;
		private Tab liveTab;
		private Tab updateTab;
		private User user;
		
		
		public User getUser() {
			return user;
		}

		public void setUser(User user) {
			this.user = user;
		}

		public UserDataManagementPanel(){
			super();
		}
		
		
		public void init(){
			 this.setId("DataManagementPanel");
			 this.setPosition("center,center");
			 this.setTitle("User Data management");
			 this.setWidth("50%");
			 this.setHeight("40%");
			 this.setBorder("normal");
			 this.setContentStyle("overflow:auto");		
			 this.setClosable(true);
			 this.setSizable(true);
			 
			 tbbDataManagement = new Tabbox();
			 tbbDataManagement.setWidth("100%");		 
			 tbbDataManagement.setParent(this);
			 initTab();
		 }
		
		public void initTab(){		
			deleteTab = new Tab("Delete");
						
			tbbDataManagement.appendChild(new Tabs());			
			tbbDataManagement.appendChild(new Tabpanels());			
			tbbDataManagement.getTabs().appendChild(deleteTab);			
			initDeleteTab();	
			
			mashupTab = new Tab("Data mashup");		
						
			tbbDataManagement.getTabs().appendChild(mashupTab);			
			initMashupTab();
			
			feedTab = new Tab("Data feed");
			tbbDataManagement.getTabs().appendChild(feedTab);
			initFeedTab();
			
			liveTab = new Tab("Live data");
			tbbDataManagement.getTabs().appendChild(liveTab);
			initLiveTab();
			if(user.getUsername().equals("admin")){
				updateTab = new Tab("Update data");
				tbbDataManagement.getTabs().appendChild(updateTab);
				initUpdateTab();
			}
		}
		
				
		private void initUpdateTab() {
			// TODO Auto-generated method stub
			UserUpdateDataTab updateTabpanel = new UserUpdateDataTab ();
			updateTabpanel.setUser(user);
			updateTabpanel.setHeight("100%");
			updateTabpanel.init();
			tbbDataManagement.getTabpanels().appendChild(updateTabpanel);
		}

		private void initLiveTab() {
			// TODO Auto-generated method stub
			UserLiveDataTab liveTabpanel = new UserLiveDataTab();
			liveTabpanel.setUser(user);
			liveTabpanel.setHeight("100%");
			liveTabpanel.init();
			tbbDataManagement.getTabpanels().appendChild(liveTabpanel);
		}

		private void initDeleteTab() {
			// TODO Auto-generated method stub
			UserDeleteDataTab deleteTabpanel = new UserDeleteDataTab();
			deleteTabpanel.setUser(user);
			deleteTabpanel.setHeight("100%");
			deleteTabpanel.init();
			tbbDataManagement.getTabpanels().appendChild(deleteTabpanel);
		}
		
		private void initMashupTab(){
			UserDataMashupTab mashupTabpanel = new UserDataMashupTab();
			mashupTabpanel.setUser(user);
			mashupTabpanel.setHeight("100%");
			mashupTabpanel.init();
			tbbDataManagement.getTabpanels().appendChild(mashupTabpanel);
		}
		
		private void initFeedTab(){
			UserDataFeedTab feedTabpanel = new UserDataFeedTab();
			//feedTabpanel.setUser(user);
			feedTabpanel.setHeight("100%");
			feedTabpanel.init();
			tbbDataManagement.getTabpanels().appendChild(feedTabpanel);
		}
}
