/*
 * Copyright (C) 2013 UniCoPA
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
package unicopa.copa.server.database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import unicopa.copa.base.event.Event;
import unicopa.copa.base.event.EventGroup;
import unicopa.copa.server.database.util.DatabaseUtil;
import unicopa.copa.base.event.SingleEvent;

/**
 * The database service provides an interface to the database. It allows to
 * obtain objects from and write objects to the database.
 * 
 * @author Felix Wiemuth
 */
public class DatabaseService {

    public static class ObjectNotFoundException extends Exception {

	public ObjectNotFoundException(String message) {
	    super(message);
	}
    }

    public static class ItemNotFoundException extends Exception {

	public ItemNotFoundException(String message) {
	    super(message);
	}
    }

    private static final String RESOURCE_SQL_INITDB = "/nipgm/resources/sql/initializeDB.sql";
    private static final String RESOURCE_MYBATIS_CONFIG = "nipgm/resources/mybatis-config.xml";
    private final File database;
    private SqlSessionFactory sqlSessionFactory; // The session factory used to
						 // obtain SQL sessions in
						 // service methods

    /**
     * Create a new database service. Note: The database must already be
     * initialized by calling static method 'initDB()'.
     * 
     * @param database
     * @param username
     * @param password
     * @throws IOException
     */
    public DatabaseService(File database, String username, String password)
	    throws IOException {
	this.database = database;

	Properties properties = new Properties();
	properties.setProperty("username", username);
	properties.setProperty("password", password);
	properties.setProperty("url",
		DatabaseUtil.protocol + database.getCanonicalPath());

	InputStream inputStream = Resources
		.getResourceAsStream(RESOURCE_MYBATIS_CONFIG);
	sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream,
		properties);
    }

    public DatabaseService(File database) throws IOException {
	this(database, "", "");
    }

    /**
     * Get all event groups that match a given category and a search string.
     * 
     * @param categoryNodeID
     *            the ID of the category node in the category tree whose subtree
     *            must contain a category node of the event group
     * @param searchTerm
     *            the exact string the name of the event group must contain
     * @return
     */
    public List<EventGroup> getEventGroups(int categoryNodeID, String searchTerm) {
	throw new UnsupportedOperationException();
    }

    /**
     * Get all events of an event group that match a given category.
     * 
     * @param eventGroupID
     *            the ID of the event group to get the events from
     * @param categoryNodeID
     *            the ID of the category node in the category tree whose subtree
     *            must contain a category node of the event
     * @return
     */
    public List<Event> getEvents(int eventGroupID, int categoryNodeID) {
	throw new UnsupportedOperationException();
    }

    /**
     * Get a SingleEvent.
     * 
     * @param id
     *            the ID of the SingleEvent
     * @return
     */
    public SingleEvent getSingleEvent(int id) {
	throw new UnsupportedOperationException();
    }

    /**
     * Get the names of rightholders for an event.
     * 
     * @param eventID
     *            the ID of the event
     * @param appointedByUserID
     *            the ID of the user that appointed the rightholders to be
     *            returned, '-1' means all users
     * @return
     */
    public List<String> getRightholders(int eventID, int appointedByUserID) {
	throw new UnsupportedOperationException();
    }

    /**
     * Get the names of all rightholders for an event.
     * 
     * @param eventID
     *            the ID of the event
     * @return
     */
    public List<String> getRightholders(int eventID) {
	return getRightholders(eventID, -1);
    }

    /**
     * Get the names of deputies for an event.
     * 
     * @param eventID
     *            the ID of the event
     * @param appointedByUserID
     *            the ID of the user that appointed the deputies to be returned,
     *            '-1' means all users
     * @return
     */
    public List<String> getDeputies(int eventID, int appointedByUserID) {
	throw new UnsupportedOperationException();
    }

    /**
     * Get the names of all deputies for an event.
     * 
     * @param eventID
     *            the ID of the event
     * @return
     */
    public List<String> getDeputies(int eventID) {
	return getDeputies(eventID, -1);
    }

    /**
     * Get the names of all owners for an event.
     * 
     * @param eventID
     *            the ID of the event
     * @return
     */
    public List<String> getOwners(int eventID) {
	throw new UnsupportedOperationException();
    }

    /**
     * 
     * @param database
     * @throws unicopa.copa.server.database.util.DatabaseUtil.ConnectException
     * @throws FileNotFoundException
     * @throws SQLException
     * @throws IOException
     */
    public static void initDB(File database)
	    throws DatabaseUtil.ConnectException, FileNotFoundException,
	    SQLException, IOException {
	try (Connection conn = DatabaseUtil.getConnection(database);
		Reader reader = new BufferedReader(new InputStreamReader(
			DatabaseService.class
				.getResourceAsStream(RESOURCE_SQL_INITDB)))) {
	    ScriptRunner runner = new ScriptRunner(conn);
	    runner.runScript(reader);
	}
	// TODO do without DatabaseService?
	DatabaseService dbservice = new DatabaseService(database);
	dbservice.insertProgramVersion();
	// TODO need to shut down DatabaseService?
    }

    /**
     * Add the program version to the database.
     */
    private void insertProgramVersion() {
	throw new UnsupportedOperationException();
    }
}