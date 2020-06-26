package restassured.tests;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import org.testng.asserts.*;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import utils.UniqueValueGenerator;

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

public class TomtomProjectCreation extends UniqueValueGenerator {
	
//	    https://developer.tomtom.com/user/login?destination=user/me/apps
//		Scenario1: 
//		1. Register admin key
//		2. Add new project using the registered admin key
//		3. List projects and confirm the new project is available
//		Scenario2:
//		1. Regenerate admin key
//		2. Add new fence using the regenerated admin key
//		3. Get fences transitions and check for the fence that is newly created

	
	public static String apiKey="7GA32KbScpakcEgrgtSHfECNF2AppxJi";
	public static String adminKey=null;
	public static String projectId=null;
	public static String fenceId=null;
	
	@DataProvider(name="Files")
	public String[] getFiles()
	{
		String[] jsonPayload = new String[1];
		jsonPayload[0]="createFencePayload.json";
		
		return jsonPayload;
	}
	
	
	@BeforeMethod
	public void generateAdminKey()
	{
		
		RestAssured.baseURI="https://api.tomtom.com/";
		Response res = RestAssured.given().log().all()
				
	    .contentType(ContentType.JSON)
	    .queryParam("key", "7GA32KbScpakcEgrgtSHfECNF2AppxJi")
	    .body("{\r\n  \"secret\": \"My very secret secret\"\r\n}")
	    .when()
	    .post("geofencing/1/regenerateKey")
	    .then()
	    .assertThat()
	    .statusCode(200).extract().response();
		
		JsonPath json = res.jsonPath();
		
		adminKey = json.get("adminKey").toString();
		System.out.println("Admin key is "+adminKey);
	}
	
	//(dependsOnMethods= {"restassured.tests.UIBank.loginToUIBank"})
	
	@Test(priority=1)
	public void addNewProject()
	{
		
		String randomValue = uniqueValue("3", "string");
		System.out.println("random value "+randomValue);
		
	   	Response res = RestAssured.given().log().all()		
	    .contentType(ContentType.JSON)
	    .queryParam("key", apiKey )
	    .queryParam("adminKey", adminKey)
	    .body("{\r\n  \"name\": \"Airports in Delhi "+randomValue+"\"\r\n}")
	    .when()  
	    .post("geofencing/1/projects/project")
	    .then()
	    .assertThat()
	    .statusCode(201).extract().response();
	   	
		JsonPath json = res.jsonPath();
		
		projectId = json.get("id").toString();
		System.out.println("Project id is "+projectId);
	   	
	}
	
	@Test(dependsOnMethods={"restassured.tests.TomtomProjectCreation.addNewProject"})
	public void addNewFenceToProject()
	{
		
		String jsonInput = "{\r\n  \"name\": \"No-fly zone 26\",\r\n  \"type\": \"Feature\",\r\n  \"geometry\": {\r\n    \"radius\": 75,\r\n    \"type\": \"Point\",\r\n    \"shapeType\": \"Circle\",\r\n    \"coordinates\": [-67.137343, 45.137451]\r\n  },\r\n  \"properties\": {\r\n    \"maxSpeedKmh\": 70\r\n  }\r\n}";
		
	   	Response res = RestAssured.given().log().all()		
	    .contentType(ContentType.JSON)
	    .queryParam("key", apiKey )
	    .queryParam("adminKey", adminKey)
	    .pathParam("projectId", projectId)
	    .body(jsonInput)
	    .when()  
	    .post("geofencing/1/projects/{projectId}/fence")
	    .then()
	    .assertThat()
	    .statusCode(201).extract().response();
	   	
		JsonPath json = res.jsonPath();
		
		fenceId = json.get("id").toString();
		System.out.println("Fence id is "+projectId);
	   	
	}
	
	@Test(dependsOnMethods={"restassured.tests.TomtomProjectCreation.addNewFenceToProject"})
	public void getFenceTransitions()
	{
		
	   	Response res = RestAssured.given().log().all()		
	    .contentType(ContentType.JSON)
	    .queryParam("key", apiKey )
	    .queryParam("adminKey", adminKey)
	    .queryParam("from", "2019-08-29T01:00:00")
	    .queryParam("to", "2019-08-29T23:00:00")
	    .pathParam("fenceId", fenceId)
	    .when()  
	    .get("geofencing/1/transitions/fences/{fenceId}")
	    .then()
	    .assertThat()
	    .statusCode(200).extract().response();
	   	
	}
}