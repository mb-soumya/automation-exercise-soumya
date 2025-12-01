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

## Notes
- Tests use WebDriverManager to manage browser binaries
- Screenshots taken on failures are saved under `test-output/screenshots/`

## Additional features added

- Logging: Logback configuration included under `src/main/resources/logback.xml`.
- ExtentReports: HTML report generated at `test-output/extent-report.html` (listener automatically creates it).
- Docker: `docker-compose.yml` added to run a Selenium Standalone Chrome container. To use:

  1. Start Docker (Docker Desktop)
  2. Run `docker-compose up -d`
  3. Set system property to use remote WebDriver (not yet wired) and run tests.

- Screenshots: On failure screenshots saved under `test-output/screenshots/` and attached to Extent report.
