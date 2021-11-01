ECHO OFF
ECHO Automation Starts
:: Change the project location
set projectPath=D:\seasia\Framework\
cd %projectPath%

call mvn test verify "-Dcucumber.options=--tags @loginfunc" -f pom.xml
call mvn allure:serve
call mvn allure:report
PAUSE