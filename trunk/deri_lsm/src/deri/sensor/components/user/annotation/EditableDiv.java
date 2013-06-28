package deri.sensor.components.user.annotation;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class EditableDiv extends Div {
    public static final String ON_EDITABLE = "onEditable";
    private String text = "";
    private boolean clickable = true;
    private boolean editable = false;
    Textbox txb;
    Label lbl;
    
    // Empty constructor will do the creation
    public EditableDiv() {
        txb = new Textbox();
        lbl = new Label();
        txb.setWidth("99%");
        lbl.setWidth("99%");
        lbl.setParent(this);// Default show a label with text
        
    }
 
    public EditableDiv(int rd){
    	Radiogroup rdg = new Radiogroup();
    	rdg.setParent(this);
    	
    	Radio rdmeta = new Radio("Meta");
    	rdmeta.setParent(rdg);
    	
    	Radio rddata = new Radio("Data");
    	rddata.setParent(rdg);
    }
    
    public EditableDiv(String text) {
        this();
        setText(text);
        initEditCtrl();
    }
 
    public EditableDiv(String text, boolean clickable) {
        this();
        setText(text);
        setClickable(clickable);
        initEditCtrl();
    }
 
    // Getter and setters
    public void setText(String text) {
        this.text = text;
        txb.setValue(text);
        lbl.setValue(text);
    }
 
    public String getText() {
        return text;
    }
 
    public String getNewText(){
    	return txb.getValue();
    }
    public void setClickable(boolean clickable) {
        this.clickable = clickable;
    }
 
    public boolean isClickable() {
        return this.clickable;
    }
 
    // Initialize the listener of the whole component and the textbox in it
    private void initEditCtrl() {
        this.addEventListener(ON_EDITABLE, new EventListener() {
            @Override
			public void onEvent(Event event) throws Exception {
                toggleEdit((Boolean) event.getData());
            }
        });
 
        // This will turns the edit funtion on when click on label
        if (isClickable()) {
            lbl.addEventListener(Events.ON_CLICK, new EventListener() {
                @Override
				public void onEvent(Event event) throws Exception {
                    txb.setFocus(true);
                    Events.postEvent(new Event(EditableRow.ON_EDIT, EditableDiv.this.getParent(), null));
                }
            });
            txb.addEventListener(Events.ON_FOCUS, new EventListener() {
    			
    			@Override
    			public void onEvent(Event arg0) throws Exception {
    				// TODO Auto-generated method stub
    				//txb.setText("");
    			}
    		});
        }
    }
 
    // Replace textbox/label with label/textbox
    private void toggleEdit(boolean applyChange) {
        if (!editable) {
            lbl.detach();
            EditableDiv.this.appendChild(txb);
        } else {
            txb.detach();
            if (applyChange) {//if apply changes then set the value in
                lbl.setValue(text = txb.getValue());
            } else {
                txb.setValue(text);
                lbl.setValue(text);
            }
            EditableDiv.this.appendChild(lbl);
        }
        editable = !editable;        
    }
}
