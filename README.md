### QA Test Automation Suite

This test automation suite runs all the possible E2E test cases for the application provided in QA assignment. Test suite covers the UI and integration with REST endpoint.

#### Frameworks
* TestNG
* RestAssured
* Selenium webDriver

#### How to run the test suite

Please download the test suite to your computer, go to the QAAssignment and run following commands
	
* checkout: `git clone https://github.com/snehanemade/QAAssignment.git`
* goto dir: `cd QAAssignment`
* install dependencies: `mvn clean install`
* start test: `mvn clean test`

#### Prerequisite to run this test suite
* Maven
* Java SE Development Kit (JDK)