package com.app.mobileappwsrestassuretest;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
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
    private static List<Map<String, String>> addresses;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
    }

    /*
     * test UserLogin
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
     * test Get UserDetails()
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

        addresses = response.jsonPath().getList("addresses");
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

    /*
     * test update UserDetail()
     *
     */
    @Test
    final void c() {

        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("firstName", "Edd");
        userDetails.put("lastName", "Tom");
        userDetails.put("email", "edd@test.com");
        userDetails.put("password", "aaa");

        Response response = given()
                .contentType(JSON)
                .accept(JSON)
                .header("Authorization",authorizationHeader)
                .pathParam("id", userId)
                .body(userDetails)
                .when()
                .put(CONTEXT_PATH + "/users/{id}")
                .then()
                .statusCode(200)
                .contentType(JSON)
                .extract()
                .response();

        String firstName = response.jsonPath().getString("firstName");
        String lastName = response.jsonPath().getString("lastName");
        String email = response.jsonPath().getString("email");

        List<Map<String, String>> storedAddresses = response.jsonPath().getList("addresses");

        assertEquals("Edd", firstName);
        assertEquals("Tom", lastName);
        assertEquals("edd@test.com", email);
        assertNotNull(storedAddresses);
        assertEquals(addresses.size(), storedAddresses.size());
        assertEquals(addresses.get(0).get("streetName"), storedAddresses.get(0).get("streetName"));

    }

    /*
     * test delete UserDetail()
     *
     */
    @Ignore
    @Test
    final void d() {

        Response response = given()
                .contentType(JSON)
                .accept(JSON)
                .header("Authorization",authorizationHeader)
                .pathParam("id", userId)
                .when()
                .delete(CONTEXT_PATH + "/users/{id}")
                .then()
                .statusCode(200)
                .contentType(JSON)
                .extract()
                .response();

        String operationResult = response.jsonPath().getString("operationResult");
        assertEquals("SUCCESS", operationResult);
    }

}
