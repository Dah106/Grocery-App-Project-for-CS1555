import java.sql.*;
import oracle.jdbc.OracleStatement;


public class finalDriver extends Thread
{
	public static int numOfWarehouses = 1;
	public static int numOfStations = 8;
	public static int numOfCustomers = 100;
	public static int numOfOrders = 50;
	public static int numOfItems = 100;
	public static int numOfLineitems = 50;
	public static int numOfStockListings = numOfWarehouses * numOfItems;
	
	public static int NUM_OF_THREADS = 15;

	public static int myThreadId;

	//Assign thread id
	public finalDriver(int threadId)
  	{
     	myThreadId = threadId;
  	}

	public static void main(String[] args) 
	{
		Thread[] threadList = new Thread[NUM_OF_THREADS];

		/*
			Add Jdbc connection config here
		*/

		// spawn threads
      	for (int i = 0; i < NUM_OF_THREADS; i++)
      	{
          	threadList[i] = new finalDriver(i);
          	threadList[i].start();
      	}

      	// Start everyone at the same time
      	setGreenLight ();

      	// wait for all threads to end
      	for (int i = 0; i < NUM_OF_THREADS; i++)
      	{
          	threadList[i].join();
      	}
	}
	
	//TODO: Add code for the thread to run
	public void run()
	{

	}


	static boolean greenLight = false;
  	static synchronized void setGreenLight () { greenLight = true; }
  	synchronized boolean getGreenLight () { return greenLight; }
}


