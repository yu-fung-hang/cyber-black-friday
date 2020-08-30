create database blackfriday;

use blackfriday;

create table stock
(
    id            int not null auto_increment,
    totalNum      int not null,
    stockNum      int not null,
    name          varchar(100) not null,
    version       int,
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