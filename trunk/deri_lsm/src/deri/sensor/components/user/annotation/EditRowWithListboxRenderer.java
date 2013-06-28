package deri.sensor.components.user.annotation;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.RowRendererExt;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Textbox;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class EditRowWithListboxRenderer implements RowRenderer, RowRendererExt {
	private Object[] listOntProperties;
	private Object[] listSysProperties;
	private HashMap<String,List> hsMapParentAnnotate;
	private List lstXMLValueTag;
	
	public Object[]getListOntProperties() {
		return listOntProperties;
	}

	public void setListProperties(Collection listProperties) {
		this.listOntProperties = listProperties.toArray();
	}

	
	public Object[] getListSysProperties() {
		return listSysProperties;
	}

	public void setListSysProperties(Collection listSysProperties) {
		this.listSysProperties = listSysProperties.toArray();
	}

	
	public HashMap<String, List> getHsMapParentAnnotate() {
		return hsMapParentAnnotate;
	}

	public void setHsMapParentAnnotate(HashMap<String, List> hsMapParentAnnotate) {
		this.hsMapParentAnnotate = hsMapParentAnnotate;
	}

	
	public List getLstXMLValueTag() {
		return lstXMLValueTag;
	}

	public void setLstXMLValueTag(List lstXMLValueTag) {
		this.lstXMLValueTag = lstXMLValueTag;
	}

	@Override
    public Row newRow(Grid grid) {
        // Create EditableRow instead of Row(default)
        Row row = new EditableRow();
        row.applyProperties();
        return row;
    }
 
    @Override
    public Component newCell(Row row) {
        return null;// Default Cell
    }
 
    @Override
    public int getControls() {
        return RowRendererExt.DETACH_ON_RENDER; // Default Value
    }
 
    @Override
    public void render(final Row row, final Object data) throws Exception {
    	try{        
	        new Label((String)data).setParent(row);
	        
	        final Listbox lsbTable = new Listbox();
	        lsbTable.setParent(row);
	        lsbTable.setMold("select");
			final ListModelList lSysModel = new ListModelList(listSysProperties);
			lSysModel.addSelection(lSysModel.get(0));
			lsbTable.setModel(lSysModel);
			lsbTable.addEventListener(Events.ON_SELECT, new EventListener() {			
				@SuppressWarnings("unchecked")
				@Override
				public void onEvent(Event arg0) throws Exception {
					// TODO Auto-generated method stub
					List temp = hsMapParentAnnotate.get(data);
					temp.set(0, lsbTable.getSelectedItem().getLabel());
					//System.out.println(hsMapParentAnnotate.get(data));
				}
			});
	        
			final Textbox txtUnit = new Textbox();
			txtUnit.setText("not supported");
			txtUnit.setTooltiptext("Press Enter to save");
	        txtUnit.setParent(row);
	        txtUnit.addEventListener(Events.ON_FOCUS, new EventListener() {
				
				@Override
				public void onEvent(Event arg0) throws Exception {
					// TODO Auto-generated method stub
					txtUnit.setText("");
				}
			});
	        txtUnit.addEventListener(Events.ON_OK, new EventListener() {
				
				@Override
				public void onEvent(Event arg0) throws Exception {
					// TODO Auto-generated method stub
					hsMapParentAnnotate.get(data).set(1, txtUnit.getValue());
				}
			});

		  
	        final Hbox hbox = new Hbox();
	        hbox.setParent(row);
	        //hbox.setSpacing("5px");        
//			
//	        final Listbox lsbOntPro = new Listbox();
//	        lsbOntPro.setParent(hbox);
//	        lsbOntPro.setWidth("60%");
//			lsbOntPro.setMold("select");
//			final ListModelList lmodel = new ListModelList(listOntProperties);
//			lsbOntPro.setModel(lmodel);
//			lsbOntPro.setRows(1);
//			lsbOntPro.addEventListener(Events.ON_SELECT, new EventListener() {			
//				@SuppressWarnings("unchecked")
//				@Override
//				public void onEvent(Event arg0) throws Exception {
//					// TODO Auto-generated method stub
//					hsMapParentAnnotate.get((String)data).set(2, lsbOntPro.getSelectedItem().getLabel());
//					System.out.println(hsMapParentAnnotate.get((String)data));
//				}
//			});			
//			
//			final Image cmdAdd =  new Image("/imgs/Button/button_edit.png");
//			cmdAdd.setParent(hbox);
//			cmdAdd.setStyle("cursor:pointer"); 
//			
			final Image cmdRemove =  new Image("/imgs/Button/button_cancel.png");
			cmdRemove.setParent(hbox);
			cmdRemove.setStyle("cursor:pointer");
//			
//			
//			final Textbox txtNewPro = new Textbox("Input new ontology properties here");			
//			txtNewPro.setWidth("150px");	
//			txtNewPro.addEventListener(Events.ON_FOCUS, new EventListener() {
//				
//				@Override
//				public void onEvent(Event arg0) throws Exception {
//					// TODO Auto-generated method stub
//					txtNewPro.setText("");
//				}
//			});
//					
//         
//            //Button listener - control the editable of row
//            cmdAdd.addEventListener(Events.ON_CLICK, new EventListener() {
//                public void onEvent(Event event) throws Exception {                	
//                    final Image submitBtn = new Image("/imgs/Button/button_ok.png");
//                    final Image cancelBtn = new Image("/imgs/Button/button_cancel.png");
//                    submitBtn.setStyle("cursor:pointer");
//                    submitBtn.addEventListener(Events.ON_CLICK, new EventListener() {
//                        @SuppressWarnings({ "unchecked", "unchecked", "unchecked" })
//						public void onEvent(Event event) throws Exception {
//                            submitBtn.detach();
//                            cancelBtn.detach();
//                            txtNewPro.detach();
//                            cmdAdd.setParent(hbox);    
//                            cmdRemove.setParent(hbox);
//                            lmodel.add(txtNewPro.getValue());
//                        }
//                    });
//                    cancelBtn.setStyle("cursor:pointer");                
//                    cancelBtn.addEventListener(Events.ON_CLICK, new EventListener() {
//                        public void onEvent(Event event) throws Exception {
//                            submitBtn.detach();
//                            cancelBtn.detach();
//                            cmdAdd.setParent(hbox);
//                            cmdRemove.setParent(hbox);
//                            txtNewPro.detach();
//                        }
//                    });
//                    cmdAdd.detach();
//                    cmdRemove.detach();
//                    submitBtn.setParent(hbox);
//                    cancelBtn.setParent(hbox);   
//                    txtNewPro.setParent(hbox);                    
//                }
//            });
			
            cmdRemove.addEventListener(Events.ON_CLICK, new EventListener() {
    			
    			@Override
    			public void onEvent(Event arg0) throws Exception {
    				// TODO Auto-generated method stub
    				//editRow.setVisible(false);
    				try{
    					row.getGrid().getRows().removeChild(row); 
    					hsMapParentAnnotate.remove(data);
    				}catch(Exception e){
    					e.printStackTrace();
    				}
    				//rowList.remove(me);
    				//System.out.println(editRow.getGrid().getModel());
    				
    			}
    		});
			
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    
}
