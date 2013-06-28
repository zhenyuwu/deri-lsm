package deri.sensor.components.user.annotation;

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
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.RowRendererExt;
import org.zkoss.zul.Space;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class EditRowBtnRenderer implements RowRenderer, RowRendererExt {
 
	private List rowList;

    public List getRowList() {
		return rowList;
	}

	public void setRowList(List rowList) {
		this.rowList = rowList;
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
    public void render(Row row, Object data) throws Exception {
    	try{
	        final Map.Entry me = (Map.Entry) data;
	        final EditableRow editRow = (EditableRow) row;
	        
	        EditableDiv edv = new EditableDiv(me.getKey().toString(), false);
	        edv.setParent(editRow);
	        edv.setTooltiptext(((List)me.getValue()).get(2).toString());
	        
	        int isEditable = (Integer)(((List)me.getValue()).get(1));
	        
	        new EditableDiv(((List)me.getValue()).get(0).toString(), false).setParent(editRow);
	        new EditableDiv(((List)me.getValue()).get(3).toString(), false).setParent(editRow);
	        
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
	                                if ((cmp instanceof EditableDiv)&&j==3) {	                                	
	                                	//System.out.println(((EditableDiv)cmp).getNewText());
	                                	String st = ((EditableDiv)cmp).getNewText();
	                                	((List)me.getValue()).set(3, st);
	                                }	                            
	                            }
	                            //System.out.println(me.getValue());
	                            //System.out.println(editRow.getGrid().getModel());
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
	                        }
	                    });
	                    editBtn.detach();
	                    deleteBtn.detach();
	                    sp.detach();
	                    submitBtn.setParent(ctrlDiv);
	                    sp.setParent(ctrlDiv);
	                    cancelBtn.setParent(ctrlDiv);
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
	    				//System.out.println(editRow.getGrid().getModel());
	    				
	    			}
	    		});
	        }
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
 
}
