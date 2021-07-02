# Cyber Black Friday
Imagine that thousands of people are going to buy the same product at the same time on Cyber Black Friday, how should we design the system so that it won't crash or perform poorly when it is bombarded with orders?

In this project, I would test how efficient it would become when an online shopping system is integrated with Redis.

![](images/interface.png)

## Tools and frameworks
* Redis
* Lua
* MySQL
* Maven
* Spring Boot
* Spring
* Spring MVC
* MyBatis

## How to run this project
1. Clone this project in `IntelliJ IDEA`;
2. Modify `/src/main/resources/application-dev.properties`:
    1. Modify `spring.datasource.username` and `spring.datasource.password` to your own MySQL username and password; 
    2. Modify `spring.datasource.url` and `blackfriday.database.url` to your current time zone;
3. Start your local Redis server;
4. Run `/src/main/java/com/singfung/blackfriday/CyberBlackFridayApplication`(click the triangle near the line numbers). After that, database `blackfriday` should have been created in your local MySQL;
5. Open `http://localhost:8080/` in Firefox. 

> P.S. Chrome is not recommended in this project. When the number of users is too large, it will show the following error: `Failed to load resource: net::ERR_INSUFFICIENT_RESOURCES`.

## Emulate the traffic jam of orders
Here are a few steps to create the scene that lots of people are ordering the same product at the same time:
1. Add a product into stock. Before that you need to decide how many products there are in stock, and then do one of the following to add it to DB and Redis:
    * Run `\api-examples\insert-stock-record.http` if you are using IntelliJ IDEA Ultimate;
    * Copy the content from `\api-examples\insert-stock-record.http` to Postman and run it.
2. Enter `stockId` (1 as default) and `Number of Users` on the browser;
3. Click one of the two buttons to start an emulation:
    * `Order`: implemented using pessimistic locking. IntelliJ Console will show `Orders are full!` when the stock is empty.
    * `Order with Redis`: implemented using Redis. All orders would be saved into Redis at first. When the stock in empty, orders would be transferred from Redis into MySQL. IntelliJ Console will show a message when the whole process is complete.
4. Check the database to see how much time was spent on the emulation.
5. Click `Reset` to clear all records in MySQL and Redis. Start another emulation by redoing all these steps again.

## Experimental environment 
* Ubuntu 18.04.4
* Redis 6.2.4
* MySQL 8.0.25
* Firefox

## Test cases
### Case 1: No method is applied to prevent error

* __Parameters__: stockNum = 100, number of users = 200;
* __Result__:

    ![](images/error.png)

* __Analysis__: Although there were only 100 products in stock, 108 orders were generated, which is definitely not accepted.

### Case 2: Pessimistic locking

* __Parameters__: stockNum = 2,000, number of users = 3,000;
* __Results__:

    ![](images/plock-1.png)
    ![](images/plock-2.png)
    ![](images/plock-3.png)

* __Analysis__: It generated exactly 2,000 orders, however, it took more than __5 minutes__ to finish the whole process.

### Case 3: Redis

* __Parameters__: stockNum = 20,000, number of users = 21,000;
* __Results__:
![](images/redis-1.png)
![](images/redis-2.png)
![](images/redis-3.png)

* __Analysis__: It generated exactly 20,000 orders, and it took only around __16s__ to finish the whole process.

   | Time of                                 | Time           |
   | :---                                    |     :---:      |
   | sending requests from browser           | ≈ 2s           |
   | generating orders in Redis              | ≈ 14s          |
   | transferring orders from Redis to DB    | ≈ 1s           |
   | the whole process                       | ≈ 16s          |

## Conclusion
Not only does Redis prevent the system from generating more orders, but it also helps decrease runtime significantly.

## Credit
* The main idea of this project is from a Chinese book called [*Java EE 互联网轻量级框架整合开发*](http://www.broadview.com.cn/book/80). However, I made a lot of improvements on it.
* The Black Friday image was found on [_this link_](https://scx2.b-cdn.net/gfx/news/hires/2017/blackfriday.jpg).