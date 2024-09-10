package com.ewyboy.mineconomy;

import com.ewyboy.mineconomy.platform.Services;
import org.sqlite.JDBC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Manages the connection to the SQLite database.
 */
public class DatabaseManager {

    /**
     * The connection to the SQLite database.
     */
    private static volatile Connection connection = null;

    static {
        try {
            DriverManager.registerDriver(new JDBC());
        } catch (SQLException e) {
            Constants.LOG.error("Failed to register SQLite driver: {}", e.getMessage(), e);
        }
    }

    /**
     * Checks if a connection to the SQLite database is open.
     * @return true if a connection is open, false otherwise.
     */
    private static boolean isConnectionOpen() {
        return connection != null;
    }

    /**
     * Gets the path to the SQLite database.
     * @return the path to the SQLite database.
     */
    private static String getDatabasePath() {
        return Services.PLATFORM.getPlatformConfigDirectory().resolve("mineconomy.db").toString();
    }

    /**
     * Opens a connection to the SQLite database.
     */
    public static void openConnection() {
        if (isConnectionOpen()) {
            Constants.LOG.warn("Connection is already open. Please close the connection first.");
            return;
        }

        synchronized (DatabaseManager.class) {
            if (!isConnectionOpen()) {  // Double-checked locking pattern
                try {
                    String dbUrl = "jdbc:sqlite:" + getDatabasePath();
                    connection = DriverManager.getConnection(dbUrl);
                    Constants.LOG.info("Connection to SQLite has been established.");
                } catch (SQLException e) {
                    Constants.LOG.error("Error establishing connection: {}", e.getMessage(), e);
                }
            }
        }
    }

    /**
     * Closes the connection to the SQLite database.
     */
    public static void closeConnection() {
        if (!isConnectionOpen()) {
            Constants.LOG.warn("No open connection to close.");
            return;
        }

        synchronized (DatabaseManager.class) {
            if (isConnectionOpen()) {
                try {
                    connection.close();
                    connection = null;
                    Constants.LOG.info("Connection to SQLite has been closed.");
                } catch (SQLException e) {
                    Constants.LOG.error("Error while closing the connection: {}", e.getMessage(), e);
                }
            }
        }
    }

    /**
     * Executes a query on the SQLite database.
     * @param query the query to execute.
     */
    public static void runQuery(String query) {
        if (!isConnectionOpen()) {
            Constants.LOG.warn("No open connection. Please open a connection first.");
            return;
        }

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(query);
            Constants.LOG.info("Query executed successfully: {}", query);
        } catch (SQLException e) {
            Constants.LOG.error("Error executing query: {}", e.getMessage(), e);
        }
    }
}
