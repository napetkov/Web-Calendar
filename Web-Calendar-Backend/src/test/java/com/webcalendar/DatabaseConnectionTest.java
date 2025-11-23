package com.webcalendar;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class to verify PostgreSQL database connection
 */
@SpringBootTest
class DatabaseConnectionTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void testDatabaseConnection() throws SQLException {
        assertNotNull(dataSource, "DataSource should be configured");
        
        try (Connection connection = dataSource.getConnection()) {
            assertNotNull(connection, "Connection should not be null");
            assertFalse(connection.isClosed(), "Connection should be open");
            
            // Verify we're connected to PostgreSQL
            String databaseProductName = connection.getMetaData().getDatabaseProductName();
            assertEquals("PostgreSQL", databaseProductName, "Should be connected to PostgreSQL");
            
            System.out.println("Successfully connected to PostgreSQL database");
            System.out.println("Database: " + connection.getCatalog());
            System.out.println("URL: " + connection.getMetaData().getURL());
        }
    }

    @Test
    void testJdbcTemplate() {
        assertNotNull(jdbcTemplate, "JdbcTemplate should be configured");
        
        // Execute a simple query to verify connection
        Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
        assertEquals(1, result, "Query should return 1");
        
        System.out.println("JdbcTemplate is working correctly");
    }

    @Test
    void testDatabaseSchema() {
        // Verify that we can query the database schema
        String query = "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'public'";
        Integer tableCount = jdbcTemplate.queryForObject(query, Integer.class);
        
        assertNotNull(tableCount, "Should be able to query schema");
        assertTrue(tableCount >= 0, "Table count should be non-negative");
        
        System.out.println("Number of tables in public schema: " + tableCount);
    }
}
