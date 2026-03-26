package test;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.BeforeClass;

import io.restassured.RestAssured;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import utils.Config;

public class BaseTest {
	protected static String token;

    @BeforeClass
    public void login() {
        RestAssured.baseURI = Config.BASE_URL;

        Map<String, String> loginPayload = new HashMap<>();
        loginPayload.put("email", "admin@iykonect.com"); // replace
        loginPayload.put("password", "Admin@1234");      // replace

        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(loginPayload)
                .post(Config.LOGIN_ENDPOINT);

        token = response.jsonPath().getString("token"); // store token for other tests

        System.out.println("Login Token: " + token);
    }

}
