import java.sql.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import oracle.jdbc.OracleStatement;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Scanner;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.text.*;
import java.lang.*;

public class finalDriver extends Thread
{	

	//used to hold the jdbc connection to the DB
	//No global connection object for milestone3
	//PreparedStatement and statement objects are local too

	public static int initialNumWarehouses = 1;
	public static int initialNumDistStations = 8; //should be 8
	public static int initialNumCustomers = 100;//should be 100
	public static int initialNumItems = 1000;//should be 1000

	//Number of orders per customer: range 1 ~ 50
	public static int initialNumOrders = 10;//make it 10

	//Number of items per order: range 3 ~ 10
	public static int initialNumLineItems = 5;//should be 5
	
	//3 runs * 5 transactions = 15 threads
	public static int NUM_OF_THREADS = 15;
 	
 	//Add your username before running
	public final String userName = "";

	//Add your password before running
	public final String password = "";

	//Thread id
	/* 0 -> first transaction, first run
	 * 1 -> second transaction, first run
	 * 2 -> third transaction, first run
	 * 3 -> fourth transaction, first run
	 * 4 -> fifth transaction, first run
	 *
	 * 5 -> first transaction, second run
	 * 6 -> second transaction, second run
	 * 7 -> third transaction, second run
	 * 8 -> fourth transaction, second run
	 * 9 -> fifth transaction, second run
	 *
	 * 10 -> first transaction, third run
	 * 11 -> second transaction, third run
	 * 12 -> third transaction, third run
	 * 13 -> fourth transaction, third run
	 * 14 -> fifth transaction, third run
     */
	public static int myThreadId;

    protected static dataGenerator myDataGenerator;

    public static void main(String[] args) throws SQLException
	{	
		try{
			//Main driver has thread id -1
			finalDriver mainDriver = new finalDriver(-1);


			//Init system with user credentials
			mainDriver.initSystem();

			//Drop and create tables
			mainDriver.dropAndCreateTables();;

			//Generate inital data
			mainDriver.generateInitalData();

			Thread[] threadList = new Thread[NUM_OF_THREADS];


			// spawn threads
	      	for (int i = 0; i < NUM_OF_THREADS; i++)
	      	{	

	          	threadList[i] = new finalDriver(i);
	          	threadList[i].start();
	      	}

	      	//System.out.println("complete spawning");
	      	
	      	// Start everyone at the same time
	      	setGreenLight();

	      	// wait for all threads to end
	      	for (int i = 0; i < NUM_OF_THREADS; i++)
	      	{	

	      		//System.out.println("wait for " + i);
	          	threadList[i].join();
	      	}
	    }

      	catch (Exception e)
	    {
	       e.printStackTrace();
	    }

	}

	//TODO: Add code for the thread to run
	public void run()
	{
		try
		{	

			if(myThreadId == 0 || myThreadId == 5 || myThreadId == 10)
			{
				newOrderTransaction();
			}
			else if(myThreadId == 1 || myThreadId == 6 || myThreadId == 11)
			{	
				paymentTransaction();
			}
			else if(myThreadId == 2 || myThreadId == 7 || myThreadId == 12)
			{
				orderStatusTransaction();
			}
			else if(myThreadId == 3 || myThreadId == 8 || myThreadId == 13)
			{
				deliveryTransaction();
			}
			else if(myThreadId == 4 || myThreadId == 9 || myThreadId == 14)
			{
				stockLevelTransaction();
			}
			else
			{
				System.out.println("Thread id does not exist, check the requirement");
			}
		}
		catch (SQLException e) {
				System.out.println("Cannot close Statement. Machine error: "+e.toString());
		}
	}

    protected void initSystem() throws SQLException
    {	
    	//Create data generator
		myDataGenerator = new dataGenerator();

		myDataGenerator.numOfWarehouses = initialNumWarehouses;
		myDataGenerator.numOfStations = initialNumDistStations;
		myDataGenerator.numOfCustomers = initialNumCustomers;
		myDataGenerator.numOfOrders = initialNumOrders;
		myDataGenerator.numOfItems = initialNumItems;
		myDataGenerator.numOfLineItems = initialNumLineItems;

		myDataGenerator.createSalesTax(myDataGenerator.numOfWarehouses);

        myDataGenerator.createItemData();
        myDataGenerator.createLineItemData();
        myDataGenerator.createOrderData();
        myDataGenerator.createStock();
        myDataGenerator.createCustomerData();
        myDataGenerator.createDistributionStationData();
        myDataGenerator.createWarehouseData();

    }

    //Drop and recreate all tables
    protected void dropAndCreateTables() throws SQLException
	{	
		String query = "";
		Connection connection = null;
		Statement statement = null;

		try{
		    // Register the oracle driver.  
		    DriverManager.registerDriver (new oracle.jdbc.driver.OracleDriver());
		    
		    //This is the location of the database.  This is the database in oracle
		    //provided to the class
		    String url = "jdbc:oracle:thin:@class3.cs.pitt.edu:1521:dbclass"; 
		    
		    //create a connection to DB on class3.cs.pitt.edu
		    connection = DriverManager.getConnection(url, userName, password); 
			
		}
		catch(Exception Ex)  {
		    System.out.println("Error connecting to database.  Machine Error: " +
				       Ex.toString());
		}

		String startTransaction = "SET TRANSACTION READ WRITE";
		String dropTableWarehouse = "drop table warehouses cascade constraints";
		String dropTableDistStations = "drop table distStations cascade constraints";
		String dropTableCustomers = "drop table customers cascade constraints";
		String dropTableOrders = "drop table orders cascade constraints";
		String dropTableItems = "drop table items cascade constraints";
		String dropTableLineItems = "drop table lineItems cascade constraints";
		String dropTableStock = "drop table stock cascade constraints";
		String purge = "purge recyclebin";
		String createTableWarehouse = "create table warehouses (" +
		"warehouseID integer," +
		"name varchar2(20)," +
		"strAddress varchar2(20)," +
		"cityAddress varchar2(20)," +
		"stateAddress varchar2(20)," +
		"zipcode varchar2(20)," +
		"salesTax number(4,2)," +
		"salesSum number(20, 2)," +
		"constraint checkWarehousesSalesTax check(salesTax > 0)," +
		"constraint warehouses_PK primary key(warehouseID) )";
		String createTableDistStations = "create table distStations (" +
		"stationID	integer," +
		"warehouseID integer," +
		"name varchar2(20)," +
		"strAddress varchar2(20)," +
		"cityAddress varchar2(20)," +
		"stateAddress varchar2(20)," +
		"zipcode varchar2(20)," +
		"salesTax number(4,2)," +
		"salesSum number (20, 2)," +
		"constraint checkDistStationSalesTax check(salesTax > 0)," +
		"constraint distStations_PK primary key(stationID, warehouseID)," +
		"constraint distStations_FK foreign key(warehouseID) references warehouses(warehouseID)  Deferrable Initially Deferred)";
		String createTableCustomers = "create table customers (" +
		"custID integer," +
		"stationID integer," +
		"warehouseID integer," +
		"fname varchar2(20)," +
		"MI	varchar2(1)," +
		"lname varchar2(20)," +
		"strAddress varchar2(20)," +
		"cityAddress varchar2(20)," +
		"stateAddress varchar2(20)," +
		"zipcode varchar2(20)," +
		"phone varchar2(10)," +
		"accountOpenDate date," +
		"discount number(4,2)," +
		"balance number(20, 2)," +
		"paid number(20, 2)," +
		"paymentCount integer," +
		"deliveryCount integer," +
		"constraint customers_PK primary key(custID, stationID, warehouseID)," +
		"constraint customers_FK foreign key(stationID, warehouseID) references distStations(stationID, warehouseID)  Deferrable Initially Deferred)";
		String createTableOrders = 	"create table orders (" +
		"orderID integer," +
		"custID integer," +
		"stationID integer," +
		"warehouseID integer," +
		"orderPlaceDate date," +
		"completed integer," +
		"lineItemCount integer," +
		"constraint orders_PK primary key(orderID, custID, stationID, warehouseID)," +
		"constraint orders_FK foreign key(custID, stationID, warehouseID) references customers(custID, stationID, warehouseID)  Deferrable Initially Deferred)";
		String createTableItems = "create table items (" +
		"itemID integer," +
		"name varchar2(20)," +
		"price number(20, 2)," +
		"constraint items_PK primary key(itemID))";
		String createTableLineItems = "create table lineItems (" +
		"lineitemID integer," +
		"itemID integer," +
		"orderID integer," +
		"custID integer," +
		"stationID integer," +
		"warehouseID integer," +
		"quantity integer," +
		"amountDue number(20, 2)," +
		"deliveryDate date," +
		"constraint lineItems_PK primary key (lineitemID, orderID, custID, warehouseID, stationID)," +
		"constraint lineItems_FK1 foreign key (orderID, custID, stationID, warehouseID) references orders(orderID, custID, stationID, warehouseID) Deferrable Initially Deferred," +
		"constraint lineItems_FK2 foreign key(itemID) references items(itemID) Deferrable Initially Deferred)";
		String createTableStock = "create table stock (" +
		"itemID integer," +
		"warehouseID integer," +
		"stock integer," +
		"numSold integer," +
		"numOrders integer," +
		"constraint stock_PK primary key(itemID, warehouseID)," +
		"constraint stock_FK1 foreign key(itemID) references items(itemID) Deferrable Initially Deferred," +
		"constraint stock_FK2 foreign key(warehouseID) references warehouses(warehouseID) Deferrable Initially Deferred)";
		

		// Execute creation of database
		try {

			//Note code written by different developers
			//Due to the time constraint of milestone3
			//We keep this SQL way of initiating transaction
			//"SET TRANSACTION READ WRITE", "COMMIT" == setAutoCommit(false); connection.commit();
			// Re-initialize the database back to the original specifications entered by the user
			//connection.setAutoCommit(false);//disable auto-commit for each transaction
		    
		    //set transaction concurrency level to TRANSACTION_READ_COMMITTED
			//connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            
            statement = connection.createStatement();

            statement.executeUpdate(startTransaction);
            statement.executeUpdate(dropTableWarehouse);
            statement.executeUpdate(dropTableDistStations);
            statement.executeUpdate(dropTableCustomers);
            statement.executeUpdate(dropTableOrders);
			statement.executeUpdate(dropTableLineItems);
            statement.executeUpdate(dropTableItems);
			statement.executeUpdate(dropTableStock);
			statement.executeUpdate(purge);
			
			statement.executeUpdate(createTableWarehouse);
			statement.executeUpdate(createTableDistStations);
			statement.executeUpdate(createTableCustomers);
			statement.executeUpdate(createTableOrders);
			statement.executeUpdate(createTableItems);
			statement.executeUpdate(createTableLineItems);
			statement.executeUpdate(createTableStock);
           	statement.executeUpdate("COMMIT");

	    	System.out.println("Dropping all table...");
			System.out.println("Recreating all table...");

        } catch(SQLException Ex) {
            System.out.println("Error running re-initialization.  Machine Error: " +
                    Ex.toString());
        } finally{
            try {
                if (statement != null)
                {
                    statement.close();
                }
            } catch (SQLException e) {
                System.out.println("Cannot close Statement. Machine error: "+e.toString());
            }
        }

        connection.close();
	}

    protected void generateInitalData() throws SQLException
    {	
    	PreparedStatement prepStatement = null;
		String query = "";
		Connection connection = null;

		//Open local connection for each transaction
    	try{
		    // Register the oracle driver.  
		    DriverManager.registerDriver (new oracle.jdbc.driver.OracleDriver());
		    
		    //This is the location of the database.  This is the database in oracle
		    //provided to the class
		    String url = "jdbc:oracle:thin:@class3.cs.pitt.edu:1521:dbclass"; 
		    
		    //create a connection to DB on class3.cs.pitt.edu
		   	connection = DriverManager.getConnection(url, userName, password); 
			
		}
		catch(Exception Ex)  {
		    System.out.println("Error connecting to database.  Machine Error: " +
				       Ex.toString());
		}

		try{
		   	//disable auto-commit for each transaction
		   	connection.setAutoCommit(false);
	
		    connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

		    //This is how you can specify the format for the dates you will use
        	SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-mm-dd");

        	query = "insert into items values (?,?,?)";
	      	prepStatement = connection.prepareStatement(query);


		    for(int tempItemID = 0;tempItemID < myDataGenerator.numOfItems;tempItemID++)
		   	{	
		      	items tempItem = myDataGenerator.myItem.get(tempItemID);	
		      		
		      	int itemID = tempItem.itemID;
		      	String name = tempItem.name;
		   		double price = tempItem.price; 

			    prepStatement.setInt(1, itemID);
			    prepStatement.setString(2, name); 
			    prepStatement.setDouble(3, price); 

				prepStatement.executeUpdate();
	      	}

	      	query = "insert into lineItems values (?,?,?,?,?,?,?,?,?)";
	      	prepStatement = connection.prepareStatement(query);

	      	for(int tempLineItemID = 0; tempLineItemID < myDataGenerator.totalNumOfLineItems;tempLineItemID++)
			{		
				lineItems tempLineItem = myDataGenerator.myLineItem.get(tempLineItemID);

					      		int lineItemID = tempLineItem.lineItemID;
					      		int itemID = tempLineItem.itemID;
					      		int orderID = tempLineItem.orderID;
					      		int custID = tempLineItem.custID;
					      		int stationID = tempLineItem.stationID;
					      		int warehouseID = tempLineItem.warehouseID;
					      		int quantity = tempLineItem.quantity;
							    double amountDue = tempLineItem.amountDue; 

							    java.sql.Date dateDelivered = new java.sql.Date (tempLineItem.deliveryDate);
							  
							    prepStatement.setInt(1, lineItemID);
							    prepStatement.setInt(2, itemID); 
							    prepStatement.setInt(3, orderID); 
							    prepStatement.setInt(4, custID); 
							    prepStatement.setInt(5, stationID); 
							    prepStatement.setInt(6, warehouseID); 
							    prepStatement.setInt(7, quantity);
							    prepStatement.setDouble(8, amountDue);
							    prepStatement.setDate(9, dateDelivered);

				prepStatement.executeUpdate();
			}
	      	

			query = "insert into orders values (?,?,?,?,?,?,?)";
	      	prepStatement = connection.prepareStatement(query);

			for(int tempOrderID = 0; tempOrderID < myDataGenerator.totalNumOfOrders;tempOrderID++)
			{	
				orders tempOrder = myDataGenerator.myOrder.get(tempOrderID);

				int orderID = tempOrder.orderID;
				int custID = tempOrder.custID;
				int stationID = tempOrder.stationID;
				int warehouseID = tempOrder.warehouseID; 

				java.sql.Date dateOrderPlaced = new java.sql.Date (tempOrder.orderPlaceDate);

				int completed = tempOrder.completed;
				int lineItemCount = tempOrder.lineItemCount;


				prepStatement.setInt(1, orderID);
				prepStatement.setInt(2, custID); 
				prepStatement.setInt(3, stationID); 
				prepStatement.setInt(4, warehouseID); 
				prepStatement.setDate(5, dateOrderPlaced); 
				prepStatement.setInt(6, completed); 
				prepStatement.setInt(7, lineItemCount);

				prepStatement.executeUpdate();
			}

			query = "insert into stock values (?,?,?,?,?)";
	      	prepStatement = connection.prepareStatement(query);

	      	for(int tempStockID = 0;tempStockID < myDataGenerator.totalNumOfStocks;tempStockID++)
        	{ 	
		      	stock tempStock = myDataGenerator.myStock.get(tempStockID);	
		      		
		      	int itemID = tempStock.itemID;
		      	int warehouseID = tempStock.warehouseID;
		      	int stock = tempStock.stock;
		      	int numSold = tempStock.numSold;
		      	int numOrders = tempStock.numOrders;

				prepStatement.setInt(1, itemID);
				prepStatement.setInt(2, warehouseID); 
				prepStatement.setInt(3, stock); 
				prepStatement.setInt(4, numSold); 
				prepStatement.setInt(5, numOrders); 

				prepStatement.executeUpdate();
	      	}
		
			/* table 'customers' has nine attributes */
	      	query = "insert into customers values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	      	prepStatement = connection.prepareStatement(query);


			for(int tempCustomerID = 0; tempCustomerID < myDataGenerator.totalNumOfCustomers;tempCustomerID++)
			{	
			    customers tempCustomer = myDataGenerator.myCustomer.get(tempCustomerID);

			      		int custID = tempCustomer.custID;
			      		int stationID = tempCustomer.stationID; 
			      		int warehouseID = tempCustomer.warehouseID; 
						String fname = tempCustomer.fname;
					    String MI = tempCustomer.MI;
					    String lname = tempCustomer.lname;
					    String strAddress = tempCustomer.strAddress;
					    String cityAddress = tempCustomer.cityAddress;
					    String stateAddress = tempCustomer.stateAddress;
					    String zipcode = tempCustomer.zipcode;
					    String phone = tempCustomer.phone;

					    java.sql.Date accountOpenDate = new java.sql.Date (tempCustomer.accountOpenDate);
					    
					    double discount = tempCustomer.discount;
					    double balance = tempCustomer.balance;
					    double paid = tempCustomer.paid;
					    int paymentCount = tempCustomer.paymentCount;
					    int deliveryCount = tempCustomer.deliveryCount;

					    
					    prepStatement.setInt(1, custID);
					    prepStatement.setInt(2, stationID); 
					    prepStatement.setInt(3, warehouseID); 
					    prepStatement.setString(4, fname); 
					    prepStatement.setString(5, MI); 
					    prepStatement.setString(6, lname); 
					    prepStatement.setString(7, strAddress); 
					    prepStatement.setString(8, cityAddress); 
					    prepStatement.setString(9, stateAddress); 
					    prepStatement.setString(10, zipcode);
					    prepStatement.setString(11, phone); 
					    prepStatement.setDate(12, accountOpenDate); 
					    prepStatement.setDouble(13, discount); 
					    prepStatement.setDouble(14, balance); 
					    prepStatement.setDouble(15, paid); 
					    prepStatement.setInt(16, paymentCount); 
					    prepStatement.setInt(17, deliveryCount); 

				      	prepStatement.executeUpdate();
			}


			/* table 'distStations' has nine attributes */
	      	query = "insert into distStations values (?,?,?,?,?,?,?,?,?)";
	      	prepStatement = connection.prepareStatement(query);


		    for(int tempStationID = 0;tempStationID < myDataGenerator.totalNumOfStations;tempStationID++)
		    {	
		      	distStations tempDistStation = myDataGenerator.myStation.get(tempStationID);

		      		int stationID = tempDistStation.stationID;
		      		int warehouseID = tempDistStation.warehouseID; 
					String name = tempDistStation.name;
				    String strAddress = tempDistStation.strAddress;
				    String cityAddress = tempDistStation.cityAddress;
				    String stateAddress = tempDistStation.stateAddress;
				    String zipcode = tempDistStation.zipcode;
				    double salesTax = tempDistStation.salesTax;
				    double salesSum = tempDistStation.salesSum;

				    prepStatement.setInt(1, stationID);
				    prepStatement.setInt(2, warehouseID); 
				    prepStatement.setString(3, name); 
				    prepStatement.setString(4, strAddress); 
				    prepStatement.setString(5, cityAddress); 
				    prepStatement.setString(6, stateAddress); 
				    prepStatement.setString(7, zipcode); 
				    prepStatement.setDouble(8, salesTax); 
				    prepStatement.setDouble(9, salesSum); 

			    prepStatement.executeUpdate();
		    }


		   	/* table 'warehouses' has eight attributes */
		    query = "insert into warehouses values (?,?,?,?,?,?,?,?)";
		    prepStatement = connection.prepareStatement(query);

		    for(int tempWarehouseID = 0;tempWarehouseID < myDataGenerator.numOfWarehouses;tempWarehouseID++)
		    {	
		    	warehouses tempWarehouse = myDataGenerator.myWarehouse.get(tempWarehouseID);
		    	
		    	int warehouseID = tempWarehouse.warehouseID;
			    String name = tempWarehouse.name;
			    String strAddress = tempWarehouse.strAddress;
			    String cityAddress = tempWarehouse.cityAddress;
			    String stateAddress = tempWarehouse.stateAddress;
			    String zipcode = tempWarehouse.zipcode;
			    double salesTax = tempWarehouse.salesTax;
			    double salesSum = tempWarehouse.salesSum;

			    prepStatement.setInt(1, warehouseID); 
			    prepStatement.setString(2, name); 
			    prepStatement.setString(3, strAddress); 
			    prepStatement.setString(4, cityAddress); 
			    prepStatement.setString(5, stateAddress); 
			    prepStatement.setString(6, zipcode); 
			    prepStatement.setDouble(7, salesTax); 
			    prepStatement.setDouble(8, salesSum); 

		      	prepStatement.executeUpdate();
	      	}

	      	//Important!
	      	connection.commit();
	    }
		catch(SQLException Ex) {

		    System.out.println("Generating Inital Data -> Error running the queries. Machine Error: " + Ex.toString());
		} 
		finally{
			try {
				if (prepStatement != null) prepStatement.close();
			} catch (SQLException e) {
				System.out.println("Cannot close Statement. Machine error: "+e.toString());
			}
		}


		connection.close();
    }
	
	protected void newOrderTransaction() throws SQLException
	{	
		PreparedStatement prepStatement = null;
		String query = "";
		Connection connection = null;

		//Open local connection for each transaction
	    try{

	    	//System.out.println("New order tranx greenlight" + greenLight);
			while (!getGreenLight()) yield();

	    	// Register the oracle driver.  
		    DriverManager.registerDriver (new oracle.jdbc.driver.OracleDriver());
		    
		    //This is the location of the database.  This is the database in oracle
		    //provided to the class
		    String url = "jdbc:oracle:thin:@class3.cs.pitt.edu:1521:dbclass"; 
		    
		    //create a connection to DB on class3.cs.pitt.edu
		    connection = DriverManager.getConnection(url, userName, password); 
			
			//if(connection != null)System.out.println("new order tranx connection is not null!");
			//else System.out.println("new order tranx connection is null!");

		    connection.setAutoCommit(false);//disable auto-commit for each transaction
		    
		    //set transaction concurrency level to TRANSACTION_READ_COMMITTED
			connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			

			int wID = generateRandomNumberWithinRange(0, initialNumWarehouses - 1);
			int sID = generateRandomNumberWithinRange(0, initialNumDistStations - 1);
		    int cID = generateRandomNumberWithinRange(0, initialNumCustomers - 1);
		    
		    System.out.println("Thread id is: " + myThreadId);

		    System.out.println("new order tranx wID is: " + wID);
		    System.out.println("new order tranx sID is: " + sID);
		    System.out.println("new order tranx cID is: " + cID);

		    //Make the total count of items ordered from 1 ~ 10
		    int totalCount = 2; //hard-code to 2

		    System.out.println("new order tranx total count of items ordered is:" + totalCount);
		    
		    int[] itemID = new int[totalCount];
		    int[] itemCount = new int[totalCount];
		    
		    int count = 0;
		    int id = 0;
		    for (int i = 0; i < totalCount; i++)
		    {

		        id = generateRandomNumberWithinRange(0, initialNumItems - 1);
		        count = generateRandomNumberWithinRange(1, 5);
		        
		        System.out.println("item id is: " + id + " , count of this item is: " + count);
		        itemID[i] = id;
		        itemCount[i] = count;
		    }
		    
		    //handle orders table
		    
		    int index = 0; //keep track of latest order
		    
		    for (int i = 0; i < myDataGenerator.totalNumOfOrders; i++)
		    {
		        
		        orders tempOrder = myDataGenerator.myOrder.get(i);
		   
		        if (tempOrder.custID == cID && tempOrder.stationID == sID && tempOrder.warehouseID == wID)
		        {
		            index = i; //get latest index in order to generate the latest lineItemID
		        }
		    }
		    
		    int orderID = myDataGenerator.myOrder.get(index).orderID + 1;
		    int custID = cID;
		    int stationID = sID;
		    int warehouseID = wID;
		    long longDate = Calendar.getInstance().getTime().getTime();
		    
		    
		    java.sql.Date dateOrderPlaced = new java.sql.Date(longDate);
		    
		    int completed = 0;
		    int lineItemCount = totalCount;
		    
		    

		    query = "insert into orders values (?,?,?,?,?,?,?)";
		    prepStatement = connection.prepareStatement(query);

		    //if(prepStatement == null)System.out.println("new order tranx prepStatement IS NULL");
		    //else System.out.println("new order tranx prepStatement IS NOT NULL");

		    prepStatement.setInt(1, orderID);
		    prepStatement.setInt(2, custID);
		    prepStatement.setInt(3, stationID);
		    prepStatement.setInt(4, warehouseID);
		    prepStatement.setDate(5, dateOrderPlaced);
		    prepStatement.setInt(6, completed);
		    prepStatement.setInt(7, lineItemCount);
		    
		    prepStatement.executeUpdate();
		    
		    
		    
		    orders tempOrder = new orders();
		    
		    tempOrder.orderID = orderID;
		    tempOrder.custID = custID;
		    tempOrder.stationID = stationID;
		    tempOrder.warehouseID = warehouseID;
		    
		    tempOrder.orderPlaceDate = longDate;
		    tempOrder.completed = 0;
		    tempOrder.lineItemCount = lineItemCount;
		    
		    //update order per customer
		    myDataGenerator.myOrder.add(tempOrder);
		    myDataGenerator.totalNumOfOrders++;
		    
		    
		    //handle lineItems table
		    
		    
		    lineItems tempLineItem;
		    index = 0;
		    int lineItemID = 0;
		    
		    for (int i = 0; i < myDataGenerator.totalNumOfLineItems; i++)
		    {
		        
		        tempLineItem  = myDataGenerator.myLineItem.get(i);
		        
		        if (tempLineItem.custID == cID && tempLineItem.stationID == sID && tempLineItem.warehouseID == wID)
		        {
		            index = i; //get latest index in order to generate the latest lineItemID
		        }
		    }
		    
		    
		    
		    lineItemID = myDataGenerator.myLineItem.get(index).lineItemID + 1;
		    
		    
		    for (int i = 0; i < totalCount; i++)
		    {
		        tempLineItem = new lineItems();
		        tempLineItem.lineItemID  = lineItemID;
		        tempLineItem.itemID = itemID[i];
		        tempLineItem.orderID = orderID;
		        tempLineItem.custID = cID;
		        tempLineItem.stationID = sID;
		        tempLineItem.warehouseID = wID;
		        tempLineItem.quantity = itemCount[i];
		        
		        
		        //find the price of the item.
		        for (int j = 0; j < myDataGenerator.numOfItems; j++)
		        {
		            if (myDataGenerator.myItem.get(j).itemID == itemID[i])
		            {
		                tempLineItem.amountDue = itemCount[i] * myDataGenerator.myItem.get(j).price;
		                break;
		            }
		        }
		        
		        // Calendar c = Calendar.getInstance();
		        // c.setTime(new Date());
		        // c.add(Calendar.DATE, 5); //increment 5 days for delivery date
		        tempLineItem.deliveryDate = -1;
		        
		        
		        
		        myDataGenerator.myLineItem.add(tempLineItem);
		        myDataGenerator.totalNumOfLineItems++;
		        
		        query = "insert into lineItems values (?,?,?,?,?,?,?,?,?)";
		        prepStatement = connection.prepareStatement(query);
		        
		        prepStatement.setInt(1, lineItemID);
		        prepStatement.setInt(2, itemID[i]);
		        prepStatement.setInt(3, orderID);
		        prepStatement.setInt(4, cID);
		        prepStatement.setInt(5, sID);
		        prepStatement.setInt(6, wID);
		        prepStatement.setInt(7, itemCount[i]);
		        prepStatement.setDouble(8, (double) tempLineItem.amountDue);
		        prepStatement.setNull(9, java.sql.Types.DATE);
		        
		        prepStatement.executeUpdate();
		        lineItemID++;
		        
		        //insert lineItem data into table
		    }
		    
		    //handle stock table
		    stock tempStock = new stock();
		    
		    for (int i = 0; i < totalCount; i++)
		    {
		        for (int j = 0; j < myDataGenerator.totalNumOfStocks; j++)
		        {
		            tempStock = myDataGenerator.myStock.get(j);
		            
		            if (tempStock.itemID == itemID[i] && tempStock.warehouseID == wID)
		            {
		                myDataGenerator.myStock.get(j).numOrders++;
		                myDataGenerator.myStock.get(j).numSold+= itemCount[i];
		                myDataGenerator.myStock.get(j).stock-= itemCount[i];
		                
		                query = "update stock set stock = ? where itemID = ? AND warehouseid = ?";
		                prepStatement = connection.prepareStatement(query);
		                
		                prepStatement.setInt(1, myDataGenerator.myStock.get(j).stock);
		                prepStatement.setInt(2, itemID[i]);
		                prepStatement.setInt(3, wID);
		                
		                prepStatement.executeUpdate();
		                
		                query = "update stock set numSold = ? where itemID = ? AND warehouseid = ?";
		                prepStatement = connection.prepareStatement(query);
		                
		                prepStatement.setInt(1, myDataGenerator.myStock.get(j).numSold);
		                prepStatement.setInt(2, itemID[i]);
		                prepStatement.setInt(3, wID);
		                
		                prepStatement.executeUpdate();
		                
		                query = "update stock set numOrders = ? where itemID = ? AND warehouseid = ?";
		                prepStatement = connection.prepareStatement(query);
		                
		                prepStatement.setInt(1, myDataGenerator.myStock.get(j).numOrders);
		                prepStatement.setInt(2, itemID[i]);
		                prepStatement.setInt(3, wID);
		                
		                prepStatement.executeUpdate();
		                
		                break;
		            }
		        }
		    }
		    
		    //yield();
		    //Important!
		    //commit the transaction
		    connection.commit();
		}
	    catch(Exception Ex)  
		{
			System.out.println("Creating new order -> Machine Error: " +
					   Ex.toString());
			System.out.println("Maybe the transaction does not committed successfully. Not required for this project to know what is in fact wrong.");
		}
	    finally{
			try {
				if (prepStatement != null) prepStatement.close();
			    //Important!
			    //close the local connection
			    connection.close();

			} catch (SQLException e) {
				System.out.println("Cannot close Statement. Machine error: "+e.toString());
			}
		}
	}

	protected void paymentTransaction() throws SQLException
	{	
		PreparedStatement prepStatement = null;
		String query = "";
		Connection connection = null;

		//Open local connection for each transaction

		try{

			//System.out.println("Payment tranx greenlight" + greenLight);
			while (!getGreenLight()) yield();

			// Register the oracle driver.  
		    DriverManager.registerDriver (new oracle.jdbc.driver.OracleDriver());
		    
		    //This is the location of the database.  This is the database in oracle
		    //provided to the class
		    String url = "jdbc:oracle:thin:@class3.cs.pitt.edu:1521:dbclass"; 
		    
		    //create a connection to DB on class3.cs.pitt.edu
		   	connection = DriverManager.getConnection(url, userName, password); 

			connection.setAutoCommit(false);//disable auto-commit for each transaction
		    
		    //set transaction concurrency level to TRANSACTION_READ_COMMITTED
			connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

		    
		    int wID = generateRandomNumberWithinRange(0, initialNumWarehouses - 1);
			int sID = generateRandomNumberWithinRange(0, initialNumDistStations - 1);
		    int cID = generateRandomNumberWithinRange(0, initialNumCustomers - 1);
		    
		    // System.out.println("Thread id is: " + myThreadId);
		    
		    // System.out.println("new order tranx wID is: " + wID);
		    // System.out.println("new order tranx sID is: " + sID);
		    // System.out.println("new order tranx cID is: " + cID);

		    //Make the payment from 1 ~ 200
		    double payment = generateRandomNumberWithinRangeInDouble(200);

		    //System.out.println("payment is: " + payment);

		    //handle customer table
		    
		    customers tempCustomer = new customers();
		    
		    int index = 0;
		    for(int i = 0; i < myDataGenerator.totalNumOfCustomers; i++)
		    {
		        tempCustomer = myDataGenerator.myCustomer.get(i);
		        if (tempCustomer.custID == cID && tempCustomer.stationID == sID && tempCustomer.warehouseID == wID)
		        {
		            myDataGenerator.myCustomer.get(i).balance -= payment;
		            myDataGenerator.myCustomer.get(i).paid += payment;
		            myDataGenerator.myCustomer.get(i).paymentCount++;
		            
		            index = i;
		            break;
		        }
		    }
		    
		    query = "update customers set balance = ? where custId = ? AND warehouseid = ? and stationid = ?";
		    prepStatement = connection.prepareStatement(query);
		    
		    prepStatement.setDouble(1, myDataGenerator.myCustomer.get(index).balance);
		    prepStatement.setInt(2, cID);
		    prepStatement.setInt(3, wID);
		    prepStatement.setInt(4, sID);
		    
		    prepStatement.executeUpdate();
		    
		    
		    query = "update customers set paid = ? where custId = ? AND warehouseid = ? and stationid = ?";
		    prepStatement = connection.prepareStatement(query);
		    
		    prepStatement.setDouble(1, myDataGenerator.myCustomer.get(index).paid);
		    prepStatement.setInt(2, cID);
		    prepStatement.setInt(3, wID);
		    prepStatement.setInt(4, sID);
		    
		    prepStatement.executeUpdate();
		    
		    query = "update customers set paymentCount = ? where custId = ? AND warehouseid = ? and stationid = ?";
		    prepStatement = connection.prepareStatement(query);
		    
		    prepStatement.setDouble(1, myDataGenerator.myCustomer.get(index).paymentCount);
		    prepStatement.setInt(2, cID);
		    prepStatement.setInt(3, wID);
		    prepStatement.setInt(4, sID);
		    
		    prepStatement.executeUpdate();
		    
		    
		    //handle distStations table
		    
		    distStations tempStation = new distStations();
		    
		    for (int i = 0; i < myDataGenerator.totalNumOfStations; i++)
		    {
		        tempStation = myDataGenerator.myStation.get(i);
		        if (tempStation.stationID == sID && tempStation.warehouseID == wID)
		        {
		            myDataGenerator.myStation.get(i).salesSum += payment;
		            
		            index = i;
		            break;
		        }
		    }
		    
		    query = "update distStations set salesSum = ? where stationID = ? AND warehouseid = ?";
		    prepStatement = connection.prepareStatement(query);
		    
		    prepStatement.setDouble(1, myDataGenerator.myStation.get(index).salesSum);
		    prepStatement.setInt(2, sID);
		    prepStatement.setInt(3, wID);
		    
		    prepStatement.executeUpdate();
		    
		    
		    //handle warehouse table
		    
		    warehouses tempWarehouse = new warehouses();
		    
		    for (int i = 0; i < myDataGenerator.numOfWarehouses; i++)
		    {
		        tempWarehouse = myDataGenerator.myWarehouse.get(i);
		        
		        if (tempWarehouse.warehouseID == wID)
		        {
		            myDataGenerator.myWarehouse.get(i).salesSum += payment;
		            index = i;
		            break;
		        }
		    }
		    
		    query = "update warehouses set salesSum = ? where warehouseid = ?";
		    prepStatement = connection.prepareStatement(query);
		    
		    prepStatement.setDouble(1, myDataGenerator.myWarehouse.get(index).salesSum);
		    prepStatement.setInt(2, wID);
		    
		    prepStatement.executeUpdate();
		    
		    //Important!
		    //commit the transaction
		    connection.commit();
		}
	    catch(Exception Ex)  
		{
			System.out.println("Machine Error: " +
					   Ex.toString());
			System.out.println("Maybe the transaction does not committed successfully. Not required for this project to know what is in fact wrong.");
		}
	    finally{
			try {
				if (prepStatement != null) prepStatement.close();
			    //Important!
			    //close the local connection
			    connection.close();

			} catch (SQLException e) {
				System.out.println("Cannot close Statement. Machine error: "+e.toString());
			}
		}
	}

	protected void orderStatusTransaction() throws SQLException
	{	
		PreparedStatement prepStatement = null;
		String query = "";
		Connection connection = null;

		//Open local connection for each transaction
		try{

			//System.out.println("Order status tranx greenlight" + greenLight);
			while (!getGreenLight()) yield();

			// Register the oracle driver.  
		    DriverManager.registerDriver (new oracle.jdbc.driver.OracleDriver());
		    
		    //This is the location of the database.  This is the database in oracle
		    //provided to the class
		    String url = "jdbc:oracle:thin:@class3.cs.pitt.edu:1521:dbclass"; 
		    
		    //create a connection to DB on class3.cs.pitt.edu
		    connection = DriverManager.getConnection(url, userName, password); 

			connection.setAutoCommit(false);//disable auto-commit for each transaction
		    
		    //set transaction concurrency level to TRANSACTION_READ_COMMITTED
			connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

		    int wID = generateRandomNumberWithinRange(0, initialNumWarehouses - 1);
			int sID = generateRandomNumberWithinRange(0, initialNumDistStations - 1);
		    int cID = generateRandomNumberWithinRange(0, initialNumCustomers - 1);
		    
		    orders tempOrder = new orders();
		    long maxDate = 0;
		    int index = 0;
		    int orderID = 0;
		    for (int i = 0; i < myDataGenerator.totalNumOfOrders; i++)
		    {
		        tempOrder  = myDataGenerator.myOrder.get(i);
		        
		        if (tempOrder.custID == cID && tempOrder.stationID == sID && tempOrder.warehouseID == wID && tempOrder.orderPlaceDate > maxDate)
		        {
		            maxDate = tempOrder.orderPlaceDate;
		            orderID = tempOrder.orderID;
		            index = i; //get index of most recent order place date
		        }
		    }
		    
		    lineItems tempLineItem = new lineItems();
		    
		    for (int i = 0; i < myDataGenerator.totalNumOfLineItems; i++)
		    {
		        tempLineItem = myDataGenerator.myLineItem.get(i);
		        
		        if(tempLineItem.orderID == orderID && tempLineItem.custID == cID && tempLineItem.stationID == sID && tempLineItem.warehouseID == wID)
		        {
		            System.out.println();
		            System.out.println("Item Number: " + tempLineItem.itemID);
		            System.out.println("Quantity: " + tempLineItem.quantity);
		            System.out.println("Amount Due: " + tempLineItem.amountDue);
		            System.out.println("Delivery Date: " + new java.sql.Date(tempLineItem.deliveryDate));
		        }
		    }  

		    //Important!
		    //commit the transaction
		    connection.commit();
		}
	    catch(Exception Ex)  
		{
			System.out.println("Machine Error: " +
					   Ex.toString());
			System.out.println("Maybe the transaction does not committed successfully. Not required for this project to know what is in fact wrong.");
		}
	    finally{
			try {
				if (prepStatement != null) prepStatement.close();
			    //Important!
			    //close the local connection
			    connection.close();

			} catch (SQLException e) {
				System.out.println("Cannot close Statement. Machine error: "+e.toString());
			}
		}
	}

	protected void deliveryTransaction() throws SQLException
	{	
		PreparedStatement prepStatement = null;
		String query = "";
		Connection connection = null;
		
		//Open local connection for each transaction

		try{

			//System.out.println("Delivery tranx greenlight" + greenLight);
			while (!getGreenLight()) yield();
        

			// Register the oracle driver.  
		    DriverManager.registerDriver (new oracle.jdbc.driver.OracleDriver());
		    
		    //This is the location of the database.  This is the database in oracle
		    //provided to the class
		    String url = "jdbc:oracle:thin:@class3.cs.pitt.edu:1521:dbclass"; 
		    
		    //create a connection to DB on class3.cs.pitt.edu
		    connection = DriverManager.getConnection(url, userName, password); 

			connection.setAutoCommit(false);//disable auto-commit for each transaction
	    
	    	//set transaction concurrency level to TRANSACTION_READ_COMMITTED
			connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

		    int wID = generateRandomNumberWithinRange(0, initialNumWarehouses - 1);

		    //System.out.println("Thread id is: " + myThreadId);
		    
		    //System.out.println("new order tranx wID is: " + wID);

		    warehouses tempWarehouse = new warehouses();
		    int index = 0;
		    double tax = 0;
		    
		    //get tax rate for the warehouse
		    for (int i = 0; i < myDataGenerator.numOfWarehouses; i++)
		    {
		        tempWarehouse = myDataGenerator.myWarehouse.get(i);
		        if (tempWarehouse.warehouseID == wID)
		        {
		            index = i;
		            tax = tempWarehouse.salesTax;
		            break;
		        }
		    }
		    
		    lineItems tempLineItem = new lineItems();
		    long longDate = Calendar.getInstance().getTime().getTime();
		    java.sql.Date dateOrderPlaced = new java.sql.Date(longDate);
		    
		    //update lineItems
		    query = "update lineitems set deliveryDate = ? where warehouseid = ? and deliverydate is null";
		    prepStatement = connection.prepareStatement(query);
		    
		    prepStatement.setDate(1, dateOrderPlaced);
		    prepStatement.setInt(2, wID);
		    
		    prepStatement.executeUpdate();
		    
		    int sID = 0;
		    double discount = 0;
		    index = 0;
		    double balance = 0;
		    
		    for (int i = 0; i < myDataGenerator.totalNumOfLineItems; i++)
		    {
		        tempLineItem = myDataGenerator.myLineItem.get(i);
		        
		        if (tempLineItem.warehouseID == wID && tempLineItem.deliveryDate == -1)
		        {
		            myDataGenerator.myLineItem.get(i).deliveryDate = longDate;
		            sID = tempLineItem.stationID;
		            
		            customers tempCustomer = new customers();
		            //get customer discount rate;
		            for (int j = 0; j < myDataGenerator.totalNumOfCustomers; j++)
		            {
		                tempCustomer = myDataGenerator.myCustomer.get(j);
		                if (tempCustomer.stationID == sID && tempCustomer.warehouseID == wID && tempCustomer.custID == tempLineItem.custID)
		                {
		                    discount = tempCustomer.discount;
		                    index = j;
		                    break;
		                }
		            }
		            
		            balance = tempLineItem.amountDue * (1-discount/100) * (1 + tax/100);
		            myDataGenerator.myCustomer.get(index).balance += balance;
		            myDataGenerator.myCustomer.get(index).deliveryCount++;
		            
		            //update customers
		            query = "update customers set balance = ? where warehouseid = ? and stationid = ? and custid = ?";
		            prepStatement = connection.prepareStatement(query);
		            
		            prepStatement.setDouble(1, myDataGenerator.myCustomer.get(index).balance);
		            prepStatement.setInt(2, wID);
		            prepStatement.setInt(3, sID);
		            prepStatement.setInt(4, tempLineItem.custID);
		            
		            prepStatement.executeUpdate();
		            
		            //update customers
		            query = "update customers set deliveryCount = ? where warehouseid = ? and stationid = ? and custid = ?";
		            prepStatement = connection.prepareStatement(query);
		            
		            prepStatement.setDouble(1, myDataGenerator.myCustomer.get(index).deliveryCount);
		            prepStatement.setInt(2, wID);
		            prepStatement.setInt(3, sID);
		            prepStatement.setInt(4, tempLineItem.custID);
		            
		            prepStatement.executeUpdate();
		            
		        }
		    }

		    //Important!
	    	//commit the transaction
	    	connection.commit();
		}
		catch(Exception Ex)  
		{
			System.out.println("Machine Error: " +
					   Ex.toString());
			System.out.println("Maybe the transaction does not committed successfully. Not required for this project to know what is in fact wrong.");
		}
	    finally{
			try {
				if (prepStatement != null) prepStatement.close();
			    //Important!
			    //close the local connection
			    connection.close();

			} catch (SQLException e) {
				System.out.println("Cannot close Statement. Machine error: "+e.toString());
			}
		}
	}

	protected void stockLevelTransaction() throws SQLException
	{	
		PreparedStatement prepStatement = null;
		String query = "";
		Connection connection = null;

		//Open local connection for each transaction
		try {

			//System.out.println("Stock tranx greenlight" + greenLight);
			while (!getGreenLight()) yield();

			// Register the oracle driver.  
		    DriverManager.registerDriver (new oracle.jdbc.driver.OracleDriver());
		    
		    //This is the location of the database.  This is the database in oracle
		    //provided to the class
		    String url = "jdbc:oracle:thin:@class3.cs.pitt.edu:1521:dbclass"; 
		    
		    //create a connection to DB on class3.cs.pitt.edu
		    connection = DriverManager.getConnection(url, userName, password);

			connection.setAutoCommit(false);//disable auto-commit for each transaction
	    
	    	//set transaction concurrency level to TRANSACTION_READ_COMMITTED
			connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
		    
		    int wID = generateRandomNumberWithinRange(0, initialNumWarehouses - 1);
			int sID = generateRandomNumberWithinRange(0, initialNumDistStations - 1);

			//Make the threshold to be the total stock which is 200
			int threshold = 200;

		    ArrayList<orders> copy = new ArrayList<orders>(myDataGenerator.myOrder.size());
		    
		    orders newOrder = new orders();
		    for (orders tempOrder: myDataGenerator.myOrder)
		    {
		        newOrder = new orders();
		        newOrder.orderID = tempOrder.orderID;
		        newOrder.custID = tempOrder.custID;
		        newOrder.stationID = tempOrder.stationID;
		        newOrder.warehouseID = tempOrder.warehouseID;
		        newOrder.orderPlaceDate = tempOrder.orderPlaceDate;
		        newOrder.completed = tempOrder.completed;
		        newOrder.lineItemCount = tempOrder.lineItemCount;
		        
		        copy.add(newOrder);
		    }
		    
		    Collections.sort(copy);
		    
		    lineItems tempLineItem = new lineItems();
		    int count20 = 0; //keep track of last 20 orders
		    int countUnder = 0; //number of items under threshold
		    stock tempStock = new stock();
		    int[] itemID = new int[myDataGenerator.numOfItems];
		    
		    for (int i = 0; i < itemID.length; i++)
		    {
		        itemID[i] = -1;
		    }
		    
		    int index = 0;
		    for (int i = 0; i < copy.size(); i++)
		    {
		        if (count20 == 20)
		        {
		            break;
		        }
		        
		        if (copy.get(i).warehouseID == wID && copy.get(i).stationID == sID)
		        {
		            for (int j = 0; j < myDataGenerator.totalNumOfLineItems; j++)
		            {
		                tempLineItem = myDataGenerator.myLineItem.get(j);
		                
		                if (tempLineItem.orderID == copy.get(i).orderID && tempLineItem.custID == copy.get(i).custID && tempLineItem.stationID == copy.get(i).stationID && tempLineItem.warehouseID == wID)
		                {
		                    for (int k = 0; k < myDataGenerator.totalNumOfStocks; k++)
		                    {
		                        tempStock = myDataGenerator.myStock.get(k);
		                        if (tempLineItem.itemID == tempStock.itemID && tempStock.warehouseID == wID && tempStock.stock < threshold)
		                        {
		                            boolean contains = false;
		                            for (int m = 0; m < itemID.length; m++)
		                            {
		                                if (itemID[m] == tempStock.itemID)
		                                {
		                                    contains = true;
		                                    break;
		                                }
		                            }
		                            
		                            if (contains)
		                            {
		                                break;
		                            }
		                            else
		                            {
		                                itemID[index] = tempStock.itemID;
		                                index++;
		                                countUnder++;
		                                break;
		                            }
		                        }
		                    }
		                }
		            }
		            count20++;
		        }
		         
		    }
		    
		    System.out.println();
		    System.out.println("Count of number of items under threshold " + threshold + " for the given unique distribution station: \n" + countUnder);
		    System.out.println();  

		    //Important!
		    //commit the transaction
		    connection.commit();
		}
	    catch(Exception Ex)  
		{
			System.out.println("Machine Error: " +
					   Ex.toString());
			System.out.println("Maybe the transaction does not committed successfully. Not required for this project to know what is in fact wrong.");
		}
	    finally{
			try {
				if (prepStatement != null) prepStatement.close();
			    //Important!
			    //close the local connection
			    connection.close();

			} catch (SQLException e) {
				System.out.println("Cannot close Statement. Machine error: "+e.toString());
			}
		}
	}

	protected void reinitDb() throws SQLException
	{
	    // Drop exisiting tables and recreate them
	    dropAndCreateTables();
	    
	    // Create data based off of initial state and insert into tables
	    // In Milestone 2 write-up, it states that the database should be re-initialized to
	    //   a certain number of each attribute, but because of the size of the data, it
	    //   was deemed unnecessary to scale that large. Instead, we go back to the state entered
	    //   by the user. If you wanted to adhere to the milestone write-up, the initial variables
	    //  could be replaced with constants.
	    myDataGenerator = new dataGenerator();
	    myDataGenerator.numOfWarehouses = initialNumWarehouses;
	    myDataGenerator.numOfStations = initialNumDistStations;
	    myDataGenerator.numOfCustomers = initialNumCustomers;
	    myDataGenerator.numOfOrders = initialNumOrders;
	    myDataGenerator.numOfItems = initialNumItems;
	    myDataGenerator.numOfLineItems = initialNumLineItems;
	    
	    myDataGenerator.createSalesTax(myDataGenerator.numOfWarehouses);
	    myDataGenerator.createItemData();
	    myDataGenerator.createLineItemData();
	    myDataGenerator.createOrderData();
	    myDataGenerator.createStock();
	    myDataGenerator.createCustomerData();
	    myDataGenerator.createDistributionStationData();
	    myDataGenerator.createWarehouseData();
	    
	    generateInitalData();
	}

	//Assign thread id
	public finalDriver(int threadId) 
	{ 
		myThreadId = threadId; 
		//System.out.println("threadID is -> " + myThreadId);
	}

	static boolean greenLight = false;
  	static synchronized void setGreenLight() { greenLight = true; }
  	synchronized boolean getGreenLight() { return greenLight; }

  	//Inclusive - both low and high
    protected int generateRandomNumberWithinRange(int low, int high)
    {   
        if(low == high)
        {
            return high;
        }

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