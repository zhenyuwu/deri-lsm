package deri.sensor.components.user.annotation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

import deri.sensor.hashmaps.util.MappingWizardUtil;
import deri.sensor.meta.SensorType;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class  WrapperMetaPropertiesWizardWindow extends Window {
	private Grid grdWrapperMeta;
	private Button cmdNext;
	private Button cmdBack;
	private int order = 1;
	private SensorType type;
	private Vbox vbox;
	private HashMap<String, List> lstWrapperAnnotate;
	private ListModelList wrapperModelList;
	private EditRowBtnRenderer rowRenderer;
	private Rows rows;
	private Intbox txtTimeUpdateOccurence;
	private Listbox lsbTimeUpdateUnit;
	private Listbox lstDataformat;
	
	public static final String ON_DELETE = "onDelete";
	
	public  WrapperMetaPropertiesWizardWindow(){
		super();
		init();
	}
	
	public String getWrapperDataFormat(){
		return lstDataformat.getSelectedItem().getLabel();
	}
	
	public int getTimeUpdate(){
		return txtTimeUpdateOccurence.getValue();
	}
	public void setOrder(int order) {
		this.order = order;
	}

	public String getTimeUpdateUnit(){
		return lsbTimeUpdateUnit.getSelectedItem().getLabel();
	}
	
 	private void init(){
		this.setTitle("Step 2 - Set your wrapper meatadata properties");
		this.setId("WrapperMetaPropertiesWizard");
		this.setBorder("normal");
		this.setStyle("overflow:auto");
		this.setHeight("180px");
		this.setWidth("350px");
		this.setSizable(true);
		this.setClosable(true);
		lstWrapperAnnotate = MappingWizardUtil.WrapperMetaAnnotate;
		wrapperModelList = new ListModelList(lstWrapperAnnotate.entrySet());
		
		vbox = new Vbox();
		vbox.setParent(this);
		vbox.setHeight("100%");
		vbox.setWidth("100%");
		vbox.setStyle("overflow:auto");

		initWrapperMetaGrid();

		addContent();
		
		Hbox hboxWButton = new Hbox();
		hboxWButton.setParent(vbox);
		hboxWButton.setSpacing("10px");
		hboxWButton.setStyle("padding-left:60%");
		//hboxWButton.setHeight("10%");
		cmdBack = new Button("Back");
		cmdBack.setParent(hboxWButton);
		cmdBack.addEventListener(Events.ON_CLICK, new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub
				backToPreviousStep();
			}
		});
		
		cmdNext = new Button("Next");
		cmdNext.setParent(hboxWButton);		
		cmdNext.addEventListener(Events.ON_CLICK, new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub
				moveToNextStep();
			}
		});
	}
	
	public int getOrder(){
		return order;
	}
	
	public String timeUpdateUnit(){
		return lsbTimeUpdateUnit.getSelectedItem().getLabel();
	}
	
	public int timeUpdateValue(){
		return txtTimeUpdateOccurence.getValue();
	}
	
	private void initWrapperMetaGrid(){
		grdWrapperMeta = new Grid();
		grdWrapperMeta.setId("grdWrapperMeta");
		grdWrapperMeta.setMold("paging");
		grdWrapperMeta.setPageSize(4);
		grdWrapperMeta.setParent(vbox);
		grdWrapperMeta.setWidth("100%");
		
		Columns clms = new Columns();
		clms.setSizable(true);
		clms.setParent(grdWrapperMeta);
		
		Column column = new Column();
		column.setParent(clms);
		column.setLabel("Properties");
		column.setWidth("30%");

		column = new Column();
		column.setParent(clms);
		column.setLabel("Value");
		column.setWidth("70%");

		rows = new Rows();
		rows.setParent(grdWrapperMeta);
		
		Row row = new Row();
		row.appendChild(new Label("Data format"));
		row.setParent(rows);
		
		lstDataformat = new Listbox();
		lstDataformat.setRows(1);
		lstDataformat.setMold("select");
		lstDataformat.setParent(row);
		lstDataformat.appendChild(new Listitem("XML"));
		lstDataformat.appendChild(new Listitem("JSON"));
		lstDataformat.setSelectedIndex(0);
		
		row = new Row();
		row.appendChild(new Label("Time to update"));
		row.setParent(rows);
		
		Hbox hbox1 = new Hbox();
		hbox1.setParent(row);
		
		txtTimeUpdateOccurence = new Intbox();
		txtTimeUpdateOccurence.setParent(hbox1);		
		
		lsbTimeUpdateUnit= new Listbox();
		lsbTimeUpdateUnit.setParent(hbox1);
		lsbTimeUpdateUnit.setMold("select");
		initialize_lsbTimeUnit();
	}
	
	public void addContent(){		
//		grdWrapperMeta.setRowRenderer(rowRenderer = new EditRowBtnRenderer());
//		rowRenderer.setRowList(wrapperModelList.getInnerList());
//		grdWrapperMeta.setModel(wrapperModelList);
		
	}
	
	private void initialize_lsbTimeUnit() {
		// TODO Auto-generated method stub
		lsbTimeUpdateUnit.appendChild(new Listitem("seconds"));
		lsbTimeUpdateUnit.appendChild(new Listitem("minutes"));
		lsbTimeUpdateUnit.appendChild(new Listitem("hours"));
		lsbTimeUpdateUnit.appendChild(new Listitem("days"));
		lsbTimeUpdateUnit.setSelectedIndex(0);
	}
	
	private void backToPreviousStep(){
		UserAnnotateWizard u = (UserAnnotateWizard) this.getParent();
		u.moveToStep(this.getOrder()-1);
	}
	private void moveToNextStep(){		
		UserAnnotateWizard u = (UserAnnotateWizard) this.getParent();
		u.moveToStep(this.getOrder()+1);
	}

	public void reloadData(){		
		//System.out.println(wrapperModelList);
		wrapperModelList = new ListModelList(lstWrapperAnnotate.entrySet());
		rowRenderer.setRowList(wrapperModelList.getInnerList());
		grdWrapperMeta.setModel(wrapperModelList);
	}
	

	public static void main(String[] agrs){
		HashMap<String, List> lstWrapperAnnotate = MappingWizardUtil.WrapperMetaAnnotate;
		Set set = lstWrapperAnnotate.entrySet();
		ListModelList wrapperModelList = new ListModelList(lstWrapperAnnotate.entrySet());		
		System.out.println(wrapperModelList);
		System.out.println("Remove one");
		Iterator iter = set.iterator();
		while(iter.hasNext()){
			Map.Entry me = ((Map.Entry) iter.next());						
			System.out.println(me);
			List l = wrapperModelList.getInnerList();
			l.remove(me);
			System.out.println(l);
			System.out.println(wrapperModelList);
			break;			
		}		

		//System.out.println(set);
		//System.out.println(wrapperModelList);
//		System.out.println(lstWrapperAnnotate );
	}
}