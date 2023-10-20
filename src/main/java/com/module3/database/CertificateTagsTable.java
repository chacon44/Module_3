package com.module3.database;

import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.module3.database.DatabaseData.*;
import static com.module3.database.LogMessages.*;

public class CertificateTagsTable {
    private static final Logger LOGGER = Logger.getLogger(CertificateDatabase.class.getName());


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

            if(dropTable) {
                stmt.execute(DROP_TABLE);
                LOGGER.log(Level.INFO, TABLE_DROPPED);
            }

            try {
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
