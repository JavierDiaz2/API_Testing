package com.perfdog.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.util.UUID;
import java.util.Random;

import static io.restassured.RestAssured.given;

public class AppTest {

    public String generateRandomUsername() {
        return "user_" + UUID.randomUUID().toString().substring(0, 8);
    }

    public Response createUser(String username, String password) {
        RestAssured.baseURI = "https://petstore.swagger.io/v2";

        String requestBody = "{ \"id\": 1, \"username\": \"" + username + "\", \"firstName\": \"Test\","
                + " \"lastName\": \"User\", \"email\": \"test@example.com\","
                + " \"password\": \"" + password + "\", \"phone\": \"123456789\", \"userStatus\": 1 }";

        return given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/user");
    }

    @Test
    public void createUserTest() {
        String username = generateRandomUsername();
        String password = "password123";

        Response response = createUser(username, password);

        int statusCode = response.getStatusCode();
        System.out.println("Create User Response: " + response.getBody().asString());

        Assert.assertEquals(statusCode, 200, "Verify that the user was created successfully.");
    }

    @Test
    public void loginUserTest() {
        String username = generateRandomUsername();
        String password = "password123";

        createUser(username, password);

        Response response = given()
                .queryParam("username", username)
                .queryParam("password", password)
                .when()
                .get("/user/login");

        int statusCode = response.getStatusCode();
        String responseBody = response.getBody().asString();

        System.out.println("Login Response: " + responseBody);

        Assert.assertEquals(statusCode, 200, "Verify that the user can login successfully.");
        Assert.assertTrue(responseBody.contains("message"), "Response should contain a message.");
    }

    @Test
    public void listAvailablePetsTest() {
        RestAssured.baseURI = "https://petstore.swagger.io/v2";

        Response response = given()
                .queryParam("status", "available")
                .when()
                .get("/pet/findByStatus");

        int statusCode = response.getStatusCode();
        String responseBody = response.getBody().asString();

        System.out.println("List Pets Response: " + responseBody);

        Assert.assertEquals(statusCode, 200, "Verify that the request is successful.");
        Assert.assertTrue(responseBody.startsWith("["), "Response should be a JSON array.");
    }

    @Test
    public void getPetDetailsTest() {
        RestAssured.baseURI = "https://petstore.swagger.io/v2";

        int randomPetId = new Random().nextInt(10) + 1;

        Response response = given()
                .when()
                .get("/pet/" + randomPetId);

        int statusCode = response.getStatusCode();
        String responseBody = response.getBody().asString();

        System.out.println("Pet Details Response: " + responseBody);

        if (statusCode == 200) {
            Assert.assertTrue(responseBody.contains("\"id\":" + randomPetId), "Response should contain the correct pet ID.");
        } else {
            System.out.println("Pet ID " + randomPetId + " not found.");
            Assert.assertEquals(statusCode, 404, "Verify that the pet does not exist.");
        }
    }

    @Test
    public void createOrderTest() {
        RestAssured.baseURI = "https://petstore.swagger.io/v2";

        int petId = 1;
        int orderId = 2212;

        String requestBody = "{ \"id\": " + orderId + ", \"petId\": " + petId + ", \"quantity\": 1,"
                + " \"shipDate\": \"2025-03-10T12:00:00.000Z\", \"status\": \"placed\", \"complete\": true }";

        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/store/order");

        int statusCode = response.getStatusCode();
        String responseBody = response.getBody().asString();

        System.out.println("Create Order Response: " + responseBody);

        Assert.assertEquals(statusCode, 200, "Verify that the order was created successfully.");
        Assert.assertTrue(responseBody.contains("\"id\":" + orderId), "Response should contain the order ID.");
    }

    @Test
    public void logoutUserTest() {
        RestAssured.baseURI = "https://petstore.swagger.io/v2";

        Response response = given()
                .when()
                .get("/user/logout");

        int statusCode = response.getStatusCode();
        String responseBody = response.getBody().asString();

        System.out.println("Logout Response: " + responseBody);

        Assert.assertEquals(statusCode, 200, "Verify that the user can make a Logout.");
        Assert.assertTrue(responseBody.contains("message"), "Response should contain a message.");
    }
}
