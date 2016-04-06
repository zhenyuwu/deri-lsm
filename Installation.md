# Introduction #

Add your content here.


# Details #

# LSM Wrapper requirements #
## Hardware requirements ##
  * Virtuoso server
  * Rabbitmq server
  * Hadoop computer system (master, slaves)
  * Apache 7.2 server
## Library requirements ##
Virtuoso Universal Server 6.3 (commercial or open source edition)
  * Apache Tomcat 7
  * Hadoop libs
  * JavaSE-1.6
  * RabbitMq client libs
  * Cqels libs
  * dom4j-1.6.1.jar, saxon9he.jar, jaxen-1.1-beta-7.jar
# Virtuoso for Linux (Enterprise Edition) #
## Before You Install ##
To ensure a smooth installation, please review the following checklist before you start the setup program
### Have you downloaded the software? ###
If you have not already done so, please download your copy of OpenLink Virtuoso from the Virtuoso web site. The URL is http://www.openlinksw.com/virtuoso/. When you visit the site, select "Software Download".
### Reinstalling Virtuoso? ###

If you are reinstalling Virtuoso, you must first stop the Database Servers. You can shut these services down with the following command executed from your Linux shell prompt:

Beta 1 Users

> sh virtuoso-lite.rc stop

> or

> virtuoso-lite.rc stop

> or

> ./virtuoso-lite.rc stop

Beta 2 Users

> sh virtuoso-stop.sh

> or

> virtuoso-stop.sh

> or

> ./virtuoso-stop.sh

You may also want to first uninstall prior Virtuoso RPM packages.
The following command (executed as root) can be used to uninstall the package:

rpm -e virtuoso-lite-1.1-3\_glibc2

> or

> rpm -e virtuoso-enterprise-1.1-3\_glibc2

> or

> rpm -e virtuoso-lite-1.1-3\_libc5

depending on Linux system and Virtuoso Server being used.
### Are ports 1111,1112, 8889 and 8890 free? ###

During the installation you will be prompted for an HTTP port number for use by each Virtuoso System Manager. If you are already running a web server or other listener on that port, you will encounter problems, and should choose another free port when you are prompted.
By default, the Virtuoso DBMS listens on port 1111, and the Demo Database on 1112. If these ports is currently in use by another server then after the installation completes, you will need to change the port numbers in the Local Virtuoso ODBC or UDBC DSN that is created during the installation process, and also in the following file:

$<Directory where Virtuoso is installed>/bin/virtuoso.ini

typically

> /usr/local/virtuoso

but can of course be any location of your choosing.

To check if a port is in use you can use the following command, and review the output:

> netstat -an | more
### Do you already have a "virtuoso" user account on your Linux system? ###
Although this is no longer a requirement of the installation it may be suitable to create a new Virtuoso user account for administering the Virtuoso server.
### Do you already have functional iODBC data sources on your system? ###
The OpenLink Virtuoso for Linux installation presumes the following:
  * You intend to connect to a Local Virtuoso server using iODBC or OpenLink UDBC
  * You intend to connect to one or more Remote Virtuoso servers on your network using iODBC or UDBC
  * You intend to Attach tables hosted within non-local or Remote Virtuoso database servers, into a Local or Remote Virtuoso Server
  * The existence of functional OpenLink or third-party iODBC drivers for Linux that connect to the non database servers referred to above.
  * In the absence of iODBC drivers for your non Virtuoso database, that a functional OpenLink or third-party UDBC Drivers exists for these database servers.

Instructions on how to manually configure iODBC Data Source Names is available at the following URL:
http://www.openlinksw.com/info/docs/rel3doc/unix/odbcsdk.htm If you are using OpenLink Multi-Tier ODBC Drivers for your remote database connections then we highly recommend the use of the Virtuoso System Manager for creating iODBC Data Source Names.
### Are your iODBC Data Source Names functional? ###
It is a useful sanity-check to ensure that your iODBC or UDBC data sources are functional, OpenLink provides an "odbctest" program for verifying basic usability of iODBC data sources. Both of these programs reside in the "samples" directory situated beneath the directory into which RPM installs Virtuoso.
## Installation Process ##
Once you have downloaded the required tar file from the web or ftp site and have placed the file in a designated installation directory you are ready to commence the Virtuoso installation process.

You may want to specify a new user to own the Virtuoso installation and then you can place the installation tar file into that users home directory.
  * First step is to un-tar the file to obtain the install script and archive file. Use the following command:
> > tar xvf <tar file>.tar


> replacing <tar file> with the relevant name of the file you downloaded.
  * Run the install script using the following command:
> > [virtuoso@myserver virtuoso]$ sh /install.sh


> The installation process will now begin and prompt you for required information.
  * The install script will first try and detect running Virtuoso server instances. In cases where an old Virtuoso server is being upgraded you must ensure that the old Virtuoso is first shutdown. It is advisable that you backup up your systems before continuing.
Checking requested components
Checking for existing installations and/or instances of Virtuoso...
> > 7269 ?        00:06:23 virtuoso


> /proc/net/tcp: Permission denied

> Alert: [processes; ](running.md)

> Warning: You have Virtuoso previously installed on this machine.

> To avoid existing data being overwritten or lost, you may press ^C to

> exit this installer, terminate and remove the existing installation,
> or proceed taking care to enter non-conflicting data below, by pressing

> Enter.

If you are upgrading a server then the installer will save copies of old configuration data for later use.

> Preserving any existing configurations...

> find: db: No such file or directory

> find: demo: No such file or directory

> find: bin: No such file or directory

> 0 blocks
  * You should have received a license file via email. This file should be copied to the server installation directory prior to installation. The installer will ask you for its location if none is found. If you do not have the license file at this time the installer will still continue.

> Checking licensing...

> A license file is required for the Virtuoso Server Instance(s) to
> start. Please enter the full path and name of a valid Virtuoso
> license file [./virtuoso.lic]:

  * Now the installer will continue to install files from the package. Every file installed will be echoed to the screen.
> > Unpacking enterprise.tar.Z
> > bin/
> > bin/v27/
> > bin/v27/bin/
> > ...
> > ...
> > vsp/releasenotes.css
> > vsp/releasenotes.html
> > vsp/vsp\_auth.sql
> > ...done
  * When the files have been installed from the package the location supplied for the license file will be checked. If one is not found then you will receive the following warning:


> WARNING: The license file entered is not valid or does not exist.
> You will need to copy a valid license file manually
> to the demo and db directories after the Virtuoso        installation completes.

  * The Virtuoso server installation has a few variations. The next question from the installer asks which one you want to make default. Even though each variation is installed, a convenient symbolic link will be made to bin/virtuoso from the selected type. Running ls -l in the bin directory after installation will reveal all.
Checking for installed optional components...
Please choose which Virtuoso server you wish to install:
    1. Universal Server
    1. Universal Server with iODBC
    1. Universal Server with iODBC and support for Java
    1. Universal Server with iODBC and support for PHP
    1. Universal Server with iODBC and support for Java and PHP
    1. Universal Server with iODBC and support for CLR Hosting via Mono
    1. Universal Server with iODBC and support for CLR and Java
    1. Universal Server with iODBC and support for CLR, Java and PHP4

> [8](8.md) :
  * The installer will search for required third-party components depending on which option is selected in the previous step. For example, choosing the Virtuoso with Java support will require a Java runtime. Results of the search are listed as suggestions from which you can confirm or type the actual location.

> Attempting to locate the java runtime library (libjvm.so)...

> Suggested file locations:

> /usr/local/lib

> /usr/local/jdk1.3/jre/lib/i386/classic

> /usr/local/jdk1.3/jre/lib/i386/server

> /usr/local/jdk1.3/jre/lib/i386/client

> /usr/opt/blackdown-jdk-1.3.1/jre/lib/i386/classic

> /usr/opt/blackdown-jdk-1.3.1/jre/lib/i386/server

> /usr/opt/blackdown-jdk-1.3.1/jre/lib/i386/client

> /usr/local/jdk1.3/jre/lib/i386/classic

> /usr/local/jdk1.3/jre/lib/i386/server

> /usr/local/jdk1.3/jre/lib/i386/client

> Please select one of the above locations or press Enter to use
the default (/usr/local/jdk1.3/jre/lib/i386/client):
  * The Virtuoso installer includes a Mono runtime distribution, since Mono is a developing platform you may have your own installation that you can supply the path to here.

> Please enter the full path to the Mono installation directory or
Enter to use the default (/home/openlink/virtuoso/mono)

  * Virtuoso now has enough information to configure its environment and startup scripts.
> > Setting up initial values in scripts and config files
> > Setting up file bin/odbc.ini
> > Setting up file bin/odbcinst.ini
> > Setting up file bin/demo-start.sh
> > Setting up file bin/demo-stop.sh
> > Setting up file bin/virtuoso-start.sh
> > Setting up file bin/virtuoso-stop.sh
> > Setting up file db/virtuoso.ini
> > Setting up file demo/demo.ini
> > Setting up file mono/bin/mcs
> > Setting up file virtuoso-enterprise.csh
> > Setting up file virtuoso-enterprise.sh
> > Setting up file initd\_startup.template
> > Setting up file initd\_startup\_demo.template
  * The installer will now ask a series of questions for configuring the Virtuoso .INI files. First confirm or alter the default database SQL listener port number. This is the port number that this database will be available for ODBC connections and iSQL sessions.


> Configuring port numbers for default and demo databases

> Please enter a port on which to run the default database

> SQL listener (default 1111):
  * Now you must specify the port number of the default database HTTP listener. This is the HTTP port that Virtuoso will listen on by default, you web-based administration interface for Virtuoso will be found here. If you confirm the default of 8889, then your Virtuoso will be available from: http://server:8889/, changing server to the name of your machine appropriately.

> Please enter a port on which to run the default database

> HTTP listener (default 8889):

  * Now you must supply a friendly name that you wish to identify your Virtuoso server by on the network using Zero Configuration.

> See Also:

> The Zero Configuration section.

> Please enter a name for default database Zero Configuration

(default "Virtuoso Universal Server at myserver:1111"):
  * The Zero Configuration details need a default username to make the connection with.
> > Please enter a user name for default database Zero Configuration


> (default "dba"):

  * Now we have all of the above details to supply again for the Demo Virtuoso database. The port number supplied here must be different to those supplied previously.

> Please enter a port on which to run the demo database

> SQL listener (default 1112):

> Please enter a port on which to run the demo database

> HTTP listener (default 8890):

> Please enter a name for demonstration database Zero Configuration

> (default Virtuoso Universal Server (demonstration) at myserver:1112):

> Please enter a user name for demonstration database Zero Configuration

> (default demo):
  * The Virtuoso servers have default administrator usernames of "dba". The "dba" accounts have the default password of "dba". It is recommended that you alter this as soon as possibly for security. The installer now provides the opportunity to do this the first time the server is started.

> Changing passwords for the Database Universal Server

> Please enter a replacement for the Default Database Server's
> SQL Administrative (dba) account password (twice) :

> Confirm Password :

  * The Virtuoso servers have default WebDAV administrator usernames of "dav". The "dav" accounts have the default password of "dav". It is recommended that you alter this as soon as possibly for security. The installer now provides the opportunity to do this the first time the server is started.

> Please enter a replacement for the Default Database's
> WebDAV administrative (dav) account password (twice) :

> Confirm Password :

> Please wait while the passwords are changed:

> Changing passwords for the Demo Universal Server

  * Likewise for the Demo server, you can change the default passwords:
Please enter a replacement for the Demonstration Database Server's
SQL Administrative (dba) account password (twice) :

> Confirm Password :

> Please enter a replacement for the Demonstration Database Server's
WebDAV Administrative (dav) account password (twice) :

> Confirm Password :

> Please wait while the passwords are changed:

> done.
  * Now we have all of the above details to supply again for the Demo Virtuoso database. The port number supplied here must be different to those supplied previously.

> Please enter a port on which to run the demo database
> SQL listener (default 1112):

> Please enter a port on which to run the demo database
HTTP listener (default 8890):

> Please enter a name for demonstration database Zero Configuration
(default Virtuoso Universal Server (demonstration) at myserver:1112):

> Please enter a user name for demonstration database Zero Configuration
(default demo):
  * The Virtuoso servers have default administrator usernames of "dba". The "dba" accounts have the default password of "dba". It is recommended that you alter this as soon as possibly for security. The installer now provides the opportunity to do this the first time the server is started.

> Changing passwords for the Database Universal Server

> Please enter a replacement for the Default Database Server's
SQL Administrative (dba) account password (twice) :

> Confirm Password :

  * The Virtuoso servers have default WebDAV administrator usernames of "dav". The "dav" accounts have the default password of "dav". It is recommended that you alter this as soon as possibly for security. The installer now provides the opportunity to do this the first time the server is started.

> Please enter a replacement for the Default Database's
WebDAV administrative (dav) account password (twice) :

> Confirm Password :

> Please wait while the passwords are changed:

> Changing passwords for the Demo Universal Server

  * Likewise for the Demo server, you can change the default passwords:
Please enter a replacement for the Demonstration Database Server's
SQL Administrative (dba) account password (twice) :

> Confirm Password :

> Please enter a replacement for the Demonstration Database Server's
WebDAV Administrative (dav) account password (twice) :

> Confirm Password :

> Please wait while the passwords are changed:

> done.

## Post Installation ##
### Post-Installation Sanity Check ###

A quick way to check that the database is running, is to point a browser to the http port. The following example URLs will show the System Manager for the default, and the demo Virtuoso databases:

> http://localhost:8889

> http://localhost:8890

> http://a_virtuoso_server.org:8890

### Troubleshooting DB Startup Failures ###
> Install failure

Check the .log file in:

> $<Directory where Virtuoso is installed>/bin/virtuoso.log

in order to review the installed files binaries and databases (defaults) for issues if install fails.

> .lck existance

Check .log in case of .lck issue

Delete the .lck file and repeat the steps from above.

> .trx incompatiblity issue

Check .log in case of .trx incompatiblity issue

> oplmgr issue

See OpenLink License Management for Linux
## Starting Virtuoso Automatically on Reboot ##
Two files, named initd\_startup.template and initd\_startup\_demo.template, are placed in the top level of the Virtuoso installation directory. On systems using SysVinit (most Linux distributions, Solaris) the appropriate one should be copied into /etc/init.d/ and symlinks created in the rcN.d/ directories, e.g.:

> bash# cp initd\_startup.template /etc/init.d/virtuoso

Perform some sanity checks here, that the directory it uses exists, etc:

> bash# vi /etc/init.d/virtuoso

> Test that it works correctly:

> bash# /etc/init.d/virtuoso stop

> bash# /etc/init.d/virtuoso start

Finally, create symbolic links in the regular runlevel directories pointing to that script:

> bash# cd /etc/rc2.d ; ln -s ../init.d/virtuoso S99virtuoso

> bash# cd /etc/rc3.d ; ln -s ../init.d/virtuoso S99virtuoso

> bash# cd /etc/rc5.d ; ln -s ../init.d/virtuoso S99virtuoso

Virtuoso should now start when the service is restarted.

For more details, please visit

> http://docs.openlinksw.com/virtuoso/installation.html
# Create LSM Wrapper virtuoso graphs #
Using iTeractive SQL virtuoso web interface to create 2 LSM Wrapper graphs.
Sparql command for metadata graph:

  * sparql create graph <http://lsm.deri.ie/metadata#>*

and for data graph:

  * sparql create graph <http://lsm.deri.ie/data#>*

# Installing RabbitMQ server on Ubuntu #
## Download the Server ##

> [rabbitmq-server\_2.8.4-1\_all.deb](http://www.rabbitmq.com/releases/rabbitmq-server/v2.8.4/rabbitmq-server_2.8.4-1_all.deb)

Packaged as .deb for Debian-based Linux	rabbitmq-server\_2.8.4-1\_all.deb

rabbitmq-server is included in Ubuntu since 9.04. However, the versions included are often quite old. You will probably get better results installing the .deb from our website. Check the Ubuntu package details for which version of the server is available for which versions of the distribution.

You can either download it with the link above and install with dpkg, or use our APT repository (see below).

All dependencies should be met automatically.
## Run RabbitMQ Server ##
Customise RabbitMQ Environment Variables

The server should start using defaults. You can customise the RabbitMQ environment. Also see how to configure components.

### Start the Server ###

The server is started as a daemon by default when the RabbitMQ server package is installed.

As an administrator, start and stop the server by using rabbitmq-server stop/start/etc.

Note: The server is set up to run as system user rabbitmq. If you change the location of the Mnesia database or the logs, you must ensure the files are owned by this user (and also update the environment variables).

![http://deri-lsm.googlecode.com/files/rabbitmq.png](http://deri-lsm.googlecode.com/files/rabbitmq.png)

**Our APT repository**
To use our APT repository:
  1. Add the following line to your /etc/apt/sources.list:

> deb http://www.rabbitmq.com/debian/ testing main

> 2. (optional) To avoid warnings about unsigned packages, add our public key to your trusted key list using apt-key(8):

> 3. wget http://www.rabbitmq.com/rabbitmq-signing-key-public.asc

> sudo apt-key add rabbitmq-signing-key-public.asc

> 4. Run apt-get update.

> 5. Install packages as usual; for instance,

> sudo apt-get install rabbitmq-server

**Controlling system limits**

To adjust system limits (in particular the number of open file handles), edit the file/etc/default/rabbitmq-server to invoke ulimit when the service is started, for example:

> ulimit -n 1024

will set the maximum number of file open handles for the service process to 1024 (the default).

**Managing the Broker**

To stop the server or check its status, etc., you can use rabbitmqctl (as an administrator). It should be available on the path. All rabbitmqctl commands will report the node absence if no broker is running.
  * Use rabbitmqctl stop to stop the server.
  * Use rabbitmqctl status to check whether it is running.

More info on rabbitmqctl.

# Running LSM Wrapper demo #
(Please read LSM Wrapper developing tutorial to understand how to write a new wrapper)
  1. Startup virtuoso server, rabbitmq server.
  1. Create Wrapper configuration folder
Create Wrapper configuration folder which is named “SimpleWrapperConf”. This folder has to be placed in the same directory with lsmwrapper.jar file.

![http://deri-lsm.googlecode.com/files/samefolder.png](http://deri-lsm.googlecode.com/files/samefolder.png)

3. Copy lsm wrapper library folder into the folder which contains lsmwrapper.jar. This library folder includes all the necessary libraries for lsm wrapper deployment.


4.Startup rabbitmq message bus queue. The message bus queue will receive all the triple data and deliver it to all the registered channel. In this demo, all triple data will be inserted into LSM virtuoso data graph (http://lsm.deri.ie/data#).
**Open terminal** Move to folder which contains lsmwrapper.jars
_Run command:
  * alpha@ubuntu:~/Testdemo$ java -cp lsmwrapper.jar lsm.rabbitmq.Reciever_


![http://deri-lsm.googlecode.com/files/reciever.png](http://deri-lsm.googlecode.com/files/reciever.png)

5.Run all wrappers in Wrapper Repository and push data to message bus queue by using this command:

  * alpha@ubuntu:~/Testdemo$ java -jar lsmwrapper.jar**or you can specify the wrapper you want to run:**

  * lpha@ubuntu:~/Testdemo$ java -cp lsmwrapper.jar lsm.wrapper.YahooWeatherWrapper

![http://deri-lsm.googlecode.com/files/feed.png](http://deri-lsm.googlecode.com/files/feed.png)