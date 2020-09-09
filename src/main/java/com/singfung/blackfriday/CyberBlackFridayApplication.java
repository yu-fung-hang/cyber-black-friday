package com.singfung.blackfriday;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.Properties;

@SpringBootApplication
@MapperScan(basePackages = {"com.singfung.blackfriday.dao"}, sqlSessionFactoryRef = "sqlSessionFactory")
@EnableTransactionManagement
@EnableScheduling
@EnableAsync
public class CyberBlackFridayApplication
{
    public static void main(String[] args)
    {
        initDataBase();
        SpringApplication.run(CyberBlackFridayApplication.class, args);
    }

    private static void initDataBase()
    {
        try
        {
            Properties props = Resources.getResourceAsProperties("application-dev.properties");
            String url = props.getProperty("blackfriday.database.url");
            String name = props.getProperty("blackfriday.database.name");
            String username = props.getProperty("spring.datasource.username");
            String password = props.getProperty("spring.datasource.password");
            String driver = props.getProperty("spring.datasource.driver-class-name");
            String isExistSQL = "SELECT count(SCHEMA_NAME) as COUNT FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME='" + name + "'";

            Class.forName(driver);
            Connection conn = DriverManager.getConnection(url, username, password);
            PreparedStatement ps = conn.prepareStatement(isExistSQL);
            ResultSet rs = ps.executeQuery();

            if (rs.next() && rs.getInt("COUNT") == 0)
            {
                ScriptRunner runner = new ScriptRunner(conn);
                runner.setErrorLogWriter(null);
                runner.setLogWriter(null);
                Resources.setCharset(StandardCharsets.UTF_8);
                runner.runScript(Resources.getResourceAsReader("sql/blackfriday.sql"));
            }

            ps.close();
            conn.close();
        } catch (IOException | SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
