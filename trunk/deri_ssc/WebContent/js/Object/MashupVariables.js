var metaHash= new Hashtable();
var eleNum = 0;

var socketHost = window.location.host + window.location.pathname;
if(socketHost.lastIndexOf('engine') !== -1){
    socketHost = socketHost.substring(0,socketHost.lastIndexOf('engine'));
}
var SSCHost = "http://" + socketHost;
var lsmSensorSocket = socketHost + "websocket/LSMSensorMessage";
var lsmStreamSocket = socketHost + "websocket/LSMStreamMessage";
var lsmTwitterSocket = socketHost + "websocket/TwitterStreamMessage";
var lsmEndPointSocket = socketHost + "websocket/EndPointMessage";
var lsmCqelsSocket = socketHost + "websocket/CqelsMessage";
var socketHash = new Hashtable();
var endpoints = new Array();
var selectedBlock;
//var defaultQuery='select * \n from <http://lsm.deri.ie/sensormeta#> \n where { '
//   		+'\n     ?s ?p ?o. '
//	+'\n } limit 10';

var defaultQuery='';
var availableSparqlEndPoints = [
                     "http://lsm.deri.ie/sparql",           
                     "http://data.uni-muenster.de/sparql",
                     "http://dbpedia.org/sparql",
                     "http://bnb.data.bl.uk/sparql",
                     "http://sparql.data.southampton.ac.uk",
                     "http://sparql.sindice.com",
                     "http://linkedgeodata.org/sparql",                    
                     ];

var timeUnits = [
                                "milliseconds",
                                "seconds",
                                "minutes",
                                "hours",
                                "days",                                           
                                ];

var lsmIcon = new Hashtable();
lsmIcon.put("Weather","images/32x32/weather.png");
lsmIcon.put("COSM","images/32x32/pachube.png");
lsmIcon.put("Bike hire","images/32x32/bikehire.png");
lsmIcon.put("Airport","images/32x32/airport.png");
lsmIcon.put("Traffic","images/32x32/traffic.png");
lsmIcon.put("GSN","images/32x32/gsn.png");
lsmIcon.put("Railway Station","images/32x32/trainstation.png");
lsmIcon.put("URL","images/32x32/url.png");
lsmIcon.put("LSMStream","images/32x32/lsmstream.png");
lsmIcon.put("Twitter","images/32x32/twitter.png");
lsmIcon.put("EndPoint","images/32x32/sparql.png");
lsmIcon.put("Merge","images/32x32/merge.png");
lsmIcon.put("ToRDFStream","images/32x32/stream.png");
lsmIcon.put("Location","images/32x32/location.png");
lsmIcon.put("Timer","images/32x32/timer.png");
lsmIcon.put("Cqels","images/32x32/cqels.png");

var user = new Object();
user.username = null;
user.pass = null;
user.userId = null;
function timeToMilliseconds(timeValue,unit){
	var mili = 1;
	if(unit==="days")
		mili = timeValue*60*24*60*1000;
	else if(unit==="hours")
		mili = timeValue*60*60*1000;
	else if(unit==="minutes")
		mili = timeValue*60*1000;
	else if(unit==="seconds")
		mili = timeValue*1000;
	else mili = timeValue;
	return mili;
}