package com.epam.esm.database;

import static com.epam.esm.database.DatabaseData.*;
import static com.epam.esm.database.LogMessages.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import static com.epam.esm.database.DatabaseData.CREATE_CERTIFICATE_TAGS_TABLE;

@Configuration
@PropertySource("classpath:jdbc.properties")
@Component
public class DatabaseConfig {
    @Value("${db.driver}")
    private String DRIVER;
    @Value("${db.url}")
    private String URL;
    @Value("${user}")
    private String USERNAME;
    @Value("${password}")
    private String PASSWORD;

    private static final Logger LOGGER = Logger.getLogger(DatabaseConfig.class.getName());


    @Bean
    public DataSource dataSource() throws IllegalStateException {

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(DRIVER);
        dataSource.setUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);

        Connection conn;
        Statement stmt;

        try {
            boolean dropTable = false;

            conn = dataSource.getConnection();
            LOGGER.log(Level.INFO, COULD_CONNECT);
            stmt = conn.createStatement();

//            if(dropTable) {
//                stmt.execute(DROP_TABLE);
//                LOGGER.log(Level.INFO, TABLE_DROPPED);
//            }

            try {
                stmt.executeUpdate(CREATE_CERTIFICATE_TABLE);
                stmt.executeUpdate(CREATE_TAG_TABLE);
                stmt.executeUpdate(CREATE_CERTIFICATE_TAGS_TABLE);
                LOGGER.log(Level.INFO, TABLE_CREATED);
            } catch (SQLException e){
                LOGGER.log(Level.SEVERE, TABLE_NOT_CREATED, e);
            }


                stmt.close();
            conn.close();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, COULD_NOT_CONNECT, e);
        }

        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {

        return new JdbcTemplate(dataSource());
    }

}
