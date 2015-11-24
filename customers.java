/*
	Created by Danchen, Muneeb & Jeffery
	Date created: 17/11/2015 07:00 A.M EST
	Last modified by 17/11/2015 11:41 P.M EST
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

public class customers
{
   
	protected int custID;
	protected int stationID;
	protected int warehouseID;
	protected String fname;
	protected String MI;
	protected String lname;
	protected String strAddress;
	protected String cityAddress;
	protected String stateAddress;
	protected String zipcode;
	protected String phone;
	protected long accountOpenDate;
	protected double discount;
	protected double balance;
	protected double paid;
	protected int paymentCount;
	protected int deliveryCount;
	protected ArrayList<orders> myOrders;

	protected customers()
	{	
		this.custID = 0;
		this.stationID = 0;
		this.warehouseID = 0;
		this.fname = "foo";
		this.MI = "s";
		this.lname = "bar";
		this.strAddress = "foo Ave.";
		this.cityAddress = "foo";
		this.stateAddress = "foo";
		this.zipcode = "12345";
		this.phone = "1234567890";
		this.accountOpenDate = 0;
		this.discount = 0;
		this.balance = 0;
		this.paid = 0;
		this.paymentCount = 0;
		this.deliveryCount = 0;
		myOrders = new ArrayList<orders>();
	}
}