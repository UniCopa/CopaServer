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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import unicopa.copa.base.UserRole;
import unicopa.copa.base.UserEventSettings;
import unicopa.copa.base.UserSettings;

import unicopa.copa.base.event.CategoryNodeImpl;
import unicopa.copa.base.event.Event;
import unicopa.copa.base.event.EventGroup;
import unicopa.copa.base.event.SingleEvent;
import unicopa.copa.server.database.data.persistence.*;
import unicopa.copa.server.database.util.DatabaseUtil;

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

    private static final String RESOURCE_SQL_INITDB = "/sql/initializeDB.sql";
    private static final String RESOURCE_MYBATIS_CONFIG = "mybatis-config.xml";
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
	List<Integer> nodeList = getAllChildNodes(categoryNodeID);
	if (categoryNodeID != 0) {
	    nodeList.add(categoryNodeID);
	}
	try (SqlSession session = sqlSessionFactory.openSession()) {
	    EventGroupMapper mapper = session.getMapper(EventGroupMapper.class);
	    List<EventGroup> list = mapper.getEventGroups(nodeList, "%"
		    + searchTerm + "%");
	    return list;
	}
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
	List<Integer> nodeList = getAllChildNodes(categoryNodeID);
	if (categoryNodeID != 0) {
	    nodeList.add(categoryNodeID);
	}
	try (SqlSession session = sqlSessionFactory.openSession()) {
	    EventMapper mapper = session.getMapper(EventMapper.class);
	    List<Event> list = mapper.getEvents(eventGroupID, nodeList);
	    return list;
	}
    }

    /**
     * Get the eventGroup by its ID.
     * 
     * @param eventGroupID
     *            the ID of the eventGroup.
     * @return
     */
    public EventGroup getEventGroup(int eventGroupID) {
	try (SqlSession session = sqlSessionFactory.openSession()) {
	    EventGroupMapper mapper = session.getMapper(EventGroupMapper.class);
	    EventGroup eGroup = mapper.getEventGroup(eventGroupID);
	    return eGroup;
	}
    }

    /**
     * Get the event by its ID.
     * 
     * @param eventID
     *            the ID of the event.
     * @return
     */
    public Event getEvent(int eventID) {
	try (SqlSession session = sqlSessionFactory.openSession()) {
	    EventMapper mapper = session.getMapper(EventMapper.class);
	    Event e = mapper.getEvent(eventID);
	    return e;
	}
    }

    /**
     * Get a list of user-IDs of users that are subscribers for the event.
     * 
     * @param eventID
     *            the event ID for the event the users should have subscribed
     *            to.
     * @return
     */
    public List<Integer> getSubscribedUserIDs(int eventID) {
	try (SqlSession session = sqlSessionFactory.openSession()) {
	    PersonMapper mapper = session.getMapper(PersonMapper.class);
	    List<Integer> iDList = mapper.getSubscribedUserIDs(eventID);
	    return iDList;
	}
    }

    /**
     * Get the ID of a user.
     * 
     * @param userName
     *            the user name of the user
     * @return
     */
    public int getUserID(String userName) {
	return 0; // TODO implement
    }

    /**
     * Get the E-Mail-Address of the given user.
     * 
     * @param userID
     *            the user-ID
     * @return the E-Mail address as String
     */
    public String getEmailAddress(int userID) {
	try (SqlSession session = sqlSessionFactory.openSession()) {
	    PersonMapper mapper = session.getMapper(PersonMapper.class);
	    String email = mapper.getEmailAddress(userID);
	    return email;
	}
    }

    /**
     * Get the user settings of the given user.
     * 
     * @param userID
     *            the user-ID
     * @return the UserSettings for the user
     */
    public UserSettings getUserSettings(int userID) {
	try (SqlSession session = sqlSessionFactory.openSession()) {
	    UserSettingMapper mapper = session
		    .getMapper(UserSettingMapper.class);
	    HashSet<String> uGCMKeys = mapper.getUserGCMKey(userID);
	    HashSet<Integer> subscriptions = mapper.getSubscriptions(userID);
	    Boolean eMailNoty = mapper.getEmailNotification(userID);
	    String language = mapper.getLanguage(userID);
	    List<Map<String, Integer>> listEventColor = mapper
		    .getEventColors(userID);
	    Map<Integer, UserEventSettings> eventSettings = new HashMap<Integer, UserEventSettings>();
	    for (Map<String, Integer> map : listEventColor) {
		eventSettings.put(map.get("EVENTID"), new UserEventSettings(
			String.valueOf(map.get("COLOR"))));
	    }
	    return new UserSettings(uGCMKeys, eMailNoty, language,
		    eventSettings, subscriptions);
	}
    }

    /**
     * Get a SingleEvent.
     * 
     * @param id
     *            the ID of the SingleEvent
     * @returns
     */
    public SingleEvent getSingleEvent(int id) {
	try (SqlSession session = sqlSessionFactory.openSession()) {
	    SingleEventMapper mapper = session
		    .getMapper(SingleEventMapper.class);
	    SingleEvent sEH = mapper.getSingleEvent(id);
	    return sEH;
	}
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
	try (SqlSession session = sqlSessionFactory.openSession()) {
	    PrivilegeMapper mapper = session.getMapper(PrivilegeMapper.class);
	    List<String> privList = mapper.getPrivileged(eventID,
		    appointedByUserID, 1);
	    return privList;
	}
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
     * Get the child nodes of the node categoryID
     * 
     * @param categoryID
     *            the ID of the node
     * @return
     */
    private List<Integer> getChildNodeIDs(int categoryID) {
	try (SqlSession session = sqlSessionFactory.openSession()) {
	    CategoryMapper mapper = session.getMapper(CategoryMapper.class);
	    List<Integer> nodeList = mapper.getChildNodeIDs(categoryID);
	    return nodeList;
	}
    }

    /**
     * Uses getChildNodes recursive to get all leaves that are below the node
     * categoryID
     * 
     * @param categoryID
     *            the ID of the node
     * @return
     */
    private List<Integer> getAllChildNodes(int categoryID) {
	List<Integer> nodeList = new ArrayList<>();
	nodeList.clear();
	if (getChildNodeIDs(categoryID).isEmpty()) {
	    return nodeList;
	} else {
	    nodeList = getChildNodeIDs(categoryID);
	    for (int i = 0; i < nodeList.size(); i++) {
		nodeList.addAll(getAllChildNodes(nodeList.get(i)));
	    }
	    return nodeList;
	}
    }

    /**
     * Get the role a user holds for a specific event. The roles will be checked
     * and returned in the following order: UserRole.ADMINISTRATOR if the user
     * holds this role in general, UserRole.RIGHTHOLER, UserRole.DEPUTY,
     * UserRole.OWNER if the user holds the role for the specified event
     * UserRole.User otherwise.
     * 
     * @param userID
     *            the ID of the user
     * @param eventID
     *            the ID of the event
     * @return the role the user holds for the specified event
     */
    public UserRole getUsersRoleForEvent(int userID, int eventID) {
	throw new UnsupportedOperationException();
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
	try (SqlSession session = sqlSessionFactory.openSession()) {
	    PrivilegeMapper mapper = session.getMapper(PrivilegeMapper.class);
	    List<String> privList = mapper.getPrivileged(eventID,
		    appointedByUserID, 2);
	    return privList;
	}
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
	try (SqlSession session = sqlSessionFactory.openSession()) {
	    PrivilegeMapper mapper = session.getMapper(PrivilegeMapper.class);
	    List<String> privList = mapper.getPrivileged(eventID, -1, 3);
	    return privList;
	}
    }

    public CategoryNodeImpl getCategoryTree(int categoryID) {

	return null;
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