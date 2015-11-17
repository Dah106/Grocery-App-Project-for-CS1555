/*
	Created by Danchen, Muneeb & Jeffery
	Date created: 16/11/2015 21:00 P.M EST
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

public class warehouses
{
	protected warehouses()
	{
		warehouseID = 0;
		name = "foo";
		strAddress = "foo Ave.";
		cityAddress = "foo";
		stateAddress = "foo";
		zipcode = "12345";
		salesTax = 50;
		salesSum = 10000;
	}

	protected int warehouseID;
	protected String name;
	protected String strAddress;
	protected String cityAddress;
	protected String stateAddress;
	protected String zipcode;
	protected double salesTax;
	protected double salesSum;
}