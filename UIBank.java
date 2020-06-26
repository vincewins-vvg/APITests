package restassured.tests;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import org.testng.asserts.*;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.hamcrest.CoreMatchers;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

public class UIBank {
	
//	 Test # 1 
//	 1) Register for an account
//	 2) Login
//	 3) Get Account Details
//	 Test # 2
//	 1) Login
//	 2) Apply Login
//	 3) Verify Loan Status
	
	public static String username="vincewins";
	public static String password="vincewins";
	public static String loanId=null;
	
	
	@Test
	public void loginToUIBank()
	{
		
		RestAssured.baseURI="https://uibank-api.azurewebsites.net/api";
		Response res = RestAssured.given().log().all()
		
	    .contentType(ContentType.JSON)
	    .body("{\"username\":\"vincewins\",\"password\":\"vincewins\"}")
	    .when()
	    .post("users/login")
	    .then()
	    .assertThat()
	    .statusCode(200).extract().response();
	}
	
	@Test(dependsOnMethods= {"restassured.tests.UIBank.loginToUIBank"})
	public void getAccountDetails()
	{
		RestAssured.baseURI="https://uibank-api.azurewebsites.net/api";
		
		Response res = RestAssured.given().log().all()
		
	    .contentType(ContentType.JSON)
	    .header("Authorization","eo0MxBdHeXAfA7FlYxg4JYt7dXWJNCgUDK5Hjd0co1OK11BnQvHstjuDHWUNakan")
	    .queryParam("filter[where][accountId]", "5ef1f743fff4370065ec3957")
	    .when()
	    .get("transactions")
	    .then()
	    .assertThat()
	    .statusCode(200).extract().response();
	}
	
	@Test(dependsOnMethods= {"restassured.tests.UIBank.loginToUIBank"})
	public void createLoan()
	{
		RestAssured.baseURI="https://uibank-api.azurewebsites.net/api";
		
		Response res = RestAssured.given().log().all()
		
	    .contentType(ContentType.JSON)
	    .body("{\"email\":\"vincewins@gmail.com\",\"amount\":12000,\"term\":3,\"income\":100000,\"age\":32}")
	    .when()
	    .post("quotes/newquote")
	    .then()
	    .assertThat()
	    .statusCode(200).extract().response();
		
		JsonPath json = res.jsonPath();
		loanId = json.get("quoteid").toString();
		System.out.println("Loan Id is: "+loanId);
	}
	
	@Test(dependsOnMethods= {"restassured.tests.UIBank.loginToUIBank","restassured.tests.UIBank.createLoan"})
	public void getLoanStatus()
	{
		RestAssured.baseURI="https://uibank-api.azurewebsites.net/api";
		
		Response res = RestAssured.given().log().all()
		
	    .contentType(ContentType.JSON)
	    
	    .when()
	    .get("quotes/"+loanId)
	    .then()
	    .assertThat()
	    .body("id", equalTo(loanId))
	    .statusCode(200).extract().response();
		System.out.println(res.prettyPrint());
	}
}