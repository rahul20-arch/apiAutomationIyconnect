package test;

import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import utils.Config;

public class LogoutTest {

    private Map<String, String> doLoginAndGetCookies() {

        RestAssured.baseURI = Config.BASE_URL;

        Map<String, String> loginPayload = new HashMap<>();
        loginPayload.put("email", "admin@iykonect.com");
        loginPayload.put("password", "Admin@1234");

        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(loginPayload)
                .post(Config.LOGIN_ENDPOINT);

        response.then().log().all();

        Assert.assertEquals(response.getStatusCode(), 200, "Login failed");

        // ✅ Get cookies instead of token
        Map<String, String> cookies = response.getCookies();

        System.out.println("Cookies: " + cookies);

        Assert.assertFalse(cookies.isEmpty(), "Cookies should not be empty after login");

        return cookies;
    }

    @Test
    public void testLogoutAPI() {

        Map<String, String> cookies = doLoginAndGetCookies();

        RestAssured.baseURI = Config.BASE_URL;

        Response logoutResp = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .cookies(cookies) // ✅ pass cookies
                .post(Config.LOGOUT_ENDPOINT);

        logoutResp.then().log().all();

        int status = logoutResp.getStatusCode();
        boolean ok = (status == 200) || (status == 204);

        Assert.assertTrue(ok, 
            "Expected logout to return 200 or 204 but got: " 
            + status + " and body: " + logoutResp.asString());
     // Verify session is destroyed
        RestAssured
            .given()
                .cookies(cookies)
            .when()
                .get("/api/users") // protected API
            .then()
                .statusCode(404);
    }
}