package deri.sensor.wrapper;

import deri.sensor.boston.BostonCycleHireParser;
import deri.sensor.utils.ThreadUtil;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class BostonBikeHireWrapper extends Thread{
	@Override
	public void run() {
		System.out.println("----------------------------------- London Bike dock hires Update has started -----------------------------------");
		long count = 0;
		for(;;){
			String url = "http://www.thehubway.com/data/stations/bikeStations.xml";
				String xml = WebServiceURLRetriever.RetrieveFromURL(url);				
				xml = xml.trim().replaceFirst("^([\\W]+)<","<");
				BostonCycleHireParser.getCycleHireElementFromUrl(xml);
				System.out.println("Boston Bike dock hires Update update is finished");
				ThreadUtil.sleepForHours(3);
			}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BostonBikeHireWrapper londonBike = new BostonBikeHireWrapper();
		londonBike.start();
	}
}
