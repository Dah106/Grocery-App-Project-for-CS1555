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
    protected int numOfStations;
    protected int numOfCustomers;
    protected int numOfOrders;
    protected int numOfItems;
    protected int numOfLineItems;

    protected int totalNumOfStocks;
    protected int totalNumOfStations;
    protected int totalNumOfCustomers;
    protected int totalNumOfOrders;
    protected int totalNumOfLineItems;

    protected ArrayList<stock> myStock;
    protected ArrayList<items> myItem;
    protected ArrayList<lineItems> myLineItem;
    protected ArrayList<orders> myOrder;
    protected ArrayList<customers> myCustomer;
    protected ArrayList<distStations> myStation;
    protected ArrayList<warehouses> myWarehouse;

    protected double[] warehouseTax;

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

    protected final double MAX_SALES_TAX = 10;
    protected final double MAX_SALES_SUM = 1000;

    protected final int ZIPCODE_LENGTH = 5;

    protected final int PHONE_NUMBER_LENGTH = 10;

    protected final String BEGIN_ORDER_TIME = "2015-04-01 00:00:00";
    protected final String END_ORDER_TIME = "2015-06-18 00:00:00";

    protected final String BEGIN_DELIVERY_TIME = "2015-06-18 00:00:00";
    protected final String END_DELIVERY_TIME = "2015-12-01 00:00:00";

    protected final double MAX_DISCOUNT = 99;
    protected final double MAX_BALANCE = 1000; 
    protected final double MAX_PAID_AMOUNT = 1000; 

    protected final int MAX_PAYMENT_COUNT = 100;
    protected final int MAX_DELIVERY_COUNT = 1000; 

    protected final int MAX_COMPLETE_COUNT = 100;
    protected final int MAX_LINEITEM_COUNT = 100;

    protected final double MAX_PRICE = 10;
    protected final int MAX_STOCK_AND_SOLD_AND_ORDER_COUNT = 1000;

    protected final int MAX_QUANTITY = 10; 
    protected final double MAX_AMOUNT_DUE = 1000;

    protected final static DecimalFormat decimalFormat = new DecimalFormat("#.00");
    
    //Override default constructor
    protected dataGenerator()
    {  
        this.numOfWarehouses = 0;
        this.numOfStations = 0;
        this.numOfCustomers = 0;
        this.numOfOrders = 0;
        this.numOfItems = 0;
        this.numOfLineItems = 0;

        this.totalNumOfStocks = 0;
        this.totalNumOfStations = 0;
        this.totalNumOfCustomers = 0;
        this.totalNumOfOrders = 0;
        this.totalNumOfLineItems = 0;

        this.myStock = new ArrayList<stock>();
        this.myItem = new ArrayList<items>();
        this.myLineItem = new ArrayList<lineItems>();
        this.myOrder = new ArrayList<orders>();
        this.myCustomer = new ArrayList<customers>();
        this.myStation = new ArrayList<distStations>();
        this.myWarehouse = new ArrayList<warehouses>();
    }


    protected void createSalesTax(int numOfWarehouses)
    {   
        this.warehouseTax = new double[numOfWarehouses];

        for(int i = 0;i < numOfWarehouses;i++)
        {   
            warehouseTax[i] = generateRandomSalesTax();
        }
    } 

    protected void createItemData()
    {

        for(int tempItemID = 0;tempItemID < numOfItems;tempItemID++)
        {   
            //create item
            items tempItem = new items();
                
            tempItem.itemID = tempItemID;
            tempItem.name = generateRandomName();

            //Consistency check
            tempItem.price = generateRandomNumberWithinRangeInDouble(MAX_PRICE);
                
            myItem.add(tempItem);
        }
    }

    protected void createLineItemData()
    {
        totalNumOfLineItems = numOfLineItems*numOfOrders*numOfCustomers*numOfStations*numOfWarehouses;

        int resetLineItemID = 0;
        int resetOrderID = 0;
        int resetCustomerID = 0;
        int resetStationID = 0;
        int resetWarehouseID = 0;

        for(int tempLineItemID = 0;tempLineItemID < totalNumOfLineItems;tempLineItemID++)
        {   
            if(resetLineItemID == numOfLineItems)
            {
                resetLineItemID = 0;
                resetOrderID++;
            }

            if(resetOrderID == numOfOrders)
            {
                resetOrderID = 0;
                resetCustomerID++;
            }

            if(resetCustomerID == numOfCustomers)
            {
                resetCustomerID = 0;
                resetStationID++;
            }

            if(resetStationID == numOfStations)
            {
                resetStationID = 0;
                resetWarehouseID++;
            }

            lineItems tempLineItem = new lineItems();

            //Plus 1 to since array index is zero-indexed
            tempLineItem.lineItemID = resetLineItemID;
            tempLineItem.itemID = generateRandomNumberWithinRange(0, numOfItems - 1);
            
            tempLineItem.orderID = resetOrderID;

            tempLineItem.custID = resetCustomerID;
            tempLineItem.stationID = resetStationID;
            tempLineItem.warehouseID = resetWarehouseID;
                            
            tempLineItem.quantity = generateRandomNumberWithinRange(1, MAX_QUANTITY);;

            tempLineItem.amountDue = myItem.get(tempLineItem.itemID).price * tempLineItem.quantity;
            tempLineItem.deliveryDate = generateRandomTimeBetweenTwoDates(BEGIN_DELIVERY_TIME, END_DELIVERY_TIME);

            myLineItem.add(tempLineItem);

            resetLineItemID++;
        }
    }

    protected void createOrderData()
    {
        totalNumOfOrders = numOfOrders*numOfCustomers*numOfStations*numOfWarehouses;

        int resetOrderID = 0;
        int resetCustomerID = 0;
        int resetStationID = 0;
        int resetWarehouseID = 0;

        for(int tempOrderID = 0;tempOrderID < totalNumOfOrders;tempOrderID++)
        {

            if(resetOrderID == numOfOrders)
            {
                resetOrderID = 0;
                resetCustomerID++;
            }

            if(resetCustomerID == numOfCustomers)
            {
                resetCustomerID = 0;
                resetStationID++;
            }

            if(resetStationID == numOfStations)
            {
                resetStationID = 0;
                resetWarehouseID++;
            }

            orders tempOrder = new orders();

            tempOrder.orderID = resetOrderID;
            tempOrder.custID = resetCustomerID;
            tempOrder.stationID = resetStationID;
            tempOrder.warehouseID = resetWarehouseID;

            tempOrder.orderPlaceDate = generateRandomTimeBetweenTwoDates(BEGIN_ORDER_TIME, END_ORDER_TIME);
            tempOrder.completed = 1;
            tempOrder.lineItemCount = numOfLineItems;

            //update order per customer
            myOrder.add(tempOrder);

            resetOrderID++;
        }
    }

    protected void createStock()
    {   
        totalNumOfStocks = numOfItems * numOfWarehouses;

        int resetItemID = 0;
        int resetWarehouseID = 0;
        for(int tempStockID = 0;tempStockID < totalNumOfStocks;tempStockID++)
        {   
            //System.out.println("resetItemID id: " + resetItemID);

            if(resetItemID == numOfItems)
            {
                resetItemID = 0;
                resetWarehouseID++;
            }

            stock tempStock = new stock();

            tempStock.itemID = resetItemID;
            tempStock.warehouseID = resetWarehouseID;
            tempStock.stock = generateRandomNumberWithinRange(0, 1000);
            tempStock.numSold = 0;
            tempStock.numOrders = 0;

            myStock.add(tempStock);

            resetItemID++;
        }

        for(int lineItemID = 0;lineItemID < totalNumOfLineItems;lineItemID++)
        {
            int tempItemID = myLineItem.get(lineItemID).itemID;
            int tempWarehouseID = myLineItem.get(lineItemID).warehouseID;
            double tempQuantity = myLineItem.get(lineItemID).quantity;

            stock tempStock = new stock();
            for(int i = 0;i < totalNumOfStocks;i++)
            {   
                tempStock = myStock.get(i);

                if(tempStock.itemID == tempItemID && tempStock.warehouseID == tempWarehouseID)
                {
                    tempStock.numSold += tempQuantity;
                    tempStock.numOrders++;
                }
            }

            myStock.add(tempStock);
        }
    }

    protected void createCustomerData()
    {

        totalNumOfCustomers = numOfCustomers*numOfStations*numOfWarehouses;

        int resetCustomerID = 0;
        int resetStationID = 0;
        int resetWarehouseID = 0;

        for(int tempCustomerID = 0;tempCustomerID < totalNumOfCustomers;tempCustomerID++)
        {   
            if(resetCustomerID == numOfCustomers)
            {
                resetCustomerID = 0;
                resetStationID++;
            }

            if(resetStationID == numOfStations)
            {
                resetStationID = 0;
                resetWarehouseID++;
            }

            //create customer per distribution station
            customers tempCustomer = new customers();

            //Plus 1 to since array index is zero-indexed
            tempCustomer.custID = resetCustomerID;
            tempCustomer.stationID = resetStationID;
            tempCustomer.warehouseID = resetWarehouseID;
                    

            tempCustomer.fname = generateRandomName();
            tempCustomer.MI = String.valueOf(LEXICON.charAt(generateRandomNumberWithinRange(0, LENGTH_OF_LEXICON - 1)));
            tempCustomer.lname = generateRandomName();
            tempCustomer.strAddress = generateRandomStreetAddress();

            int randomIndex = generateRandomNumberWithinRange(0, LENGTH_OF_US_CITIES - 1) % LENGTH_OF_US_CITIES;
            tempCustomer.cityAddress = US_CITIES[randomIndex];
            tempCustomer.stateAddress = US_STATES[randomIndex];

            tempCustomer.zipcode = generateRandomZipcode();
            tempCustomer.phone = generateRandomPhone();
            tempCustomer.accountOpenDate = generateRandomTimeBetweenTwoDates(BEGIN_DELIVERY_TIME, END_ORDER_TIME);
            tempCustomer.discount = generateRandomNumberWithinRangeInDouble(MAX_DISCOUNT);
            tempCustomer.balance = 0;
                    
            //Consistency check

            double paid = 0;

            int paymentCount = numOfLineItems * numOfOrders;
            for(int i = tempCustomerID*paymentCount;i < tempCustomerID*paymentCount + paymentCount;i++)
            {   
                //System.out.println("line item id is " + i);
                paid += myLineItem.get(i).amountDue *(1 - tempCustomer.discount/100) * (1 + warehouseTax[tempCustomer.warehouseID]/100);
            }

            tempCustomer.paid = paid;
            
            tempCustomer.paymentCount = paymentCount;
            
            //All orders are completed in milestone 1
            tempCustomer.deliveryCount = paymentCount;
                    
                    
            //update customer per distribution station
            myCustomer.add(tempCustomer);

            resetCustomerID++;
        }
    }

    protected void createDistributionStationData()
    {
        int resetStationID = 0;
        int resetWarehouseID = 0;

        totalNumOfStations = numOfStations * numOfWarehouses;
        for(int tempStationID = 0;tempStationID < totalNumOfStations;tempStationID++)
        {   

            if(resetStationID == numOfStations)
            {
                resetStationID = 0;
                resetWarehouseID++;
            }

            //create distribution station
            distStations tempDistStation = new distStations();
                
            //Plus 1 to since array index is zero-indexed
            tempDistStation.stationID = resetStationID;
            tempDistStation.warehouseID = resetWarehouseID;
                
            tempDistStation.name = generateRandomName();
            tempDistStation.strAddress = generateRandomStreetAddress();

            int randomIndex = generateRandomNumberWithinRange(0, LENGTH_OF_US_CITIES - 1) % LENGTH_OF_US_CITIES;
            tempDistStation.cityAddress = US_CITIES[randomIndex];
            tempDistStation.stateAddress = US_STATES[randomIndex];

            tempDistStation.zipcode = generateRandomZipcode();
            
            tempDistStation.salesTax = warehouseTax[tempDistStation.warehouseID];

            double tempSalesSum = 0;

            int tempIndex = tempStationID * numOfCustomers;
            for(int i = 0; i < numOfCustomers;i++)
            {
                tempSalesSum += myCustomer.get(tempIndex).paid;
                tempIndex++;
            }


            tempDistStation.salesSum = tempSalesSum;
            //update distribution station
            myStation.add(tempDistStation);

            resetStationID++;
        }
    }

    protected void createWarehouseData()
    {   

        for(int tempWarehouseID = 0;tempWarehouseID < numOfWarehouses;tempWarehouseID++)
        {   
            //create warehouse
            warehouses tempWarehouse = new warehouses();
            
            tempWarehouse.warehouseID = tempWarehouseID;

            tempWarehouse.name = generateRandomName();
            tempWarehouse.strAddress = generateRandomStreetAddress();

            int randomIndex = generateRandomNumberWithinRange(0, LENGTH_OF_US_CITIES - 1) % LENGTH_OF_US_CITIES;
            tempWarehouse.cityAddress = US_CITIES[randomIndex];
            tempWarehouse.stateAddress = US_STATES[randomIndex];

            tempWarehouse.zipcode = generateRandomZipcode();

            tempWarehouse.salesTax = warehouseTax[tempWarehouseID];

            
            double tempSalesSum = 0;

            int tempIndex = tempWarehouseID * numOfStations;
            for(int i = 0; i < numOfStations;i++)
            {
                tempSalesSum += myStation.get(tempIndex).salesSum;
                tempIndex++;
            }

            //Consistency check
            tempWarehouse.salesSum = tempSalesSum;

            //update warehouse per system
            myWarehouse.add(tempWarehouse);
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
        testGenerator.numOfWarehouses = 2;
        testGenerator.numOfStations = 2;
        testGenerator.numOfCustomers = 2;
        testGenerator.numOfOrders = 2;
        testGenerator.numOfItems = 10;
        testGenerator.numOfLineItems = 2;

        testGenerator.createSalesTax(testGenerator.numOfWarehouses);

        testGenerator.createItemData();
        testGenerator.createLineItemData();
        testGenerator.createOrderData();
        testGenerator.createStock();
        testGenerator.createCustomerData();
        testGenerator.createDistributionStationData();
        testGenerator.createWarehouseData();
         
       
        System.out.println("\n\n\n");


        for(int tempItemID = 0;tempItemID < testGenerator.numOfItems;tempItemID++)
        {   
            items tempItem = testGenerator.myItem.get(tempItemID);

                System.out.println("items: " +
                           tempItem.itemID + ", " +
                           tempItem.name + ", " +
                           tempItem.price);
        }

        
        System.out.println("\n\n\n");


        for(int tempLineItemID = 0;tempLineItemID < testGenerator.totalNumOfLineItems;tempLineItemID++)
        {   
                            lineItems tempLineItem = testGenerator.myLineItem.get(tempLineItemID);

                            Date dateDelivered = new Date(tempLineItem.deliveryDate);

                            System.out.println("lineItems: " +
                                       tempLineItem.lineItemID + ", " +
                                       tempLineItem.itemID + ", " +
                                       tempLineItem.orderID + ", " +
                                       tempLineItem.custID + ", " +
                                       tempLineItem.stationID + ", " +
                                       tempLineItem.warehouseID + ", " +
                                       tempLineItem.quantity + ", " +
                                       tempLineItem.amountDue + ", " +
                                       dateDelivered);
        }


        
        System.out.println("\n\n\n");

        for(int tempOrderID = 0;tempOrderID < testGenerator.totalNumOfOrders;tempOrderID++)
        {   
            orders tempOrder = testGenerator.myOrder.get(tempOrderID);

            Date dateOrderPlaced = new Date(tempOrder.orderPlaceDate);
                    
                        System.out.println("orders: " +
                                   tempOrder.orderID + ", " +
                                   tempOrder.custID + ", " +
                                   tempOrder.stationID + ", " +
                                   tempOrder.warehouseID + ", " +
                                   dateOrderPlaced + ", " +
                                   tempOrder.completed + ", " +
                                   tempOrder.lineItemCount);
        }

        System.out.println("\n\n\n");

        for(int tempStockID = 0;tempStockID < testGenerator.totalNumOfStocks;tempStockID++)
        {   
            stock tempStock = testGenerator.myStock.get(tempStockID);

            System.out.println("stock: " +
                               tempStock.itemID + ", " +
                               tempStock.warehouseID + ", " +
                               tempStock.stock + ", " +
                               tempStock.numSold + ", " +
                               tempStock.numOrders);
        }

        System.out.println("\n\n\n");

        for(int tempCustomerID = 0;tempCustomerID < testGenerator.totalNumOfCustomers;tempCustomerID++)
        {   
            customers tempCustomer = testGenerator.myCustomer.get(tempCustomerID);

                    Date date_registered = new Date(tempCustomer.accountOpenDate);
                
                    System.out.println("customers: " +
                               tempCustomer.custID + ", " +
                               tempCustomer.stationID + ", " +
                               tempCustomer.warehouseID + ", " +
                               tempCustomer.fname + ", " +
                               tempCustomer.MI + ", " +
                               tempCustomer.lname + ", " +
                               tempCustomer.strAddress + ", " +
                               tempCustomer.cityAddress + ", " +
                               tempCustomer.stateAddress + ", " +
                               tempCustomer.zipcode + ", " +
                               tempCustomer.phone + ", " + 
                               date_registered + ", " + 
                               tempCustomer.discount + ", " + 
                               tempCustomer.balance + ", " + 
                               tempCustomer.paid + ", " + 
                               tempCustomer.paymentCount + ", " + 
                               tempCustomer.deliveryCount);
        }

        System.out.println("\n\n\n");
        
        for(int tempStationID = 0;tempStationID < testGenerator.totalNumOfStations;tempStationID++)
        {   
            distStations tempDistStation = testGenerator.myStation.get(tempStationID);

                System.out.println("distStations: " +
                           tempDistStation.stationID + ", " +
                           tempDistStation.warehouseID + ", " +
                           tempDistStation.name + ", " +
                           tempDistStation.strAddress + ", " +
                           tempDistStation.cityAddress + ", " +
                           tempDistStation.stateAddress + ", " +
                           tempDistStation.zipcode + ", " +
                           tempDistStation.salesTax + ", " + 
                           tempDistStation.salesSum);
        }

        System.out.println("\n\n\n");

        System.out.println("\n\n\n");

        for(int tempWarehouseID = 0;tempWarehouseID < testGenerator.numOfWarehouses;tempWarehouseID++)
        {   
            warehouses tempWarehouse = testGenerator.myWarehouse.get(tempWarehouseID);

            System.out.println("warehouses: " +
                       tempWarehouse.warehouseID + ", " +
                       tempWarehouse.name + ", " +
                       tempWarehouse.strAddress + ", " +
                       tempWarehouse.cityAddress + ", " +
                       tempWarehouse.stateAddress + ", " +
                       tempWarehouse.zipcode + ", " +
                       tempWarehouse.salesTax + ", " + 
                       tempWarehouse.salesSum);
        }

    }

}