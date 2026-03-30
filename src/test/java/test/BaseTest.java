package test;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.BeforeClass;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import utils.Config;

public class BaseTest {

    protected static Map<String, String> cookies;

    @BeforeClass
    public void login() {
        RestAssured.baseURI = Config.BASE_URL;
        generateSession();
    }

    // ✅ Login & store cookies
    public static void generateSession() {

        Map<String, String> loginPayload = new HashMap<>();
        loginPayload.put("email", "admin@iykonect.com");
        loginPayload.put("password", "Admin@1234");

        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(loginPayload)
                .post(Config.LOGIN_ENDPOINT);

        cookies = response.getCookies();

        System.out.println("Session Cookies: " + cookies);
    }

    // 🔥 AUTO REFRESH (for expired session)
    public static Response sendRequest(String method, String endpoint, Object body) {

        Response response;

        if (method.equalsIgnoreCase("GET")) {
            response = RestAssured.given()
                    .cookies(cookies)
                    .get(endpoint);

        } else {
            response = RestAssured.given()
                    .cookies(cookies)
                    .contentType(ContentType.JSON)
                    .body(body)
                    .post(endpoint);
        }

        // 🔁 If session expired → re-login
        if (response.getStatusCode() == 401) {
            System.out.println("⚠ Session expired. Logging in again...");

            generateSession();

            return sendRequest(method, endpoint, body);
        }

        return response;
    }
}