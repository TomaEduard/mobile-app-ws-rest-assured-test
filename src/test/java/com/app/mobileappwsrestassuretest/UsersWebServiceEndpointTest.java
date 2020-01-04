package com.app.mobileappwsrestassuretest;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runners.MethodSorters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UsersWebServiceEndpointTest {

    private final String CONTEXT_PATH = "/mobile-app-ws";
    private final String EMAIL_ADDRESS = "eduard.daniel.toma@gmail.com";
    private final String JSON = "application/json";

    private static String authorizationHeader;
    private static String userId;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
    }

    /*
    * testUserLogin
    *
    */
    @Test
    final void a() {

        Map<String, String> loginDetails = new HashMap<>();
        loginDetails.put("email", EMAIL_ADDRESS);
        loginDetails.put("password", "123");

        Response response = given()
                .contentType(JSON)
                .accept(JSON)
                .body(loginDetails)
                .when()
                .post(CONTEXT_PATH + "/users/login")
                .then()
                .statusCode(200).extract().response();

        authorizationHeader = response.header("Authorization");
        userId = response.header("UserID");

        assertNotNull(authorizationHeader);
        assertNotNull(userId);
    }

    /*
     * testGetUserDetails()
     *
     */
    @Test
    final void b() {

        Response response = given()
                .header("Authorization", authorizationHeader)
                .accept(JSON)
                .when()
                .get(CONTEXT_PATH + "/users/" + userId)
                .then()
                .statusCode(200)
                .contentType(JSON)
                .extract()
                .response();

        String userPublicId = response.jsonPath().getString("userId");
        String firstName = response.jsonPath().getString("firstName");
        String lastName = response.jsonPath().getString("lastName");
        String userEmail = response.jsonPath().getString("email");

        List<Map<String, String>> addresses = response.jsonPath().getList("addresses");
        String addressId1 = addresses.get(0).get("addressId");
        String addressId2 = addresses.get(1).get("addressId");

        assertNotNull(userPublicId);
        assertNotNull(firstName);
        assertNotNull(lastName);
        assertNotNull(userEmail);

        assertEquals(EMAIL_ADDRESS, userEmail);
        assertEquals(2, addresses.size());
        assertEquals(30, addressId1.length());
        assertEquals(30, addressId2.length());
    }

}
