create database blackfriday;

use blackfriday;

create table Stock
(
    id            int not null auto_increment,
    totalNum      int not null,
    stockNum      int not null,
    version       int not null,
    note          varchar(255),
    primary key (id)
);

create table OrderInfo
(
    id            int not null auto_increment,
    userId        int not null,
    productId     int not null,
    itemsNum      int not null,
    orderTime     timestamp not null,
    note          varchar(255),
    primary key (id)
);