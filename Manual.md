# Introduction #

Linked Sensor Middleware (LSM) is a platform that brings together the live real world sensed data and the Semantic Web. It provides many functionalities such as: i) wrappers for real time data collection and publish- ing; ii) a web interface for data annotation and visualisation; and iii) a SPARQL endpoint for querying unified Linked Stream Data and Linked Data.


# Details #

This  document provides information about the Web-based front-end of LSM. It contains:
  * An overview about the Web-based interface of LSM
  * Instructions on how to access data through LSM and how to use the LSM functionalities
  * Description of the LSM ontology structure and the Sparql endpoint service
> The Web-based interface can be access at http://lsm.deri.ie

## Exploring live data with LSM ##
LSM provides an user friendly interface that allows sensor data to be published, visualised, annotated, and queried. The interface uses a map overlay to display the sensor information.

http://deri-lsm.googlecode.com/files/interface.PNG

The LSM web interface has 5 main components:

http://deri-lsm.googlecode.com/files/components.JPG
http://deri-lsm.googlecode.com/files/components2.JPG

## Sensor toolbar ##
The Sensor toolbar is used to show the different types of sensors available.
http://deri-lsm.googlecode.com/files/sensortoolbar.JPG
## Map area ##
The Map area allows users to view live sensor data according to the sensor’s location. It can also display historical data. All the data is available for download in RDF format.
### View live sensor data ###
LSM provides two options for visualising live sensor data: Quick view and Detailed view.
#### Quick View ####
This functionality provides a quick view of latest data of a particular sensor.

http://deri-lsm.googlecode.com/files/quickview1.JPG
http://deri-lsm.googlecode.com/files/quickview2.JPG

#### Detailed view ####
This functionality is used for showing information for a sensor or a group of sensors in details.

http://deri-lsm.googlecode.com/files/detailedview1.JPG

A new window will be opened that includes all sensors around the icon selected.

http://deri-lsm.googlecode.com/files/detailedview2.JPG
http://deri-lsm.googlecode.com/files/detailedview3.JPG

### Historical Data ###

LSM provides some visualizations for historical data such as charts and plots for numeric data.

http://deri-lsm.googlecode.com/files/historicaldata.JPG

### RDF data ###

Any live or hitstorical sensor data can be downloaded in RDF format from the links with the RDF icon.

http://deri-lsm.googlecode.com/files/RDFData.JPG

## Search tab ##
### Location-based search ###
This is the simplest search function which allows users to locate sensor positions on the map by entering an address.

http://deri-lsm.googlecode.com/files/locationsearch.JPG

### Facted search ###
#### Quick sensor search ####
Quick sensor search allows users to quickly find specific types of sensors around a location.

http://deri-lsm.googlecode.com/files/factedsearch1.JPG
http://deri-lsm.googlecode.com/files/factedsearch2.JPG

#### Advanced Search ####
Apart from the quick search, the data can also be navigated via its relevant properties using the advanced search option. It enables users to filter the information based on: i) sensor type (given by a sensor taxonomy); ii) sensor location (by choosing an area in the map); or iii) sensor specification (like context, history data, accuracy, etc).

http://deri-lsm.googlecode.com/files/advancedsearch1.JPG
http://deri-lsm.googlecode.com/files/advancedsearch2.JPG
http://deri-lsm.googlecode.com/files/advancedsearch3.JPG
http://deri-lsm.googlecode.com/files/advancedsearch4.JPG
http://deri-lsm.googlecode.com/files/advancedsearch5.JPG

## Filter panel ##
The Filter Panel is used to save customised views created by users. Users can use this functionality to save the current view of interest. In the next usage of LSM, saved views can be reused without repeat the navigating actions.

http://deri-lsm.googlecode.com/files/filterpanel.JPG

## User panel ##
The User panel provides functionalities such as setting a notification criteria, adding new sensors to the system, managing sensor data, and creating data feeds. These functionalities are only for registered user.

### User notification ###

http://deri-lsm.googlecode.com/files/notification1.JPG
http://deri-lsm.googlecode.com/files/notification2.JPG

### User annotation ###
This function is used to annotate a sensor data source of interest. It provides an annotation wizard to guide the user through the anotation process driven by the sensor ontologies.

http://deri-lsm.googlecode.com/files/annotation1.JPG
http://deri-lsm.googlecode.com/files/annotation2.JPG
http://deri-lsm.googlecode.com/files/annotation3.JPG
http://deri-lsm.googlecode.com/files/annotation4.JPG
http://deri-lsm.googlecode.com/files/annotation5.JPG
http://deri-lsm.googlecode.com/files/annotation6.JPG

### User data management ###
http://deri-lsm.googlecode.com/files/datamanagement1.JPG

#### Data purging ####
This functionality allows users to remove a certain chunk of historical data. Users can delete data the meets some condition such as certain time period or produced by a particular sensor. Note that this function is only authorised the users who added the sensor data sources being deleted.

#### Data mashup ####

http://deri-lsm.googlecode.com/files/datamashup.JPG

#### Data feed ####
The data feed function is built for advanced user. To register a data feed, the user first has to enter a CQELS query (http://code.google.com/p/cqels/wiki/CQELS_language), which is then executed continously to output matched data. Second, the user’s e-mail address is also required for sending the feed’s URL.
http://deri-lsm.googlecode.com/files/datafeed1.JPG
http://deri-lsm.googlecode.com/files/datafeed2.JPG

## Sparql endpoint service ##
LSM provides a SPARQL endpoint for Semantic Web developers. Users can directly query LSM datasets. Futhermore, users can also combine data from other SPARQL endpoints like Linkedgeodata or DBPedia with  the data from the LSM SPARQL enpoint via the SPARQL 1.1 Federation extension http://www.w3.org/TR/sparql11-federated-query/

http://deri-lsm.googlecode.com/files/sparqlendpoint.JPG

## Mobile application demo ##
http://deri-lsm.googlecode.com/files/moblie1.JPG
http://deri-lsm.googlecode.com/files/moblie2.JPG

You can download LSM Manual [here](http://deri-lsm.googlecode.com/files/lsmmanual_v1.1.pdf)