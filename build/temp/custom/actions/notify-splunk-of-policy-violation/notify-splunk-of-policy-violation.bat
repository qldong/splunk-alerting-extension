@echo off
rem ------------------------------------------------------------
rem Batch file to send policy violation  notifications to Splunk
rem ------------------------------------------------------------
..\..\..\jdk\bin\java -Dlog4j.configuration=file:..\..\conf/log4j.xml -jar ..\..\lib\splunkClient.jar PolicyViolation %*
