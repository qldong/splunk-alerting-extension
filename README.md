# AppDynamics Splunk Monitoring Extension

##Use Case

Splunk [www.splunk.com](http://www.splunk.com) indexes and makes searchable data from any app, server or network device in real time including logs, config files, messages, alerts, scripts and metrics.

AppDynamics integrates directly with Splunk to correlate performance degradation with your existing logging infrastructure. With an unsurpassed ability to monitor the performance of mission-critical applications with application diagnostics and infrastructure data, the AppDynamics and Splunk® Enterprise integration accelerates collaboration between Development and Operations teams by improving their ability to identify, troubleshoot and resolve application performance and availability issues.

You can mine application performance monitoring data from AppDynamics using its REST API. You can then process the data within Splunk using Search Processing Language (SPL). It also contains a notification client that can be extracted to an AppDynamics Controller that will relay event and policy violation notifications in AppDynamics to Splunk and has the ability to cross-launch into AppDynamics from Splunk.

##Installation

These instructions assume that you are familiar with using both AppDynamics and Splunk. 

Links within this file go to AppDynamics 3.7 documentation. If you are running an earlier version, use the Search feature to find the associated topics.

####Prerequisites


- You have installed AppDynamics version 3.5 or newer. If you do not already have a license, sign up for a trial license. You can choose either a SaaS solution or an On-Premise installation.
- You have installed Splunk version 4.x or newer.
- You have installed the AppDynamics App for Splunk from Splunkbase. 
- You have access to the following AppDynamics Controller information, which is required to set up the integration: 
   - hostname/IP address
   - port number
   - account name
   - user name
   - password
   
	If you use a SaaS account, AppDynamics provides you with the required information.
- You have access to the [AppDynamics documentation](http://docs.appdynamics.com/display/PRO13S/AppDynamics+Pro+Documentation). When you trial or buy the product, AppDynamics provides access credentials to you.
- $SPLUNK_HOME is set to the directory where Splunk is installed.

####Steps
1.  Download and unpack the tar.gz file.
2.  Locate and edit the file: $SPLUNK_HOME/etc/apps/appdynamics/local/metrics.conf
2.  Add one section for each individual metric you want to mine from AppDynamics. You need the following:
    -   AppDynamics metric name, to name the section in the metrics.conf file, and for use as as unique identifier in Splunk
    -   REST URL of the metric from the AppDynamics Metric Browser, see the [AppDynamics REST documentation](http://docs.appdynamics.com/display/PRO13S/Use+the+AppDynamics+REST+API)  (login required).
    -   polling interval - how frequently, in seconds, Splunk will run the script to get this metric
    -   username and password
        -   for On-Premise or SaaS multi-tenant, <username>@<account> and the password
        -   for On-Premise or SaaS single-tenant, <username>@customer1 and the password

For example, if you want to mine a metric called AverageResponseTime for the ViewCart.sendItems business transaction, the entry would be similar to this:

  	[ViewCart.sendItems_AverageResponseTime|ViewCart.sendItems_AverageResponseTime]  
 	url = http://<controller-host>:<port>/controller/rest/applications/  
 	Acme%20Online%20Book%20Store/metric-data?metricpath=Business%20Transaction%20Performance%7CBusiness%20Transactions%7CECommerce%7CViewCart.sendItems%7CAverage%20Response%20Time%20(ms)&time-range-type=BEFORE_NOW&duration-in-mins=15  
 	interval = 60  
 	username = user1@customer1  
 	password = welcome

##Metrics

1.  Launch the AppDynamics App in Splunk.
2.  Enter index=appdynamics in the Search field of the AppDynamics App in Splunk.  

![](http://appsphere.appdynamics.com/t5/image/serverpage/image-id/75iE92D91F9F93C4D85/image-size/original?v=mpbl-1&px=-1)


##Notifications in Splunk

**Note**: This feature is currently available only for single-tenant Controllers.

###For AppDynamics SaaS customers:
Contact AppDynamics Support and ask them to extract the splunkClient zip file on your behalf.

###For AppDynamics On-Premise Controllers:

1.  Locate and copy the splunkClient zip file:
    -   For Linux: $SPLUNK_HOME/etc/apps/appdynamics/splunkClient/splunkClient-3.7-linux.zip
    -   For Windows: $SPLUNK_HOME/etc/apps/appdynamics/splunkClient/splunkClient-3.7-windows.zip

2.  Extract the splunkClient zip file to the Controller installation directory on the machine where the AppDynamics Controller is installed
    
**Note**: The splunkClient.zip includes a custom.xml file containing notifications. If your Controller already has a custom.xml file, edit it and merge the contents.

##Setting up .splunkrc file

A sample.splunkrc file is part of the splunkClient zip file.

1.  Edit the .splunkrc file to add information that allows the Controller to communicate with Splunk. If you do not already have a .splunkrc file, edit the file <controller-home>/custom/conf/.splunkrc and change the properties to suit your Splunk installation
      
	    # Host at which Splunk is reachable (OPTIONAL)  
    	host=localhost  
    	# Port at which Splunk is reachable (OPTIONAL)  
    	# Use the admin port, which is 8089 by default.  
    	port=8089  
     	# Splunk username  
    	username=admin  
    	# Splunk password  
    	password=changeme  
    	# Access scheme (OPTIONAL)  
    	scheme=https  
    	# Namespace to use (OPTIONAL)  
    	namespace=**:**

2.  Copy the .splunkrc file to the platform home directory of the user that started the Controller. In Linux, this is the environment variable $HOME location; in Windows, it is the environment variable %USERPROFILE% location.

##Custom Notifications

####Custom Notifications in AppDynamics

1.  Use the AppDynamics Controller UI to configure the custom actions notify-splunk-of-event and notify-splunk-of-policy-violation in the Global Notifications and Policy Notifications screens. Details are available in [Configure Custom Notifications](http://docs.appdynamics.com/display/PRO13S/Integrate+using+Custom+Action+Scripts) (login required).
2.  Add the following field extraction section to your $SPLUNK_HOME/etc/apps/search/default/props.conf file:
    
		[source::http-simple]  
		EXTRACT-AppD = url="http[s]*://(?<nurl>[^"|]+)"
    	
3.  Add the following workflow action to your $SPLUNK_HOME/etc/apps/search/default/workflow_actions.conf file:
    
		[LaunchAppD]  
		display_location = both  
		fields = url  
		label = Launch in AppDynamics  
		link.method = get  
		link.target = blank  
		link.uri = http://$!nurl$  
		type = link
    
####Custom Notifications in Splunk from AppDynamics

![](http://appsphere.appdynamics.com/t5/image/serverpage/image-id/77iE7C5FD5831E6CCE0/image-size/original?v=mpbl-1&px=-1)

##Launching AppDynamics from Splunk

On an event in the Splunk Search App, click the blue pulldown and choose Launch in AppDynamics. See the screenshot above.


##Contributing

Always feel free to fork and contribute any changes directly via [GitHub](https://github.com/Appdynamics/splunk-alerting-extension).

##Community

Find out more in the [AppSphere](http://appsphere.appdynamics.com/t5/Extensions/Splunk-Alerting-Extension/idi-p/823) community.

##Support

For any questions or feature request, please contact [AppDynamics Center of Excellence](mailto://ace-request@appdynamics.com).
