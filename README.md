# CS1555-Project
CS 1555 Term Project

All work done in equal parts by members of group.

Running Milestone 1:
- point source to "source ~panos/1555/bash.env"
- to compile project:
	javac groceryDeliveryApp.java
- to run project: java groceryDeliveryApp
	
- in program, allows for anonymity and scalability:	

Please enter the username: 

Please enter the password: 

Please enter the number of warehouses for the system: 

Please enter the number of distribution stations for the system: 

Please enter the number of customers for the system: 

Please enter the number of orders for the system: 

Please enter the number of items for the system: 

Please enter the number of line items for the system: 


- once program is run, open new unixs shell and access sqlplus
- in sqlplus, run program run.sql to view contents of db

------------------------------------------------------------------------

Modified table schema for Milestone 2:

Add table "stock" to track the stock of each item per warehouse

Program User Input is changed to adapt to Milestone 2 requirement

- in program, allows for anonymity and scalability:	

Please enter the username: 

Please enter the password: 

Please enter the number of warehouses for the system: 

Please enter the number of distribution stations per warehouse: 

Please enter the number of customers per distribution station: 

Please enter the number of orders per customer: 

Please enter the number of items per warehouse: 

Please enter the number of line items per order: 

Running through transactions:

Which transaction would you like to execute:

1 for New Order Transaction

2 for Payment Transaction

3 for Order Status Transaction

4 for Delivery Transaction

5 for Stock Level Transaction

0 to Re-initialize the database

-1 to stop performing transactions

Enter Choice:
