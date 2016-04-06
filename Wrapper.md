# Introduction #

Add your content here.


# Details #

# LSM Wrapper developing tutorial #
## Wrapper's Life Cycle and LSM Container ##
> An instance of a wrapper is created whenever a Wrapper Container (WC) is generated and load configurations from the Wrappers Configuration Repository. The Wrappers Configuration Repository includes all the wrapper configuration file in XML format. One configuration file presents for one wrapper. In case LSM wants to activate a new wrapper, its configuration file will be added to Wrappers Configuration Repository.

> A Wrapper Connection Properties is an object which contains its initialization parameters as defined in the configuration file. The process is presented as the following steps (as illustrated in the figure).

1.Look for a wrapper configuration files in the repository. If found, one Wrapper Connection Properties instance will be created and load all the parameters which are defined in configuration file.


2.If there is a Wrapper Connection Properties is created, according to the Wrapper class name, the Container instantiates the appropriate wrapper object and calls its initialize method. If the initialize method returns true, otherwise return false. Back to Step 1.



3.To collect newest data, each wrapper is also a thread. When all the wrapper instances are created, these threads will be start to collect real data from sensor and convert to triples data.
> for(AbstractWrapper w:getListOfWrappers()){
> > new Thread(w).start();
> > }

## How to write a new wrapper ##
All standard wrappers are subclasses of lsm.wrapper.AbstractWrapper. Subclasses must implement the following four methods:
  1. boolean initialize()
  1. void run()
Each wrapper is a thread in the LSM. If you want to do some kind of processing in a fixed time interval, you can override the run() method. The run method is useful for time driven wrappers in which the production of a sensor data is triggered by a timer.

**initialize()**

This method is called after the wrapper object creation. For more information on the life cycle of a wrapper, see Wrapper's Life Cycle and LSM Container. The complete method prototype is

public boolean initialize();

In this method, the wrapper should try to initialize its connection to the actual data producing/receiving device(s) (e.g., weather sensor or traffic camera). The wrapper should return true if it can successfully initialize the connection, false otherwise.
LSM provides access to the wrapper parameters through the following methods getPro() and setPro(Properties pro).
The pro variable saves all the configuration parameters which are loaded from wrapper XML configuration file. The XML configuration file has the basic following fragment:


&lt;properties&gt;



> 

&lt;comment&gt;

London traffic camera configuration

&lt;/comment&gt;


> 

&lt;entry key="wrapper.classname"&gt;

lsm.wrapper.LondonTrafficCamWrapper

&lt;/entry&gt;


> 

&lt;entry key="wrapper.sleeptimevalue"&gt;

5

&lt;/entry&gt;


> 

&lt;entry key="wrapper.sleeptimeunit"&gt;

minute

&lt;/entry&gt;


> 

&lt;entry key="wrapper.type"&gt;

http

&lt;/entry&gt;




&lt;/properties&gt;



To access initialization parameter, for example the parameter named classname, you can use the following code:
> prop.getProperty("wrapper.classname")

Because the wrapper is also a thread, so that one wrapper is initialized, the thread sleep time unit and the time duration are loaded. For example, in yahoo weather wrapper:

@Override
> public boolean initialize() {
> > // TODO : init url here
> > url="http://weather.yahooapis.com/forecastrss?w=2344917";
> > sleepUnit = prop.getProperty("wrapper.sleeptimeunit");
> > sleepDuration = Integer.parseInt(prop.getProperty("wrapper.sleeptimevalue"));
> > return true;

> }
postRDF(String nt)
The postRDF() method will feed the triples data to message bus (ex: rabbitmq server, virtuoso graph...). The nt parameter is the triples in N3 format.
public void postRDF(String nt){
> > //TODO : feed data to rabbitmq server
> > try {
> > > Pusher push = new Pusher();
> > > // open one rabbit connection
> > > push.openRabbitConnection();
> > > // push triples to message bus
> > > push.push(nt);
> > > push.closeRabbitConnection();

> > } catch (Exception e) {
> > > // TODO Auto-generated catch block
> > > e.printStackTrace();

> > }
}
run()
As described before, the wrapper acts as a bridge between the actual hardware device(s) or other kinds of stream data sources and LSM, thus in order for the wrapper to produce data, it should keep track of the newly produced data items. This method is responsible for forwarding the newly received data to the LSM engine.
The method should be implemented as below :

@Override
public void run() {

> // TODO Auto-generated method stub
> System.out.println("-----Yahoo weather Update has started ------");
> for(;;){
> > // This method in the AbstractWrapper sends the triples data to the registered message bus
> > postRDF(feed(url));
> > //the thread should sleep for some finite time before polling the data source or producing the next 			data
> > if(sleepUnit.equals("day"))
> > > ThreadUtil.sleepForDays(sleepDuration);

> > else if(sleepUnit.equals("minute"))
> > > ThreadUtil.sleepForMinutes(sleepDuration);

> > else if(sleepUnit.equals("second"))
> > > ThreadUtil.sleepForSeconds(sleepDuration);

> > else if(sleepUnit.equals("hour"))
> > > ThreadUtil.sleepForHours(sleepDuration);

> }
}

Weather Underground wrapper example:
@Override
> public void run() {
> > // TODO Auto-generated method stub
> > > System.out.println("--- Weather Underground Update has started --");
> > > for(;;){
> > > > postRDF(feed(url));
> > > > if(sleepUnit.equals("day"))
> > > > > ThreadUtil.sleepForDays(sleepDuration);

> > > > else if(sleepUnit.equals("minute"))
> > > > > ThreadUtil.sleepForMinutes(sleepDuration);

> > > > else if(sleepUnit.equals("second"))
> > > > > ThreadUtil.sleepForSeconds(sleepDuration);

> > > > else if(sleepUnit.equals("hour"))
> > > > ThreadUtil.sleepForHours(sleepDuration);

> > > }

> }
A fully functional wrapper
package lsm.wrapper;
/
  * This wrapper collects data comes from one WeatherUnderground sensor which has URL
  * is http://api.wunderground.com/weatherstation/WXCurrentObXML.asp?ID=IGAUTENG8
  * In this example we receive data in XML format from sensor and  publish to triples data by using
  * XSLT protocol. The ontology we are using in LSM engine is LSM ontology which is based on GSN ontology.
  * 
  * 
public class WUnderGroundWrapper extends AbstractWrapper {
> String url, sleepUnit;
> int sleepDuration;
> private SensorManager sensorManager = new SensorManager();

> @Override
> public boolean initialize() {
> > // TODO : init url here
> > > url="http://api.wunderground.com/weatherstation/WXCurrentObXML.asp?ID=IGAUTENG8";
> > > sleepUnit = prop.getProperty("wrapper.sleeptimeunit");
> > > sleepDuration = Integer.parseInt(prop.getProperty("wrapper.sleeptimevalue"));
> > > return true;

> }
> @Override
> public void run() {
> > // TODO Auto-generated method stub
> > System.out.println("------Weather Underground Update has started ------------");
> > > for(;;){
> > > > // post the data to message bus
> > > > postRDF(feed(url));
> > > > if(sleepUnit.equals("day"))
> > > > > ThreadUtil.sleepForDays(sleepDuration);

> > > > else if(sleepUnit.equals("minute"))
> > > > > ThreadUtil.sleepForMinutes(sleepDuration);

> > > > else if(sleepUnit.equals("second"))
> > > > > ThreadUtil.sleepForSeconds(sleepDuration);

> > > > else if(sleepUnit.equals("hour"))
> > > > ThreadUtil.sleepForHours(sleepDuration);

> > > }

> }

> private String feed(String url){
> > //TODO : load data from URL and convert to Ntriple string here
> > String nt="";
> > try{
> > > String xml = WebServiceURLRetriever.RetrieveFromURL(url);
> > > Sensor sensor = sensorManager.getSpecifiedSensorWithSource(url);
> > > if(sensor==null) return null;
> > > Observation newest = 	sensorManager.getNewestObservationForOneSensor(sensor.getId());
> > > if(newest == null || DateUtil.isBefore(newest.getTimes(), WUnderGroundXMLParser.readerTime)){		           				System.setProperty("javax.xml.transform.TransformerFactory",
> > > > "net.sf.saxon.TransformerFactoryImpl");
> > > > TransformerFactory tFactory = TransformerFactory.newInstance();
> > > > String xsltPath =  												          								XSLTMapFile.sensordata2xslt.get(SourceType.getSourceType(sensor.getSourceType()));
> > > > try {
> > > > > Transformer transformer =    tFactory.newTransformer(new StreamSource(new File(xsltPath)));
> > > > > String id = sensor.getId().substring(sensor.getId().lastIndexOf("/")+1);
> > > > > String foi = "http://lsm.deri.ie/resource/" +
> > > > > > Double.toString(sensor.getPlace().getLat()).replace(".","").replace("-", "") +
> > > > > > > Double.toString(sensor.getPlace().getLng()).replace(".","").replace("-", "");

> > > > > transformer.setParameter("sensorId", id);
> > > > > transformer.setParameter("sourceType", sensor.getSourceType());
> > > > > transformer.setParameter("sensorType", sensor.getSensorType());
> > > > > transformer.setParameter("sourceURL", sensor.getSource());
> > > > > transformer.setParameter("foi",foi );
> > > > > InputStream inputStream = new ByteArrayInputStream(xml.getBytes("UTF-8"));
> > > > > Writer outWriter = new StringWriter();
> > > > > StreamResult result = new StreamResult( outWriter );
> > > > > transformer.transform(new StreamSource(inputStream),result);
> > > > > nt = outWriter.toString().trim();

> > > > } catch (Exception e) {
> > > > > e.printStackTrace();

> > > > }

> > > }

> > }catch(Exception e){
> > > e.printStackTrace();

> > }
> > return nt;

> }
}

Sensor data example:
…...............................................


<temperature\_string>

69.7 F (20.9 C)

</temperature\_string>




<temp\_f>

69.7

</temp\_f>




<temp\_c>

20.9

</temp\_c>




<relative\_humidity>

35

</relative\_humidity>




<wind\_string>

From the NNE at 1.0 MPH Gusting to 4.0 MPH

</wind\_string>




<wind\_dir>

NNE

</wind\_dir>




<wind\_degrees>

14

</wind\_degrees>




<wind\_mph>

1.0

</wind\_mph>




<wind\_gust\_mph>

4.0

</wind\_gust\_mph>




<pressure\_string>

30.11" (1019.5 mb)

</pressure\_string>




<pressure\_mb>

1019.5

</pressure\_mb>




<pressure\_in>

30.11

</pressure\_in>




<dewpoint\_string>

40.8 F (4.9 C)

</dewpoint\_string>




<dewpoint\_f>

40.8

</dewpoint\_f>




<dewpoint\_c>

4.9

</dewpoint\_c>




<heat\_index\_string/>




<heat\_index\_f/>




<heat\_index\_c/>




<windchill\_string/>




<windchill\_f/>




<windchill\_c/>




<solar\_radiation/>


…...............................................
Convert to triples data:
<http://lsm.deri.ie/resource/135567139969100002Q21690871> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.oclc.org/NET/ssnx/ssn#Observation>.
<http://lsm.deri.ie/resource/135567139969100002Q21690871> <http://purl.oclc.org/NET/ssnx/ssn#observedBy> <http://lsm.deri.ie/resource/8a8291b73215690e0132156982bb044d>.
<http://lsm.deri.ie/resource/135567139969100002Q21690871> <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> "2012-05-19T16:53:16.91+07:00"^^<http://www.w3.org/2001/XMLSchema#dateTime>.
<http://lsm.deri.ie/resource/135567139969100002Q21690871> <http://purl.oclc.org/NET/ssnx/ssn#featureOfInterest> <http://lsm.deri.ie/resource/http://lsm.deri.ie/resource/25744859695434572818782997131348>.
<http://lsm.deri.ie/resource/135567139969100003Q21789336> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://lsm.deri.ie/ont/lsm.owl#AirTemperature>.
<http://lsm.deri.ie/resource/135567139969100003Q21789336> <http://lsm.deri.ie/ont/lsm.owl#isObservedPropertyOf> <http://lsm.deri.ie/resource/135567139969100002Q21690871>.
<http://lsm.deri.ie/resource/135567139969100003Q21789336> <http://lsm.deri.ie/ont/lsm.owl#value> "21.4"^^<http://www.w3.org/2001/XMLSchema#double>.
<http://lsm.deri.ie/resource/135567139969100003Q21789336> <http://lsm.deri.ie/ont/lsm.owl#unit> "C".
<http://lsm.deri.ie/resource/135567139969100003Q21789336> <http://purl.oclc.ie/NET/ssnx/ssn#observedProperty> <http://lsm.deri.ie/resource/5395423154665>.
<http://lsm.deri.ie/resource/135567139969100003Q21789336> <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> "2012-05-19T16:53:16.91+07:00"^^<http://www.w3.org/2001/XMLSchema#dateTime>.
<http://lsm.deri.ie/resource/135567139969100005Q5884085> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://lsm.deri.ie/ont/lsm.owl#Status>.
<http://lsm.deri.ie/resource/135567139969100005Q5884085> <http://lsm.deri.ie/ont/lsm.owl#isObservedPropertyOf> <http://lsm.deri.ie/resource/135567139969100002Q21690871>.
<http://lsm.deri.ie/resource/135567139969100005Q5884085> <http://lsm.deri.ie/ont/lsm.owl#value> "".
<http://lsm.deri.ie/resource/135567139969100005Q5884085> <http://purl.oclc.ie/NET/ssnx/ssn#observedProperty> <http://lsm.deri.ie/resource/5395417401338>.
<http://lsm.deri.ie/resource/135567139969100005Q5884085> <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> "2012-05-19T16:53:16.91+07:00"^^<http://www.w3.org/2001/XMLSchema#dateTime>.
<http://lsm.deri.ie/resource/135567139969100006Q8920243> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://lsm.deri.ie/ont/lsm.owl#AtmosphereHumidity>.
<http://lsm.deri.ie/resource/135567139969100006Q8920243> <http://lsm.deri.ie/ont/lsm.owl#isObservedPropertyOf> <http://lsm.deri.ie/resource/135567139969100002Q21690871>.
<http://lsm.deri.ie/resource/135567139969100006Q8920243> <http://lsm.deri.ie/ont/lsm.owl#value> "34"^^<http://www.w3.org/2001/XMLSchema#double>.
<http://lsm.deri.ie/resource/135567139969100006Q8920243> <http://lsm.deri.ie/ont/lsm.owl#unit> "in".
<http://lsm.deri.ie/resource/135567139969100006Q8920243> <http://purl.oclc.ie/NET/ssnx/ssn#observedProperty> <http://lsm.deri.ie/resource/5395341713068>.
<http://lsm.deri.ie/resource/135567139969100006Q8920243> <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> "2012-05-19T16:53:16.91+07:00"^^<http://www.w3.org/2001/XMLSchema#dateTime>.
<http://lsm.deri.ie/resource/135567139969100007Q1324883> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://lsm.deri.ie/ont/lsm.owl#AtmospherePressure>.
<http://lsm.deri.ie/resource/135567139969100007Q1324883> <http://lsm.deri.ie/ont/lsm.owl#isObservedPropertyOf> <http://lsm.deri.ie/resource/135567139969100002Q21690871>.
<http://lsm.deri.ie/resource/135567139969100007Q1324883> <http://lsm.deri.ie/ont/lsm.owl#value> "30.09"^^<http://www.w3.org/2001/XMLSchema#double>.
<http://lsm.deri.ie/resource/135567139969100007Q1324883> <http://lsm.deri.ie/ont/lsm.owl#unit> "in".
<http://lsm.deri.ie/resource/135567139969100007Q1324883> <http://purl.oclc.ie/NET/ssnx/ssn#observedProperty> <http://lsm.deri.ie/resource/5395345638854>.
<http://lsm.deri.ie/resource/135567139969100007Q1324883> <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> "2012-05-19T16:53:16.91+07:00"^^<http://www.w3.org/2001/XMLSchema#dateTime>.
<http://lsm.deri.ie/resource/135567139969100008Q20728841> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://lsm.deri.ie/ont/lsm.owl#Direction>.
<http://lsm.deri.ie/resource/135567139969100008Q20728841> <http://lsm.deri.ie/ont/lsm.owl#isObservedPropertyOf> <http://lsm.deri.ie/resource/135567139969100002Q21690871>.
<http://lsm.deri.ie/resource/135567139969100008Q20728841> <http://lsm.deri.ie/ont/lsm.owl#value> "NNW".
<http://lsm.deri.ie/resource/135567139969100008Q20728841> <http://purl.oclc.ie/NET/ssnx/ssn#observedProperty> <http://lsm.deri.ie/resource/5395372015952>.
<http://lsm.deri.ie/resource/135567139969100008Q20728841> <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> "2012-05-19T16:53:16.91+07:00"^^<http://www.w3.org/2001/XMLSchema#dateTime>.
<http://lsm.deri.ie/resource/135567139969100009Q27525703> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://lsm.deri.ie/ont/lsm.owl#WindChill>.
<http://lsm.deri.ie/resource/135567139969100009Q27525703> <http://lsm.deri.ie/ont/lsm.owl#isObservedPropertyOf> <http://lsm.deri.ie/resource/135567139969100002Q21690871>.
<http://lsm.deri.ie/resource/135567139969100009Q27525703> <http://lsm.deri.ie/ont/lsm.owl#value> "336"^^<http://www.w3.org/2001/XMLSchema#double>.
<http://lsm.deri.ie/resource/135567139969100009Q27525703> <http://lsm.deri.ie/ont/lsm.owl#unit> "C".
<http://lsm.deri.ie/resource/135567139969100009Q27525703> <http://purl.oclc.ie/NET/ssnx/ssn#observedProperty> <http://lsm.deri.ie/resource/5395434919219>.
<http://lsm.deri.ie/resource/135567139969100009Q27525703> <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> "2012-05-19T16:53:16.91+07:00"^^<http://www.w3.org/2001/XMLSchema#dateTime>.
<http://lsm.deri.ie/resource/135567139969100010Q31970563> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://lsm.deri.ie/ont/lsm.owl#WindSpeed>.
<http://lsm.deri.ie/resource/135567139969100010Q31970563> <http://lsm.deri.ie/ont/lsm.owl#isObservedPropertyOf> <http://lsm.deri.ie/resource/135567139969100002Q21690871>.
<http://lsm.deri.ie/resource/135567139969100010Q31970563> <http://lsm.deri.ie/ont/lsm.owl#value> "3.0"^^<http://www.w3.org/2001/XMLSchema#double>.
<http://lsm.deri.ie/resource/135567139969100010Q31970563> <http://lsm.deri.ie/ont/lsm.owl#unit> "mph".
<http://lsm.deri.ie/resource/135567139969100010Q31970563> <http://purl.oclc.ie/NET/ssnx/ssn#observedProperty> <http://lsm.deri.ie/resource/5395438576035>.
<http://lsm.deri.ie/resource/135567139969100010Q31970563> <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> "2012-05-19T16:53:16.91+07:00"^^<http://www.w3.org/2001/XMLSchema#dateTime>.