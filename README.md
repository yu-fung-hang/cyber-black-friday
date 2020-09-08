# Cyber Black Friday
Imagine that thousands of people would buy the same product at the same time during Cyber Black Friday, how Redis would help relieve the traffic jam of orders? In this project, 
I would test how efficient it would become when an online shopping system is integrated with Redis.

## Technologies Used
* `Spring Boot 2.2.1.RELEASE`
* `Redis`
* `MySQL`

## How to run this project
1. Clone this project in `IntelliJ IDEA`;
2. Modify `/src/main/resources/application-dev.properties`:
    1. Modify `spring.datasource.username` and `spring.datasource.password` to your own MySQL username and password; 
    2. Modify `spring.datasource.url` and `blackfriday.database.url` to your current Time Zone;
3. Start your local Redis server;
4. 
