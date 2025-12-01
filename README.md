# Automation Exercise — Java (Selenium + TestNG)

This project implements the assignment automation using Java, Selenium, TestNG and RestAssured.

## Prerequisites
- Java 17+ installed
- Maven installed (or use IntelliJ embedded)
- Google Chrome installed

## How to run
1. Open project in IntelliJ (File -> Open -> choose folder with pom.xml)
2. Wait for Maven to download dependencies
3. Run all tests:
```
mvn test
```

Run headed (see browser UI):
```
mvn -Dheadless=false test
```

Run a single test class (example):
```
mvn -Dtest=com.company.automation.tests.AuthTest test
```


