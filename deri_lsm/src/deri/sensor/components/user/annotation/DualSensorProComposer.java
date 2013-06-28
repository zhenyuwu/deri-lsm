package deri.sensor.components.user.annotation;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.ListModelList;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class DualSensorProComposer extends GenericForwardComposer {
	private SensorDataPropertiesWizardWindow sensorProWizard;
	 
    // Handling dualListBox just like origional ZK component
    @Override
	public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        sensorProWizard.setModel(new ListModelList());
        sensorProWizard.setRenderer(new SpecifiedSensorListItemRenderer());
    }
}
