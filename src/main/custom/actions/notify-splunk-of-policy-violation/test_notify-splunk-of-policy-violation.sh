#
# Copyright 2013 AppDynamics
# 
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
# 
# http://www.apache.org/licenses/LICENSE-2.0
# 
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
#!/bin/sh -x
#ACME Book Store Application, 2, Tue Apr 17 14:02:55 PDT 2012, 1, ERROR, Response time policy violation, Business Transaction response time is much higher than normal, 8, 5, BUSINESS_TRANSACTION, ViewCart.addToCart, 86, 2, APPLICATION_COMPONENT_NODE, Node_8003, 3, 0, APPLICATION_COMPONENT_NODE, Node_8000, 2, 2, APPLICATION_COMPONENT_NODE, Node_8000, 2, Average Response Time (ms) Baseline Condition, 77, GREATER_THAN, ABSOLUTE, 5, 6, APPLICATION_COMPONENT_NODE, Node_8000, 2, Calls per Minute Condition, 78, GREATER_THAN, ABSOLUTE, 50, 940, Business Transaction response time is much higher than normal triggered at Tue Apr 17 14:02:55 PDT 2012. This policy was violated because the following conditions were met for the ViewCart.addToCart Business Transaction for the last 5 minute(s):   For Evaluation Entity: Node_8003 Node  For Evaluation Entity: Node_8000 Node - Average Response Time (ms) Baseline Condition is greater than 5. Observed value = 6 - Calls per Minute Condition is greater than 50. Observed value = 940, 2, http://WIN-FKL67IRSIPI:8090/controller/#location=APP_INCIDENT_DETAIL&incident=
#"FamilySearch-staging", "592", "Mon Jan 07 21:00:55 GMT+00:00 2013", "1", "ERROR", "AppDynamics Policy violation notification", "SplunkTest2", "47442141", "1", "BUSINESS_TRANSACTION", "/fs/search-ct-ws", "22910", "2", "APPLICATION_COMPONENT_NODE", "hazj-search-controller-ctl501.search.refa.ft", "6602", "1", "APPLICATION_COMPONENT_NODE", "hazj-search-controller-ctl 501.search.refa.ft", "6602", "condition 1", "189736357", "GREATER_THAN", "ABSOLUTE", "5", "142", "APPLICATION_COMPONENT_NODE", "hcap-search-controller-ctl502.search.refa.ft", "6603", "1", "APPLICATION_COMPONENT_NODE", "hcap-search-controller-ctl502.search.refa.ft", "6603", "condition 1", "189736357", "GREATER_THAN", "ABSOLUTE", "5", "132", "SplunkTest2 triggered at Mon Jan 07 21:00:55 GMT+00:00 2013. This policy was violated because the following conditions were met for the /fs/search-ct-ws Business Transaction for the last 1 minute(s):   For Evaluation Entity: hazj-search-controller-ctl501.search.refa.ft Node - condition 1 is greater than 5.     Observed value = 142  For Evaluation Entity: hcap-search-controller-ctl502.search.refa.ft Node - condition 1 is greater than 5. Observed value = 132", "49758", "http://ut01-appdyn01.iaas:9890/controller/#location=APP_INCIDENT_DETAIL&incident="
APP_NAME="FamilySearch-staging"
APP_ID=592
PVN_ALERT_TIME="Mon Jan 07 21:00:55 GMT+00:00 2013"
PRIORITY=1
SEVERITY="ERROR"
TAG="AppDynamics Policy violation notification"
RULE_NAME="SplunkTest2"
RULE_ID=47442141
PVN_TIME_PERIOD_IN_MINUTES=1
PVN_AFFECTED_ENTITY_TYPE="BUSINESS_TRANSACTION"
PVN_AFFECTED_ENTITY_NAME="/fs/search-ct-ws"
PVN_AFFECTED_ENTITY_ID=22910
PVN_NUMBER_OF_EVALUATION_ENTITIES=2
PVN_EVALUATION_ENTITY_TYPE_1="APPLICATION_COMPONENT_NODE"
PVN_EVALUATION_ENTITY_NAME_1="hazj-search-controller-ctl501.search.refa.ft"
PVN_EVALUATION_ENTITY_ID_1=6602
NUMBER_OF_TRIGGERED_CONDITIONS_1=1
SCOPE_TYPE_1="APPLICATION_COMPONENT_NODE"
SCOPE_NAME_1="hazj-search-controller-ctl 501.search.refa.ft"
SCOPE_ID_1=6602
PVN_TC_CONDITION_NAME_1="condition 1"
PVN_TC_CONDITION_ID_1=189736357
OPERATOR_1="GREATER_THAN"
CONDITION_UNIT_TYPE_1="ABSOLUTE"
THRESHOLD_VALUE_1=5
OBSERVED_VALUE_1=142
PVN_EVALUATION_ENTITY_TYPE_2="APPLICATION_COMPONENT_NODE"
PVN_EVALUATION_ENTITY_NAME_2="hazj-search-controller-ctl502.search.refa.ft"
PVN_EVALUATION_ENTITY_ID_2=6603
NUMBER_OF_TRIGGERED_CONDITIONS_2=1
SCOPE_TYPE_2="APPLICATION_COMPONENT_NODE"
SCOPE_NAME_2="hcap-search-controller-ctl502.search.refa.ft"
SCOPE_ID_2=6603
PVN_TC_CONDITION_NAME_2="condition 1"
PVN_TC_CONDITION_ID_2=189736357
OPERATOR_2="GREATER_THAN"
CONDITION_UNIT_TYPE_2="ABSOLUTE"
THRESHOLD_VALUE_2=5
OBSERVED_VALUE_2=132
SUMMARY_MESSAGE="SplunkTest2 triggered at Mon Jan 07 21:00:55 GMT+00:00 2013. This policy was violated because the following conditions were met for the /fs/search-ct-ws Business Transaction for the last 1 minute(s): For Evaluation Entity: hazj-search-controller-ctl501.search.refa.ft Node - condition 1 is greater than 5. Observed value = 142  For Evaluation Entity: hcap-search-controller-ctl502.search.refa.ft Node - condition 1 is greater than 5. Observed value = 132"
INCIDENT_ID="49758"
DEEP_LINK_URL="http://ut01-appdyn01.iaas:9890/controller/#location=APP_INCIDENT_DETAIL&incident="
./notify-splunk-of-policy-violation.sh "${APP_NAME}" "${APP_ID}" "${PVN_ALERT_TIME}" "${PRIORITY}" "${SEVERITY}" "${TAG}" "${RULE_NAME}" "${RULE_ID}" "${PVN_TIME_PERIOD_IN_MINUTES}" "${PVN_AFFECTED_ENTITY_TYPE}" "${PVN_AFFECTED_ENTITY_NAME}" "${PVN_AFFECTED_ENTITY_ID}" "${PVN_NUMBER_OF_EVALUATION_ENTITIES}" "${PVN_EVALUATION_ENTITY_TYPE_1}" "${PVN_EVALUATION_ENTITY_NAME_1}" "${PVN_EVALUATION_ENTITY_ID_1}" "${NUMBER_OF_TRIGGERED_CONDITIONS_1}" "${SCOPE_TYPE_1}" "${SCOPE_NAME_1}" "${SCOPE_ID_1}"  "${PVN_TC_CONDITION_NAME_1}" "${PVN_TC_CONDITION_ID_1}" "${OPERATOR_1}" "${CONDITION_UNIT_TYPE_1}" "${THRESHOLD_VALUE_1}" "${OBSERVED_VALUE_1}" "${PVN_EVALUATION_ENTITY_TYPE_2}" "${PVN_EVALUATION_ENTITY_NAME_2}" "${PVN_EVALUATION_ENTITY_ID_2}" "${NUMBER_OF_TRIGGERED_CONDITIONS_2}" "${SCOPE_TYPE_2}" "${SCOPE_NAME_2}" "${SCOPE_ID_2}"  "${PVN_TC_CONDITION_NAME_2}" "${PVN_TC_CONDITION_ID_2}" "${OPERATOR_2}" "${CONDITION_UNIT_TYPE_2}" "${THRESHOLD_VALUE_2}" "${OBSERVED_VALUE_2}" "${SUMMARY_MESSAGE}" "${INCIDENT_ID}" "${DEEP_LINK_URL}"
