# Introduction #

The following examples illustrate how to use lsm java API for adding, deleting and updating GSN sensor data into LSM triple store

# Details #

Required Java version: Java SE 1.7

Required Java libraries:

- [lsmlibs](http://deri-lsm.googlecode.com/files/lsmlibs.jar)

- [dom4j](http://sourceforge.net/projects/dom4j/)

- [jena](http://ftp.heanet.ie/mirrors/www.apache.org/dist/jena/)

## Example 1. How to add new sensor into LSM triple store ##
```

import java.util.Date;

import lsm.beans.Place;
import lsm.beans.Sensor;
import lsm.beans.User;
import lsm.server.LSMTripleStore;


public class TestLSM {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub		
        
        try{  
         /*
          * add new sensor to lsm store. For example: Air quality sensor from Lausanne
          * Sensor name: lausanne_1057         
          */
        	
         // 1. Create an instanse of Sensor class and set the sensor metadata
         Sensor sensor  = new Sensor();
         sensor.setName("lausanne_1057");
         sensor.setAuthor("sofiane");
	 sensor.setSourceType("lausanne");
	 sensor.setInfor("Air Quality Sensors from Lausanne");
         sensor.setSource("http://opensensedata.epfl.ch:22002/gsn?REQUEST=113&name=lausanne_1057");
         sensor.setMetaGraph("http://lsm.deri.ie/yourMetaGraphURL");
         sensor.setDataGraph("http://lsm.deri.ie/yourDataGraphURL");
         sensor.setSensorType("gsn");
         sensor.setTimes(new Date());
        
	// set sensor location information (latitude, longitude, city, country, continent...)
         Place place = new Place();
         place.setLat(46.529838);
         place.setLng(6.596818);
         sensor.setPlace(place);
         
         /*
          * Set sensor's author
          * If you don't have LSM account, please visit LSM Home page (http://lsm.deri.ie) to sign up
          */
         User user = new User();
         user.setUsername("sofiane");
         user.setPass("sofiane");
         sensor.setUser(user);
         
         // create LSMTripleStore instance
         LSMTripleStore lsmStore = new LSMTripleStore();
         
         //set user information for authentication
         lsmStore.setUser(user);
         
         //call sensorAdd method
         lsmStore.sensorAdd(sensor);
         
        }catch (Exception ex) {  
        	ex.printStackTrace();
            System.out.println("cannot send the data to server");                                          
        }  
    }
}
```

## Example 2. How to update new sensor data into LSM triple store ##
```
	/*
	 * An Observation is a Situation in which a Sensing method has been used to estimate or 
	 * calculate a value of a Property of a FeatureOfInterest.
	 */
	
	//create an Observation object
	Observation obs = new Observation();
	obs.setMetaGraph("http://lsm.deri.ie/yourMetaGraphURL");
        obs.setDataGraph("http://lsm.deri.ie/yourDataGraphURL");
	// set SensorURL of observation
	//for example: "http://lsm.deri.ie/resource/8a82919d3264f4ac013264f4e14501c0" is the sensorURL of lausanne_1057 sensor
	obs.setSensor("http://lsm.deri.ie/resource/8a82919d3264f4ac013264f4e14501c0");
	//set time when the observation was observed. In this example, the time is current local time.
	obs.setTimes(new Date());
	/*
	 * Relation linking an Observation to the Property that was observed
	 */
	ObservedProperty obvTem = new ObservedProperty();
	obvTem.setObservationId(obs.getId());
	obvTem.setPropertyName("Temperature");
	obvTem.setValue(9.58485958485958);
	obvTem.setUnit("C");
	obs.addReading(obvTem);
	
	ObservedProperty obvCO = new ObservedProperty();
	obvCO.setObservationId(obs.getId());
	obvCO.setPropertyName("CO");
	obvCO.setValue(0.0366300366300366);
	obvCO.setUnit("C");
	obs.addReading(obvCO);
	
	lsmStore.sensorDataUpdate(obs);
```

If the sensor metadata or sensor data are already in N-Triple format, you can use these methods **sensorAdd(String triples)** and **sensorDataUpdate(String triples)** to insert directly into LSM triple store

## Example 3. Retrieve sensor object by sensorURL id or by sensor source ##
```
       /*
        * the sensorURL id and sensor source are unique
        */
       Sensor sensor1 = lsmStore.getSensorById("http://lsm.deri.ie/resource/8a82919d3264f4ac013264f4e14501c0");
       Sensor sensor2 = lsmStore.getSensorBySource("http://opensensedata.epfl.ch:22002/gsn?REQUEST=113&name=lausanne_1057");
```

## Example 4. Delete sensor data ##
```
        /**
        * remove sensor 
        * @param sensorURL
        */
    lsmStore.sensorDelete("http://lsm.deri.ie/resource/8a82919d3264f4ac013264f4e14501c0");

        /**
        * delete all reading data of specific sensor 
        * @param sensorURL
        */         
    lsmStore.deleteAllReadings("http://lsm.deri.ie/resource/8a82919d3264f4ac013264f4e14501c0");

         /**
          * delete sensor data at a certain period of time
      	 * @param sensorURL
      	 * @param dateOperator
      	 * @param fromTime
      	 * @param toTime
         * fromDate, toDate are java Date objects
      	 */
         Date fromDate = new Date();
         lsmStore.deleteAllReadings("http://lsm.deri.ie/resource/8a82919d3264f4ac013264f4e14501c0", 
        		 "<=", fromDate, null);
      
         lsmStore.deleteAllReadings("http://lsm.deri.ie/resource/8a82919d3264f4ac013264f4e14501c0", 
        		 null, fromDate, toDate);

```