/*
	Created by Danchen, Muneeb & Jeffery
	Date created: 16/11/2015 19:59 P.M EST
	Last modified by 16/11/2015 11:41 P.M EST
	Purpose: JDBC for CS1555 term project milestone 1
    
    #####################################################################################
    To configure environment:
    Set the PATH and CLASSPATH environmental variables 
    to point to JAVA and Oracle JDBC library: source ~panos/1555/bash.env
    
    #####################################################################################
    Useful sql commands:
    check what tables you have in the database: select table_name from user_tables;
*/


import java.sql.*;  
import java.util.Random;
import java.lang.StringBuilder;
import java.text.ParseException;

public class dataGenerator
{	
	protected int numOfWarehouses;
	protected int numOfDistributionStation;
	protected int numOfCustomers;

	protected warehouses[] myWarehouse;

	protected final String LEXICON = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	protected final String[] US_CITIES = {"Montgomery","Juneau","Phoenix","Little Rock",
		"Sacramento","Denver","Hartford","Dover","Tallahassee","Atlanta","Honolulu","Boise","Springfield",
		"Indianapolis","Des Moines","Topeka","Frankfort","Baton Rouge","Augusta","Annapolis","Boston",
		"Lansing","St. Paul","Jackson","Jefferson City","Helena","Lincoln","Carson City","Concord",
		"Trenton","Santa Fe","Albany","Raleigh","Bismarck","Columbus","Oklahoma City","Salem","Harrisburg",
		"Providence","Columbia","Pierre","Nashville","Austin","Salt Lake City","Montpelier","Richmond",
		"Olympia","Charleston","Madison","Cheyenne"};
	
	protected final String[] US_STATES = {"AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DC", "DE", "FL", "GA", 
          "HI", "ID", "IL", "IN", "IA", "KS", "KY", "LA", "ME", "MD", 
          "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ", 
          "NM", "NY", "NC", "ND", "OH", "OK", "OR", "PA", "RI", "SC", 
          "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY"};

    protected final String [] STREET_POSTFIX = {"St.", "Ave.", "Rd", "Blvd", "Dr", "Way", "Ln"};


    protected final int LENGTH_OF_LEXICON = LEXICON.length();
    protected final int LENGTH_OF_US_CITIES = US_CITIES.length;
    protected final int LENGTH_OF_US_STATES = US_STATES.length;
    protected final int LENGTH_OF_STREET_POSTFIX = STREET_POSTFIX.length;


    protected final int MIN_NAME_LENGTH = 3;
    protected final int MAX_NAME_LENGTH = 10;

    protected final int MIN_STREET_NUMBER = 1;
    protected final int MAX_STREET_NUMBER = 9999;

    protected final double MAX_SALES_TAX = 100;
    protected final double MAX_SALES_SUM = 1000000;//A million

    protected final int ZIPCODE_LENGTH = 5;


	//Override default constructor
	protected dataGenerator()
	{
		this.numOfWarehouses = 0;
		this.numOfDistributionStation = 0;
		this.numOfCustomers = 0;
	}


	protected void createWarehouseData()
    {   
        //System.out.println("numOfWarehouses: " + numOfWarehouses);
    	myWarehouse = new warehouses[numOfWarehouses];

    	int lengthOfLexicon = LEXICON.length();
    	int numOfCities = US_CITIES.length;
    	int numOfStates = US_STATES.length;

    	for(int i = 0;i < numOfWarehouses;i++)
    	{   
            warehouses tempWarehouse = new warehouses();

            myWarehouse[i] = tempWarehouse;
    		myWarehouse[i].warehouseID = i + 1;
    		myWarehouse[i].name = generateRandomWarehouseOrDistStationName();
    		myWarehouse[i].strAddress = generateRandomStrAddress();
    		myWarehouse[i].cityAddress = US_CITIES[i % numOfCities];
    		myWarehouse[i].stateAddress = US_STATES[i % numOfStates];
    		myWarehouse[i].zipcode = generateRandomZipcode();
    		myWarehouse[i].salesTax = generateRandomSalesTax();
    		myWarehouse[i].salesSum = generateRandomSalesSum();
    	}
    }

    protected String generateRandomWarehouseOrDistStationName()
    {	
    	StringBuilder strBuilder = new StringBuilder();
    	
    	int maxNameLength = generateRandomNumberWithinRange(MIN_NAME_LENGTH, MAX_NAME_LENGTH);
    	for(int i = 0; i < maxNameLength;i++)
    	{	
    		int randomIndex = generateRandomNumberWithinRange(0, LENGTH_OF_LEXICON - 1);
    		strBuilder.append(LEXICON.charAt(randomIndex));
    	}

    	String result = strBuilder.toString();
    	return result;
    }

    protected String generateRandomStrAddress()
    {	
    	StringBuilder strBuilder = new StringBuilder();

    	int maxStreetLength = generateRandomNumberWithinRange(MIN_NAME_LENGTH, MAX_NAME_LENGTH);
    	for(int i = 0; i < maxStreetLength;i++)
    	{	
    		int randomIndex = generateRandomNumberWithinRange(0, LENGTH_OF_LEXICON - 1);
    		strBuilder.append(LEXICON.charAt(randomIndex));
    	}

    	int randomStreetNumber = generateRandomNumberWithinRange(MIN_STREET_NUMBER, MAX_STREET_NUMBER);
    	strBuilder.append(" ");
    	strBuilder.append(randomStreetNumber); //Implicitly convert int to string

    	int randomIndex = generateRandomNumberWithinRange(0, LENGTH_OF_STREET_POSTFIX - 1);
    	strBuilder.append(" ");
    	strBuilder.append(STREET_POSTFIX[randomIndex]);

    	String result = strBuilder.toString();
    	return result;
    }

    protected String generateRandomZipcode()
    {	
    	StringBuilder strBuilder = new StringBuilder();

    	for(int i = 0; i < ZIPCODE_LENGTH; i++)
    	{	
    		int randomNumber = generateRandomNumberWithinRange(0, 9);
    		strBuilder.append(randomNumber);
    	}

    	String result = strBuilder.toString();
    	return result;
    }

    protected double generateRandomSalesTax()
    {
    	double result = generateRandomNumberWithinRangeInDouble(MAX_SALES_TAX);
    	return result;
    }

    protected double generateRandomSalesSum()
    {
    	double result = generateRandomNumberWithinRangeInDouble(MAX_SALES_SUM);
    	return result;
    }

    protected String generateRandomCustomerName()
    {
    	String result = " a";
    	return result;
    }

    protected String generateRandomItemName()
    {
    	String result = " a";
    	return result;
    }

    protected double generateRandomPrice()
    {
    	double result = 1;
    	return result;
    }

    protected long generateRandomDate()
    {
    	long result = 12123;
    	return result;
    }

    protected long getRandomTimeBetweenTwoDates () {
    	
    	long beginTime = Timestamp.valueOf("2000-01-01").getTime();
    	long endTime = Timestamp.valueOf("2015-11-18").getTime();
    	
    	long diff = endTime - beginTime + 1;

    	long result = beginTime + (long) (Math.random() * diff);
    	return result;
	}

	//Inclusive - both low and high
	protected int generateRandomNumberWithinRange(int low, int high)
	{
		Random rGenerator = new Random();
		int result = rGenerator.nextInt(high - low + 1) + low;
		return result;
	}

	protected double generateRandomNumberWithinRangeInDouble(double high)
	{
		Random rGenerator = new Random();
		double result = rGenerator.nextDouble()*high;
		return result;
	}

}