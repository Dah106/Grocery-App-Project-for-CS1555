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
import java.util.ArrayList;

public class warehouses
{	
	protected int warehouseID;
	protected String name;
	protected String strAddress;
	protected String cityAddress;
	protected String stateAddress;
	protected String zipcode;
	protected double salesTax;
	protected double salesSum;

	protected warehouses()
	{
		this.warehouseID = 0;
		this.name = "foo";
		this.strAddress = "foo Ave.";
		this.cityAddress = "foo";
		this.stateAddress = "foo";
		this.zipcode = "12345";
		this.salesTax = 50;
		this.salesSum = 10000;
	}
}