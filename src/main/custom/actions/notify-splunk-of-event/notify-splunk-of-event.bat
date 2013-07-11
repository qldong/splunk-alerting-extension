@echo off
rem ------------------------------------------------
rem Batch file to send event notifications to Splunk
rem ------------------------------------------------
..\..\..\jdk/bin/java -Dlog4j.configuration=file:..\..\conf/log4j.xml -jar ..\..\lib\splunkClient.jar Event %*
