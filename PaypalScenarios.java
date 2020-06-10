package restassured.tests;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import org.testng.asserts.*;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import static org.hamcrest.Matchers.containsString;
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

public class PaypalScenarios {
	
//	#1) Create a new product with hard coded value in the body [PostMan]
//			a) Verify the status code
//			b) Verify the response contains category and type as expected
//
//	#2) Create multiple products [using dataprovider + RestAssured]
//
//	#3) Verify that the created products are listed
	
	public static String prodId;
	
	@DataProvider(name="file")
	public String[] productPayload()
	{
		
		String[] createProd = new String[2];
		createProd[0] = "createProduct.json";
		createProd[1] = "createProduct2.json";
		
		return createProd;
	}
	
	
	@Test(dataProvider="file")
	public void createProduct(String fileName)
	{
		
		File filesFromDP = new File(fileName);
	    RestAssured.baseURI="https://api.sandbox.paypal.com/v1";
		
		Response res = RestAssured.given().log().all()
		.header("Authorization", "Bearer A21AAHap7zXjjRwQxDX-y1uncLn2K0JcSKdp__VQ5BN9tEUmkoE-uEpM6fEOvXfgFG3u3AoroHI7PbtpPekM1pHWkn1d-naDA")
		.contentType(ContentType.JSON)
		.accept(ContentType.JSON)
		.body(filesFromDP)
		.when()
		.post("catalogs/products")
		.then()
		.assertThat()
		.statusCode(201).extract().response();
		
		prodId = res.jsonPath().get("id").toString(); 
		System.out.println(prodId);
	}
	
	@Test(dependsOnMethods="createProduct")
	public void getCreatedProduct()
	{
		RestAssured.baseURI="https://api.sandbox.paypal.com/v1";
		
		Response res =
	    RestAssured.given().log().all()
	    .contentType(ContentType.JSON)
	    .header("Authorization", "Bearer A21AAHap7zXjjRwQxDX-y1uncLn2K0JcSKdp__VQ5BN9tEUmkoE-uEpM6fEOvXfgFG3u3AoroHI7PbtpPekM1pHWkn1d-naDA")
	    .when()
	    .get("catalogs/products/"+prodId)
	    .then().assertThat().statusCode(200).extract().response();
		
		JsonPath json = res.jsonPath();
		
		String categoryValue = json.get("category").toString();
		String typeValue = json.get("type").toString();
		
		assertEquals(categoryValue,"SOFTWARE");
		assertEquals(typeValue,"SERVICE");
	
	}
}
	
	
	
	
	
	
