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
public class lineItems
{
	protected int lineItemID;
	protected int itemID;
	protected int orderID;
	protected int custID;
	protected int stationID;
	protected int warehouseID;
	protected int quantity;
	protected double amountDue; 
	protected long deliveryDate;

	protected lineItems()
	{	
		this.lineItemID = 0;
		this.itemID = 0;
		this.orderID = 0;
		this.custID = 0;
		this.stationID = 0;
		this.warehouseID = 0;
		this.quantity = 0;
		this.amountDue = 0;
		this.deliveryDate = 0;
	}

}