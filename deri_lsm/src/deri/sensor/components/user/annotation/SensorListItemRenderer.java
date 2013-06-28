package deri.sensor.components.user.annotation;

import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public abstract class SensorListItemRenderer implements ListitemRenderer {

	@Override
	public void render(Listitem item, Object data) throws Exception {
		// TODO Auto-generated method stub
		item.setValue(data);
        doRender(item, data);
	}

	public Listhead getListhead() {
        return null;
    }
 
    protected abstract void doRender(Listitem item, Object data) throws Exception;
}
