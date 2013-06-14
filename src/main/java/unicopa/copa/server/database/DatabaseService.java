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
import java.util.Date;
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
import unicopa.copa.base.event.SingleEventUpdate;
import unicopa.copa.server.database.data.db.DBCategoryNode;
import unicopa.copa.server.database.data.persistence.*;
import unicopa.copa.server.database.util.DatabaseUtil;

/**
 * The database service provides an interface to the database. It allows to
 * obtain objects from and write objects to the database.
 * 
 * @author Felix Wiemuth
 */
public class DatabaseService {
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
     * @throws ObjectNotFoundException
     */
    public List<EventGroup> getEventGroups(int categoryNodeID, String searchTerm)
	    throws ObjectNotFoundException {
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
     * @throws ObjectNotFoundException
     */
    public List<Event> getEvents(int eventGroupID, int categoryNodeID)
	    throws ObjectNotFoundException {
	getEventGroup(eventGroupID);
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
     * @throws ObjectNotFoundException
     */
    public EventGroup getEventGroup(int eventGroupID)
	    throws ObjectNotFoundException {
	try (SqlSession session = sqlSessionFactory.openSession()) {
	    EventGroupMapper mapper = session.getMapper(EventGroupMapper.class);
	    EventGroup eGroup = mapper.getEventGroup(eventGroupID);
	    if (eGroup == null)
		throw new ObjectNotFoundException(
			"There is no EventGroup with ID=" + eventGroupID
				+ " in the database");
	    return eGroup;
	}
    }

    /**
     * Get the event by its ID.
     * 
     * @param eventID
     *            the ID of the event.
     * @return
     * @throws ObjectNotFoundException
     */
    public Event getEvent(int eventID) throws ObjectNotFoundException {
	try (SqlSession session = sqlSessionFactory.openSession()) {
	    EventMapper mapper = session.getMapper(EventMapper.class);
	    Event e = mapper.getEvent(eventID);
	    if (e == null)
		throw new ObjectNotFoundException("There is no Event with ID="
			+ eventID + "in the database");
	    return e;
	}
    }

    /**
     * Get the SingleEventUpdates of SingleEvents that belong to an event the
     * user subscribed and that occured since the given date
     * 
     * @param since
     *            the data from when to return updates
     * @param userID
     *            the ID of the user
     * @return
     */
    public List<SingleEventUpdate> getSubscribedSingleEventUpdates(int userID,
	    Date since) {
	throw new UnsupportedOperationException();
    }

    /**
     * Get the SingleEventUpdates that belong to the specified event and that
     * occured since the given date
     * 
     * @param eventID
     *            the ID of the event where to get updates from
     * @param since
     *            the data from when to return updates
     * @return
     */
    public List<SingleEventUpdate> getSingleEventUpdates(int eventID, Date since) {
	throw new UnsupportedOperationException();
    }

    /**
     * Get a list of user-IDs of users that are subscribers for the event.
     * 
     * @param eventID
     *            the event ID for the event the users should have subscribed
     *            to.
     * @return
     * @throws ObjectNotFoundException
     */
    public List<Integer> getSubscribedUserIDs(int eventID)
	    throws ObjectNotFoundException {
	getEvent(eventID);
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
     * @throws ObjectNotFoundException
     */
    public int getUserID(String userName) throws ObjectNotFoundException {
	try (SqlSession session = sqlSessionFactory.openSession()) {
	    PersonMapper mapper = session.getMapper(PersonMapper.class);
	    Integer userID = mapper.getUserID(userName);
	    if (userID == null)
		throw new ObjectNotFoundException("There is no user with name="
			+ userName + " in the database");
	    return userID;
	}
    }

    /**
     * Get the E-Mail-Address of the given user.
     * 
     * @param userID
     *            the user-ID
     * @return the E-Mail address as String
     * @throws ObjectNotFoundException
     */
    public String getEmailAddress(int userID) throws ObjectNotFoundException {
	getUserName(userID);
	try (SqlSession session = sqlSessionFactory.openSession()) {
	    PersonMapper mapper = session.getMapper(PersonMapper.class);
	    String email = mapper.getEmailAddress(userID);
	    return email;
	}
    }

    // TODO throw exception if settings do not exist
    /**
     * Get the user settings of the given user.
     * 
     * @param userID
     *            the user-ID
     * @return the UserSettings for the user
     */
    public UserSettings getUserSettings(int userID)
	    throws ObjectNotFoundException {
	getUserName(userID);
	try (SqlSession session = sqlSessionFactory.openSession()) {
	    UserSettingMapper mapper = session
		    .getMapper(UserSettingMapper.class);
	    HashSet<String> uGCMKeys = mapper.getUserGCMKey(userID);
	    Boolean eMailNoty = mapper.getEmailNotification(userID);
	    String language = mapper.getLanguage(userID);
	    List<Map<String, Integer>> listEventColor = mapper
		    .getEventColors(userID);
	    Map<Integer, UserEventSettings> eventSettings = new HashMap<>();
	    for (Map<String, Integer> map : listEventColor) {
		eventSettings.put(map.get("EVENTID"), new UserEventSettings(
			String.valueOf(map.get("COLOR"))));
	    }
	    return new UserSettings(uGCMKeys, eMailNoty, language,
		    eventSettings);
	}
    }

    /**
     * Get a SingleEvent.
     * 
     * @param id
     *            the ID of the SingleEvent
     * @throws ObjectNotFoundException
     * @returns
     */
    public SingleEvent getSingleEvent(int id) throws ObjectNotFoundException {
	try (SqlSession session = sqlSessionFactory.openSession()) {
	    SingleEventMapper mapper = session
		    .getMapper(SingleEventMapper.class);
	    SingleEvent sEH = mapper.getSingleEvent(id);
	    if (sEH == null)
		throw new ObjectNotFoundException(
			"There is no SingleEvent with ID=" + id
				+ " in the database");
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
     * @throws ObjectNotFoundException
     */
    public List<String> getRightholders(int eventID, int appointedByUserID)
	    throws ObjectNotFoundException {
	getEvent(eventID);
	if (appointedByUserID != -1)
	    getUserName(appointedByUserID);
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
     * @throws ObjectNotFoundException
     */
    public List<String> getRightholders(int eventID)
	    throws ObjectNotFoundException {
	return getRightholders(eventID, -1);
    }

    /**
     * Get the child nodes of the node categoryID
     * 
     * @param categoryID
     *            the ID of the node
     * @return
     * @throws ObjectNotFoundException
     */
    private List<Integer> getChildNodeIDs(int categoryID)
	    throws ObjectNotFoundException {
	try (SqlSession session = sqlSessionFactory.openSession()) {
	    CategoryMapper mapper = session.getMapper(CategoryMapper.class);
	    List<Integer> nodeList = mapper.getChildNodeIDs(categoryID);
	    if (nodeList == null)
		throw new ObjectNotFoundException(
			"There is no Category with ID=" + categoryID
				+ " in the database");
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
     * @throws ObjectNotFoundException
     */
    private List<Integer> getAllChildNodes(int categoryID)
	    throws ObjectNotFoundException {
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
     * @throws ObjectNotFoundException
     */
    public UserRole getUsersRoleForEvent(int userID, int eventID)
	    throws ObjectNotFoundException {
	getEvent(eventID);
	getUserName(userID);
	try (SqlSession session = sqlSessionFactory.openSession()) {
	    PersonMapper mapper = session.getMapper(PersonMapper.class);
	    Map<String, Integer> result = mapper.isAdmin(userID);
	    if (result == null) {
		Integer priv = mapper.getPrivilege(userID, eventID);
		if (priv == null)
		    return UserRole.USER;
		switch (priv) {
		case 1:
		    return UserRole.RIGHTHOLDER;
		case 2:
		    return UserRole.DEPUTY;
		case 3:
		    return UserRole.OWNER;
		}
	    } else
		return UserRole.ADMINISTRATOR;
	}
	return null;
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
     * @throws ObjectNotFoundException
     */
    public List<String> getDeputies(int eventID, int appointedByUserID)
	    throws ObjectNotFoundException {
	getEvent(eventID);
	if (appointedByUserID != -1)
	    getUserName(appointedByUserID);
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
     * @throws ObjectNotFoundException
     */
    public List<String> getDeputies(int eventID) throws ObjectNotFoundException {
	return getDeputies(eventID, -1);
    }

    /**
     * Get the names of all owners for an event.
     * 
     * @param eventID
     *            the ID of the event
     * @return
     * @throws ObjectNotFoundException
     */
    public List<String> getOwners(int eventID) throws ObjectNotFoundException {
	getEvent(eventID);
	try (SqlSession session = sqlSessionFactory.openSession()) {
	    PrivilegeMapper mapper = session.getMapper(PrivilegeMapper.class);
	    List<String> privList = mapper.getPrivileged(eventID, -1, 3);
	    return privList;
	}
    }

    /**
     * Get the categoryNodeImpl with ID=categoryID.
     * 
     * @param categoryID
     *            the ID of the category
     * @return
     * @throws ObjectNotFoundException
     */
    public CategoryNodeImpl getCategoryTree(int categoryID)
	    throws ObjectNotFoundException {
	CategoryNodeImpl catTree = null;
	try (SqlSession session = sqlSessionFactory.openSession()) {
	    CategoryMapper mapper = session.getMapper(CategoryMapper.class);
	    DBCategoryNode dBCategoryNode;
	    if (mapper.getChildNodeIDs(categoryID).isEmpty()) {
		dBCategoryNode = mapper.getDBCategoryNodeLeaf(categoryID);
	    } else {
		dBCategoryNode = mapper.getDBCategoryNode(categoryID);
	    }
	    if (dBCategoryNode == null)
		throw new ObjectNotFoundException(
			"There is no Category with ID=" + categoryID
				+ " in the database");
	    catTree = new CategoryNodeImpl(dBCategoryNode.getId(),
		    dBCategoryNode.getName());
	    for (int child : dBCategoryNode.getChildren()) {
		catTree.addChildNode(getCategoryTree(child));
	    }
	}
	return catTree;
    }

    /**
     * Update the UserSettings of the User with ID = userID
     * 
     * @param userSetting
     *            the new UserSettings
     * @param userID
     *            the userID
     * @throws ObjectNotFoundException
     */
    public void updateUserSetting(UserSettings userSetting, int userID)
	    throws ObjectNotFoundException {
	getUserName(userID);
	try (SqlSession session = sqlSessionFactory.openSession()) {
	    UserSettingMapper mapper = session
		    .getMapper(UserSettingMapper.class);
	    mapper.deleteAllGCMKeys(userID);
	    mapper.insertGCMKeys(userSetting.getGCMKeys(), userID);
	    mapper.updatePerson(userSetting.getLanguage(),
		    userSetting.isEmailNotificationEnabled(), userID);
	    mapper.deleteAllSubscriptions(userID);
	    for (int eventID : userSetting.getSubscriptions()) {
		mapper.insertSubscription(eventID, userSetting
			.getEventSettings(eventID).getColorCode(), userID);
	    }
	    session.commit();
	}
    }

    /**
     * Returns the userName to a given userID
     * 
     * @param userID
     *            the ID of the user
     * @return
     * @throws ObjectNotFoundException
     */
    public String getUserName(int userID) throws ObjectNotFoundException {
	try (SqlSession session = sqlSessionFactory.openSession()) {
	    PersonMapper mapper = session.getMapper(PersonMapper.class);
	    String userName = mapper.getUserName(userID);
	    if (userName == null)
		throw new ObjectNotFoundException("There is not User with ID="
			+ userID + " in the database");
	    return userName;
	}
    }

    /**
     * Removes the Privilege from the user with ID=userID for the event with
     * ID=evenID
     * 
     * @param userID
     *            the ID of the user
     * @param eventID
     *            the ID of the event
     * @throws ObjectNotFoundException
     */
    public void removePrivilege(int userID, int eventID)
	    throws ObjectNotFoundException {
	getEvent(eventID);
	getUserName(userID);
	try (SqlSession session = sqlSessionFactory.openSession()) {
	    PrivilegeMapper mapper = session.getMapper(PrivilegeMapper.class);
	    mapper.removePrivilege(userID, eventID);
	}
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