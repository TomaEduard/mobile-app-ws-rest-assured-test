package com.app.mobileappwsrestassuretest;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

public class TestCreateUser {

    private final String CONTEXT_PATH = "/mobile-app-ws";

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
    }

    @Test
    final void testCreateUser() {

        List<Map<String, Object>> userAddresses = new ArrayList<>();

        Map<String, Object> shippingAddress = new HashMap<>();
        shippingAddress.put("city", "Alba-Iulia");
        shippingAddress.put("country", "Romania");
        shippingAddress.put("streetName", "Viilor, nr.22");
        shippingAddress.put("postalCode", "123456");
        shippingAddress.put("type", "shipping");

        Map<String, Object> billingAddress = new HashMap<>();
        billingAddress.put("city", "Alba-Iulia");
        billingAddress.put("country", "Romania");
        billingAddress.put("streetName", "Viilor, nr.22");
        billingAddress.put("postalCode", "123456");
        billingAddress.put("type", "billing");

        userAddresses.add(shippingAddress);
        userAddresses.add(billingAddress);

        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("firstName", "Eduard");
        userDetails.put("lastName", "Toma");
        userDetails.put("email", "eduard.daniel.toma@gmail.com");
        userDetails.put("password", "123");
        userDetails.put("addresses", userAddresses);

        Response response = given()
                .contentType("application/json")
                .accept("application/json")
                .body(userDetails)
                .when()
                .post(CONTEXT_PATH + "/users")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .extract()
                .response();

        String userId = response.jsonPath().getString("userId");
        assertNotNull(userId);
        assertEquals(30, userId.length());
//        assertTrue(userId.length() == 30);

        // take the response body
        String bodyString = response.body().asString();
        try {
            // transform response body into a JSON Object
            JSONObject responseBodyJson = new JSONObject(bodyString);
            // take the addresses key and save separately for assured testing
            JSONArray addresses = responseBodyJson.getJSONArray("addresses");
            // validate
            assertNotNull(addresses);
            assertEquals(2, addresses.length());

            // select and save into a local variable the addressId from the first position of addresses[0]
            String addressId = addresses.getJSONObject(0).getString("addressId");
            assertNotNull(addressId);
            assertEquals(30, addressId.length());

        } catch (JSONException e) {
            fail(e.getMessage());
        }

    }

}
