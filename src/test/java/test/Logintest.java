package test;

import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import utils.Config;

public class Logintest {
	 @Test
	    public void testLoginAPI() {
	        RestAssured.baseURI = Config.BASE_URL;

	        Map<String, String> loginPayload = new HashMap<>();
	        loginPayload.put("email", "admin@iykonect.com");
	        loginPayload.put("password", "Admin@1234");

	        Response response = RestAssured
	                .given()
	                .contentType(ContentType.JSON)
	                .body(loginPayload)
	                .post(Config.LOGIN_ENDPOINT);

	        Assert.assertEquals(response.getStatusCode(), 200);
	        Assert.assertNotNull(response.jsonPath().getString("token"));
	    }
}
