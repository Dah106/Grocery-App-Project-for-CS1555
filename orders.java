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

public class orders
{	
	protected int orderID;
	protected int custID;
	protected int stationID;
	protected int warehouseID;
	protected long orderPlaceDate;
	protected int completed; 
	protected int lineItemCount;
	protected ArrayList<lineItems> myLineItems;

	protected orders()
	{	
		this.orderID = 0;
		this.custID = 0;
		this.stationID = 0;
		this.warehouseID = 0;
		this.orderPlaceDate = 0;
		this.completed = 0;
		this.lineItemCount = 0;
		myLineItems = new ArrayList<lineItems>();
	}

}