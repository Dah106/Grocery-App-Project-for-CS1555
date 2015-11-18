drop table warehouses cascade constraints;
drop table distStations cascade constraints;
drop table customers cascade constraints;
drop table orders cascade constraints;
drop table lineItems cascade constraints;
drop table items cascade constraints;

purge recyclebin;

create table warehouses(
	warehouseID	integer,
	name varchar2(20),
	strAddress varchar2(20),
	cityAddress	varchar2(20),
	stateAddress varchar2(20),
	zipcode varchar2(20),
	salesTax number(4,2),
	salesSum number(20, 2),
	constraint checkWarehousesSalesTax check(salesTax > 0),
	constraint warehouses_PK primary key(warehouseID)
);

create table distStations(
	stationID	integer,
	warehouseID integer,
	name varchar2(20),
	strAddress varchar2(20),
	cityAddress varchar2(20),
	stateAddress varchar2(20),
	zipcode varchar2(20),
	salesTax number(4,2),
	salesSum number (20, 2),
	constraint checkDistStationSalesTax check(salesTax > 0),
	constraint distStations_PK primary key(stationID, warehouseID),
	constraint distStations_FK foreign key(warehouseID) references warehouses(warehouseID) initially immediate deferrable
);

create table customers(
	custID integer,
	stationID integer,
	warehouseID integer,
	fname varchar2(20),
	MI	varchar2(1),
	lname varchar2(20),
	strAddress varchar2(20),
	cityAddress varchar2(20),
	stateAddress varchar2(20),
	zipcode varchar2(20),
	phone varchar2(10),
	accountOpenDate date,
	discount number(4,2),
	balance number(20, 2),
	paid number(20, 2),
	paymentCount integer,
	deliveryCount integer,
	constraint customers_PK primary key(custID, stationID, warehouseID),
	constraint customers_FK foreign key(stationID, warehouseID) references distStations(stationID, warehouseID) initially immediate deferrable
);

--for completed orders (1 for completed, 0 for not completed)
create table orders(
	orderID integer,
	custID integer,
	stationID integer,
	warehouseID integer,
	orderPlaceDate date,
	completed integer,
	lineItemCount integer,
	constraint orders_PK primary key(orderID, custID, stationID, warehouseID),
	constraint orders_FK foreign key(custID, stationID, warehouseID) references customers(custID, stationID, warehouseID) initially immediate deferrable
);


create table items(
	itemID integer,
	warehouseID integer,
	name varchar2(20),
	price number(20, 2),
	stock integer,
	soldCount integer,
	orderCount integer,
	constraint items_PK primary key(itemID, warehouseID),
	constraint items_FK foreign key(warehouseID) references warehouses(warehouseID) initially immediate deferrable
);

create table lineItems(
	lineitemID integer,
	itemID integer,
	orderID integer,
	custID integer,
	stationID integer,
	warehouseID integer,
	quantity integer,
	amountDue number(20, 2),
	deliveryDate date,
	constraint lineItems_PK primary key(lineitemID, orderID, custID, warehouseID, stationID),
	constraint lineItems_FK1 foreign key(orderID, custID, stationID, warehouseID) references orders(orderID, custID, stationID, warehouseID) initially immediate deferrable,
	constraint lineItems_FK2 foreign key(itemID, warehouseID) references items(itemID, warehouseID) initially immediate deferrable
);