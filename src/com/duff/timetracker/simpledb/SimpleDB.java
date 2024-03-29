/*
 * Copyright 2010-2012 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.duff.timetracker.simpledb;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//import com.amazon.aws.demo.AWSAndroidDemo;
//import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.CreateDomainRequest;
import com.amazonaws.services.simpledb.model.DeleteAttributesRequest;
import com.amazonaws.services.simpledb.model.DeleteDomainRequest;
import com.amazonaws.services.simpledb.model.GetAttributesRequest;
import com.amazonaws.services.simpledb.model.GetAttributesResult;
import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.ListDomainsRequest;
import com.amazonaws.services.simpledb.model.ListDomainsResult;
import com.amazonaws.services.simpledb.model.PutAttributesRequest;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.SelectRequest;

public class SimpleDB {

	public static String DOMAIN_NAME = "TimeTracker";
	public static String USER_ATTRIBUTE_NAME = "User";
	public static String DATE_ATTRIBUTE_NAME = "Date";
	public static String PROJECT_ATTRIBUTE_NAME = "Project";
	public static String TASK_ATTRIBUTE_NAME = "Task";
	public static String HOURS_ATTRIBUTE_NAME = "Hours";
	public static String NOTES_ATTRIBUTE_NAME = "Notes";

	private static AmazonSimpleDBClient mSdbClient;

	private static AmazonSimpleDBClient sdb = null;
	private static String nextToken = null;
	private static int prevNumDomains = 0;

	public static AmazonSimpleDBClient getInstance() {
		if (mSdbClient == null) {
			if (!PropertyLoader.getInstance().hasCredentials()) {
				throw new RuntimeException("Cant find credentials!!!!!");
			}
				AWSCredentials credentials = new BasicAWSCredentials(PropertyLoader.getInstance().getAccessKey(), PropertyLoader.getInstance().getSecretKey());
			mSdbClient = new AmazonSimpleDBClient(credentials);
		}

        return mSdbClient;
	}
	
	public static List<String> getDomainNames() {
		return getInstance().listDomains().getDomainNames();
	}
	
	public static List<String> getDomainNames(int numDomains) {
		prevNumDomains = numDomains;
		return getDomainNames(numDomains, null);
	}
	
	private static List<String> getDomainNames(int numDomains, String nextToken) {
		ListDomainsRequest req = new ListDomainsRequest();
		req.setMaxNumberOfDomains(numDomains);
		if(nextToken != null)
			req.setNextToken(nextToken);
		ListDomainsResult result = getInstance().listDomains(req);
		List domains = result.getDomainNames();
		SimpleDB.nextToken = result.getNextToken(); 
		return domains;
	}
	
	public static List<String> getMoreDomainNames() {
		if(nextToken == null) {
			return new ArrayList<String>();
		} else {
			return getDomainNames(prevNumDomains, nextToken);
		}

	}
	
	public static void createDomain( String domainName ) {
		getInstance().createDomain( new CreateDomainRequest( domainName ) );
	}
		
	public static void deleteDomain( String domainName ) {
		getInstance().deleteDomain( new DeleteDomainRequest( domainName ) );
	}

	public static void createItem( String domainName, String itemName ) {
		List<ReplaceableAttribute> attributes = new ArrayList<ReplaceableAttribute>(1);
		attributes.add( new ReplaceableAttribute().withName( "Name").withValue( "Value") );
		getInstance().putAttributes( new PutAttributesRequest( domainName, itemName, attributes ) );
	}

	public static void createAttributeForItem( String domainName, String itemName, String attributeName, String attributeValue ) {
		List<ReplaceableAttribute> attributes = new ArrayList<ReplaceableAttribute>(1);
		attributes.add( new ReplaceableAttribute().withName( attributeName ).withValue( attributeValue ).withReplace( true ) );
		getInstance().putAttributes( new PutAttributesRequest( domainName, itemName, attributes ) );
	}

	public static String[] getItemNamesForDomain( String domainName ) {
		SelectRequest selectRequest = new SelectRequest( "select itemName() from `" + domainName + "`" ).withConsistentRead( true );
		List items = getInstance().select( selectRequest ).getItems();	
		
		String[] itemNames = new String[ items.size() ];
		for ( int i = 0; i < items.size(); i++ ) {
			itemNames[ i ] = ((Item)items.get( i )).getName();
		}
		
		return itemNames;
	}

	public static List<Item> getItemNamesForDomainFromUser(String domainName, String userName) {
		SelectRequest selectRequest = new SelectRequest( "select * from " + domainName + " where " + USER_ATTRIBUTE_NAME + " = '" + userName + "' intersection " + DATE_ATTRIBUTE_NAME + " is not null order by " + DATE_ATTRIBUTE_NAME + " desc" ).withConsistentRead( true );
		return getInstance().select( selectRequest ).getItems();
	}


	public static HashMap<String,String> getAttributesForItem( String domainName, String itemName ) {
		GetAttributesRequest getRequest = new GetAttributesRequest( domainName, itemName ).withConsistentRead( true );
		GetAttributesResult getResult = getInstance().getAttributes( getRequest );	
		
		HashMap<String,String> attributes = new HashMap<String,String>(30);
		for ( Object attribute : getResult.getAttributes() ) {
			String name = ((Attribute)attribute).getName();
			String value = ((Attribute)attribute).getValue();
			
			attributes.put(  name, value );
		}

		return attributes;
	}
	
	public static void updateAttributesForItem( String domainName, String itemName, HashMap<String,String> attributes ) {
		List<ReplaceableAttribute> replaceableAttributes = new ArrayList<ReplaceableAttribute>( attributes.size() ); 
		
		for ( String attributeName : attributes.keySet() ) {
			replaceableAttributes.add( new ReplaceableAttribute().withName( attributeName ).withValue( attributes.get( attributeName ) ).withReplace( true ) );
		}

		getInstance().putAttributes( new PutAttributesRequest( domainName, itemName, replaceableAttributes ) );
	}

	public static void deleteItem( String domainName, String itemName ) {
		getInstance().deleteAttributes( new DeleteAttributesRequest( domainName, itemName ) );
	}
	
	public static void deleteItemAttribute( String domainName, String itemName, String attributeName ) {
		getInstance().deleteAttributes(  new DeleteAttributesRequest( domainName, itemName ).withAttributes( new Attribute[] { new Attribute().withName( attributeName ) } ) );
	}
	
}
