package com.company.automation.api;

import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.testng.Assert.assertEquals;

public class ApiProductsTest extends ApiBase {

    @Test
    public void getProducts_shouldReturn200() {
        Response r = given().when().get("/products").then().extract().response();
        // depending on the site this may be HTML (200) or JSON; we simply assert status
        assertEquals(r.statusCode(), 200);
    }
}
