/*
	Created by Danchen, Muneeb & Jeffery
	Date created: 1/12/2015 07:00 A.M EST
	Last modified by 1/12/2015 11:41 P.M EST
	Purpose: JDBC for CS1555 term project milestone 1

	#####################################################################################
	To configure environment:
    Set the PATH and CLASSPATH environmental variables 
    to point to JAVA and Oracle JDBC library: source ~panos/1555/bash.env
    
	#####################################################################################
	Useful sql commands:
    check what tables you have in the database: select table_name from user_tables;
*/
public class stock 
{
	protected int itemID;
	protected int warehouseID;
	protected int stock;

	protected stock()
	{
		this.itemID = 0;
		this.warehouseID = 0;
		this.stock = 0;
	}
}