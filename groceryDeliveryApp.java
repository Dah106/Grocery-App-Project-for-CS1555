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
    private ResultSet resultSet; //used to hold the result of your query if one exists
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
		myDataGenerator.numOfDistStations = reader.nextInt();

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

		myDataGenerator.createWarehouseData();
 		myDataGenerator.createDistributionStationData();
 		myDataGenerator.createCustomerData();
 		myDataGenerator.createOrderData();
 		myDataGenerator.createItemAndData();
 		myDataGenerator.createLineItemData();
    }

    private void generateInitalData()
    {
    	int counter = 1;
		try{
		   	
		    statement = connection.createStatement(); //create an instance

		    //This is how you can specify the format for the dates you will use
        	SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-mm-dd");

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

	      	/* table 'distStations' has nine attributes */
	      	query = "insert into distStations values (?,?,?,?,?,?,?,?,?)";
	      	prepStatement = connection.prepareStatement(query);

	      	for(int tempWarehouseID = 0;tempWarehouseID < myDataGenerator.numOfWarehouses;tempWarehouseID++)
	      	{	
	      		warehouses tempWarehouse = myDataGenerator.myWarehouse.get(tempWarehouseID);

		      	for(int tempStationID = 0;tempStationID < myDataGenerator.numOfDistStations;tempStationID++)
		      	{	
		      		 distStations tempDistStation = tempWarehouse.myStations.get(tempStationID);

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
	      	}

	      	/* table 'customers' has nine attributes */
	      	query = "insert into customers values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	      	prepStatement = connection.prepareStatement(query);

	      	for(int tempWarehouseID = 0;tempWarehouseID < myDataGenerator.numOfWarehouses;tempWarehouseID++)
	      	{	
	      		warehouses tempWarehouse = myDataGenerator.myWarehouse.get(tempWarehouseID);

		      	for(int tempStationID = 0;tempStationID < myDataGenerator.numOfDistStations;tempStationID++)
		      	{	
		      		 distStations tempDistStation = tempWarehouse.myStations.get(tempStationID);

			      	for(int tempCustomerID = 0; tempCustomerID < myDataGenerator.numOfCustomers;tempCustomerID++)
			      	{	
			      		customers tempCustomer = tempDistStation.myCustomers.get(tempCustomerID);

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

					    long accountOpenDate = tempCustomer.accountOpenDate;
					    java.sql.Date dateRegistered = new java.sql.Date (accountOpenDate);
					    
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
					    prepStatement.setDate(12, dateRegistered); 
					    prepStatement.setDouble(13, discount); 
					    prepStatement.setDouble(14, balance); 
					    prepStatement.setDouble(15, paid); 
					    prepStatement.setInt(16, paymentCount); 
					    prepStatement.setInt(17, deliveryCount); 

				      	prepStatement.executeUpdate();
			      	}
			    }
			}


	      	query = "insert into orders values (?,?,?,?,?,?,?)";
	      	prepStatement = connection.prepareStatement(query);
	      	for(int tempWarehouseID = 0;tempWarehouseID < myDataGenerator.numOfWarehouses;tempWarehouseID++)
	      	{	
	      		warehouses tempWarehouse = myDataGenerator.myWarehouse.get(tempWarehouseID);

		      	for(int tempStationID = 0;tempStationID < myDataGenerator.numOfDistStations;tempStationID++)
		      	{	
		      		 distStations tempDistStation = tempWarehouse.myStations.get(tempStationID);

			      	for(int tempCustomerID = 0; tempCustomerID < myDataGenerator.numOfCustomers;tempCustomerID++)
			      	{	
			      		customers tempCustomer = tempDistStation.myCustomers.get(tempCustomerID);

				      	for(int tempOrderID = 0; tempOrderID < myDataGenerator.numOfOrders;tempOrderID++)
				      	{	
				      		orders tempOrder = tempCustomer.myOrders.get(tempOrderID);

				      		int orderID = tempOrder.orderID;
				      		int custID = tempOrder.custID;
				      		int stationID = tempOrder.stationID;
				      		int warehouseID = tempOrder.warehouseID; 

							long dateOrderPlaced = tempOrder.orderPlaceDate;
						    java.sql.Date orderPlaceDate = new java.sql.Date (dateOrderPlaced);

						    int completed = tempOrder.completed;
						    int lineItemCount = tempOrder.lineItemCount;


						    prepStatement.setInt(1, orderID);
						    prepStatement.setInt(2, custID); 
						    prepStatement.setInt(3, stationID); 
						    prepStatement.setInt(4, warehouseID); 
						    prepStatement.setDate(5, orderPlaceDate); 
						    prepStatement.setInt(6, completed); 
						    prepStatement.setInt(7, lineItemCount);

						    prepStatement.executeUpdate();
				      	}
				    }
				}
			}


	      	query = "insert into items values (?,?,?,?,?,?)";
	      	prepStatement = connection.prepareStatement(query);

	      	for(int tempWarehouseID = 0;tempWarehouseID < myDataGenerator.numOfWarehouses;tempWarehouseID++)
	      	{	
	      		warehouses tempWarehouse = myDataGenerator.myWarehouse.get(tempWarehouseID);

		      	for(int tempItemID = 0;tempItemID < myDataGenerator.numOfItems;tempItemID++)
		      	{	
		      		items tempItem = tempWarehouse.myItems.get(tempItemID);	
		      		
		      		int itemID = tempItem.itemID;
		      		int warehouseID = tempItem.warehouseID;
		      		String name = tempItem.name;
		      		double price = tempItem.price;
		      		int soldCount = tempItem.soldCount;
				    int orderCount = tempItem.orderCount; 

				  
				    prepStatement.setInt(1, itemID);
				    prepStatement.setInt(2, warehouseID); 
				    prepStatement.setString(3, name); 
				    prepStatement.setDouble(4, price); 
				    prepStatement.setInt(5, soldCount); 
				    prepStatement.setInt(6, orderCount);

				    prepStatement.executeUpdate();
	      		}
	      	}

	      	query = "insert into stock values (?,?,?)";
	      	prepStatement = connection.prepareStatement(query);

	      	for(int tempWarehouseID = 0;tempWarehouseID < myDataGenerator.numOfWarehouses;tempWarehouseID++)
	      	{	
	      		warehouses tempWarehouse = myDataGenerator.myWarehouse.get(tempWarehouseID);

		      	for(int tempItemID = 0;tempItemID < myDataGenerator.numOfItems;tempItemID++)
		      	{	
		      		stock tempStock = tempWarehouse.myStock.get(tempItemID);	
		      		
		      		int itemID = tempStock.itemID;
		      		int warehouseID = tempStock.warehouseID;
		      		int stock = tempStock.stock;

				    prepStatement.setInt(1, itemID);
				    prepStatement.setInt(2, warehouseID); 
				    prepStatement.setInt(3, stock); 

				    prepStatement.executeUpdate();
	      		}
	      	}

	      	query = "insert into lineItems values (?,?,?,?,?,?,?,?,?)";
	      	prepStatement = connection.prepareStatement(query);

	      	for(int tempWarehouseID = 0;tempWarehouseID < myDataGenerator.numOfWarehouses;tempWarehouseID++)
	      	{	
	      		warehouses tempWarehouse = myDataGenerator.myWarehouse.get(tempWarehouseID);

		      	for(int tempStationID = 0;tempStationID < myDataGenerator.numOfDistStations;tempStationID++)
		      	{	
		      		 distStations tempDistStation = tempWarehouse.myStations.get(tempStationID);

			      	for(int tempCustomerID = 0; tempCustomerID < myDataGenerator.numOfCustomers;tempCustomerID++)
			      	{	
			      		customers tempCustomer = tempDistStation.myCustomers.get(tempCustomerID);

				      	for(int tempOrderID = 0; tempOrderID < myDataGenerator.numOfOrders;tempOrderID++)
				      	{	
				      		orders tempOrder = tempCustomer.myOrders.get(tempOrderID);
				      		
					      	for(int tempLineItemID = 0; tempLineItemID < myDataGenerator.numOfLineItems;tempLineItemID++)
					      	{		
					      		lineItems tempLineItem = tempOrder.myLineItems.get(tempLineItemID);

					      		int lineItemID = tempLineItem.lineItemID;
					      		int itemID = tempLineItem.itemID;
					      		int orderID = tempLineItem.orderID;
					      		int custID = tempLineItem.custID;
					      		int stationID = tempLineItem.stationID;
					      		int warehouseID = tempLineItem.warehouseID;
					      		int quantity = tempLineItem.quantity;
							    double amountDue = tempLineItem.amountDue; 

								long dateDelivered = tempLineItem.deliveryDate;
							    java.sql.Date orderPlaceDate = new java.sql.Date (dateDelivered);
							  
							    prepStatement.setInt(1, lineItemID);
							    prepStatement.setInt(2, itemID); 
							    prepStatement.setInt(3, orderID); 
							    prepStatement.setInt(4, custID); 
							    prepStatement.setInt(5, stationID); 
							    prepStatement.setInt(6, warehouseID); 
							    prepStatement.setInt(7, quantity);
							    prepStatement.setDouble(8, amountDue);
							    prepStatement.setDate(9, orderPlaceDate);

							    prepStatement.executeUpdate();
					      	}
					    }
					}
				}
			}


		    /*
		     * The preparedStatement can be and should be reused instead of creating a new object.
		     * NOTE that when you have many insert statements (more than 300), creating a new statement 
		     * for every insert will end up in throwing an error.
		     */
		   	
		   	String selectQuery = "SELECT * FROM warehouses"; //sample query
	    
	    	resultSet = statement.executeQuery(selectQuery); //run the query on the DB table

		    System.out.println("\nAfter the insert, data is...\n");
		    counter=1;
		    System.out.println("Table 'warehouses': ");
		    System.out.println("\n");
		    while(resultSet.next()) {
			System.out.println("Record " + counter + ": " +
					   resultSet.getInt(1) + ", " +
					   resultSet.getString(2) + ", " +
					   resultSet.getString(3) + ", " +
					   resultSet.getString(4) + ", " +
					   resultSet.getString(5) + ", " +
					   resultSet.getString(6) + ", " +
					   resultSet.getDouble(7) + ", " +
					   resultSet.getDouble(8));
			counter ++;
		    }

			selectQuery = "SELECT * FROM distStations"; //sample query
	    
	    	resultSet = statement.executeQuery(selectQuery); //run the query on the DB table

		    System.out.println("\nAfter the insert, data is...\n");
		    counter=1;
		    System.out.println("Table 'distStations': ");
		    System.out.println("\n");
		    while(resultSet.next()) {
			System.out.println("Record " + counter + ": " +
					   resultSet.getInt(1) + ", " +
					   resultSet.getString(2) + ", " +
					   resultSet.getString(3) + ", " +
					   resultSet.getString(4) + ", " +
					   resultSet.getString(5) + ", " +
					   resultSet.getString(6) + ", " +
					   resultSet.getDouble(7) + ", " +
					   resultSet.getDouble(8) + ", " + 
					   resultSet.getDouble(9));
			counter ++;
		    }

		    selectQuery = "SELECT * FROM customers"; //sample query
	    
	    	resultSet = statement.executeQuery(selectQuery); //run the query on the DB table

		    System.out.println("\nAfter the insert, data is...\n");
		    counter=1;
		    System.out.println("Table 'customers': ");
		    System.out.println("\n");
		    while(resultSet.next()) {
			System.out.println("Record " + counter + ": " +
					   resultSet.getInt(1) + ", " +
					   resultSet.getInt(2) + ", " +
					   resultSet.getInt(3) + ", " +
					   resultSet.getString(4) + ", " +
					   resultSet.getString(5) + ", " +
					   resultSet.getString(6) + ", " +
					   resultSet.getString(7) + ", " +
					   resultSet.getString(8) + ", " + 
					   resultSet.getString(9) + ", " +
					   resultSet.getString(10) + ", " + 
					   resultSet.getString(11) + ", " + 
					   resultSet.getDate(12) + ", " + 
					   resultSet.getDouble(13) + ", " + 
					   resultSet.getDouble(14) + ", " + 
					   resultSet.getDouble(15) + ", " + 
					   resultSet.getInt(16) + ", " + 
					   resultSet.getInt(17));
			counter ++;
		    }

		    selectQuery = "SELECT * FROM orders"; //sample query
	    
	    	resultSet = statement.executeQuery(selectQuery); //run the query on the DB table

		    System.out.println("\nAfter the insert, data is...\n");
		    counter=1;
		    System.out.println("Table 'orders': ");
		    System.out.println("\n");
		    while(resultSet.next()) {
			System.out.println("Record " + counter + ": " +
					   resultSet.getInt(1) + ", " +
					   resultSet.getInt(2) + ", " +
					   resultSet.getInt(3) + ", " +
					   resultSet.getString(4) + ", " +
					   resultSet.getDate(5) + ", " +
					   resultSet.getString(6) + ", " +
					   resultSet.getString(7));
			counter ++;
		    }

		    selectQuery = "SELECT * FROM items"; //sample query
	    
	    	resultSet = statement.executeQuery(selectQuery); //run the query on the DB table

		    System.out.println("\nAfter the insert, data is...\n");
		    counter=1;
		    System.out.println("Table 'items': ");
		    System.out.println("\n");
		    while(resultSet.next()) {
			System.out.println("Record " + counter + ": " +
					   resultSet.getInt(1) + ", " +
					   resultSet.getInt(2) + ", " +
					   resultSet.getString(3) + ", " +
					   resultSet.getDouble(4) + ", " +
					   resultSet.getInt(5) + ", " +
					   resultSet.getInt(6));
			counter ++;
		    }

		    selectQuery = "SELECT * FROM stock"; //sample query
	    
	    	resultSet = statement.executeQuery(selectQuery); //run the query on the DB table

		    System.out.println("\nAfter the insert, data is...\n");
		    counter=1;
		    System.out.println("Table 'stock': ");
		    System.out.println("\n");
		    while(resultSet.next()) {
			System.out.println("Record " + counter + ": " +
					   resultSet.getInt(1) + ", " +
					   resultSet.getInt(2) + ", " +
					   resultSet.getInt(3));
			counter ++;
		    }


		    selectQuery = "SELECT * FROM lineItems"; //sample query
	    
	    	resultSet = statement.executeQuery(selectQuery); //run the query on the DB table

		    System.out.println("\nAfter the insert, data is...\n");
		    counter=1;
		    System.out.println("Table 'lineItems': ");
		    System.out.println("\n");
		    while(resultSet.next()) {
			System.out.println("Record " + counter + ": " +
					   resultSet.getInt(1) + ", " +
					   resultSet.getInt(2) + ", " +
					   resultSet.getInt(3) + ", " +
					   resultSet.getInt(4) + ", " +
					   resultSet.getInt(5) + ", " +
					   resultSet.getInt(6) + ", " +
					   resultSet.getInt(7) + ", " +
					   resultSet.getDouble(8) + ", " +
					   resultSet.getDate(9) );
			counter ++;
		    }
		    resultSet.close();
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