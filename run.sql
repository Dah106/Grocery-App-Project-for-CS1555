set wrap off;
set linesize 300;
set pagesize 2000;
set serveroutput on size 30000;
alter session set nls_date_format = 'DD-MON-YY';


select * from items order by itemID;
select * from lineItems order by lineitemID;
select * from orders order by orderID;
select * from stock order by itemID;
select * from customers order by custID;
select * from distStations order by stationID;
select * from warehouses order by warehouseID;
