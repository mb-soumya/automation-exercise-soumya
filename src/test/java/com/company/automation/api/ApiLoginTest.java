package com.company.automation.api;

import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.testng.Assert.assertTrue;

public class ApiLoginTest extends ApiBase {

    @Test
    public void apiLogin_smoke() {
        // Try a POST to /login - some apps redirect to UI; accept 200/302/401
        Response resp = given().formParam("email", "test@example.com")
                .formParam("password", "Pwd@1234!")
                .when().post("/login")
                .then().extract().response();

        int status = resp.statusCode();
        assertTrue(status == 200 || status == 302 || status == 401);
    }
}
