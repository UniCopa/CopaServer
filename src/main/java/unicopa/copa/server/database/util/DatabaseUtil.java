/*
 * Copyright (C) 2013 Felix Wiemuth
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package nipgm.util;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import static nipgm.util.Util.*;

/**
 * Utility methods for general database operations.
 *
 * @author Felix Wiemuth
 */
public class DatabaseUtil {

    public static class ConnectException extends Exception {

        private ConnectException(String messgage) {
            super(messgage);
        }
    }

    public static class CreateException extends Exception {

        public CreateException(String message) {
            super(message);
        }
    }
    public static String driver = "org.apache.derby.jdbc.EmbeddedDriver";
    public static String protocol = "jdbc:derby:";

    public static void createNewDatabase(File directory) throws CreateException, SQLException, ConnectException {
        try (Connection conn = getConnection(directory, false)) {
            throw new CreateException("The database already exists!");
        } catch (ConnectException ex) {
            //Ok, database seems not to exist, try create one
            getConnection(directory, true).close();
        }
    }

    /**
     * Get a connection to the database (embedded), configured the way it is
     * needed for this application. If the connetion fails, everything is closed
     * again automatically.
     */
    public static Connection getConnection(File database, boolean create) throws ConnectException {
        Connection conn = null;
        try {
            StringBuilder sb = new StringBuilder(protocol).append(database.getCanonicalPath());
            if (create) {
                sb.append(";create=true");
            }
            conn = DriverManager.getConnection(sb.toString());
        } catch (SQLException sqle) {
            StringBuilder sb = new StringBuilder(formatSQLException(sqle));
            //close connection again
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException sqle2) {
                sb.append("\n").append(formatSQLException(sqle2));
            }
            throw new ConnectException(sb.toString());
        } catch (IOException ex) {
            throw new ConnectException("I/O exception: " + ex.getMessage());
        }

        return conn;
    }

    public static Connection getConnection(File database) throws ConnectException {
        return getConnection(database, false);
    }

    /*
     * The basis of following code is taken from this example:
     * https://svn.apache.org/repos/asf/db/derby/code/trunk/java/demo/simple/SimpleApp.java
     */
    /**
     * Shutdown the database.
     *
     * @param conn
     */
    public static boolean shutDownDatabase() {
        try {
            // the shutdown=true attribute shuts down Derby
            DriverManager.getConnection("jdbc:derby:;shutdown=true");

            // To shut down a specific database only, but keep the
            // engine running (for example for connecting to other
            // databases), specify a database in the connection URL:
            //DriverManager.getConnection("jdbc:derby:" + dbName + ";shutdown=true");
            return true; //is not executed
        } catch (SQLException se) {
            if (((se.getErrorCode() == 50000)
                    && ("XJ015".equals(se.getSQLState())))) {
                // we got the expected exception
                return true; //shut down normally
                // Note that for single database shutdown, the expected
                // SQL state is "08006", and the error code is 45000.
            } else {
                // if the error code or SQLState is different, we have
                // an unexpected exception (shutdown failed)
                return false; //did not shutdown normally
            }
        }
    }

    /**
     * Loads the JDBC driver.
     */
    public static void loadDriver() {
        /*
         *  The JDBC driver is loaded by loading its class.
         *  If you are using JDBC 4.0 (Java SE 6) or newer, JDBC drivers may
         *  be automatically loaded, making this code optional.
         *
         *  In an embedded environment, this will also start up the Derby
         *  engine (though not any databases), since it is not already
         *  running. In a client environment, the Derby engine is being run
         *  by the network server framework.
         *
         *  In an embedded environment, any static Derby system properties
         *  must be set before loading the driver to take effect.
         */
        try {
            Class.forName(driver).newInstance();
            //System.out.println("Loaded the appropriate driver");
        } catch (ClassNotFoundException cnfe) {
            err("\nUnable to load the JDBC driver " + driver);
            err("Please check your CLASSPATH.");
            //cnfe.printStackTrace(System.err);
        } catch (InstantiationException ie) {
            err("\nUnable to instantiate the JDBC driver " + driver);
            ie.printStackTrace(System.err);
        } catch (IllegalAccessException iae) {
            err("\nNot allowed to access the JDBC driver " + driver);
            iae.printStackTrace(System.err);
        }
    }

    /**
     * Formats details of an SQLException chain as
     * <code>String</code>. Details included are SQL State, Error code,
     * Exception message.
     *
     * @param e the SQLException from which to get details.
     */
    public static String formatSQLException(SQLException e) {
        // Unwraps the entire exception chain to unveil the real cause of the
        // Exception.
        StringBuilder sb = new StringBuilder();
        while (e != null) {
            sb.append("----- SQLException -----")
                    .append("\n  SQL State:  ").append(e.getSQLState())
                    .append("\n  Error Code: ").append(e.getErrorCode())
                    .append("\n  Message:    ").append(e.getMessage())
                    .append("\n");
            e = e.getNextException();
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
}
