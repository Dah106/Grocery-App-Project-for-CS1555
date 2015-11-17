/*
	Created by Danchen, Muneeb & Jeffery
	Date created: 14/11/2015 19:55 P.M EST
	Last modified by 16/11/2015 11:41 P.M EST

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

		System.out.print("Please enter the number of distribution stations for the system: ");
		myDataGenerator.numOfDistributionStation= reader.nextInt();

		System.out.print("Please enter the number of customers for the system: ");
		myDataGenerator.numOfCustomers = reader.nextInt();

 		myDataGenerator.createWarehouseData();

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
    }

    private void generateInitalData()
    {
    	int counter = 1;
		try{
		   	
		    statement = connection.createStatement(); //create an instance

		   //   String selectQuery = "SELECT * FROM warehouses"; //sample query
	    
	    // resultSet = statement.executeQuery(selectQuery);

		   	/* table 'warehouses' has eight attributes */
		    query = "insert into warehouses values (?,?,?,?,?,?,?,?)";
		    prepStatement = connection.prepareStatement(query);

		    for(int i = 0; i < myDataGenerator.numOfWarehouses;i++)
		    {	
		    	int warehouseID = myDataGenerator.myWarehouse[i].warehouseID;
			    String name = myDataGenerator.myWarehouse[i].name;
			    String strAddress = myDataGenerator.myWarehouse[i].strAddress;
			    String cityAddress = myDataGenerator.myWarehouse[i].cityAddress;
			    String stateAddress = myDataGenerator.myWarehouse[i].stateAddress;
			    String zipcode = myDataGenerator.myWarehouse[i].zipcode;
			    double salesTax = myDataGenerator.myWarehouse[i].salesTax;
			    double salesSum = myDataGenerator.myWarehouse[i].salesSum;

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


		    // java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd");

		    // java.sql.Date date_reg = new java.sql.Date (df.parse("2012-02-24").getTime());
		    

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