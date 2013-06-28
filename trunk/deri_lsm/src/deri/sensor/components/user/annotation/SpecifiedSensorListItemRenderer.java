package deri.sensor.components.user.annotation;

import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class SpecifiedSensorListItemRenderer extends SensorListItemRenderer{

	@Override
	protected void doRender(Listitem item, Object data) throws Exception {
		// TODO Auto-generated method stub
//		Map.Entry  me = (Map.Entry) data;
//		Listcell lc = new Listcell(me.getKey().toString());
//		lc.setParent(item);
//		lc.setTooltiptext(((List)me.getValue()).get(1).toString());     		
//        new Listcell(((List)me.getValue()).get(0).toString()).setParent(item);
		Listcell lc = new Listcell(data.toString());
		
		lc.setParent(item);
	}

	@Override
	public Listhead getListhead() {
		Listhead lh = new Listhead();
		new Listheader("Property", null, "100%").setParent(lh);
        //new Listheader("Ontology annotation", null, "80px").setParent(lh);
        return lh;
    }
}
