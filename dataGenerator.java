/*
	Created by Danchen, Muneeb & Jeffery
	Date created: 16/11/2015 19:59 P.M EST
	Last modified by 18/11/2015 12:41 A.M EST
	Purpose: JDBC for CS1555 term project milestone 1
    
    #####################################################################################
    To configure environment:
    Set the PATH and CLASSPATH environmental variables 
    to point to JAVA and Oracle JDBC library: source ~panos/1555/bash.env
    
    #####################################################################################
    Useful sql commands:
    check what tables you have in the database: select table_name from user_tables;
*/


import java.sql.Date;  
import java.sql.Timestamp;
import java.util.Random;
import java.util.ArrayList;
import java.util.HashMap;
import java.text.SimpleDateFormat;
import java.lang.StringBuilder;
import java.text.ParseException;
import java.text.DecimalFormat;

public class dataGenerator
{	
	protected int numOfWarehouses;
	protected int numOfDistStations;
	protected int numOfCustomers;
    protected int numOfOrders;
    protected int numOfItems;
    protected int numOfLineItems;

	protected warehouses[] myWarehouse;
    protected distStations[] myDistributionStation;
    protected customers[] myCustomer;
    protected orders[] myOrder;
    protected items[] myItem;
    protected lineItems[] myLineItem;

	protected final String LEXICON = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	protected final String[] US_CITIES = {"Montgomery","Juneau","Phoenix","Little Rock",
		"Sacramento","Denver","Hartford","Dover","Tallahassee","Atlanta","Honolulu","Boise","Springfield",
		"Indianapolis","Des Moines","Topeka","Frankfort","Baton Rouge","Augusta","Annapolis","Boston",
		"Lansing","St. Paul","Jackson","Jefferson City","Helena","Lincoln","Carson City","Concord",
		"Trenton","Santa Fe","Albany","Raleigh","Bismarck","Columbus","Oklahoma City","Salem","Harrisburg",
		"Providence","Columbia","Pierre","Nashville","Austin","Salt Lake City","Montpelier","Richmond",
		"Olympia","Charleston","Madison","Cheyenne"};
	
	protected final String[] US_STATES = {"AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "FL", "GA", 
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

    protected final int PHONE_NUMBER_LENGTH = 10;

    protected final String BEGIN_TIME = "2000-01-01 00:00:00";
    protected final String END_TIME = "2015-11-18 00:00:00";

    protected final double MAX_DISCOUNT = 0.99;
    protected final double MAX_BALANCE = 1000000; //a million
    protected final double MAX_PAID_AMOUNT = 1000000; //a million

    protected final int MAX_PAYMENT_COUNT = 100;
    protected final int MAX_DELIVERY_COUNT = 1000; //a thousand

    protected final int MAX_COMPLETE_COUNT = 100;
    protected final int MAX_LINEITEM_COUNT = 100;

    protected final double MAX_PRICE = 10000; //ten thousand
    protected final int MAX_STOCK_AND_SOLD_AND_ORDER_COUNT = 1000; //a thousand

    protected final int MAX_QUANTITY = 1000; //a thousand
    protected final double MAX_AMOUNT_DUE = 10000; //ten thousand

    protected final static DecimalFormat decimalFormat = new DecimalFormat("#.00");

    protected HashMap<Integer, oneWarehouse> listOfWarehouses;
	
    //Override default constructor
	protected dataGenerator()
	{  
        this.listOfWarehouses = new HashMap<Integer, oneWarehouse>();
		this.numOfWarehouses = 0;
		this.numOfDistStations = 0;
		this.numOfCustomers = 0;
        this.numOfOrders = 0;
        this.numOfItems = 0;
        this.numOfLineItems = 0;
	}

	protected void createWarehouseData()
    {   
        //System.out.println("numOfWarehouses: " + numOfWarehouses);
    	myWarehouse = new warehouses[numOfWarehouses];

    	for(int i = 0;i < numOfWarehouses;i++)
    	{   
            warehouses tempWarehouse = new warehouses();
            myWarehouse[i] = tempWarehouse;
    		
            myWarehouse[i].warehouseID = i + 1;
    		myWarehouse[i].name = generateRandomName();
    		myWarehouse[i].strAddress = generateRandomStreetAddress();

            int randomIndex = generateRandomNumberWithinRange(0, LENGTH_OF_US_CITIES - 1) % LENGTH_OF_US_CITIES;
    		myWarehouse[i].cityAddress = US_CITIES[randomIndex];
    		myWarehouse[i].stateAddress = US_STATES[randomIndex];

    		myWarehouse[i].zipcode = generateRandomZipcode();

    		myWarehouse[i].salesTax = generateRandomSalesTax();
    		myWarehouse[i].salesSum = generateRandomNumberWithinRangeInDouble(MAX_SALES_SUM);

            //Update warehouse id
            listOfWarehouses.put(myWarehouse[i].warehouseID, new oneWarehouse());
    	}
    }

    protected void createDistributionStationData()
    {
        myDistributionStation = new distStations[numOfDistStations];

        for(int i = 0;i < numOfDistStations;i++)
        {
            distStations tempDistStation = new distStations();
            myDistributionStation[i] = tempDistStation;

            int tempStationID = generateRandomNumberWithinRange(1, numOfDistStations);
            int tempWarehouseID = generateRandomNumberWithinRange(1, numOfWarehouses);
            
            boolean searchComplete = false;
            while(!searchComplete)
            {   
                //Try assigning the stationID to given (warehouseID)
                //If a stationID has already been assigned to a warehouseID
                //Keep generating a new stationID that will fit

                //If not, record the stationID and mark completion
                if(listOfWarehouses.get(tempWarehouseID).hasDistStation(tempStationID))
                {   
                    tempStationID = generateRandomNumberWithinRange(1, numOfDistStations);
                }
                else
                {   

                    listOfWarehouses.get(tempWarehouseID).addStation(tempStationID);
                    searchComplete = true;
                }
            }

            myDistributionStation[i].stationID = tempStationID;
            myDistributionStation[i].warehouseID = tempWarehouseID;
            
            myDistributionStation[i].name = generateRandomName();
            myDistributionStation[i].strAddress = generateRandomStreetAddress();

            int randomIndex = generateRandomNumberWithinRange(0, LENGTH_OF_US_CITIES - 1) % LENGTH_OF_US_CITIES;
            myDistributionStation[i].cityAddress = US_CITIES[randomIndex];
            myDistributionStation[i].stateAddress = US_STATES[randomIndex];

            myDistributionStation[i].zipcode = generateRandomZipcode();
            myDistributionStation[i].salesTax = generateRandomSalesTax();
            myDistributionStation[i].salesSum = generateRandomNumberWithinRangeInDouble(MAX_SALES_SUM);

            //System.out.println("warehouseID: " + tempWarehouseID + " stationID: " + tempStationID);
        }

    }

    protected void createCustomerData()
    {
        myCustomer = new customers[numOfCustomers];
        for(int i = 0;i < numOfCustomers;i++)
        {
            customers tempCustomer = new customers();
            myCustomer[i] = tempCustomer;

            int tempCustomerID = generateRandomNumberWithinRange(1, numOfCustomers);
            int tempStationID = generateRandomNumberWithinRange(1, numOfDistStations);
            int tempWarehouseID = generateRandomNumberWithinRange(1, numOfWarehouses);
            
    
            boolean searchComplete = false;
            while(!searchComplete)
            {   
                //Try assigning the customerID to given (warehouseID, stationID)
                //Check If the stationID is assigned to given warehouseID
                //Keep generating all three IDs

                if(listOfWarehouses.get(tempWarehouseID).hasDistStation(tempStationID))
                {   
                    //Check if customerID is assigned to given stationID
                    //If yes, try generating a different customerID
                    //If not, record the customerID, mark completion
                    if(listOfWarehouses.get(tempWarehouseID).stationSet.get(tempStationID).hasCustomer(tempCustomerID))
                    {   

                        tempCustomerID = generateRandomNumberWithinRange(1, numOfCustomers);
                    } 
                    else
                    {
                        listOfWarehouses.get(tempWarehouseID).stationSet.get(tempStationID).addCustomer(tempCustomerID);
                        searchComplete = true;
                    }
                }
                else
                {
                    tempCustomerID = generateRandomNumberWithinRange(1, numOfCustomers);
                    tempStationID = generateRandomNumberWithinRange(1, numOfDistStations);
                    tempWarehouseID = generateRandomNumberWithinRange(1, numOfWarehouses);
                }
            }

            myCustomer[i].custID = tempCustomerID;
            myCustomer[i].stationID = tempStationID;
            myCustomer[i].warehouseID = tempWarehouseID;
            

            myCustomer[i].fname = generateRandomName();
            myCustomer[i].MI = String.valueOf(LEXICON.charAt(generateRandomNumberWithinRange(0, LENGTH_OF_LEXICON - 1)));
            myCustomer[i].lname = generateRandomName();
            myCustomer[i].strAddress = generateRandomStreetAddress();

            int randomIndex = generateRandomNumberWithinRange(0, LENGTH_OF_US_CITIES - 1) % LENGTH_OF_US_CITIES;
            myCustomer[i].cityAddress = US_CITIES[randomIndex];
            myCustomer[i].stateAddress = US_STATES[randomIndex];

            myCustomer[i].zipcode = generateRandomZipcode();
            myCustomer[i].phone = generateRandomPhone();
            myCustomer[i].accountOpenDate = generateRandomTimeBetweenTwoDates(BEGIN_TIME, END_TIME);
            myCustomer[i].discount = generateRandomNumberWithinRangeInDouble(MAX_DISCOUNT);
            myCustomer[i].balance = generateRandomNumberWithinRangeInDouble(MAX_BALANCE);
            myCustomer[i].paid = generateRandomNumberWithinRangeInDouble(MAX_PAID_AMOUNT);
            myCustomer[i].paymentCount = generateRandomNumberWithinRange(0, MAX_PAYMENT_COUNT);
            myCustomer[i].deliveryCount = generateRandomNumberWithinRange(0, MAX_DELIVERY_COUNT);
        }
    }

    protected void createOrderData()
    {
        myOrder = new orders[numOfOrders];
        for(int i = 0;i < numOfOrders;i++)
        {
            orders tempOrder = new orders();
            myOrder[i] = tempOrder;

            int tempOrderID = generateRandomNumberWithinRange(1, numOfOrders);
            int tempCustomerID = generateRandomNumberWithinRange(1, numOfCustomers);
            int tempStationID = generateRandomNumberWithinRange(1, numOfDistStations);
            int tempWarehouseID = generateRandomNumberWithinRange(1, numOfWarehouses);
            
            boolean searchComplete = false;
            while(!searchComplete)
            {
                //Try assigning the orderID to given (warehouseID, stationID, custID)
                //Check If the stationID is assigned to given warehouseID
                //If not, Keep generating all four IDs
                if(listOfWarehouses.get(tempWarehouseID).hasDistStation(tempStationID))
                {
                    //Check if customerID is assigned to given stationID
                    if(listOfWarehouses.get(tempWarehouseID).stationSet.get(tempStationID).hasCustomer(tempCustomerID))
                    {    
                        //check if orderID is assigned to this cutomerID
                        //If yes, keep generating a different orderID
                        //If not, record the orderID and mark completion
                        if(listOfWarehouses.get(tempWarehouseID).stationSet.get(tempStationID).customerSet.get(tempCustomerID).hasOrder(tempOrderID))
                        {
                            tempOrderID = generateRandomNumberWithinRange(1, numOfOrders);
                        }
                        else
                        {
                            listOfWarehouses.get(tempWarehouseID).stationSet.get(tempStationID).customerSet.get(tempCustomerID).addOrder(tempOrderID);  
                            searchComplete = true; 
                        }
                    }
                    else
                    {   
                        tempOrderID = generateRandomNumberWithinRange(1, numOfOrders);
                        tempCustomerID = generateRandomNumberWithinRange(1, numOfCustomers);
                        tempStationID = generateRandomNumberWithinRange(1, numOfDistStations);
                    }
                }
                else
                {
                    tempOrderID = generateRandomNumberWithinRange(1, numOfOrders);
                    tempCustomerID = generateRandomNumberWithinRange(1, numOfCustomers);
                    tempStationID = generateRandomNumberWithinRange(1, numOfDistStations);
                    tempWarehouseID = generateRandomNumberWithinRange(1, numOfWarehouses);
                }
            }

            myOrder[i].orderID = tempOrderID;
            myOrder[i].custID = tempCustomerID;
            myOrder[i].stationID = tempStationID;
            myOrder[i].warehouseID = tempWarehouseID;

            myOrder[i].orderPlaceDate = generateRandomTimeBetweenTwoDates(BEGIN_TIME, END_TIME);
            myOrder[i].completed = generateRandomNumberWithinRange(0, MAX_COMPLETE_COUNT);
            myOrder[i].lineItemCount = generateRandomNumberWithinRange(0, MAX_LINEITEM_COUNT);

        }
    }

    protected void createItemData()
    {
        myItem = new items[numOfItems];
        for(int i = 0;i < numOfItems;i++)
        {
            items tempItem = new items();
            myItem[i] = tempItem;

            int tempItemID = generateRandomNumberWithinRange(1, numOfItems);
            int tempWarehouseID = generateRandomNumberWithinRange(1, numOfWarehouses);

            boolean searchComplete = false;
            while(!searchComplete)
            {
                //Try assigning the itemID to given (warehouseID)
                //If a itemID has already been assigned to a warehouseID
                //Keep generating a new stationID that will fit
                //If not, record the itemID and mark completion
                if(listOfWarehouses.get(tempWarehouseID).hasItem(tempItemID))
                {   
                    tempItemID = generateRandomNumberWithinRange(1, numOfItems);
                }
                else
                {   

                    listOfWarehouses.get(tempWarehouseID).addItem(tempItemID);
                    searchComplete = true;
                }
            }

            myItem[i].itemID = tempItemID;
            myItem[i].warehouseID = tempWarehouseID;
            myItem[i].name = generateRandomName();
            myItem[i].price = generateRandomNumberWithinRangeInDouble(MAX_PRICE);
            myItem[i].stock = generateRandomNumberWithinRange(0, MAX_STOCK_AND_SOLD_AND_ORDER_COUNT);
            myItem[i].soldCount = generateRandomNumberWithinRange(0 ,MAX_STOCK_AND_SOLD_AND_ORDER_COUNT);
            myItem[i].orderCount = generateRandomNumberWithinRange(0, MAX_STOCK_AND_SOLD_AND_ORDER_COUNT);

        }
    }

    protected void createLineItemData()
    {
        myLineItem = new lineItems[numOfLineItems];
        for(int i = 0;i < numOfLineItems;i++)
        {
            lineItems tempLineItem = new lineItems();
            myLineItem[i] = tempLineItem;

            int tempLineItemID = generateRandomNumberWithinRange(1, numOfLineItems);
            int tempItemID = generateRandomNumberWithinRange(1, numOfItems);
            int tempOrderID = generateRandomNumberWithinRange(1, numOfOrders);
            int tempCustomerID = generateRandomNumberWithinRange(1, numOfCustomers);
            int tempStationID = generateRandomNumberWithinRange(1, numOfDistStations);
            int tempWarehouseID = generateRandomNumberWithinRange(1, numOfWarehouses);


            boolean searchComplete = false;
            while(!searchComplete)
            {   

                //Try assigning the lineItemID to given (warehouseID, stationID, custID, orderID)
                //Check If the stationID is assigned to given warehouseID
                //Also check if the itemID is assigned to given warehouseID
                //If not, keep generating all six IDs
                if(listOfWarehouses.get(tempWarehouseID).hasDistStation(tempStationID) && listOfWarehouses.get(tempWarehouseID).hasItem(tempItemID))
                {
                    //Check if customerID is assigned to given stationID
                    //If not, keep generating four IDs (except warehouseID)
                    if(listOfWarehouses.get(tempWarehouseID).stationSet.get(tempStationID).hasCustomer(tempCustomerID))
                    {    
                        //Check if orderID is assigned to this cutomerID
                        //If not, keep generating three IDs (except stationID & warehouseID)
                        if(listOfWarehouses.get(tempWarehouseID).stationSet.get(tempStationID).customerSet.get(tempCustomerID).hasOrder(tempOrderID))
                        {
                            //Check if lineItemID is assigned to this orderID
                            //If not, record the lineItemID and mark completion
                            if(listOfWarehouses.get(tempWarehouseID).stationSet.get(tempStationID).customerSet.get(tempCustomerID).orderSet.get(tempOrderID).hasLineItem(tempLineItemID))
                            {
                                tempLineItemID = generateRandomNumberWithinRange(1, numOfLineItems);
                            }
                            else
                            {
                                listOfWarehouses.get(tempWarehouseID).stationSet.get(tempStationID).customerSet.get(tempCustomerID).orderSet.get(tempOrderID).addLineItem(tempLineItemID);  
                                searchComplete = true; 
                            }
                        }
                        else
                        {
                            tempLineItemID = generateRandomNumberWithinRange(1, numOfLineItems);
                            tempItemID = generateRandomNumberWithinRange(1, numOfItems);
                            tempOrderID = generateRandomNumberWithinRange(1, numOfOrders);
                            tempCustomerID = generateRandomNumberWithinRange(1, numOfCustomers);
                        }
                    }
                    else
                    {   
                        tempLineItemID = generateRandomNumberWithinRange(1, numOfLineItems);
                        tempItemID = generateRandomNumberWithinRange(1, numOfItems);
                        tempOrderID = generateRandomNumberWithinRange(1, numOfOrders);
                        tempCustomerID = generateRandomNumberWithinRange(1, numOfCustomers);
                        tempStationID = generateRandomNumberWithinRange(1, numOfDistStations);
                    }
                }
                else
                {
                    tempLineItemID = generateRandomNumberWithinRange(1, numOfLineItems);
                    tempItemID = generateRandomNumberWithinRange(1, numOfItems);
                    tempOrderID = generateRandomNumberWithinRange(1, numOfOrders);
                    tempCustomerID = generateRandomNumberWithinRange(1, numOfCustomers);
                    tempStationID = generateRandomNumberWithinRange(1, numOfDistStations);
                    tempWarehouseID = generateRandomNumberWithinRange(1, numOfWarehouses);
                }
            }

            myLineItem[i].lineItemID = tempLineItemID;
            myLineItem[i].itemID = tempItemID;
            myLineItem[i].orderID = tempOrderID;
            myLineItem[i].custID = tempCustomerID;
            myLineItem[i].stationID = tempStationID;
            myLineItem[i].warehouseID = tempWarehouseID;
            
            myLineItem[i].quantity = generateRandomNumberWithinRange(0, MAX_QUANTITY);
            myLineItem[i].amountDue = generateRandomNumberWithinRangeInDouble(MAX_AMOUNT_DUE);
            myLineItem[i].deliveryDate = generateRandomTimeBetweenTwoDates(BEGIN_TIME, END_TIME);

        }
    }

    protected String generateRandomName()
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

    protected String generateRandomStreetAddress()
    {	
    	StringBuilder strBuilder = new StringBuilder();

        int randomStreetNumber = generateRandomNumberWithinRange(MIN_STREET_NUMBER, MAX_STREET_NUMBER);
        strBuilder.append(randomStreetNumber); //Implicitly convert int to string
        strBuilder.append(" ");
    	int maxStreetLength = generateRandomNumberWithinRange(MIN_NAME_LENGTH, MAX_NAME_LENGTH);
    	for(int i = 0; i < maxStreetLength;i++)
    	{	
    		int randomIndex = generateRandomNumberWithinRange(0, LENGTH_OF_LEXICON - 1);
    		strBuilder.append(LEXICON.charAt(randomIndex));
    	}

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

    //Make sure sales tax is greater than 0
    protected double generateRandomSalesTax()
    {   
        double result = 0;
        while(result == 0)
        {
            result = generateRandomNumberWithinRangeInDouble(MAX_SALES_TAX);
        }
    	return result;
    }

    protected String generateRandomPhone()
    {
        StringBuilder strBuilder = new StringBuilder();

        for(int i = 0; i < PHONE_NUMBER_LENGTH; i++)
        {      
            int randomNumber;
            if(i == 0)
            { 
                randomNumber = generateRandomNumberWithinRange(1, 9);
            }
            else
            {  
                randomNumber = generateRandomNumberWithinRange(0, 9);
            }
            strBuilder.append(randomNumber);
        }

        String result = strBuilder.toString();
        return result;
    }


    protected long generateRandomTimeBetweenTwoDates(String begin, String end) {
    	
        long beginTime = Timestamp.valueOf(begin).getTime();
        long endTime = Timestamp.valueOf(end).getTime();

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

    public static void main(String[] args) 
    {   
        //This is how you can specify the format for the dates you will use
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-mm-dd");
        
        dataGenerator testGenerator = new dataGenerator();
        testGenerator.numOfWarehouses = 8;
        testGenerator.numOfDistStations = 8;
        testGenerator.numOfCustomers = 10;
        testGenerator.numOfOrders = 10;
        testGenerator.numOfItems = 10;
        testGenerator.numOfLineItems = 10;

        testGenerator.createWarehouseData();
        testGenerator.createDistributionStationData();
        testGenerator.createCustomerData();
        testGenerator.createOrderData();
        testGenerator.createItemData();
        testGenerator.createLineItemData();

        System.out.println("\n\n\n");

        for(int i = 0;i < testGenerator.numOfWarehouses;i++)
        {
            System.out.println("warehouses: " +
                       testGenerator.myWarehouse[i].warehouseID + ", " +
                       testGenerator.myWarehouse[i].name + ", " +
                       testGenerator.myWarehouse[i].strAddress + ", " +
                       testGenerator.myWarehouse[i].cityAddress + ", " +
                       testGenerator.myWarehouse[i].stateAddress + ", " +
                       testGenerator.myWarehouse[i].zipcode + ", " +
                       testGenerator.myWarehouse[i].salesTax + ", " + 
                       testGenerator.myWarehouse[i].salesSum);
        }

        System.out.println("\n\n\n");
        
        for(int i = 0;i < testGenerator.numOfDistStations;i++)
        {
            System.out.println("distStations: " +
                       testGenerator.myDistributionStation[i].stationID + ", " +
                       testGenerator.myDistributionStation[i].warehouseID + ", " +
                       testGenerator.myDistributionStation[i].name + ", " +
                       testGenerator.myDistributionStation[i].strAddress + ", " +
                       testGenerator.myDistributionStation[i].cityAddress + ", " +
                       testGenerator.myDistributionStation[i].stateAddress + ", " +
                       testGenerator.myDistributionStation[i].zipcode + ", " +
                       testGenerator.myDistributionStation[i].salesTax + ", " + 
                       testGenerator.myDistributionStation[i].salesSum);
        }

        System.out.println("\n\n\n");

    
        for(int i = 0;i < testGenerator.numOfCustomers;i++)
        {   

            Date date_registered = new Date(testGenerator.myCustomer[i].accountOpenDate);
        
            System.out.println("customers: " +
                       testGenerator.myCustomer[i].custID + ", " +
                       testGenerator.myCustomer[i].stationID + ", " +
                       testGenerator.myCustomer[i].warehouseID + ", " +
                       testGenerator.myCustomer[i].fname + ", " +
                       testGenerator.myCustomer[i].MI + ", " +
                       testGenerator.myCustomer[i].lname + ", " +
                       testGenerator.myCustomer[i].strAddress + ", " +
                       testGenerator.myCustomer[i].cityAddress + ", " +
                       testGenerator.myCustomer[i].stateAddress + ", " +
                       testGenerator.myCustomer[i].zipcode + ", " +
                       testGenerator.myCustomer[i].phone + ", " + 
                       date_registered + ", " + 
                       testGenerator.myCustomer[i].discount + ", " + 
                       testGenerator.myCustomer[i].balance + ", " + 
                       testGenerator.myCustomer[i].paid + ", " + 
                       testGenerator.myCustomer[i].paymentCount + ", " + 
                       testGenerator.myCustomer[i].deliveryCount);
        }

        System.out.println("\n\n\n");

        for(int i = 0;i < testGenerator.numOfOrders;i++)
        {   

            Date dateOrderPlaced = new Date(testGenerator.myOrder[i].orderPlaceDate);
        
            System.out.println("orders: " +
                       testGenerator.myOrder[i].orderID + ", " +
                       testGenerator.myOrder[i].custID + ", " +
                       testGenerator.myOrder[i].stationID + ", " +
                       testGenerator.myOrder[i].warehouseID + ", " +
                       dateOrderPlaced + ", " +
                       testGenerator.myOrder[i].completed + ", " +
                       testGenerator.myOrder[i].lineItemCount);
        }
        
        System.out.println("\n\n\n");

        for(int i = 0;i < testGenerator.numOfItems;i++)
        {   

            System.out.println("items: " +
                       testGenerator.myItem[i].itemID + ", " +
                       testGenerator.myItem[i].warehouseID + ", " +
                       testGenerator.myItem[i].name + ", " +
                       testGenerator.myItem[i].price + ", " +
                       testGenerator.myItem[i].stock + ", " +
                       testGenerator.myItem[i].soldCount + ", " +
                       testGenerator.myItem[i].orderCount);
        }
        
        System.out.println("\n\n\n");

        for(int i = 0;i < testGenerator.numOfLineItems;i++)
        {   
            Date dateDelivered = new Date(testGenerator.myLineItem[i].deliveryDate);

            System.out.println("lineItems: " +
                       testGenerator.myLineItem[i].lineItemID + ", " +
                       testGenerator.myLineItem[i].itemID + ", " +
                       testGenerator.myLineItem[i].orderID + ", " +
                       testGenerator.myLineItem[i].custID + ", " +
                       testGenerator.myLineItem[i].stationID + ", " +
                       testGenerator.myLineItem[i].warehouseID + ", " +
                       testGenerator.myLineItem[i].quantity + ", " +
                       testGenerator.myLineItem[i].amountDue + ", " +
                       dateDelivered);
        }
        
        System.out.println("\n\n\n");
    }

}