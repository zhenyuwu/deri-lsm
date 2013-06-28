package deri.sensor.wrapper;

import java.net.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.io.*;

import deri.sensor.ADSBHub.flight.ADSBFlightSignalTelnetParser;
import deri.sensor.database.ServiceLocator;
import deri.sensor.javabeans.Place;
import deri.sensor.javabeans.Sensor;
import deri.sensor.javabeans.User;
import deri.sensor.manager.SensorManager;
import deri.sensor.manager.UserActiveManager;
import deri.sensor.meta.SensorTypeEnum;

/**
 * TelnetWrapper - connect to a given host and service
 * This does not hold a candle to a real TelnetWrapper client, but
 * shows some ideas on how to implement such a thing.
 * @version $Id: TelnetWrapper.java,v 1.11 2004/02/16 02:44:43 ian Exp $
 */
public class TelnetWrapper {
	String host = "10.196.2.55";
	int portNum;
	private Sensor sensor;
	private UserActiveManager userManager = ServiceLocator.getUserActiveManager();
	private SensorManager sensorManager = ServiceLocator.getSensorManager();
	public static void main(String[] argv) {
		new TelnetWrapper().talkTo(argv);
	}
	private void talkTo(String av[]) {
		if (av.length >= 1)
			host = av[0];
		else
			host = "10.196.2.55";
			//host = "192.168.1.230";
		if (av.length >= 2)
			portNum = Integer.parseInt(av[1]);
		else portNum = 30004;
		System.out.println("Host " + host + "; port " + portNum);
		try {
			Socket s = new Socket(host, portNum);
			this.addNewADSBHub();
			// Connect the remote to our stdout
			Pipe pipe = new Pipe(s.getInputStream(), System.out);
			//new Pipe(s.getInputStream(), System.out).start();
			pipe.setSensor(sensor);
			pipe.start();

			// Connect our stdin to the remote
			//new Pipe(System.in, s.getOutputStream()).start();

		} catch(IOException e) {
			System.out.println(e);
			return;
		}
		System.out.println("Connected OK");
	}

	public void addNewADSBHub(){
		List<Sensor> sensors = sensorManager.getAllSensorWithSpecifiedSensorType(SensorTypeEnum.ADSBHub.toString());
		if (sensors.size()>0){
			sensor = sensors.get(0);
			return; 
		}
		User admin = userManager.getUser("admin");
		Place place = new Place();
		place.setTimes(new Date());
		place.setAuthor("hoan");
		place.setLat(0);
		place.setLng(0);
		
		sensor = new Sensor();
		sensor.setName("admin");
		sensor.setUser(admin);
		sensor.setSensorType(SensorTypeEnum.ADSBHub.toString());
		sensor.setSourceType("stream");
		sensor.setTimes(new Date());
		sensor.setPlace(place);
		sensorManager.addObject(sensor);
		
	}
	/* This class handles one half of a full-duplex connection.
	 * Line-at-a-time mode.
	 */
	class Pipe extends Thread {
		BufferedReader is;
		PrintStream os;
		Sensor sensor;
		
		
		public void setSensor(Sensor sensor) {
			this.sensor = sensor;
		}

		/** Construct a Pipe to read from is and write to os */
		Pipe(InputStream is, OutputStream os) {
			this.is = new BufferedReader(new InputStreamReader(is));
			this.os = new PrintStream(os);
		}

		/** Do the reading and writing. */
		@Override
		public void run() {
			String line;
			String flightModel="";
			String[] stSplit;
			ArrayList<String> flightInfor = new ArrayList<String>();			
			try {
				while ((line = is.readLine()) != null) {
					//os.print(line);
					//os.print("\r\n");
					stSplit = line.split(",");
					if(!stSplit[4].equals(flightModel)){
						//System.out.println(flightInfor);
						ADSBFlightSignalTelnetParser.getFlightDetailsFromStream(flightInfor,sensor);
						flightModel = stSplit[4];
						flightInfor = new ArrayList<String>();
					}
					flightInfor.add(line);
					os.flush();
				}
			} catch(IOException e) {
				throw new RuntimeException(e.getMessage());
			}
		}
	}
	
	
}