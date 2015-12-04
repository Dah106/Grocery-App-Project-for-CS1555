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
import java.util.Scanner;
import java.util.InputMismatchException;

public class groceryDeliveryApp {
	

	private static Connection connection; //used to hold the jdbc connection to the DB
    private Statement statement; //used to create an instance of the connection

    private PreparedStatement prepStatement; //used to create a prepared statement, that will be later reused
    //private ResultSet resultSet; //used to hold the result of your query if one exists
    private String query;  //this will hold the query we are using

    protected String userName;
    protected String password;

    private dataGenerator myDataGenerator;

    // This is how you can specify the format for the dates you will use
	SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-mm-dd");

    public static void main(String[] args) throws SQLException, ParseException
    {	
    	groceryDeliveryApp myApp = new groceryDeliveryApp();
    	myApp.initSystem();
    	myApp.generateInitalData();
    	/*
		 * NOTE: the connection should be created once and used through out the whole project;
		 * Is very expensive to open a connection therefore you should not close it after every operation on database
		 */
		connection.close();
    }

    private void initSystem() throws SQLException
    {	
    	myDataGenerator = new dataGenerator();

	    Scanner reader = new Scanner(System.in);  // Reading from System.in
		
		System.out.println("Welcome to our online order system!");
		
		System.out.print("Please enter the username: ");
		userName = reader.next();
		
		System.out.print("Please enter the password: ");
		password = reader.next();
		
		System.out.print("Please enter the number of warehouses for the system: ");
		myDataGenerator.numOfWarehouses = reader.nextInt();

		System.out.print("Please enter the number of distribution stations per warehouse: ");
		myDataGenerator.numOfStations = reader.nextInt();

		System.out.print("Please enter the number of customers per distribution station: ");
		myDataGenerator.numOfCustomers = reader.nextInt();

		System.out.print("Please enter the number of orders per customer: ");
		myDataGenerator.numOfOrders = reader.nextInt();

		System.out.print("Please enter the number of items per warehouse: ");
		myDataGenerator.numOfItems = reader.nextInt();

		System.out.print("Please enter the number of line items per order: ");
		myDataGenerator.numOfLineItems = reader.nextInt();

		reader.close();

 	
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

    
}