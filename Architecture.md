# Architecture #

[![](http://deri-lsm.googlecode.com/files/architecture.jpg)](http://deri-lsm.googlecode.com/files/lsmmanual_v1.1.pdf)

## Data Acquisition Layer ##

The data acquisition layer provides wrappers to collect sensor readings and transform them according to the Linked Stream Data layout previously described.LSM currently provides three types of wrappers:

**Physical wrappers:** are used for collecting sensor data coming from physical devices via physical connections, for instance, serial and ad-hoc network connections.


**Mediate wrappers:** mediate the connections to other sensor middlewares by transforming data from a variety of data formats  and data feeding protocols into RDF. LSM currently provides mediate wrappers to some of the most used middlewares like [GSN ](http://sourceforge.net/apps/trac/gsn/),[Pachube ](http://pachube.com), the sensor gateway/web services ([NOAA](http://www.noaa.gov/)),and the [London Transport syndication](http://www.tfl.gov.uk/businessandpartners/syndication/default.aspx).

**Linked Data wrappers:** provide access to the sensor data sources being collected and stored in relational databases by exposing the relational data structure in RDF via mapping rules like the ones provided in the [D2R](http://www4.wiwiss.fu-berlin.de/bizer/d2rmap/d2rmap.htm) mapping language.

## Linked Data Layer ##

The Linked Data layer provides the interface to access not only Linked Sensor Data which was collected and annotated by the wrappers provided, but also outgoing links to data in the Linked Data Cloud. The outgoing links are provided by users via the annotation module. LSM also automatically extracts relevant links from DBpedia, Geonames and LinkedGeoData via spatial relationships, for example, point of interests nearby a sensor location. In the current implementation, we use Virtuoso as the triple storage, the relational database and the spatial indexer for this layer.

## Data Access Layer ##

The Data Access layer supports the declarative queries on top of the Linked Data layer. Queries over Linked Stream Data can follow either a pull-based or a push-based model. LSM provides two query processors, one for each of the query models, as described below. These query processors also
enable data access from remote SPARQL endpoints via a federation extension of the [SPARQL 1.1 language ](http://www.w3.org/TR/sparql11-federated-query/).

**Linked Data query processor:** supports traditional pull-based queries under SPARQL language over sensor metadata and sensor readings (live and historical).

**CQELS engine:** supports push-based continuous queries over Linked Stream Data under [CQELS language ](http://code.google.com/p/cqels/wiki/CQELS_language). This query engine allows user to actively filter and integrate real-time data to create new streams of data from existing sources.

## Application layer ##

The query processing capability provided by the Data Access layer allows the easy and rapid application development for end-users or machine-users. In addition, the Application layer offers the following extra functionalities:

**SPARQL Endpoint:** makes the data available in LSM accessible as a citizen of the Linked Data Cloud.

**Mashup composer:** enables the composition of existing data sources to derive new sensor data
sources. The derived data source is created via either a visual wizard or a continuous query under the CQELS language.

**Linked Sensor Explorer:** enables the exploration of existing data
sources. A faceted browsing functionality helps the user to filter sensor data based on relevantproperties, e.g. location and meanings of readings. In the background the exploration and navigation actions are translated into complex queries and executed on top of the Linked Data query processor, but all are done transparently to the user.

**Streaming channels:** actively stream integrated data sources via streaming protocols like [Google PubSubHubbub ](http://code.google.com/p/pubsubhubbub/) and [XMPP](http://xmpp.org/).