package deri.sensor.javabeans;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import deri.sensor.components.Map.SensorMarkerContentThread;
/**
 * @author Hoan Nguyen Mau Quoc
 */
public class SensorServer {
	public static void start(Component info,double lat,double lng)
    throws InterruptedException {
		final Desktop desktop = Executions.getCurrent().getDesktop();
		if (!desktop.isServerPushEnabled()) {
			desktop.enableServerPush(true);
			new SensorMarkerContentThread(info,lat,lng).start();
		}
	}

	public static void stop() throws InterruptedException {
		final Desktop desktop = Executions.getCurrent().getDesktop();
		if (desktop.isServerPushEnabled()) {
			desktop.enableServerPush(false);			
		} 
	}
}
