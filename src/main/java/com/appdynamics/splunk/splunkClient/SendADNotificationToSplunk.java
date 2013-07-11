/**
 * Copyright 2013 AppDynamics
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.appdynamics.splunk.splunkClient;
/**
 *  This class will be used to relay AppDynamics notifications to the 
 *  Splunk Server via REST.
 *
 * Copyright (c) AppDynamics, Inc.
 * @author Pranta Das
 * Created on: August 14, 2012.
 */

import com.appdynamics.common.NotificationParameters;
import com.dtdsoftware.splunk.logging.RestEventData;
import com.dtdsoftware.splunk.logging.SplunkLogEvent;
import com.dtdsoftware.splunk.logging.SplunkRestInput;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

// Splunk imports

public class SendADNotificationToSplunk implements NotificationParameters 
{
	private static Logger logger =
				Logger.getLogger(SendADNotificationToSplunk.class);

	/**
	 * Parameter data types
	 */
	 static final String STRING = "STRING";
	 static final String INT = "INT";
	 static final String LONG = "LONG";
	 static final String STRING_ARRAY = "STRING_ARRAY";
	 static final String INT_ARRAY = "INT_ARRAY";

	 static final String[][] pvnParmsAndTypes = 
	{		
	 /*[0]*/{APPLICATION_NAME, STRING, SplunkAppDynamicsEvent.APPDYNAMICS_APPLICATION_NAME},
	 /*[1]*/{APPLICATION_ID, LONG, SplunkAppDynamicsEvent.APPDYNAMICS_APPLICATION_ID},
	 /*[2]*/{PVN_ALERT_TIME, LONG, SplunkAppDynamicsEvent.COMMON_DVC_TIME},
	 /*[3]*/{PRIORITY, INT, SplunkAppDynamicsEvent.COMMON_PRIORITY},
	 /*[4]*/{SEVERITY, STRING, SplunkAppDynamicsEvent.COMMON_SEVERITY},
	 /*[5]*/{TAG, STRING, SplunkAppDynamicsEvent.APPDYNAMICS_TAG},
	 /*[6]*/{PVN_RULE_NAME, STRING, SplunkAppDynamicsEvent.COMMON_NAME},
	 /*[7]*/{PVN_RULE_ID, LONG, SplunkAppDynamicsEvent.APPDYNAMICS_RULE_ID},
	 /*[8]*/{PVN_TIME_PERIOD_IN_MINUTES, INT, SplunkAppDynamicsEvent.COMMON_DURATION},
	 /*[9]*/{PVN_AFFECTED_ENTITY_TYPE, STRING, SplunkAppDynamicsEvent.APPDYNAMICS_AFFECTED_ENTITY_TYPE},
	 /*[10]*/{PVN_AFFECTED_ENTITY_NAME, STRING, SplunkAppDynamicsEvent.APPDYNAMICS_AFFECTED_ENTITY_NAME},
	 /*[11]*/{PVN_AFFECTED_ENTITY_ID, LONG, SplunkAppDynamicsEvent.APPDYNAMICS_AFFECTED_ENTITY_ID},
	 /*[12]*/{PVN_NUMBER_OF_EVALUATION_ENTITIES, INT, null},
	 /*[13]*/{PVN_EVALUATION_ENTITY_TYPE, STRING, SplunkAppDynamicsEvent.APPDYNAMICS_EVALUATION_ENTITY_TYPE},
	 /*[14]*/{PVN_EVALUATION_ENTITY_NAME, STRING, SplunkAppDynamicsEvent.APPDYNAMICS_EVALUATION_ENTITY_NAME},
	 /*[15]*/{PVN_EVALUATION_ENTITY_ID, LONG, SplunkAppDynamicsEvent.APPDYNAMICS_EVALUATION_ENTITY_ID},
	 /*[16]*/{NUMBER_OF_TRIGGERED_CONDITIONS_PER_EVALUATION_ENTITY, INT, null},
	 /*[17]*/{PVN_TC_SCOPE_TYPE, STRING, SplunkAppDynamicsEvent.APPDYNAMICS_SCOPE_TYPE},
	 /*[18]*/{PVN_TC_SCOPE_NAME, STRING, SplunkAppDynamicsEvent.APPDYNAMICS_SCOPE_NAME},
	 /*[19]*/{PVN_TC_SCOPE_ID, LONG, SplunkAppDynamicsEvent.APPDYNAMICS_SCOPE_ID},
	 /*[20]*/{PVN_TC_CONDITION_NAME, STRING, SplunkAppDynamicsEvent.APPDYNAMICS_CONDITION_NAME},
	 /*[21]*/{PVN_TC_CONDITION_ID, STRING, SplunkAppDynamicsEvent.APPDYNAMICS_CONDITION_ID},
	 /*[22]*/{PVN_TC_OPERATOR, STRING, SplunkAppDynamicsEvent.APPDYNAMICS_OPERATOR},
	 /*[23]*/{PVN_TC_CONDITION_UNIT_TYPE, STRING, SplunkAppDynamicsEvent.APPDYNAMICS_CONDITION_UNIT_TYPE},
	 /*[24]*/{PVN_TC_USE_DEFAULT_BASELINE, STRING, SplunkAppDynamicsEvent.APPDYNAMICS_USE_DEFAULT_BASELINE},
	 /*[25]*/{PVN_TC_BASELINE_NAME, STRING, SplunkAppDynamicsEvent.APPDYNAMICS_BASELINE_NAME},
	 /*[26]*/{PVN_TC_BASELINE_ID, LONG, SplunkAppDynamicsEvent.APPDYNAMICS_BASELINE_ID},
	 /*[37]*/{PVN_TC_THRESHOLD_VALUE, LONG, SplunkAppDynamicsEvent.APPDYNAMICS_THRESHOLD_VALUE},
	 /*[28]*/{PVN_TC_OBSERVED_VALUE, LONG, SplunkAppDynamicsEvent.APPDYNAMICS_OBSERVED_VALUE},
	 /*[29]*/{PVN_SUMMARY_MESSAGE, STRING, SplunkAppDynamicsEvent.COMMON_DESC},
	 /*[30]*/{PVN_INCIDENT_ID, STRING, SplunkAppDynamicsEvent.COMMON_EVENT_ID},
	 /*[31]*/{CONTROLLER_DEEP_LINK_URL, STRING, SplunkAppDynamicsEvent.COMMON_URL},
     /*[32]*/{PVN_EVENT_TYPE, STRING, SplunkAppDynamicsEvent.APPDYNAMICS_POLICY_EVENT_TYPE}
	};
	
	 static final int PVN_NUM_OF_EVAL_ENTITIES_INDEX = 12;
	 static final int PVN_NUM_OF_EVAL_ENTITIES_ATTRS = 3;
	 static final int PVN_NUM_OF_TRIG_CONDS_INDEX = 16;
	 static final int PVN_NUM_OF_TRIG_CONDS_ATTRS = 12;
	 static final int PVN_TC_CONDITION_UNIT_TYPE_INDEX = 23;
	 static final int PVN_NUMBER_OF_BASELINE_PARMS = 3;
	 static final String[][] eventNotificationParmsAndTypes =
	{		
	 /*[0]*/{APPLICATION_NAME, STRING, SplunkAppDynamicsEvent.APPDYNAMICS_APPLICATION_NAME},
	 /*[1]*/{APPLICATION_ID, LONG, SplunkAppDynamicsEvent.APPDYNAMICS_APPLICATION_ID},
	 /*[2]*/{EN_TIME, INT, SplunkAppDynamicsEvent.COMMON_DVC_TIME},
	 /*[3]*/{PRIORITY, INT, SplunkAppDynamicsEvent.COMMON_PRIORITY},
	 /*[4]*/{SEVERITY, STRING, SplunkAppDynamicsEvent.COMMON_SEVERITY},
	 /*[5]*/{TAG, STRING, SplunkAppDynamicsEvent.APPDYNAMICS_TAG},
	 /*[6]*/{EN_NAME, STRING, SplunkAppDynamicsEvent.COMMON_NAME},
	 /*[7]*/{EN_ID, LONG, SplunkAppDynamicsEvent.COMMON_EVENT_ID},
	 /*[8]*/{EN_INTERVAL_IN_MINUTES, INT, SplunkAppDynamicsEvent.COMMON_DURATION },
	 /*[9]*/{EN_NUMBER_OF_EVENT_TYPES, INT, null},
	/*[10]*/{EN_EVENT_TYPE, STRING_ARRAY, SplunkAppDynamicsEvent.APPDYNAMICS_EVENT_TYPES},
	/*[11]*/{EN_NUMBER_OF_EVENTS, INT_ARRAY, 
							SplunkAppDynamicsEvent.APPDYNAMICS_NUMBER_OF_EVENTS_FOR_TYPES},
	/*[12]*/{EN_NUMBER_OF_EVENT_SUMMARIES, INT, null},
	/*[13]*/{EN_EVENT_SUMMARY_ID, LONG, SplunkAppDynamicsEvent.APPDYNAMICS_EVENT_SUMMARY_ID},
	/*[14]*/{EN_EVENT_SUMMARY_TIME, STRING, SplunkAppDynamicsEvent.APPDYNAMICS_EVENT_SUMMARY_TIME},
	/*[15]*/{EN_EVENT_SUMMARY_TYPE, STRING, SplunkAppDynamicsEvent.APPDYNAMICS_EVENT_SUMMARY_TYPE},
	/*[16]*/{EN_EVENT_SEVERITY, STRING, SplunkAppDynamicsEvent.APPDYNAMICS_EVENT_SUMMARY_SEVERITY},
	/*[17]*/{EN_EVENT_SUMMARY_STRING, STRING, SplunkAppDynamicsEvent.COMMON_DESC},
	/*[18]*/{CONTROLLER_DEEP_LINK_URL, STRING, SplunkAppDynamicsEvent.COMMON_URL}
	};

	 static final int EN_NUM_OF_EV_TYPS_INDEX = 9;
	 static final int EN_NUM_OF_EV_TYPS_ATTRS = 2;
	 static final int EN_NUM_OF_EV_SUMRY_INDEX = 12;
	 static final int EN_NUM_OF_EV_SUMRY_ATTRS = 5;


	 private static final String TRUE = "true";
	
    /**
     * Default property values
     */
    private static final String DEFAULT_HOSTNAME
                = "localhost";
    private static final int DEFAULT_PORT = 8089;

    private static final String userHome = System.getProperty("user.home"),
                                fileName = userHome +"/.splunkrc";
    private static String userName = "admin", password = "ad@Splunk0";
    private static final String BANNER = "***************************"+
                "*****************************************************";
	/**
	 * 	Connection properties
	 */
	private static final String SPLUNK_HOST = "host";
	private static final String SPLUNK_PORT = "port";
	private static final String SPLUNK_USERNAME = "username";
	private static final String SPLUNK_PASSWORD = "password";

	
	static String               hostname;
    static int                  port;
	static Properties 		    theProperties;
	static String 				deepLinkUrl;

    static RestEventData        restEventData = new RestEventData();
    static SplunkRestInput      splunkRestInput;

	/**
	 * Set up connection to the web server using the global configuration
	 * information from the input properties file
	 *  
	 * @return true if successful, false otherwise
	 */
	private static boolean setupConnection()
	{
		/**
		 * Read the Splunk client properties file for the connection properties
		 */
		theProperties = new Properties();
		hostname = DEFAULT_HOSTNAME;
		port = DEFAULT_PORT;

		System.getProperties();

		logger.info(" Reading .splunkrc file:" +
				fileName);
		
	    FileInputStream propFile = null;
		try 
		{
			propFile = new FileInputStream(fileName);
		} 
		catch (FileNotFoundException e) 
		{
			logger.error(e);
		}

        if (propFile != null)
        {
            try
            {
                theProperties.load(propFile);
            }
            catch (IOException e)
            {
                logger.error(e);
            }

            String strVal;

            if ((strVal = theProperties.getProperty(SPLUNK_HOST)) != null)
                hostname = strVal.trim();

            if ((strVal = theProperties.getProperty(SPLUNK_PORT)) != null)
                port = Integer.parseInt(strVal.trim());

            if ((strVal = theProperties.getProperty(SPLUNK_USERNAME)) != null)
                userName = strVal.trim();

            if ((strVal = theProperties.getProperty(SPLUNK_PASSWORD)) != null)
                password = strVal.trim();
        }

        try
        {
            if (splunkRestInput == null)
            {
                splunkRestInput = new SplunkRestInput(userName, password, hostname, port, restEventData, false);
                splunkRestInput.setMaxQueueSize("30MB");
                splunkRestInput.setDropEventsOnQueueFull(false);
            }
        } catch (Exception e) {
            logger.error("Unable to connect to Splunk", e);
            return false;
        }


		return true;
	}
	/**
	 * Tear down connection to the Splunk server
	 */
	private static void teardownConnection()
	{
		logger.info(" Calling disconnect");
	

        if (splunkRestInput != null)
        {
            splunkRestInput.closeStream();
            splunkRestInput = null;
        }

		logger.info(" Disconnected from Splunk.");
	}

	/**
	 * Send an event to Splunk via REST
     * @param event  The event being passed to Splunk
     */
	private static void sendEvent(SplunkLogEvent event)
	{
		// send event to the cell
		logger.info(" Sending event to Splunk.");
        splunkRestInput.sendEvent(event.toString());
	}

	/**
	 * Utility function to set a name value pair
	 * 
	 * @param nameValuePairs - The map comtaining the name value pairs
	 * @param name - the name of the attribute to set
	 * @param valueObject - the value of the attribute to set
	 * @param dataType - the data type of the attribute
     */
	private static void setNameValuePair(
					 HashMap<String, String> nameValuePairs,
					 String name,
					 String valueObject,
					 String dataType)
	{

		valueObject = transformIfNecessary(name, valueObject);

		nameValuePairs.put(name, valueObject);
	}


	/**
	 *	Transforms the value of an attribute if necessary. 
	 *
	 * @param name
	 * @param valueString
	 * @return
     */
	private static String transformIfNecessary(String name, String valueString)
	{
		if (name.equalsIgnoreCase(SplunkAppDynamicsEvent.COMMON_URL))
		{

			deepLinkUrl = valueString;
		}

		return valueString;
	}

	private static void completeDeepLinkUrl(
					HashMap<String, String> nameValues)
	{
		long incidentOrEventId = 0L;

        if (nameValues.get(SplunkAppDynamicsEvent.APPDYNAMICS_EVENT_SUMMARY_ID) != null)
        {
                incidentOrEventId=
                        Long.parseLong(nameValues.get(SplunkAppDynamicsEvent.APPDYNAMICS_EVENT_SUMMARY_ID));
        }
        else
        if (nameValues.get(SplunkAppDynamicsEvent.COMMON_EVENT_ID) != null)
        {
            incidentOrEventId=
                    Long.parseLong(nameValues.get(SplunkAppDynamicsEvent.COMMON_EVENT_ID));
        }

        if (nameValues.get(SplunkAppDynamicsEvent.COMMON_URL) != null)
        {
                nameValues.put(SplunkAppDynamicsEvent.COMMON_URL, deepLinkUrl+incidentOrEventId);
                return;
        }
    }

	/**
	 * This method will send an event notification received from AppDynamics 
	 * to Splunk
	 * 
	 * @param args - the list of arguments received with the event notification
	 * @return true if all goes well, false otherwise
     */
	private static boolean sendEventNotification(String args[])
	{
		try
		{

			int numEventTypes=0;
			
			try 
			{
				numEventTypes = Integer.parseInt(
									args[EN_NUM_OF_EV_TYPS_INDEX+1]);
			}
			catch(NumberFormatException nfe)
			{
				logger.error("Unable to parse numEventTypes from:"+args[EN_NUM_OF_EV_TYPS_INDEX+1], nfe);
			}
			
			int numEventSummariesIndex = EN_NUM_OF_EV_TYPS_INDEX+2
									+(numEventTypes*EN_NUM_OF_EV_TYPS_ATTRS);
									

            int numEventSummaries = 0;
            try
            {
		    	numEventSummaries = Integer.parseInt(args[numEventSummariesIndex]);
            }
            catch(NumberFormatException nfe)
            {
                logger.error("Unable to parse numEventSummaries from:"+args[numEventSummariesIndex], nfe);
            }

			int numParms = EN_NUM_OF_EV_TYPS_INDEX+2
					 +(numEventTypes*EN_NUM_OF_EV_TYPS_ATTRS)
					 +(numEventSummaries*EN_NUM_OF_EV_SUMRY_ATTRS)+1,
			argsIndex=1,
			parmIndex=0;
			HashMap<String, String>[] nameValues =
									new HashMap[numEventSummaries];
			nameValues[0]= new HashMap<String,String>();

			for (int i=0; i < numParms; i++)
			{
				if ((argsIndex-1) == EN_NUM_OF_EV_TYPS_INDEX)
				{
					argsIndex++;
					StringBuffer eventTypes = new StringBuffer("{");
					StringBuffer numberOfEventsForEventType = new StringBuffer("{");
					for (int j = 0; j < numEventTypes; 
								j++, i+=EN_NUM_OF_EV_TYPS_ATTRS)
					{
                        if (j > 0)
                        {
                            eventTypes.append(", ");
                            numberOfEventsForEventType.append(", ");
                        }
						eventTypes.append(args[argsIndex++]);
						numberOfEventsForEventType.append(
                                args[argsIndex++]);
					}

                    setNameValuePair(nameValues[0],
                            eventNotificationParmsAndTypes[
                                       EN_NUM_OF_EV_TYPS_INDEX+1][2],
                            eventTypes.append("}").toString(),
                            eventNotificationParmsAndTypes[
                                    EN_NUM_OF_EV_TYPS_INDEX+1][1]);
					
                    setNameValuePair(nameValues[0],
                            eventNotificationParmsAndTypes[
                                       EN_NUM_OF_EV_TYPS_INDEX+2][2],
                            numberOfEventsForEventType.append("}").toString(),
                            eventNotificationParmsAndTypes[
                                    EN_NUM_OF_EV_TYPS_INDEX+2][1]);

                    parmIndex += EN_NUM_OF_EV_TYPS_ATTRS+1;
				}
				else 
				if (argsIndex == numEventSummariesIndex)
				{
					argsIndex++;
					HashMap<String, String> tmp =
						(HashMap<String, String>) nameValues[0].clone();
					for (int j = 0; j < numEventSummaries; 
								j++, i+=EN_NUM_OF_EV_SUMRY_ATTRS)
					{
						if (j > 0)
						{
							nameValues[j]= 
								new HashMap<String, String>();
							
							nameValues[j].putAll(tmp);

						}
						for (int k=0; k < EN_NUM_OF_EV_SUMRY_ATTRS; k++)
						{
                            setNameValuePair(nameValues[j], 
                                    eventNotificationParmsAndTypes[
                                           EN_NUM_OF_EV_SUMRY_INDEX+1+k][2],
                                    args[argsIndex++], 
                                    eventNotificationParmsAndTypes[
                                           EN_NUM_OF_EV_SUMRY_INDEX+1+k][1]);
						}
					}
					parmIndex += EN_NUM_OF_EV_SUMRY_ATTRS+1;
				}
				else
				{
					setNameValuePair(nameValues[0], 
							     eventNotificationParmsAndTypes[parmIndex][2],
							     args[argsIndex++], 
							     eventNotificationParmsAndTypes[parmIndex][1]);
					parmIndex++;
				}
				
			}
			
			for (int i = 0; i < numEventSummaries; i++)
			{
                if (i > 0)
                {
                    nameValues[i].put(SplunkAppDynamicsEvent.COMMON_DESC,
                                        nameValues[0].get(SplunkAppDynamicsEvent.COMMON_DESC));
                    nameValues[i].put(SplunkAppDynamicsEvent.COMMON_URL,
                                    nameValues[0].get(SplunkAppDynamicsEvent.COMMON_URL));
                    nameValues[i].put(SplunkAppDynamicsEvent.COMMON_EVENT_ID,
                                    nameValues[0].get(SplunkAppDynamicsEvent.COMMON_EVENT_ID));
                }

				completeDeepLinkUrl(nameValues[i]);

                SplunkLogEvent splunkLogEvent = createSplunkEvent(nameValues[i]);

				sendEvent(splunkLogEvent);
			}
		} 
		catch (Exception e)
		{
			logger.error(e);
			return false;
		} 
		
		return true;
	}

	/**
	 * This method will send an policy violation notification received from 
	 * AppDynamics to Splunk
	 * 
	 * @param args - the list of arguments received with the violation 
	 * notification
	 * 
	 * @return true if all goes well, false otherwise
     */
	private static boolean sendPolicyViolationNotification(String args[])
	{
        int numEvaluationEntities =0;
		try
        {
            numEvaluationEntities = Integer.parseInt(
                    args[PVN_NUM_OF_EVAL_ENTITIES_INDEX+1]);
        }
        catch(NumberFormatException nfe)
        {
            logger.error("Unable to parse numEvaluationEntities from:"+args[PVN_NUM_OF_EVAL_ENTITIES_INDEX+1], nfe);

        }
		int	numParms = PVN_NUM_OF_EVAL_ENTITIES_INDEX
				+1+(numEvaluationEntities*PVN_NUM_OF_EVAL_ENTITIES_ATTRS)+2,
			argsIndex = 1,
			parmIndex=0;

		try
		{
			HashMap<String,String>[] nameValues =
									new HashMap[numEvaluationEntities*50];

			nameValues[0]= new HashMap<String, String>();

			while (argsIndex < args.length)
			{
				if ((argsIndex-1) == PVN_NUM_OF_EVAL_ENTITIES_INDEX)
				{
					HashMap<String,String> tmp =
						(HashMap<String,String>) nameValues[0].clone();
					argsIndex++;
					for (int j = 0; j < numEvaluationEntities; j++)
					{

						if (j > 0)
						{
							nameValues[j]= 
								new HashMap<String,String>();
							
							nameValues[j].putAll(tmp);

						}

						for (int k=0; k < PVN_NUM_OF_EVAL_ENTITIES_ATTRS; k++)
						{

							if ((PVN_NUM_OF_EVAL_ENTITIES_INDEX+k+2) == 
								PVN_NUM_OF_TRIG_CONDS_INDEX)
								{
									argsIndex++;

									int numTriggeredConditions = 0;
                                    try
                                    {
										numTriggeredConditions=Integer.parseInt(args[argsIndex]);
                                    }
                                    catch(NumberFormatException nfe)
                                    {
                                        logger.error("Unable to parse numTriggeredConditions from:"
                                                +args[argsIndex], nfe);

                                    }
									numParms += PVN_NUM_OF_TRIG_CONDS_ATTRS+1;
									HashMap<String,String> tmp2
									  =	(HashMap<String,String>)
												nameValues[0].clone();
									argsIndex++;
									int l;
									for (l = 0; l < numTriggeredConditions; l++)
									{

										if (l > 0)
										{
											nameValues[l]= 
												new HashMap<String,String>();
											
											nameValues[l].putAll(tmp2);

										}

										for (int m=0; 
												 m<PVN_NUM_OF_TRIG_CONDS_ATTRS; 
												 m++)
										{
											setNameValuePair(nameValues[l], 
											   pvnParmsAndTypes[
					                            PVN_NUM_OF_TRIG_CONDS_INDEX+1+m]
					                            							[2],
					                           args[argsIndex++], 
					                           pvnParmsAndTypes[
					                            PVN_NUM_OF_TRIG_CONDS_INDEX+1+m]
					                            						[1]);
											if ((PVN_NUM_OF_TRIG_CONDS_INDEX+1+m) 
													== 
											   PVN_TC_CONDITION_UNIT_TYPE_INDEX) 
											{
												if (!args[argsIndex-1].startsWith(
																		BASELINE_PREFIX))
												{
													m+=PVN_NUMBER_OF_BASELINE_PARMS;
													numParms -=3;
                                                }
												else if (args[argsIndex].equalsIgnoreCase(
															TRUE))
												{
													m+=PVN_NUMBER_OF_BASELINE_PARMS-1;
													numParms -=2;
                                                }
											}
										}
									}
									
								}
								else
								{
									
			                        setNameValuePair(nameValues[j], 
			                                  pvnParmsAndTypes[
			                                     PVN_NUM_OF_EVAL_ENTITIES_INDEX+1+k][2],
			                                  args[argsIndex++], 
			                                  pvnParmsAndTypes[
			                                     PVN_NUM_OF_EVAL_ENTITIES_INDEX+1+k][1]
			                                  );
								}
							}
					}
					
					parmIndex += (PVN_NUM_OF_EVAL_ENTITIES_ATTRS+PVN_NUM_OF_TRIG_CONDS_ATTRS+2);
				}
				else
				{
					setNameValuePair(nameValues[0], 
							pvnParmsAndTypes[parmIndex][2],
							args[argsIndex++], 
							pvnParmsAndTypes[parmIndex][1]);
					parmIndex++;
				}
				
			}

			for (int i = 0; nameValues[i] != null; i++)
			{
				if (i > 0)
				{
					nameValues[i].put(SplunkAppDynamicsEvent.COMMON_DESC,
                                        nameValues[0].get(SplunkAppDynamicsEvent.COMMON_DESC));
                    nameValues[i].put(SplunkAppDynamicsEvent.COMMON_URL,
                                    nameValues[0].get(SplunkAppDynamicsEvent.COMMON_URL));
                    nameValues[i].put(SplunkAppDynamicsEvent.COMMON_EVENT_ID,
                                    nameValues[0].get(SplunkAppDynamicsEvent.COMMON_EVENT_ID));
				}
				
				completeDeepLinkUrl(nameValues[i]);

                SplunkLogEvent splunkLogEvent = createSplunkEvent(nameValues[i]);

				sendEvent(splunkLogEvent);

			}
			
		} 
		catch (Exception e)
		{
			logger.error(e);
			return false;
		}
		
		return true;
	}

    /**
     * Method to create a Splunk event from the HashMap of nameValue pairs
     *
     * @param nameValuePairs
     * @return a SplunkLogEvent
     */
    private static SplunkLogEvent createSplunkEvent(HashMap<String, String> nameValuePairs)
    {
        SplunkLogEvent splunkLogEvent = new SplunkLogEvent(
                        nameValuePairs.get(SplunkAppDynamicsEvent.COMMON_NAME),
                        nameValuePairs.get(SplunkAppDynamicsEvent.COMMON_EVENT_ID)
                       );
        for (Map.Entry<String, String> entry : nameValuePairs.entrySet())
        {
            splunkLogEvent.addPair(entry.getKey(),entry.getValue());
        }

        return splunkLogEvent;
    }

    static void removeDoubleQuotes(String[] args)
    {

        for (int i=0; i < args.length; i++)
        {
            args[i]=args[i].replaceAll("\"", "");
        }
    }

	/**
	 * Main method called from AppDynamics custom action shell script 
	 * or batch file.
	 * 
	 * @param args - arguments passed
	 */
	public static void main(String[] args) 
	{
		if (args.length < 10)
		{
			logger.error("Too few arguments"+
									" ... exiting");
			System.exit(-1);
		}
		
		
		logger.info(BANNER);
		logger.info("Received notification parameters:"+
				Arrays.toString(args));

        removeDoubleQuotes(args);

		int rc = 0;

		try 
		{
			String notificationType;
			boolean connectionSetup = setupConnection();
		
			if (!connectionSetup)
			{
				logger.error("Unable to setup connection");
				System.exit(-1);
			}
	
			notificationType = args[0];
			
			if (notificationType.equalsIgnoreCase(EVENT_NOTIFICATION))
			{
				logger.info("*** Processing event notification from AppDynamics");
				rc = sendEventNotification(args) ? 0 : -1;
			}
			else 
			if(notificationType.equalsIgnoreCase(POLICY_VIOLATION_NOTIFICATION))
			{
				logger.info("*** Processing policy violation notification from"+
									" AppDynamics");				
				rc = sendPolicyViolationNotification(args) ? 0 : -1;
			}
			else
			{
				throw new IllegalArgumentException(notificationType);
			}
			
			teardownConnection();

			logger.info(BANNER);				
		} 
		catch (Throwable t) 
		{
			logger.error(t);
		}
		
		System.exit(rc);
	}//main
	
}
