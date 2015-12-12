/*
	Created by Danchen, Muneeb & Jeffery
	Date created: 14/11/2015 19:55 P.M EST
	Last modified by 18/11/2015 12:41 A.M EST

	This is the main program for milestone 1
	Purpose: JDBC for CS1555 term project milestone 1

	#####################################################################################
	To configure environment:
    Set the PATH and CLASSPATH environmental variables 
    to point to JAVA and Oracle JDBC library: source ~panos/1555/bash.env
    run createSchema.sql

    To compile and run:
    javac groceryDeliveryApp.java
    java groceryDeliveryApp
	#####################################################################################
	Useful sql commands:
    check what tables you have in the database: select table_name from user_tables;
*/

import java.sql.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Scanner;
import java.util.InputMismatchException;
import java.util.List;

public class groceryDeliveryApp{
	

	private static Connection connection; //used to hold the jdbc connection to the DB
    private Statement statement; //used to create an instance of the connection

    private PreparedStatement prepStatement; //used to create a prepared statement, that will be later reused
    private ResultSet resultSet; //used to hold the result of your query if one exists
    private String query;  //this will hold the query we are using

    protected String userName;
    protected String password;
    private static Scanner reader;
    private dataGenerator myDataGenerator;
	
	private int initialNumWarehouses;
	private int initialNumDistStations;
	private int initialNumCustomers;
	private int initialNumOrders;
	private int initialNumItems;
	private int initialNumLineItems;

    // This is how you can specify the format for the dates you will use
	SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-mm-dd");

    public static void main(String[] args) throws SQLException, ParseException
    {	
        reader = new Scanner(System.in);
    	groceryDeliveryApp myApp = new groceryDeliveryApp();
    	myApp.initSystem();
		myApp.dropAndCreateTables();
    	myApp.generateInitalData();
        myApp.transactions();
        reader.close();
    	/*
		 * NOTE: the connection should be created once and used through out the whole project;
		 * Is very expensive to open a connection therefore you should not close it after every operation on database
		 */
		connection.close();
    }

    private void initSystem() throws SQLException
    {	
    	myDataGenerator = new dataGenerator();

	    //Scanner reader = new Scanner(System.in);  // Reading from System.in
		
		System.out.println("Welcome to our online order system!");
		
		System.out.print("Please enter the username: ");
		userName = reader.next();
		
		System.out.print("Please enter the password: ");
		password = reader.next();
		
		System.out.print("Please enter the number of warehouses for the system: ");
		initialNumWarehouses = reader.nextInt();
		myDataGenerator.numOfWarehouses = initialNumWarehouses;

		System.out.print("Please enter the number of distribution stations per warehouse: ");
		initialNumDistStations = reader.nextInt();
		myDataGenerator.numOfStations = initialNumDistStations;

		System.out.print("Please enter the number of customers per distribution station: ");
		initialNumCustomers = reader.nextInt();
		myDataGenerator.numOfCustomers = initialNumCustomers;

		System.out.print("Please enter the number of orders per customer: ");
		initialNumOrders = reader.nextInt();
		myDataGenerator.numOfOrders = initialNumOrders;

		System.out.print("Please enter the number of items per warehouse: ");
		initialNumItems = reader.nextInt();
		myDataGenerator.numOfItems = initialNumItems;

		System.out.print("Please enter the number of line items per order: ");
		initialNumLineItems = reader.nextInt();
		myDataGenerator.numOfLineItems = initialNumLineItems;

		//reader.close();

 	
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

		myDataGenerator.createSalesTax(myDataGenerator.numOfWarehouses);

        myDataGenerator.createItemData();
        myDataGenerator.createLineItemData();
        myDataGenerator.createOrderData();
        myDataGenerator.createStock();
        myDataGenerator.createCustomerData();
        myDataGenerator.createDistributionStationData();
        myDataGenerator.createWarehouseData();
    }

    private void generateInitalData()
    {
    	int counter = 1;
		try{
		   	
		   	connection.setAutoCommit(false);//disable auto-commit for each transaction
		    statement = connection.createStatement(); //create an instance

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

	      	connection.commit();

	    }
		catch(SQLException Ex) {
		    System.out.println("Error running the queries. Machine Error: " + Ex.toString());

		} 
		// catch (ParseException e) {
		// 	System.out.println("Error parsing the date. Machine Error: " + e.toString());

		// }
		finally{
			try {
				if (statement != null) statement.close();
				if (prepStatement != null) prepStatement.close();
			} catch (SQLException e) {
				System.out.println("Cannot close Statement. Machine error: "+e.toString());
			}
		}
    }
	
	private void transactions() throws SQLException
	{
		//Scanner reader = new Scanner(System.in);  // Reading from System.in
		
                int choice = 0;
                
                while (true)
                {
                    System.out.println();
                    System.out.println("Which transaction would you like to execute:");
                    System.out.println("1 for New Order Transaction");
                    System.out.println("2 for Payment Transaction");
                    System.out.println("3 for Order Status Transaction");
                    System.out.println("4 for Delivery Transaction");
                    System.out.println("5 for Stock Level Transaction");
					System.out.println("0 to Re-initialize the database");
                    System.out.println("-1 to stop performing transactions");

                    System.out.print("Enter Choice: ");
                    choice = reader.nextInt();


                    if (choice == -1)
                    {
                        break;
                    }
					else if (choice == 0)
					{
						reinitDb();
					}
                    else if (choice == 1)
                    {
                        newOrderTransaction();
                    }
                    else if(choice == 2)
                    {
                        paymentTransaction();
                    }
                    else if (choice == 3)
                    {
                        orderStatusTransaction();
                    }
                    else if (choice == 4)
                    {
                        deliveryTransaction();
                    }
                    else if (choice == 5)
                    {
                        stockLevelTransaction();
                    }
                }
                
                System.out.println();
                System.out.println("Thank you for using our services!!!");
           		
	}
	
	private void dropAndCreateTables() throws SQLException
	{
		// Re-initialize the database back to the original specifications entered by the user
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
            
        } catch(SQLException Ex) {
            System.out.println("Error running re-initialization.  Machine Error: " +
                    Ex.toString());
        } finally{
            try {
                if (statement != null)
                    statement.close();
            } catch (SQLException e) {
                System.out.println("Cannot close Statement. Machine error: "+e.toString());
            }
        }
	}
	
	private void newOrderTransaction() throws SQLException
	{
	    
	    connection.setAutoCommit(false);//disable auto-commit for each transaction
	    statement = connection.createStatement(); //create an instance
	    
	    statement = connection.createStatement();
	    String selectQuery = "SELECT * FROM orders";
	    resultSet = statement.executeQuery(selectQuery);
	    System.out.println("\nBefore transaction");
	    System.out.println("orderID  custID  stationID  warehouseID  orderPlaceDate  completed  lineItemCount");
	    while (resultSet.next()) {
	        System.out.println(resultSet.getInt("orderID") + "   " +
	        resultSet.getInt("custID") + "   " +
	        resultSet.getInt("stationID") + "   " +
	        resultSet.getDouble("warehouseID") + "   " +
	        resultSet.getDate("orderPlaceDate") + "   " +
	        resultSet.getDouble("completed") + "   " +
	        resultSet.getInt("lineItemCount"));
	    }
	    resultSet.close();
	    
	    //Scanner reader = new Scanner(System.in);
	    
	    System.out.println("Enter Customer Warehouse ID:");
	    int wID = reader.nextInt();
	    System.out.println("Enter Customer Station ID");
	    int sID = reader.nextInt();
	    System.out.println("Enter Customer ID");
	    int cID = reader.nextInt();
	    
	    System.out.println("Enter total count of all items ordered:");
	    int totalCount = reader.nextInt();
	    
	    int[] itemID = new int[totalCount];
	    int[] itemCount = new int[totalCount];
	    
	    int count = 0;
	    int id = 0;
	    for (int i = 0; i < totalCount; i++)
	    {
	        System.out.println("Enter Item ID:");
	        id = reader.nextInt();
	        System.out.println("Enter the number of orders for this item:");
	            count = reader.nextInt();
	        
	        itemID[i] = id;
	        itemCount[i] = count;
	    }
	    
	    //handle orders table
	    
	    orders tempOrder;
	    int index = 0; //keep track of latest order
	    
	    
	    for (int i = 0; i < myDataGenerator.totalNumOfOrders; i++)
	    {
	        
	        tempOrder  = myDataGenerator.myOrder.get(i);
	        
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
	    prepStatement.setInt(1, orderID);
	    prepStatement.setInt(2, custID);
	    prepStatement.setInt(3, stationID);
	    prepStatement.setInt(4, warehouseID);
	    prepStatement.setDate(5, dateOrderPlaced);
	    prepStatement.setInt(6, completed);
	    prepStatement.setInt(7, lineItemCount);
	    
	    prepStatement.executeUpdate();
	    
	    
	    
	    tempOrder = new orders();
	    
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
	        
	        Calendar c = Calendar.getInstance();
	        c.setTime(new Date());
	        c.add(Calendar.DATE, 5); //increment 5 days for delivery date
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
	    
	    connection.commit();
	    
	    statement = connection.createStatement();
	    selectQuery = "SELECT * FROM orders";
	    resultSet = statement.executeQuery(selectQuery);
	    System.out.println("\nAfter transaction");
	    System.out.println("orderID  custID  stationID  warehouseID  orderPlaceDate  completed  lineItemCount");
	    while (resultSet.next()) {
	        System.out.println(resultSet.getInt("orderID") + "   " +
	        resultSet.getInt("custID") + "   " +
	        resultSet.getInt("stationID") + "   " +
	        resultSet.getDouble("warehouseID") + "   " +
	        resultSet.getDate("orderPlaceDate") + "   " +
	        resultSet.getDouble("completed") + "   " +
	        resultSet.getInt("lineItemCount"));
	    }
	    resultSet.close();
	}

	private void paymentTransaction() throws SQLException
	{
	    statement = connection.createStatement();
	    String selectQuery = "SELECT * FROM customers";
	    resultSet = statement.executeQuery(selectQuery);
	    System.out.println("\nBefore transaction");
	    System.out.println("\ncustID  stationID  warehouseID  balance  paid  paymentCount");
	    while (resultSet.next()) {
	        System.out.println(resultSet.getInt("custID") + "   " +
	        resultSet.getInt("stationID") + "   " +
	        resultSet.getInt("warehouseID") + "   " +
	        resultSet.getDouble("balance") + "   " +
	        resultSet.getDouble("paid") + "   " +
	        resultSet.getInt("paymentCount"));
	    }
	    resultSet.close();
	    
	    System.out.println("Enter Customer Warehouse ID:");
	    int wID = reader.nextInt();
	    System.out.println("Enter Customer Station ID");
	    int sID = reader.nextInt();
	    System.out.println("Enter Customer ID");
	    int cID = reader.nextInt();
	    
	    System.out.println("Enter payment amount");
	    double payment = reader.nextInt();
	    reader.nextLine();
	    
	    
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
	    
	    connection.commit();
	    
	    statement = connection.createStatement();
	    selectQuery = "SELECT * FROM customers";
	    resultSet = statement.executeQuery(selectQuery);
	    System.out.println("\nAfter transaction");
	    System.out.println("\ncustID  stationID  warehouseID  balance  paid  paymentCount");
	    while (resultSet.next()) {
	        System.out.println(resultSet.getInt("custID") + "   " +
	        resultSet.getInt("stationID") + "   " +
	        resultSet.getInt("warehouseID") + "   " +
	        resultSet.getDouble("balance") + "   " +
	        resultSet.getDouble("paid") + "   " +
	        resultSet.getInt("paymentCount"));
	    }
	    resultSet.close();
	}

	private void orderStatusTransaction()
	{
	    System.out.println("Enter Customer Warehouse ID:");
	    int wID = reader.nextInt();
	    System.out.println("Enter Customer Station ID");
	    int sID = reader.nextInt();
	    System.out.println("Enter Customer ID");
	    int cID = reader.nextInt();
	    reader.nextLine();
	    
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
	}

	private void deliveryTransaction() throws SQLException
	{
	    
	    System.out.println("Enter id of warehouse: ");
	    int wID = reader.nextInt();
	    reader.nextLine();
	    
	    statement = connection.createStatement();
	    String selectQuery = "SELECT * FROM lineItems";
	    resultSet = statement.executeQuery(selectQuery);
	    System.out.println("\nBefore transaction");
	    System.out.println("\nlineitemID  itemID  orderID  custID  stationID  warehouseID quantity amountDue deliveryDate");
	    while (resultSet.next()) {
	        System.out.println(resultSet.getInt("lineitemID") + "   " +
	        resultSet.getInt("itemID") + "   " +
	        resultSet.getInt("orderID") + "   " +
	        resultSet.getInt("custID") + "   " +
	        resultSet.getInt("stationID") + "   " +
	        resultSet.getInt("warehouseID") + "   " +
	        resultSet.getInt("quantity") + "   " +
	        resultSet.getDouble("amountDue") + "   " +
	        resultSet.getDate("deliveryDate"));
	    }
	    resultSet.close();
	    
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
	    
	    connection.commit();
	    
	    statement = connection.createStatement();
	    selectQuery = "SELECT * FROM lineItems";
	    resultSet = statement.executeQuery(selectQuery);
	    System.out.println("\nAfter transaction");
	    System.out.println("\nlineitemID  itemID  orderID  custID  stationID  warehouseID quantity amountDue deliveryDate");
	    while (resultSet.next()) {
	        System.out.println(resultSet.getInt("lineitemID") + "   " +
	        resultSet.getInt("itemID") + "   " +
	        resultSet.getInt("orderID") + "   " +
	        resultSet.getInt("custID") + "   " +
	        resultSet.getInt("stationID") + "   " +
	        resultSet.getInt("warehouseID") + "   " +
	        resultSet.getInt("quantity") + "   " +
	        resultSet.getDouble("amountDue") + "   " +
	        resultSet.getDate("deliveryDate"));
	    }
	    resultSet.close();
	    
	}

	private void stockLevelTransaction()
	{
	    
	    System.out.println("Enter Distribution Station ID: ");
	    int sID = reader.nextInt();
	    System.out.println("Enter Warehouse ID: ");
	    int wID = reader.nextInt();
	    System.out.println("Enter threshold: ");
	    int threshold = reader.nextInt();
	    reader.nextLine();
	    
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
	    System.out.println("Count of number of items under threshold for the given unique distribution station: \n" + countUnder);
	        System.out.println();  
	}

	private void reinitDb() throws SQLException
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
    
}