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
import java.util.List;
import java.util.HashMap;

public class oneWarehouse
{
	//For every warehouse
	protected int warehouseID;	
	protected HashMap<Integer, oneStation> stationSet;
	protected HashMap<Integer, oneItem> itemSet;

	protected oneWarehouse()
	{
		this.warehouseID = 0;
		this.stationSet = new HashMap<Integer, oneStation>();
		this.itemSet = new HashMap<Integer, oneItem>();
	}

	protected boolean hasItem(int ID)
	{
		if(this.itemSet.isEmpty())
		{
			//System.out.println("oneWarehouse " + warehouseID + " is empty");
			return false;
		}
		else
		{	
			if(this.itemSet.containsKey(ID)) return true;
			else return false;
		}
	}

	protected void addItem(int ID)
	{	
		oneItem tempOneItem = new oneItem();
		tempOneItem.itemID = ID;
		this.itemSet.put(ID, tempOneItem);
	}

	protected boolean hasDistStation(int ID)
	{	
		if(this.stationSet.isEmpty())
		{
			//System.out.println("oneWarehouse " + warehouseID + " is empty");
			return false;
		}
		else
		{	
			if(this.stationSet.containsKey(ID)) return true;
			else return false;
		}
	}

	protected void addStation(int ID)
	{	
		oneStation tempOneStation = new oneStation();
		tempOneStation.stationID = ID;
		this.stationSet.put(ID, tempOneStation);
	}

	protected class oneStation
	{	
		//For every distribution station
		protected int stationID;
		protected HashMap<Integer, oneCustomer> customerSet;

		protected oneStation()
		{	
			this.stationID = 0;
			this.customerSet = new HashMap<Integer, oneCustomer>();
		}

		protected boolean hasCustomer(int ID)
		{
			if(this.customerSet.isEmpty())
			{	
				return false;
			}
			else
			{
				if(this.customerSet.containsKey(ID)) return true;
				else return false;
			}
		}

		protected void addCustomer(int ID)
		{	
			oneCustomer tempOneCustomer = new oneCustomer();
			tempOneCustomer.customerID = ID;
			this.customerSet.put(ID, tempOneCustomer);
		}

		public String toString()
		{
			return "oneStationID: " + stationID;
		}
	}

	protected class oneCustomer
	{	
		//For every customer
		protected int customerID;
		protected HashMap<Integer, oneOrder> orderSet;

		protected oneCustomer()
		{
			this.customerID = 0;
			this.orderSet = new HashMap<Integer, oneOrder>();
		}

		protected boolean hasOrder(int ID)
		{
			if(this.orderSet.isEmpty())
			{
				return false;
			}
			else
			{
				if(this.orderSet.containsKey(ID)) return true;
				else return false;
			}
		}

		protected void addOrder(int ID)
		{	
			oneOrder tempOneOrder = new oneOrder();
			tempOneOrder.orderID = ID;
			this.orderSet.put(ID, tempOneOrder);
		}
	}

	protected class oneOrder
	{	
		//For every order
		protected int orderID;
		protected HashMap<Integer, oneLineItem> lineItemSet;

		protected oneOrder()
		{	
			this.orderID = 0;
			this.lineItemSet = new HashMap<Integer, oneLineItem>();
		}

		protected boolean hasLineItem(int ID)
		{	
			if(this.lineItemSet.isEmpty())
			{
				return false;
			}
			else
			{
				if(lineItemSet.containsKey(ID)) return true;
				else return false;
			}
		}

		protected void addLineItem(int ID)
		{
			oneLineItem tempOneLineItem = new oneLineItem();
			tempOneLineItem.lineItemID = ID;
			this.lineItemSet.put(ID, tempOneLineItem);
		}
	}

	protected class oneLineItem
	{
		//For every line order
		protected int lineItemID;

		protected oneLineItem()
		{
			this.lineItemID = 0;
		}
	}

	protected class oneItem
	{

		//For every item
		protected int itemID;

		protected oneItem()
		{
			this.itemID = 0;
		}
	}
}