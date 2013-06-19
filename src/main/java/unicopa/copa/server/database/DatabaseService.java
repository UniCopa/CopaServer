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
import java.util.Set;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import unicopa.copa.base.UserData;

import unicopa.copa.base.ServerStatusNote;
import unicopa.copa.base.UserEventSettings;
import unicopa.copa.base.UserRole;
import unicopa.copa.base.UserSettings;
import unicopa.copa.base.event.CategoryNodeImpl;
import unicopa.copa.base.event.Event;
import unicopa.copa.base.event.EventGroup;
import unicopa.copa.base.event.SingleEvent;
import unicopa.copa.base.event.SingleEventUpdate;
import unicopa.copa.server.database.data.db.DBCategoryNode;
import unicopa.copa.server.database.data.db.DBSingleEventUpdate;
import unicopa.copa.server.database.data.db.DBUserData;
import unicopa.copa.server.database.data.persistence.CategoryMapper;
import unicopa.copa.server.database.data.persistence.EventGroupMapper;
import unicopa.copa.server.database.data.persistence.EventMapper;
import unicopa.copa.server.database.data.persistence.PersonMapper;
import unicopa.copa.server.database.data.persistence.PrivilegeMapper;
import unicopa.copa.server.database.data.persistence.ServerStatusMapper;
import unicopa.copa.server.database.data.persistence.SingleEventMapper;
import unicopa.copa.server.database.data.persistence.SingleEventUpdateMapper;
import unicopa.copa.server.database.data.persistence.UserSettingMapper;
import unicopa.copa.server.database.util.DatabaseUtil;

/**
 * The database service provides an interface to the database. It allows to
 * obtain objects from and write objects to the database.
 * 
 * @author Felix Wiemuth, David Knacker
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
     * @return a List of EventGroups
     * @throws ObjectNotFoundException
     *             is thrown if the given CategoryNode or one of the childNodes
     *             in CategeoryNode is not existend in the databse
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
     * @return a list of Events
     * @throws ObjectNotFoundException
     *             is thrown if the given eventGroup, or category does not
     *             exists in the database
     */
    public List<Event> getEvents(int eventGroupID, int categoryNodeID)
	    throws ObjectNotFoundException {
	checkEventGroup(eventGroupID);
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
     * @return the EventGroup
     * @throws ObjectNotFoundException
     *             is thrown if the given eventgroup does not exists in the
     *             database
     */
    public EventGroup getEventGroup(int eventGroupID)
	    throws ObjectNotFoundException {
	checkEventGroup(eventGroupID);
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
     * @return the event
     * @throws ObjectNotFoundException
     *             is thrown if the given event does not exist in the database
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
     * @return the list of the singleEventUpdates
     * @throws ObjectNotFoundException
     *             is thrown if the given user does not exist in the database
     * @throws IncorrectObjectException
     *             is thrown if the given date is null
     */
    public List<SingleEventUpdate> getSubscribedSingleEventUpdates(int userID,
	    Date since) throws ObjectNotFoundException,
	    IncorrectObjectException {
	checkNull(since, "given Date");
	checkUser(userID);
	Set<Integer> subscribedEvents = getUserSettings(userID)
		.getSubscriptions();
	List<SingleEventUpdate> singleEventUpdateList = new ArrayList<>();
	for (int eventID : subscribedEvents) {
	    singleEventUpdateList.addAll(getSingleEventUpdates(eventID, since));
	}
	return singleEventUpdateList;
    }

    /**
     * Get the SingleEventUpdates that belong to the specified event and that
     * occured since the given date
     * 
     * @param eventID
     *            the ID of the event where to get updates from
     * @param since
     *            the data from when to return updates
     * @return the list of SingleEventUpdates
     * @throws ObjectNotFoundException
     *             is thrown if the given event does not exist in the database
     * @throws IncorrectObjectException
     *             is thrown if the given date is null
     */
    public List<SingleEventUpdate> getSingleEventUpdates(int eventID, Date since)
	    throws ObjectNotFoundException, IncorrectObjectException {
	checkNull(since, "given Date");
	checkEvent(eventID);
	try (SqlSession session = sqlSessionFactory.openSession()) {
	    SingleEventUpdateMapper mapper = session
		    .getMapper(SingleEventUpdateMapper.class);
	    List<DBSingleEventUpdate> dbSingleEventUpdates = mapper
		    .getDBSingleEventUpdates(eventID, since.getTime());
	    List<SingleEventUpdate> singleEventUpdates = new ArrayList<>();
	    for (DBSingleEventUpdate dbSingleEvent : dbSingleEventUpdates) {
		singleEventUpdates.add(new SingleEventUpdate(
			getSingleEvent(dbSingleEvent.getUpdatedSingleEvent()),
			dbSingleEvent.getOldSingleEventID(), new Date(
				dbSingleEvent.getUpdateDate()), dbSingleEvent
				.getCreatorName(), dbSingleEvent.getComment()));
	    }
	    return singleEventUpdates;
	}
    }

    /**
     * Checks if there is a Event with ID = eventID in the database, if not a
     * ObjectNotFound Exception is thrown, else true is returned
     * 
     * @param eventID
     * @return true if the given event exists in the database, false if not
     * @throws ObjectNotFoundException
     *             is thrown if the given event does not exist in the database
     */
    private boolean eventExists(int eventID) throws ObjectNotFoundException {
	try (SqlSession session = sqlSessionFactory.openSession()) {
	    EventMapper mapper = session.getMapper(EventMapper.class);
	    int a = mapper.eventExists(eventID);
	    if (a == 0)
		return false;
	    return true;
	}
    }

    /**
     * Get a list of user-IDs of users that are subscribers for the event.
     * 
     * @param eventID
     *            the event ID for the event the users should have subscribed
     *            to.
     * @return a list of user-IDs of the subscribers
     * @throws ObjectNotFoundException
     *             is thrown if the given event does not exist in the database
     */
    public List<Integer> getSubscribedUserIDs(int eventID)
	    throws ObjectNotFoundException {
	checkEvent(eventID);
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
     * @return the userID
     * @throws ObjectNotFoundException
     *             is thrown if the given user does not exist in the database
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
     * Get the ID of a user by his email address.
     * 
     * @param email
     *            the email address of the user
     * @return the userID
     * @throws ObjectNotFoundException
     *             is thrown if the given email does not match to a entry in the
     *             database
     */
    public int getUserIDByEmail(String email) throws ObjectNotFoundException {
	if (!emailExsists(email))
	    throw new ObjectNotFoundException("There is no entry with email="
		    + email + " in the database");
	try (SqlSession session = sqlSessionFactory.openSession()) {
	    PersonMapper mapper = session.getMapper(PersonMapper.class);
	    int userID = mapper.getUserIDByEmail(email);
	    return userID;
	}
    }

    /**
     * Get the email address of the given user.
     * 
     * @param userID
     *            the user-ID
     * @return the E-Mail address as String
     * @throws ObjectNotFoundException
     *             is thrown if the given user does not exist in the database
     */
    public String getEmailAddress(int userID) throws ObjectNotFoundException {
	checkUser(userID);
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
     * @throws ObjectNotFoundException
     *             is thrown if the given user does not exist in the database
     */
    public UserSettings getUserSettings(int userID)
	    throws ObjectNotFoundException {
	checkUser(userID);
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
     * Get the SingleEvent.
     * 
     * @param id
     *            the ID of the SingleEvent
     * @returns the singleEvent
     * @throws ObjectNotFoundException
     *             is thrown if the given singleEvent does not exist in the
     *             database
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
     * Get all (still valid) SingleEvents for an Event where the scheduled date
     * is past the given date.
     * 
     * @param eventID
     *            the ID of the event where to get the current SingleEvents from
     * @param since
     *            the date since when SingleEvents should be returned
     * @return the list of singleEvents
     * @throws ObjectNotFoundException
     *             is thrown if the given event does not exist in the database
     * @throws IncorrectObjectException
     *             is thrown if the given date is null
     */
    public List<SingleEvent> getCurrentSingleEvents(int eventID, Date since)
	    throws ObjectNotFoundException, IncorrectObjectException {
	checkNull(since, "given Date");
	checkEvent(eventID);
	try (SqlSession session = sqlSessionFactory.openSession()) {
	    SingleEventMapper mapper = session
		    .getMapper(SingleEventMapper.class);
	    List<SingleEvent> singleEventList = mapper.getCurrentSingleEvent(
		    eventID, since.getTime());
	    return singleEventList;
	}
    }

    /**
     * 
     * Get the names of rightholders for an event.
     * 
     * @param eventID
     *            the ID of the event
     * @param appointedByUserID
     *            the ID of the user that appointed the rightholders to be
     *            returned, '-1' means all users
     * @return the list of the rightholders ids
     * @throws ObjectNotFoundException
     *             is thrown if the given event or the given user does not exist
     *             in the database
     */
    public List<String> getRightholders(int eventID, int appointedByUserID)
	    throws ObjectNotFoundException {
	checkEvent(eventID);
	if (appointedByUserID != -1)
	    checkUser(appointedByUserID);
	try (SqlSession session = sqlSessionFactory.openSession()) {
	    PrivilegeMapper mapper = session.getMapper(PrivilegeMapper.class);
	    List<String> privList = mapper.getPrivileged(eventID,
		    appointedByUserID, 1);
	    if (privList == null)
		return new ArrayList<>();
	    return privList;
	}
    }

    /**
     * Get the names of all rightholders for an event.
     * 
     * @param eventID
     *            the ID of the event
     * @return the list of the rightholders ids
     * @throws ObjectNotFoundException
     *             is thrown if the given event does not exist in the database
     */
    public List<String> getRightholders(int eventID)
	    throws ObjectNotFoundException {
	return getRightholders(eventID, -1);
    }

    /**
     * Get all events where a user holds higher roles. The map returned maps
     * from RIGHTHOLDER, DEPUTY and OWNER to the IDs of the events where the
     * user hold this role.
     * 
     * @param userID
     *            the ID of the user
     * @return the map with the userRole and the associated List of userIDs
     * @throws ObjectNotFoundException
     *             is thrown if the given user does not exist in the database
     */
    public Map<UserRole, List<Integer>> getUsersPriviligedEvents(int userID)
	    throws ObjectNotFoundException {
	checkUser(userID);
	try (SqlSession session = sqlSessionFactory.openSession()) {
	    PrivilegeMapper mapper = session.getMapper(PrivilegeMapper.class);
	    List<Map<String, Integer>> userPrivList = mapper
		    .getUsersPriviligedEvents(userID);
	    List<Integer> ownerEvents = new ArrayList<>();
	    List<Integer> deputyEvents = new ArrayList<>();
	    List<Integer> rightholderEvents = new ArrayList<>();
	    for (Map<String, Integer> result : userPrivList) {
		switch (result.get("KINDOFPRIVILEGE")) {
		case (1):
		    rightholderEvents.add(result.get("EVENTID"));
		    break;
		case (2):
		    deputyEvents.add(result.get("EVENTID"));
		    break;
		case (3):
		    ownerEvents.add(result.get("EVENTID"));
		    break;
		default:
		    // TODO throw exception?
		    break;
		}
	    }
	    Map<UserRole, List<Integer>> userPrivMap = new HashMap<>();
	    userPrivMap.put(UserRole.OWNER, ownerEvents);
	    userPrivMap.put(UserRole.DEPUTY, deputyEvents);
	    userPrivMap.put(UserRole.RIGHTHOLDER, rightholderEvents);
	    return userPrivMap;
	}
    }

    /**
     * Get all users a user gave higher roles to. The map returned maps from
     * DEPUTY and OWNER to the user data of the users who got this role.
     * 
     * @param userID
     *            the ID of the user who gave the roles
     * @return the map with the userRole and the associated List of userIDs
     * @throws ObjectNotFoundException
     *             is thrown if the given user does not exist in the database
     */
    public Map<UserRole, List<UserData>> getUsersAppointedUsers(int userID)
	    throws ObjectNotFoundException {
	try (SqlSession session = sqlSessionFactory.openSession()) {
	    PrivilegeMapper mapper = session.getMapper(PrivilegeMapper.class);
	    List<DBUserData> dbUserDataList = mapper
		    .getUsersAppointedUsers(userID);
	    List<UserData> ownerList = new ArrayList<>();
	    List<UserData> deputyList = new ArrayList<>();
	    for (DBUserData user : dbUserDataList) {
		switch (user.getKindOfPrivilege()) {
		case (2):
		    deputyList.add(new UserData(user.getFirstname() + " "
			    + user.getFamilyname(), user.getEmail()));
		    break;
		case (3):
		    ownerList.add(new UserData(user.getFirstname() + " "
			    + user.getFamilyname(), user.getEmail()));
		    break;
		default:
		    // TODO throw Exception?
		    break;
		}
	    }
	    Map<UserRole, List<UserData>> usersAppointedUsers = new HashMap<>();
	    usersAppointedUsers.put(UserRole.DEPUTY, deputyList);
	    usersAppointedUsers.put(UserRole.OWNER, ownerList);
	    return usersAppointedUsers;
	}
    }

    /**
     * Check whether a user is appointed by another user with a special role at
     * a specific event.
     * 
     * @param userID
     *            the ID of the user who to check to be appointed
     * @param appointedByUserID
     *            the ID of the user who appointed
     * @param eventID
     *            the ID of the event
     * @param role
     *            the role the appointed user should have
     * @return true if the user granted the privilege to the user for the given
     *         event, else false
     * @throws IncorrectObjectException
     *             is thrown if the given userRole is invalid
     * @throws ObjectNotFoundException
     *             is thrown if the given user does not exist in the database
     */
    public boolean isAppointedBy(int userID, int appointedByUserID,
	    int eventID, UserRole role) throws ObjectNotFoundException,
	    IncorrectObjectException {
	checkEvent(eventID);
	checkUser(appointedByUserID);
	checkUser(userID);
	try (SqlSession session = sqlSessionFactory.openSession()) {
	    PrivilegeMapper mapper = session.getMapper(PrivilegeMapper.class);
	    int kindOfPrivilege = 0;
	    switch (role) {
	    case DEPUTY:
		kindOfPrivilege = 2;
		break;
	    case OWNER:
		kindOfPrivilege = 3;
		break;
	    case RIGHTHOLDER:
		kindOfPrivilege = 1;
		break;
	    default:
		throw new IncorrectObjectException(
			"isAppointedBy not defined for UserRole = " + role);
	    }
	    Integer rows = mapper.isAppointedBy(userID, appointedByUserID,
		    eventID, kindOfPrivilege);
	    if (rows == 0)
		return false;
	    return true;
	}
    }

    /**
     * Get the child nodes of the node categoryID
     * 
     * @param categoryID
     *            the ID of the node
     * @return the categoryID list of the child nodes
     * @throws ObjectNotFoundException
     *             is thrown if the given category does not exist in the
     *             database
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
     * @return a category id list of all children of the given category (childs,
     *         gradchildren,...)
     * @throws ObjectNotFoundException
     *             is thrown if the given category does not exist in the
     *             database
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
     * Get the role a user holds in general. This will be either UserRole.USER
     * or UserRole.ADMINISTRATOR.
     * 
     * @param userID
     *            the ID of the user
     * @return the role the user holds
     * @throws ObjectNotFoundException
     *             is thrown if the given user does not exist in the database
     */
    public UserRole getUserRole(int userID) throws ObjectNotFoundException {
	return getUsersRoleForEvent(userID, 0);
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
     *             is thrown if the given user or the event does not exist in
     *             the database
     */
    public UserRole getUsersRoleForEvent(int userID, int eventID)
	    throws ObjectNotFoundException {
	checkEvent(eventID);
	checkUser(userID);
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
     * gives the given user the admin privilege
     * 
     * @param userID
     * @throws ObjectNotFoundException
     *             is thrown if the given user does not exist in the database
     */
    public void addAdministrator(int userID) throws ObjectNotFoundException {
	checkUser(userID);
	try (SqlSession session = sqlSessionFactory.openSession()) {
	    PrivilegeMapper mapper = session.getMapper(PrivilegeMapper.class);
	    mapper.insertAdmin(userID, new Date().getTime());
	    session.commit();
	}
    }

    /**
     * removes the admin privilege of the given User
     * 
     * @param userID
     * @throws ObjectNotFoundException
     *             is thrown if the given user does not exist in the database
     */
    public void removeAdministrator(int userID) throws ObjectNotFoundException {
	checkUser(userID);
	try (SqlSession session = sqlSessionFactory.openSession()) {
	    PrivilegeMapper mapper = session.getMapper(PrivilegeMapper.class);
	    mapper.deleteAdmin(userID);
	    session.commit();
	}
    }

    /**
     * Set the role of a user for a specific event.
     * 
     * @param userID
     *            the ID of the user
     * @param evenID
     *            the ID of the event where to set the role for the user
     * @param role
     *            the role to set
     * @param gavePrivilegeID
     *            the ID of the user that permitted the privilege
     * @throws IncorrectObjectException
     *             is thrown if the given userRole is invalid
     * @throws ObjectNotFoundException
     *             is thrown if one of the given users or the event does not
     *             exist in the database
     */
    public void setUserRoleForEvent(int userID, int eventID, UserRole role,
	    int gavePrivilegeID) throws IncorrectObjectException,
	    ObjectNotFoundException {
	int kindOfPrivilege = 0;
	switch (role) {
	case DEPUTY:
	    kindOfPrivilege = 2;
	    break;
	case OWNER:
	    kindOfPrivilege = 3;
	    break;
	case RIGHTHOLDER:
	    kindOfPrivilege = 1;
	    break;
	case USER:
	    removePrivilege(userID, eventID);
	    break;
	default:
	    throw new IncorrectObjectException(
		    "setUserRoleForEvent not defined for UserRole = " + role);
	}
	if (role != UserRole.USER)
	    insertPrivilege(userID, eventID, kindOfPrivilege, gavePrivilegeID,
		    new Date());
    }

    /**
     * Get the names of deputies for an event.
     * 
     * @param eventID
     *            the ID of the event
     * @param appointedByUserID
     *            the ID of the user that appointed the deputies to be returned,
     *            '-1' means all users
     * @return the list of userIDs
     * @throws ObjectNotFoundException
     *             is thrown if the given user or the event does not exist in
     *             the database
     */
    public List<String> getDeputies(int eventID, int appointedByUserID)
	    throws ObjectNotFoundException {
	checkEvent(eventID);
	if (appointedByUserID != -1)
	    checkUser(appointedByUserID);
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
     * @return the list of userIDs
     * @throws ObjectNotFoundException
     *             is thrown if the given event does not exist in the database
     */
    public List<String> getDeputies(int eventID) throws ObjectNotFoundException {
	return getDeputies(eventID, -1);
    }

    /**
     * Get the names of all owners for an event.
     * 
     * @param eventID
     *            the ID of the event
     * @return the list of userIds
     * @throws ObjectNotFoundException
     *             is thrown if the given event does not exist in the database
     */
    public List<String> getOwners(int eventID) throws ObjectNotFoundException {
	checkEvent(eventID);
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
     * @return the categoryNodeImpl
     * @throws ObjectNotFoundException
     *             is thrown if the given category does not exist in the
     *             database
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
     *             is thrown if the given user does not exist in the database
     * @throws IncorrectObjectException
     *             is thrown if the given userSettings is null, the colorCode in
     *             the given UserSettings is invalid or the gCMKey in the given
     *             UserSettings is invalid
     * @throws ObjectAlreadyExsistsException
     *             is thrown if there already is another user with the given
     *             gCMKey in the database
     */
    public void updateUserSetting(UserSettings userSetting, int userID)
	    throws ObjectNotFoundException, IncorrectObjectException,
	    ObjectAlreadyExsistsException {
	checkNull(userSetting, "given UserSettings");
	checkUser(userID);
	try (SqlSession session = sqlSessionFactory.openSession()) {
	    UserSettingMapper mapper = session
		    .getMapper(UserSettingMapper.class);
	    mapper.deleteAllGCMKeys(userID);
	    if (userSetting.getGCMKeys() != null
		    && !userSetting.getGCMKeys().isEmpty()) {
		for (String gcmKey : userSetting.getGCMKeys()) {
		    if (existsGCMKey(gcmKey))
			throw new ObjectAlreadyExsistsException(
				"There is already an GCMKey with value="
					+ gcmKey + " in the database");
		}
		mapper.insertGCMKeys(userSetting.getGCMKeys(), userID);
	    }
	    mapper.updatePerson(userSetting.getLanguage(),
		    userSetting.isEmailNotificationEnabled(), userID);
	    mapper.deleteAllSubscriptions(userID);
	    for (int eventID : userSetting.getSubscriptions()) {
		checkColor(userSetting.getEventSettings(eventID).getColorCode());
		mapper.insertSubscription(eventID, userSetting
			.getEventSettings(eventID).getColorCode(), userID);
	    }
	    session.commit();
	}
    }

    /**
     * Checks if the given colerCode only Contains a-f,A-F,0-9 and has the
     * length of 6, if it does not match this pattern an
     * IncorrectObjectException is thrown
     * 
     * @param colorCode
     * @throws IncorrectObjectException
     *             is thrown if the given colorCode is invalid
     */
    public void checkColor(String colorCode) throws IncorrectObjectException {
	String pattern = "[a-fA-F0123456789]*";
	if (colorCode == null || colorCode.length() != 6
		|| !colorCode.matches(pattern))
	    throw new IncorrectObjectException(colorCode
		    + " is not a valid colorCode!");
    }

    /**
     * Returns the userName to a given userID
     * 
     * @param userID
     *            the ID of the user
     * @return the userName
     * @throws ObjectNotFoundException
     *             is thrown if the given user does not exist in the database
     */
    public String getUserName(int userID) throws ObjectNotFoundException {
	checkUser(userID);
	try (SqlSession session = sqlSessionFactory.openSession()) {
	    PersonMapper mapper = session.getMapper(PersonMapper.class);
	    String userName = mapper.getUserName(userID);
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
     *             is thrown if the given user, or the given event does not
     *             exist in the database
     */
    public void removePrivilege(int userID, int eventID)
	    throws ObjectNotFoundException {
	checkEvent(eventID);
	checkUser(userID);
	try (SqlSession session = sqlSessionFactory.openSession()) {
	    PrivilegeMapper mapper = session.getMapper(PrivilegeMapper.class);
	    mapper.removePrivilege(userID, eventID);
	}
    }

    /**
     * Inserts a the SingleEvent in the databases at the same time the new
     * singleEventID is set in the SingleEvent Object
     * 
     * @param singleEvent
     *            the singleEvent
     * @throws ObjectNotFoundException
     *             is thrown when there is no Event entry in the database with
     *             eventID that is given in the singleEvent object
     * @throws IncorrectObjectException
     *             is thrown when the give singleEvent object,the date,the
     *             location or the supervisor is null
     */
    private void insertSingleEvent(SingleEvent singleEvent, boolean isRecent)
	    throws ObjectNotFoundException, IncorrectObjectException {
	checkNull(singleEvent, "given SingleEvent");
	checkNull(singleEvent.getDate(), "Date in given SingleEvent");
	checkNull(singleEvent.getLocation(),
		"String(location) in given SingleEvent");
	checkNull(singleEvent.getSupervisor(),
		"String(supervisor) in given SingleEvent");
	checkEvent(singleEvent.getEventID());
	checkString(singleEvent.getLocation(), 70);
	checkString(singleEvent.getSupervisor(), 70);
	try (SqlSession session = sqlSessionFactory.openSession()) {
	    SingleEventMapper mapper = session
		    .getMapper(SingleEventMapper.class);
	    mapper.insertSingleEvent(singleEvent, singleEvent.getDate()
		    .getTime(), isRecent);
	    session.commit();
	}
    }

    /**
     * Checks weather a Object is null. If so a Incorrect ObjectException is
     * thrown
     * 
     * @param o
     * @throws IncorrectObjectException
     *             throws if the given object is null
     */
    private void checkNull(Object o, String param)
	    throws IncorrectObjectException {
	if (o == null)
	    throw new IncorrectObjectException(
		    "Cannot perform database operation: parameter " + param
			    + " must not be null.");
    }

    /**
     * Inserts a new Person into the database
     * 
     * @param userName
     *            the user name, must not be null, should be unique
     * @param firstName
     *            the first name, must not be null
     * @param familyName
     *            the family name, must not be null
     * @param email
     *            the E-Mail, must not be null, should be unique
     * @param titel
     *            the title
     * @param language
     *            the language, default is english
     * @param eMailNotification
     *            should the person be notified per E-Mail
     * @throws ObjectAlreadyExsistsException
     *             is thrown if there is already an entry with the same email or
     *             userName in the database
     * @throws IncorrectObjectException
     *             is thrown if the userName, the firstName, the familyName or
     *             the email is null
     */
    public void insertPerson(String userName, String firstName,
	    String familyName, String email, String titel, String language,
	    boolean eMailNotification) throws ObjectAlreadyExsistsException,
	    IncorrectObjectException {
	checkNull(userName, "given String(userName)");
	checkString(userName, 20);
	checkNull(firstName, "given String(firstName)");
	checkString(firstName, 35);
	checkNull(familyName, "given String(familyName)");
	checkString(familyName, 35);
	checkNull(email, "given String(email)");
	checkString(email, 100);
	checkString(language, 50);
	checkString(titel, 50);
	if (userNameExsists(userName))
	    throw new ObjectAlreadyExsistsException(
		    "There is already a User in the database with UserName="
			    + userName);
	if (emailExsists(email))
	    throw new ObjectAlreadyExsistsException(
		    "There is already a User in the database with E-Mail="
			    + email);
	try (SqlSession session = sqlSessionFactory.openSession()) {
	    PersonMapper mapper = session.getMapper(PersonMapper.class);
	    mapper.insertPerson(userName, firstName, familyName, email, titel,
		    language, eMailNotification);
	    session.commit();
	}
    }

    /**
     * Returns true if there is a person entry in the database with the given
     * userName
     * 
     * @param userName
     * @return true if the given user exists in the database, else false
     */
    private boolean userNameExsists(String userName) {
	try (SqlSession session = sqlSessionFactory.openSession()) {
	    PersonMapper mapper = session.getMapper(PersonMapper.class);
	    if (mapper.userNameExists(userName) == 0)
		return false;
	    return true;
	}
    }

    /**
     * Returns true if there is a person entry in the database with the given
     * E-Mail
     * 
     * @param email
     * @return true if the given email exists in the database, else false
     */
    private boolean emailExsists(String email) {
	try (SqlSession session = sqlSessionFactory.openSession()) {
	    PersonMapper mapper = session.getMapper(PersonMapper.class);
	    if (mapper.emailExists(email) == 0)
		return false;
	    return true;
	}
    }

    /**
     * Returns true if there is a person entry in the database with the given
     * userID
     * 
     * @param userID
     * @return returns true if the given user exists in the database, else false
     */
    private boolean userIDExsists(int userID) {
	try (SqlSession session = sqlSessionFactory.openSession()) {
	    PersonMapper mapper = session.getMapper(PersonMapper.class);
	    if (mapper.userIDExists(userID) == 0)
		return false;
	    return true;
	}
    }

    /**
     * Checks if the User exsists in the database, if not a
     * ObjectNotFoundException is thrown
     * 
     * @param userID
     * @throws ObjectNotFoundException
     *             is thrown if the given user does not exist in the database
     */
    private void checkUser(int userID) throws ObjectNotFoundException {
	if (!userIDExsists(userID))
	    throw new ObjectNotFoundException("There is no User with ID="
		    + userID + " in the database");
    }

    /**
     * Inserts the given singleEventUpdate into the database. This includes the
     * new SingleEvent that is given in singleEventUpdate. If oldSingleEventID =
     * 0 then updatedSingleEvent is inserted as a new SingleEvent (). If the
     * updatedSingleEvent = null, then the SingleEvent counts as canceled
     * 
     * @param singleEventUpdate
     * @throws ObjectNotFoundException
     *             is thrown when there is no Event entry in the database with
     *             oldSingleEventID that is given in the singleEventUpdate
     *             object
     * @throws IncorrectObjectException
     *             is thrown if the given singleEventUpdate or the updateDate
     *             object is null
     */
    public void insertSingleEventUpdate(SingleEventUpdate singleEventUpdate)
	    throws ObjectNotFoundException, IncorrectObjectException {
	checkNull(singleEventUpdate, "given singleEventUpdate");
	checkNull(singleEventUpdate.getUpdateDate(),
		"Date in given SingleEventUpdate");
	checkString(singleEventUpdate.getComment(), 1000);
	checkString(singleEventUpdate.getCreatorName(), 70);
	if (singleEventUpdate.getOldSingleEventID() != 0) {
	    checkSingleEvent(singleEventUpdate.getOldSingleEventID());
	    if (!isRecent(singleEventUpdate.getOldSingleEventID()))
		throw new IncorrectObjectException(
			"An update can only be performed on an active SingleEvent. SingleEvent with ID="
				+ singleEventUpdate.getOldSingleEventID()
				+ " is deprecated.");
	}
	if (singleEventUpdate.getUpdatedSingleEvent() != null) {
	    insertSingleEvent(singleEventUpdate.getUpdatedSingleEvent(), true);
	    updateSingleEventStatus(singleEventUpdate.getOldSingleEventID(),
		    false);
	    try (SqlSession session = sqlSessionFactory.openSession()) {
		SingleEventUpdateMapper mapper = session
			.getMapper(SingleEventUpdateMapper.class);
		mapper.insertSingleEventUpdate(new DBSingleEventUpdate(
			singleEventUpdate.getUpdatedSingleEvent()
				.getSingleEventID(), singleEventUpdate
				.getOldSingleEventID(), singleEventUpdate
				.getUpdateDate().getTime(), singleEventUpdate
				.getCreatorName(), singleEventUpdate
				.getComment()));
		session.commit();
	    }
	} else {
	    try (SqlSession session = sqlSessionFactory.openSession()) {
		SingleEventUpdateMapper mapper = session
			.getMapper(SingleEventUpdateMapper.class);
		mapper.insertSingleEventUpdate(new DBSingleEventUpdate(0,
			singleEventUpdate.getOldSingleEventID(),
			singleEventUpdate.getUpdateDate().getTime(),
			singleEventUpdate.getCreatorName(), singleEventUpdate
				.getComment()));
		session.commit();
		updateSingleEventStatus(
			singleEventUpdate.getOldSingleEventID(), false);
	    }
	}
    }

    /**
     * Inserts the given Event into the database
     * 
     * @param event
     * @throws ObjectNotFoundException
     *             is thrown if one of the categoryIDs in the given event object
     *             does not exists in the database
     * @throws IncorrectObjectException
     *             is thrown it the given event object or the name in the event
     *             object is null
     */
    public void insertEvent(Event event) throws ObjectNotFoundException,
	    IncorrectObjectException {
	checkString(event.getEventName(), 70);
	checkNull(event, "given Event");
	checkNull(event.getEventName(), "String(eventName) in the given Event");
	for (int categoryID : event.getCategories()) {
	    checkCategory(categoryID);
	}
	try (SqlSession session = sqlSessionFactory.openSession()) {
	    EventMapper mapper = session.getMapper(EventMapper.class);
	    mapper.insertEvent(event);
	    session.commit();
	    mapper.insertEventCategorie(event.getEventID(),
		    event.getCategories());
	    session.commit();
	}
    }

    /**
     * Inserts the given CategoryNodeImpl into the database, also all child
     * CategoryNodeImpls will be inserted
     * 
     * @param category
     *            the root categoryNodeImpl of the categoryTree that should be
     *            inserted
     * @param parent
     *            should be -1 is the inserted CategoryNodeImpl is the root node
     * @throws IncorrectObjectException
     *             is thrown if a given categoryNodeImpl is null
     * @throws ObjectAlreadyExsistsException
     *             is thrown if one of the categories already exists in the
     *             database
     * @throws ObjectNotFoundException
     *             is thrown if there is not Category in the database with
     *             ID=parent
     */
    public void insertCategoryTree(CategoryNodeImpl category, int parent)
	    throws IncorrectObjectException, ObjectAlreadyExsistsException,
	    ObjectNotFoundException {
	checkString(category.getName(), 70);
	checkNull(category, "given CategoryNodeImpl");
	if (categoryExists(category.getId()))
	    throw new ObjectAlreadyExsistsException(
		    "There is already an entry in the category table in the database with categoryID="
			    + category.getId());
	if (!categoryExists(parent) && parent != -1)
	    throw new ObjectNotFoundException(
		    "There is no Category entry in the database with ID="
			    + parent);
	insertCategory(category, parent);
	for (CategoryNodeImpl cate : category.getChildren()) {
	    insertCategoryTree(cate, category.getId());
	}

    }

    /**
     * Inserts a Category into the database. if its not the root Category, a
     * CategoryConnection is also inserted into the database
     * 
     * @param category
     * @param parent
     * @throws IncorrectObjectException
     *             is thrown if the gven category is null
     */
    private void insertCategory(CategoryNodeImpl category, int parent)
	    throws IncorrectObjectException {
	checkNull(category, "givenCategoryNodeImpl");
	try (SqlSession session = sqlSessionFactory.openSession()) {
	    CategoryMapper mapper = session.getMapper(CategoryMapper.class);
	    mapper.insertCategory(category);
	    session.commit();
	    if (category.getId() != 0) {
		mapper.insertCategoryConnection(parent, category.getId());
		session.commit();
	    }
	}
    }

    /**
     * inserts a privilege entry with the given parameters
     * 
     * @param userID
     * @param eventID
     * @param kindOfPrivilege
     * @param gavePrivilegeID
     * @param privDate
     * @throws ObjectNotFoundException
     *             is thrown if on of the given users or the given event does
     *             not exist in the database
     * @throws IncorrectObjectException
     *             is thrown if the given date is invalid
     */
    public void insertPrivilege(int userID, int eventID, int kindOfPrivilege,
	    int gavePrivilegeID, Date privDate) throws ObjectNotFoundException,
	    IncorrectObjectException {
	if (!userIDExsists(userID))
	    throw new ObjectNotFoundException("There is no User with ID="
		    + userID);
	if (!userIDExsists(gavePrivilegeID))
	    throw new ObjectNotFoundException("There is no User with ID="
		    + gavePrivilegeID);
	checkEvent(eventID);
	checkNull(privDate, "given Date");
	try (SqlSession session = sqlSessionFactory.openSession()) {
	    PrivilegeMapper mapper = session.getMapper(PrivilegeMapper.class);
	    mapper.insertPrivilege(userID, eventID, kindOfPrivilege,
		    gavePrivilegeID, privDate.getTime());
	    session.commit();
	}
    }

    /**
     * inserts the given EventGroup into the database
     * 
     * @param eventGroup
     * @throws ObjectNotFoundException
     *             is thrown if the given category does not exist in the
     *             database
     * @throws IncorrectObjectException
     *             is thrown if the category, the eventGroupInfo or the
     *             eventGroupName in the given eventGroup is null, or if one of
     *             the given strings is longer than the corresponding database
     *             attribute
     */
    public void insertEventGroup(EventGroup eventGroup)
	    throws ObjectNotFoundException, IncorrectObjectException {
	checkNull(eventGroup.getCategories(),
		"categoryList in given eventGroup");
	checkNull(eventGroup.getEventGroupInfo(),
		"string(eventGroupInfo) in given eventGroup");
	checkNull(eventGroup.getEventGroupName(),
		"string(eventGroupName) in given eventGroup");
	checkString(eventGroup.getEventGroupName(), 70);
	checkString(eventGroup.getEventGroupInfo(), 500);
	for (int categoryID : eventGroup.getCategories()) {
	    checkCategory(categoryID);
	}
	try (SqlSession session = sqlSessionFactory.openSession()) {
	    EventGroupMapper mapper = session.getMapper(EventGroupMapper.class);
	    mapper.insertEventGroup(eventGroup);
	    session.commit();
	    mapper.insertEventGroupCategory(eventGroup.getEventGroupID(),
		    eventGroup.getCategories());
	    session.commit();
	}
    }

    /**
     * Add a new status note entry into the database
     * 
     * @param note
     * @throws IncorrectObjectException
     *             is thrown if the given string is longer than the
     *             corresponding database attribute, or is null
     */
    public void addServerStatusNote(String note)
	    throws IncorrectObjectException {
	checkString(note, 1000);
	checkNull(note, "given String");
	try (SqlSession session = sqlSessionFactory.openSession()) {
	    ServerStatusMapper mapper = session
		    .getMapper(ServerStatusMapper.class);
	    mapper.addServerStatusNote(note, new Date().getTime());
	    session.commit();
	}
    }

    /**
     * Get all server status notes since the given Date
     * 
     * @param since
     * @return the list of serverStatusNotes
     * @throws IncorrectObjectException
     *             if the given date is null
     */
    public List<ServerStatusNote> getServerStatusNote(Date since)
	    throws IncorrectObjectException {
	checkNull(since, "given Date");
	try (SqlSession session = sqlSessionFactory.openSession()) {
	    ServerStatusMapper mapper = session
		    .getMapper(ServerStatusMapper.class);
	    List<Map<String, Integer>> noteList = mapper
		    .getServerStatusNote(since.getTime());
	    List<ServerStatusNote> serverStatusNote = new ArrayList<>();
	    for (Map<String, Integer> note : noteList) {
		serverStatusNote.add(new ServerStatusNote(new Date(Long
			.parseLong(String.valueOf(note.get("NOTEDATE")), 10)),
			"" + note.get("STATUSMSG")));
	    }
	    return serverStatusNote;
	}
    }

    /**
     * Returns true if there is a category with given ID in the database, false
     * if not.
     * 
     * @param categoryID
     * @return true if the given category exists in the database, else false
     */
    private boolean categoryExists(int categoryID) {
	try (SqlSession session = sqlSessionFactory.openSession()) {
	    CategoryMapper mapper = session.getMapper(CategoryMapper.class);
	    int a = mapper.categoryExsists(categoryID);
	    if (a == 0)
		return false;
	    return true;
	}
    }

    /**
     * Updates the status of the given SingleEvent to given isRecent
     * 
     * @param singleEventID
     * @param isRecent
     * @throws ObjectNotFoundException
     *             is thrown if the given singleEvent does not exist in the
     *             database
     */
    private void updateSingleEventStatus(int singleEventID, boolean isRecent)
	    throws ObjectNotFoundException {
	checkSingleEvent(singleEventID);
	try (SqlSession session = sqlSessionFactory.openSession()) {
	    SingleEventMapper mapper = session
		    .getMapper(SingleEventMapper.class);
	    mapper.updateSingleEventStatus(isRecent, singleEventID);
	    session.commit();
	}
    }

    /**
     * Checks weather the given singleEvent is recent or not
     * 
     * @param singleEventID
     * @return true if the given singleEvent is the latest database entry for
     *         this singleEvent, else false
     * @throws ObjectNotFoundException
     *             is thrown if the given singleEvent does not exist in the
     *             database
     */
    private boolean isRecent(int singleEventID) throws ObjectNotFoundException {
	checkSingleEvent(singleEventID);
	try (SqlSession session = sqlSessionFactory.openSession()) {
	    SingleEventMapper mapper = session
		    .getMapper(SingleEventMapper.class);
	    boolean status = mapper.getSingleEventStatus(singleEventID);
	    return status;
	}
    }

    /**
     * checks weather a singleEvent exists or not
     * 
     * @param singleEventID
     * @return true if the given singleEvent exists in the database, else false
     */
    private boolean singleEventExists(int singleEventID) {
	try (SqlSession session = sqlSessionFactory.openSession()) {
	    SingleEventMapper mapper = session
		    .getMapper(SingleEventMapper.class);
	    int status = mapper.singleEventExists(singleEventID);
	    if (status == 0)
		return false;
	    return true;
	}
    }

    /**
     * if the given singleEvent is not in the database an
     * ObjectNotFoundException is thrown
     * 
     * @param singleEventID
     * @throws ObjectNotFoundException
     *             is thrown if the given singleEvent does not exist in the
     *             database
     */
    private void checkSingleEvent(int singleEventID)
	    throws ObjectNotFoundException {
	if (!singleEventExists(singleEventID))
	    throw new ObjectNotFoundException(
		    "There is no SingleEvent with ID=" + singleEventID);
    }

    /**
     * if the given Event is not in the database an ObjectNotFoundException is
     * thrown
     * 
     * @param eventID
     * @throws ObjectNotFoundException
     *             is thrown if the given event does not exist in the database
     */
    private void checkEvent(int eventID) throws ObjectNotFoundException {
	if (!eventExists(eventID))
	    throw new ObjectNotFoundException("There is no Event with ID="
		    + eventID);

    }

    /**
     * Checks weather a eventGroup exists or not
     * 
     * @param eventGroupID
     * @return true if the eventGroup exists in the database, else false
     */
    private boolean eventGroupExists(int eventGroupID) {
	try (SqlSession session = sqlSessionFactory.openSession()) {
	    EventGroupMapper mapper = session.getMapper(EventGroupMapper.class);
	    int status = mapper.eventGroupExists(eventGroupID);
	    if (status == 0)
		return false;
	    return true;
	}
    }

    /**
     * if the given EventGroup is not in the database an ObjectNotFoundException
     * is thrown
     * 
     * @param eventGroupID
     * @throws ObjectNotFoundException
     *             is thrown if the given eventGroup does not exist in the
     *             database
     */
    private void checkEventGroup(int eventGroupID)
	    throws ObjectNotFoundException {
	if (!eventGroupExists(eventGroupID))
	    throw new ObjectNotFoundException("There is no EventGroup with ID="
		    + eventGroupID + " in the database");
    }

    /**
     * if the given Category is not in the database an ObjectNotFoundException
     * is thrown
     * 
     * @param categoryID
     * @throws ObjectNotFoundException
     *             is thrown if the given category does not exist in the
     *             database
     */
    private void checkCategory(int categoryID) throws ObjectNotFoundException {
	if (!categoryExists(categoryID))
	    throw new ObjectNotFoundException("There is no Category with ID="
		    + categoryID + " in the database");
    }

    /**
     * throws an exception if a given string is longer than it should be
     * 
     * @param stringToCheck
     * @param stringMaxLength
     * @throws IncorrectObjectException
     *             is thrown if the given String is longer than the given max
     *             Length
     */
    private void checkString(String stringToCheck, int stringMaxLength)
	    throws IncorrectObjectException {
	if (stringToCheck.length() > stringMaxLength)
	    throw new IncorrectObjectException("String " + stringToCheck
		    + "is too long. maximum length is" + stringMaxLength);

    }

    /**
     * Checks weather a eventGroup exists or not
     * 
     * @param gcmKey
     * @return true if the gcMKey exists in the database, else false
     * @throws IncorrectObjectException
     *             is thrown if the gCMKey is longer than it should be, or is
     *             null
     */
    private boolean existsGCMKey(String gcmKey) throws IncorrectObjectException {
	checkString(gcmKey, 300);
	checkNull(gcmKey, "given GCMKey");
	try (SqlSession session = sqlSessionFactory.openSession()) {
	    PersonMapper mapper = session.getMapper(PersonMapper.class);
	    int status = mapper.gcmKeyExists(gcmKey);
	    if (status == 0)
		return false;
	    return true;
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