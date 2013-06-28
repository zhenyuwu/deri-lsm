package deri.sensor.components.user.annotation;


import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.RowRendererExt;
import org.zkoss.zul.Space;
import org.zkoss.zul.Vbox;
 
/**
 * @author Hoan Nguyen Mau Quoc
 */
public class EditRowWithRdButtonRenderer implements RowRenderer, RowRendererExt {
 
	private List rowList;
	private HashMap<String,List> parentHashMap;
	private List lstXMLValueTag;

    public List getRowList() {
		return rowList;
	}

	public void setRowList(List rowList) {
		this.rowList = rowList;
	}

	
	
	public HashMap<String, List> getParentHashMap() {
		return parentHashMap;
	}

	public void setParentHashMap(HashMap<String, List> parentHashMap) {
		this.parentHashMap = parentHashMap;
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
 
	public List getLstXMLValueTag() {
		return lstXMLValueTag;
	}

	public void setLstXMLValueTag(List lstXMLValueTag) {
		this.lstXMLValueTag = lstXMLValueTag;
	}

	@Override
    public void render(Row row, Object data) throws Exception {
    	try{
	        final Map.Entry me = (Map.Entry) data;
	        final EditableRow editRow = (EditableRow) row;
	        final Listbox lsbValueMap = new Listbox();
	        EditableDiv edv = new EditableDiv(me.getKey().toString(), false);
	        edv.setParent(editRow);
	        edv.setTooltiptext(((List)me.getValue()).get(2).toString());
	        
	        int isEditable = Integer.parseInt(((List)me.getValue()).get(1).toString());
	        
	        new EditableDiv(((List)me.getValue()).get(0).toString(), false).setParent(editRow);
	        final EditableDiv valueDiv = new EditableDiv(((List)me.getValue()).get(3).toString());
	        
	        if(isEditable==0){
//	        	new EditableDiv(((List)me.getValue()).get(3).toString(), false).setParent(editRow);
		        valueDiv.setParent(editRow);
	        }else{
	        	Vbox vbox = new Vbox();
	        	vbox.setParent(editRow);
	        	valueDiv.setParent(vbox);
	        	valueDiv.setVisible(false);
	        	
		        lsbValueMap.setMold("select");		        
		        for(Iterator iter=lstXMLValueTag.iterator();iter.hasNext();)
		        	lsbValueMap.appendChild(new Listitem(iter.next().toString()));
		        ListModelList lm2 = new ListModelList(lstXMLValueTag);
		        lm2.addSelection(lm2.get(0));
		        lsbValueMap.setModel(lm2);
		        
		        //lsbValueMap.setParent(editRow);
		        lsbValueMap.setParent(vbox);
		        lsbValueMap.addEventListener(Events.ON_SELECT, new EventListener() {	
					@Override
					public void onEvent(Event arg0) throws Exception {
						// TODO Auto-generated method stub
						((List)me.getValue()).set(3, lsbValueMap.getSelectedItem().getLabel());
						//System.out.println(me);
					}
				});
	        }
	        
	        final Div ctrlDiv = new Div();
	        ctrlDiv.setParent(editRow);        
	        if(isEditable==0){
	        	ctrlDiv.appendChild(new Label("uneditable"));
	        }else{
	        	final Image editBtn = new Image("/imgs/Button/button_edit.png");              
	            editBtn.setParent(ctrlDiv);
	            editBtn.setStyle("cursor:pointer");
	            
	            final Space sp = new Space();
	            sp.setParent(ctrlDiv);
	            
	            final Image deleteBtn = new Image("/imgs/Button/button_cancel.png");              
	            deleteBtn.setParent(ctrlDiv);
	            deleteBtn.setStyle("cursor:pointer");
	            
	            //Button listener - control the editable of row
	            editBtn.addEventListener(Events.ON_CLICK, new EventListener() {
	                @Override
					public void onEvent(Event event) throws Exception {
	                    final Image submitBtn = new Image("/imgs/Button/button_ok.png");
	                    final Image cancelBtn = new Image("/imgs/Button/button_cancel.png");
	                    submitBtn.setStyle("cursor:pointer");
	                    submitBtn.addEventListener(Events.ON_CLICK, new EventListener() {
	                        @Override
							@SuppressWarnings({ "unchecked", "unchecked", "unchecked" })
							public void onEvent(Event event) throws Exception {
	                            editRow.toggleEditable(true);
	                            submitBtn.detach();
	                            sp.detach();
	                            cancelBtn.detach();
	                            editBtn.setParent(ctrlDiv);
	                            sp.setParent(ctrlDiv);
	                            deleteBtn.setParent(ctrlDiv);
	                            int j=0;
	                            for (Iterator<Component> it = editRow.getChildren().iterator(); it.hasNext();) {	                            
	                                j++;
	                            	Component cmp = it.next();
	                            	if (((cmp instanceof EditableDiv)||(cmp instanceof Vbox))&&(j==3))  {	                                	
	                            		if(cmp instanceof EditableDiv){
	                            			String st = ((EditableDiv)cmp).getNewText();
	                            			((List)me.getValue()).set(3, st);
	                            		}else
	                            			for (Iterator<Component> jt = cmp.getChildren().iterator(); jt.hasNext();) {                    	
		                                        Component subcp = jt.next();
		                                        if(subcp instanceof EditableDiv){
		                                        	String st = ((EditableDiv)subcp).getNewText();
			                            			((List)me.getValue()).set(3, st);
		                                        }
		                            		}
	                                }	                            
	                            }
	                        }
	                    });
	                    cancelBtn.setStyle("cursor:pointer");                
	                    cancelBtn.addEventListener(Events.ON_CLICK, new EventListener() {
	                        @Override
							public void onEvent(Event event) throws Exception {
	                            editRow.toggleEditable(false);
	                            submitBtn.detach();
	                            sp.detach();
	                            cancelBtn.detach();
	                            editBtn.setParent(ctrlDiv);
	                            sp.setParent(ctrlDiv);
	                            deleteBtn.setParent(ctrlDiv);
	                            valueDiv.setVisible(false);
	    	                    lsbValueMap.setVisible(true);
	                        }
	                    });
	                    editBtn.detach();
	                    deleteBtn.detach();
	                    sp.detach();
	                    submitBtn.setParent(ctrlDiv);
	                    sp.setParent(ctrlDiv);
	                    cancelBtn.setParent(ctrlDiv);
	                    valueDiv.setVisible(true);
	                    lsbValueMap.setVisible(false);
	                    editRow.toggleEditable(true);
	                    
	                }
	            });
	            final String t = me.getKey().toString();
	            deleteBtn.addEventListener(Events.ON_CLICK, new EventListener() {
	    			
	    			@Override
	    			public void onEvent(Event arg0) throws Exception {
	    				// TODO Auto-generated method stub
	    				//editRow.setVisible(false);
	    				Events.postEvent(EditableRow.ON_DELETE, 
	    						editRow, me);
	    				rowList.remove(me);
	    				parentHashMap.remove(me.getKey());
//	    				System.out.println("Parne: "+parentHashMap);
	    				//System.out.println(editRow.getGrid().getModel());
	    				
	    			}
	    		});
	        }
	        //new EditableDiv(1).setParent(editRow);
	        Div d = new Div();
	        d.setParent(editRow);
//	        Radiogroup rdg = new Radiogroup();
//	    	rdg.setParent(d);
//	    	
//	    	Radio rdmeta = new Radio("Meta");
//	    	rdmeta.setParent(rdg);
//	    	
//	    	Radio rddata = new Radio("Data");
//	    	rddata.setParent(rdg);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
 
}
