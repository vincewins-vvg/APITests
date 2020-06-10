package restassured.tests;

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

public class covidDataScenario {
	
//	    Endpoint : https://developer.bestbuy.com/
//		Automation Steps:
//		1.Find the store name, address and distance near to postal code 02864 for product 
//		2. Find all the canon products of price range between $1000-$1500
//		3. Get the regular and selling price for iPhone11 Pro
//		4. Find the stores having store pick-up availability of iPhone 11 Pro in stores in RI region 
	
	
	@Test
	public void covidData()
	{
		RestAssured.baseURI="https://developer.bestbuy.com/";
		
		Response res = RestAssured.given().log().all()
		.contentType(ContentType.JSON)
		.accept(ContentType.JSON)
		.when()
		.get("https://covid-19.dataflowkit.com/v1")
		.then()
		.assertThat()
		.statusCode(200).extract().response();
		
		//verify the Content Type = json
		assertEquals(res.contentType(),ContentType.JSON);
		
		long resTimeMilSecs = res.getTimeIn(TimeUnit.MILLISECONDS);
		
		//Verify the Response Time < 600 ms
	    SoftAssert sa = new SoftAssert();
	    sa.assertTrue(resTimeMilSecs<=600, "Response time is greater than 600ms");
	 
		JsonPath json = res.jsonPath();
		
		List<String> countries = json.getList("Country_text");
		List<String> activeCases = json.getList("\"Active Cases_text\"");
		List<String> totalDeath = json.getList("\"Total Deaths_text\"");
		List<String> totalRecovered = json.getList("\"Total Recovered_text\"");
		List<String> lastUpdated = json.getList("\"Last Update\"");
		
		List<String> newCases = json.getList("\"New Cases_text\"");
		
		System.out.println(newCases);
		System.out.println(newCases.size());
		int noOfCountries = countries.size();
		System.out.println(noOfCountries);
		
		
		List<Integer> newCasesInt = new ArrayList<Integer>();
		for (int j = 0; j < newCases.size()-1; j++) {		
		{		
			if(newCases.get(j)!= "")
			{
				
		  newCasesInt.add(Integer.parseInt(newCases.get(j).replaceAll("[^a-zA-Z0-9]", "")));		 
				
		}			
		}
		}
		
		
	    Collections.sort(newCasesInt);
	    Collections.reverse(newCasesInt);
	    
	    
		
		TreeMap<String,Integer> params = new TreeMap<String,Integer>();		
		
		for (int i = 0; i <noOfCountries-1; i++) {
			
			while (countries.get(i).equals("India")) {
				
				System.out.println("Country-----> India");
				System.out.println("Active cases-----> "+activeCases.get(i));
				System.out.println("Total death-----> "+totalDeath.get(i));
				System.out.println("New cases-----> "+newCases.get(i));
				break;
			}
			
			if(newCases.get(i).toString().contains("N/A")!=true && newCases.get(i).toString().length()>1) {
			params.put(countries.get(i),Integer.parseUnsignedInt(newCases.get(i).toString().trim().replaceAll("\\D", "")));
					
			}			
						
			}
	    
	   
		System.out.println("Countries with top 3 highest new cases--->");
		for (Entry<String, Integer> entry : params.entrySet()) {
			
			
			
			if (entry.getValue().equals(newCasesInt.get(1))) {
			System.out.println("Country with highest new cases");
			System.out.println("********No 1****************");
		    System.out.println("Country : "+entry.getKey());
			System.out.println("New cases : "+entry.getValue());
				
			} 
			
			else if(entry.getValue().equals(newCasesInt.get(2)))
			{
				
				System.out.println("Country with highest new cases");
				System.out.println("********No 2****************");
			    System.out.println("Country : "+entry.getKey());
				System.out.println("New cases : "+entry.getValue());
			}
			
			else if(entry.getValue().equals(newCasesInt.get(3)))
			{
				
				System.out.println("Country with highest new cases");
				System.out.println("********No 3****************");
			    System.out.println("Country : "+entry.getKey());
				System.out.println("New cases : "+entry.getValue());
			}			
					
			}
			
		}
	

}
	
	
	
	
	
	
