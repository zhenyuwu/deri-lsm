package deri.sensor.components.user.annotation;

import java.util.Iterator;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Row;
import org.zkoss.zul.Vbox;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class EditableRow extends Row {
    // The edit event
    public static final String ON_EDIT = "onEdit";
    //The delete event
    public static final String ON_DELETE = "onDelete";
    // This constant used to remember the last edit row in grid
    private static final String LAST_EDIT_ROW = "LastEditRow";
    private boolean editable = false;
 
    public EditableRow() {
        EditableRow.this.addEventListener(ON_EDIT, new EventListener() {
            @Override
			public void onEvent(Event event) throws Exception {
                // Get last edit row
                EditableRow LastEditRow = (EditableRow) EditableRow.this.getGrid().getAttribute(LAST_EDIT_ROW);
                if (LastEditRow != EditableRow.this) {
                    EditableRow.this.getGrid().setAttribute(LAST_EDIT_ROW, EditableRow.this);
                    EditableRow.this.toggleEditable(true);                    
                    if (LastEditRow != null)
                        LastEditRow.toggleEditable(true);// Turn last edit row's editable off
                }
            }
        });
        
        EditableRow.this.addEventListener(ON_DELETE, new EventListener() {
            @Override
			public void onEvent(Event event) throws Exception {
                EditableRow.this.getGrid().getRows().removeChild(event.getTarget());                
            }                
        });
    }
 
    public boolean isEditable() {
        return editable;
    }
 
    public void setEditable(boolean editable) {
        this.editable = editable;
    }
 
    public void toggleEditable(boolean applyChange) {    	
        setEditable(!editable);
        int j=0;
        for (Iterator<Component> it = this.getChildren().iterator(); it.hasNext();) {
        	++j;
            Component cmp = it.next();
            if (((cmp instanceof EditableDiv)||(cmp instanceof Vbox))&&(j==3)) {   
            	if(cmp instanceof EditableDiv)
            		Events.postEvent(new Event(EditableDiv.ON_EDITABLE, cmp, applyChange));
            	else{
            		for (Iterator<Component> jt = cmp.getChildren().iterator(); jt.hasNext();) {                    	
                        Component subcp = jt.next();
                        if(subcp instanceof EditableDiv)
                        	Events.postEvent(new Event(EditableDiv.ON_EDITABLE, subcp, applyChange));
            		}
            	}
            }
        }
    }
}
