package com.company.automation.api;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;

public class ApiBase {

    protected RequestSpecification spec;

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = System.getProperty("base.url", "https://automationexercise.com/");
        spec = RestAssured.given().relaxedHTTPSValidation().header("Accept", "application/json");
    }
}
