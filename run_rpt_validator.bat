@ECHO OFF
set JAVA_HOME="C:\Program Files (x86)\Java\jdk1.8.0_92\jre"
set PATH=%JAVA_HOME%\bin;%PATH%
java -DReportGeneratorAppenderLogFile="C:/RPTTesterToolLogs/oms-reporting.log" -DSAPAppenderLogFile="C:/RPTTesterToolLogs/sap-reporting.log" -DReportGeneratorLogLevel="DEBUG" -DReportGeneratorRootLogLevel="ERROR" -Dlog4j.configuration=log4j.xml -jar RPTValidator.jar