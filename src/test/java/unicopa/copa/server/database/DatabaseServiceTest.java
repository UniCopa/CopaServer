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

import static org.junit.Assert.*;
import org.junit.runners.MethodSorters;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;

import unicopa.copa.base.UserEventSettings;
import unicopa.copa.base.UserRole;
import unicopa.copa.base.UserSettings;
import unicopa.copa.base.event.CategoryNodeImpl;
import unicopa.copa.base.event.Event;
import unicopa.copa.base.event.EventGroup;
import unicopa.copa.base.event.SingleEvent;
import unicopa.copa.base.event.SingleEventUpdate;
import unicopa.copa.server.database.util.DatabaseUtil;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DatabaseServiceTest {
    private static String dbURL = "CopaTestDB";
    private static DatabaseService dbs;
    private static final String RESOURCE_SQL_INSERTS = "/sql/inserts.sql";
    private static final String RESOURCE_SQL_DROP = "/sql/drop.sql";
    private static final String RESOURCE_SQL_INITDB = "/sql/initializeDB.sql";
    public static String driver = "org.apache.derby.jdbc.EmbeddedDriver";
    public static String protocol = "jdbc:derby:";
    private static File db = new File(dbURL);
    private static Connection conn = null;

    @BeforeClass
    public static void connect() {
	try {
	    if (!db.exists())
		DatabaseUtil.createNewDatabase(db);
	    Class.forName(driver).newInstance();
	    if (conn == null)
		conn = DriverManager.getConnection(protocol + dbURL);
	    Reader reader = new BufferedReader(new InputStreamReader(
		    DatabaseServiceTest.class
			    .getResourceAsStream(RESOURCE_SQL_INITDB)));
	    ScriptRunner runner = new ScriptRunner(conn);
	    runner.setLogWriter(null);
	    // runner.setErrorLogWriter(null);
	    runner.runScript(reader);

	    reader = new BufferedReader(new InputStreamReader(
		    DatabaseServiceTest.class
			    .getResourceAsStream(RESOURCE_SQL_INSERTS)));
	    runner.runScript(reader);
	    dbs = new DatabaseService(db);
	} catch (Exception except) {
	    except.printStackTrace();
	}
    }

    @AfterClass
    public static void disconnect() {
	Reader reader = new BufferedReader(new InputStreamReader(
		DatabaseServiceTest.class
			.getResourceAsStream(RESOURCE_SQL_DROP)));
	ScriptRunner runner = new ScriptRunner(conn);
	runner.setLogWriter(null);
	// runner.setErrorLogWriter(null);
	runner.runScript(reader);
    }

    @Test
    public void testGetUserRole() {
	try {
	    assertEquals(UserRole.USER, dbs.getUserRole(1));
	    assertEquals(UserRole.ADMINISTRATOR, dbs.getUserRole(2));
	} catch (ObjectNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    @Test
    public void testGetCurrentSingleEvents() {
	try {
	    List<SingleEvent> res = dbs
		    .getCurrentSingleEvents(4, new Date(100));
	    // TODO proper test
	} catch (ObjectNotFoundException | IncorrectObjectException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    @Test
    public void testGetSubscribedSingleEventUpdates() {
	SingleEventUpdate sEU1 = new SingleEventUpdate(new SingleEvent(3, 3,
		"bla", new Date(8765445), "Dr. Test", 120), 1,
		new Date(234234), "Der Cheff", "Nope");
	SingleEventUpdate sEU2 = new SingleEventUpdate(new SingleEvent(6, 7,
		"bla", new Date(2323452), "Prof. Test", 11), 2,
		new Date(13513), "ABC", "");
	List<SingleEventUpdate> sEUList = new ArrayList<>();
	sEUList.add(sEU1);
	sEUList.add(sEU2);
	List<SingleEventUpdate> resSEUList = new ArrayList<>();
	try {
	    resSEUList = dbs.getSubscribedSingleEventUpdates(2, new Date(100));
	} catch (ObjectNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IncorrectObjectException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	assertEquals(sEUList.get(0).getComment(), resSEUList.get(0)
		.getComment());
	assertEquals(sEUList.get(0).getCreatorName(), resSEUList.get(0)
		.getCreatorName());
	assertEquals(sEUList.get(0).getOldSingleEventID(), resSEUList.get(0)
		.getOldSingleEventID());
	assertEquals(sEUList.get(0).getUpdateDate(), resSEUList.get(0)
		.getUpdateDate());
	assertEquals(
		sEUList.get(0).getUpdatedSingleEvent().getDate().getTime(),
		resSEUList.get(0).getUpdatedSingleEvent().getDate().getTime());
	assertEquals(sEUList.get(0).getUpdatedSingleEvent()
		.getDurationMinutes(), resSEUList.get(0)
		.getUpdatedSingleEvent().getDurationMinutes());
	assertEquals(sEUList.get(0).getUpdatedSingleEvent().getEventID(),
		resSEUList.get(0).getUpdatedSingleEvent().getEventID());
	assertEquals(sEUList.get(0).getUpdatedSingleEvent().getLocation(),
		resSEUList.get(0).getUpdatedSingleEvent().getLocation());
	assertEquals(sEUList.get(0).getUpdatedSingleEvent().getSingleEventID(),
		resSEUList.get(0).getUpdatedSingleEvent().getSingleEventID());
	assertEquals(sEUList.get(0).getUpdatedSingleEvent().getSupervisor(),
		resSEUList.get(0).getUpdatedSingleEvent().getSupervisor());
	assertEquals(sEUList.get(1).getComment(), resSEUList.get(1)
		.getComment());
	assertEquals(sEUList.get(1).getCreatorName(), resSEUList.get(1)
		.getCreatorName());
	assertEquals(sEUList.get(1).getOldSingleEventID(), resSEUList.get(1)
		.getOldSingleEventID());
	assertEquals(sEUList.get(1).getUpdateDate(), resSEUList.get(1)
		.getUpdateDate());
	assertEquals(
		sEUList.get(1).getUpdatedSingleEvent().getDate().getTime(),
		resSEUList.get(1).getUpdatedSingleEvent().getDate().getTime());
	assertEquals(sEUList.get(1).getUpdatedSingleEvent()
		.getDurationMinutes(), resSEUList.get(1)
		.getUpdatedSingleEvent().getDurationMinutes());
	assertEquals(sEUList.get(1).getUpdatedSingleEvent().getEventID(),
		resSEUList.get(1).getUpdatedSingleEvent().getEventID());
	assertEquals(sEUList.get(1).getUpdatedSingleEvent().getLocation(),
		resSEUList.get(1).getUpdatedSingleEvent().getLocation());
	assertEquals(sEUList.get(1).getUpdatedSingleEvent().getSingleEventID(),
		resSEUList.get(1).getUpdatedSingleEvent().getSingleEventID());
	assertEquals(sEUList.get(1).getUpdatedSingleEvent().getSupervisor(),
		resSEUList.get(1).getUpdatedSingleEvent().getSupervisor());

    }

    @Test
    public void testGetEventGroups() {
	List<Integer> res = new ArrayList<Integer>();
	res.add(4);
	EventGroup eG = new EventGroup(1, "TestEvent1",
		"This is the first TestEvent", res);
	EventGroup reseG = null;
	try {
	    reseG = dbs.getEventGroups(4, "TEST").get(0);
	} catch (ObjectNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	assertEquals(eG.getEventGroupName(), reseG.getEventGroupName());
	assertEquals(eG.getEventGroupInfo(), reseG.getEventGroupInfo());
	assertEquals(eG.getEventGroupID(), reseG.getEventGroupID());
	assertEquals(eG.getCategories(), reseG.getCategories());
    }

    @Test
    public void testGetEvents() {
	ArrayList<Integer> res = new ArrayList<Integer>();
	res.add(5);
	Event e = new Event(9, 2, "Vorlesung", res);
	Event resE = null;
	try {
	    resE = dbs.getEvents(2, 0).get(0);
	} catch (ObjectNotFoundException e1) {
	    // TODO Auto-generated catch block
	    e1.printStackTrace();
	}
	assertEquals(e.getCategories(), resE.getCategories());
	assertEquals(e.getEventGroupID(), resE.getEventGroupID());
	assertEquals(e.getEventID(), resE.getEventID());
	assertEquals(e.getEventName(), resE.getEventName());
    }

    @Test
    public void testGetSingleEvent() {
	SingleEvent sE = new SingleEvent(10, 15, "test", new Date(21024000),
		"Prof. Test", 14);
	SingleEvent ressE = null;
	try {
	    ressE = dbs.getSingleEvent(10);
	} catch (ObjectNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	assertEquals("SEID", sE.getSingleEventID(), ressE.getSingleEventID());
	assertEquals("Duration", sE.getDurationMinutes(),
		ressE.getDurationMinutes());
	assertEquals("EID", sE.getEventID(), ressE.getEventID());
	assertEquals("Location", sE.getLocation(), ressE.getLocation());
	assertEquals("Supervisor", sE.getSupervisor(), ressE.getSupervisor());
	assertEquals("Date", sE.getDate(), ressE.getDate());
    }

    @Test
    public void testGetEventGroup() {
	List<Integer> res = new ArrayList<Integer>();
	res.add(4);
	res.add(6);
	res.add(8);
	EventGroup eG = new EventGroup(1, "TestEvent1",
		"This is the first TestEvent", res);
	EventGroup resG = null;
	try {
	    resG = dbs.getEventGroup(1);
	} catch (ObjectNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	assertEquals(eG.getEventGroupName(), resG.getEventGroupName());
	assertEquals(eG.getEventGroupInfo(), resG.getEventGroupInfo());
	assertEquals(eG.getEventGroupID(), resG.getEventGroupID());
	assertEquals(eG.getCategories(), resG.getCategories());
    }

    @Test
    public void testGetEvent() {
	ArrayList<Integer> res = new ArrayList<Integer>();
	res.add(4);
	res.add(5);
	Event e = new Event(2, 1, "Uebung", res);
	Event resE = null;
	try {
	    resE = dbs.getEvent(2);
	} catch (ObjectNotFoundException e1) {
	    // TODO Auto-generated catch block
	    e1.printStackTrace();
	}
	assertEquals(e.getCategories(), resE.getCategories());
	assertEquals(e.getEventGroupID(), resE.getEventGroupID());
	assertEquals(e.getEventID(), resE.getEventID());
	assertEquals(e.getEventName(), resE.getEventName());
    }

    @Test
    public void testGetEmailAddress() {
	try {
	    assertEquals("derp@Derpenson.com", dbs.getEmailAddress(2));
	} catch (ObjectNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    @Test
    public void testGetSubscribedUserIDs() {
	ArrayList<Integer> res = new ArrayList<Integer>();
	res.add(2);
	res.add(5);
	res.add(7);
	try {
	    assertEquals(res, dbs.getSubscribedUserIDs(1));
	} catch (ObjectNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    @Test
    public void testGetRightholders() {
	List<String> resN = null;
	try {
	    resN = dbs.getRightholders(1, 2);
	} catch (ObjectNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	ArrayList<String> nList = new ArrayList<String>();
	nList.add("Max Mustermann");
	nList.add("Max1 Mustermann4");
	assertEquals(nList.get(0), resN.get(0));
	assertEquals(nList.get(1), resN.get(1));
    }

    @Test
    public void testGetRightholders2() {
	List<String> resN = null;
	try {
	    resN = dbs.getRightholders(1);
	} catch (ObjectNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	ArrayList<String> nList = new ArrayList<String>();
	nList.add("Max Mustermann");
	nList.add("Test Mustermann");
	nList.add("Max1 Mustermann4");
	assertEquals(nList.get(0), resN.get(0));
	assertEquals(nList.get(1), resN.get(1));
	assertEquals(nList.get(2), resN.get(2));
    }

    @Test
    public void testGetDeputies() {
	List<String> resN = null;
	try {
	    resN = dbs.getDeputies(2, 2);
	} catch (ObjectNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	ArrayList<String> nList = new ArrayList<String>();
	nList.add("Max1 Mustermann4");
	assertEquals(nList.get(0), resN.get(0));
    }

    @Test
    public void testGetDeputies2() {
	List<String> resN = null;
	try {
	    resN = dbs.getDeputies(2);
	} catch (ObjectNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	ArrayList<String> nList = new ArrayList<String>();
	nList.add("Max1 Mustermann4");
	nList.add("Max2 Mustermann3");
	assertEquals(nList.get(0), resN.get(0));
	assertEquals(nList.get(1), resN.get(1));
    }

    @Test
    public void testGetOwners() {
	List<String> resN = null;
	try {
	    resN = dbs.getOwners(2);
	} catch (ObjectNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	ArrayList<String> nList = new ArrayList<String>();
	nList.add("Max3 Mustermann2");
	assertEquals(nList.get(0), resN.get(0));
    }

    @Test
    public void testGetUserSettings() {
	Set<String> uGCMKeys = new HashSet<String>();
	uGCMKeys.add("refgsfb");
	uGCMKeys.add("dsfbsdb");
	uGCMKeys.add("snfdggd");
	Map<Integer, UserEventSettings> eventSettings = new HashMap<Integer, UserEventSettings>();
	eventSettings.put(1, new UserEventSettings("FFFFFF"));
	eventSettings.put(2, new UserEventSettings("000000"));
	eventSettings.put(3, new UserEventSettings("FF0000"));
	eventSettings.put(4, new UserEventSettings("00FF00"));
	UserSettings uS = new UserSettings(uGCMKeys, true, "english",
		eventSettings);
	UserSettings resUs = null;
	try {
	    resUs = dbs.getUserSettings(2);
	} catch (ObjectNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	assertEquals(uS.getLanguage(), resUs.getLanguage());
	assertEquals(uS.getGCMKeys(), resUs.getGCMKeys());
	assertEquals(uS.getEventSettings(1).getColorCode(), resUs
		.getEventSettings(1).getColorCode());
	assertEquals(uS.getEventSettings(2).getColorCode(), resUs
		.getEventSettings(2).getColorCode());
	assertEquals(uS.getEventSettings(3).getColorCode(), resUs
		.getEventSettings(3).getColorCode());
	assertEquals(uS.getEventSettings(4).getColorCode(), resUs
		.getEventSettings(4).getColorCode());
	assertEquals(uS.isEmailNotificationEnabled(),
		resUs.isEmailNotificationEnabled());
	for (int i = 0; i < 6; i++) {
	    assertEquals(uS.hasSubscribed(i), resUs.hasSubscribed(i));
	}
    }

    @Test
    public void testGetCategoryNodes() {
	CategoryNodeImpl resCNI = null;
	try {
	    resCNI = dbs.getCategoryTree(1);
	} catch (ObjectNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	CategoryNodeImpl cNI1 = new CategoryNodeImpl(1, "BA");
	CategoryNodeImpl cNI2 = new CategoryNodeImpl(3, "INF");
	cNI2.addChildNode(new CategoryNodeImpl(6, "S2"));
	cNI1.addChildNode(cNI2);
	cNI1.addChildNode(new CategoryNodeImpl(4, "WI"));
	cNI1.addChildNode(new CategoryNodeImpl(5, "MN"));

	assertEquals(resCNI.getId(), cNI1.getId());
	assertEquals(resCNI.getName(), cNI1.getName());
	assertEquals(resCNI.getChildren().get(0).getId(), cNI1.getChildren()
		.get(0).getId());
	assertEquals(resCNI.getChildren().get(0).getName(), cNI1.getChildren()
		.get(0).getName());
	assertEquals(resCNI.getChildren().get(1).getId(), cNI1.getChildren()
		.get(1).getId());
	assertEquals(resCNI.getChildren().get(1).getName(), cNI1.getChildren()
		.get(1).getName());
	assertEquals(resCNI.getChildren().get(2).getId(), cNI1.getChildren()
		.get(2).getId());
	assertEquals(resCNI.getChildren().get(2).getName(), cNI1.getChildren()
		.get(2).getName());
	assertEquals(resCNI.getChildren().get(0).getChildren().get(0).getId(),
		cNI1.getChildren().get(0).getChildren().get(0).getId());
	assertEquals(
		resCNI.getChildren().get(0).getChildren().get(0).getName(),
		cNI1.getChildren().get(0).getChildren().get(0).getName());

    }

    @Test
    public void testupdateUserSetting() {
	Set<String> uGCMKeys = new HashSet<String>();
	uGCMKeys.add("test1");
	uGCMKeys.add("test2");
	Map<Integer, UserEventSettings> eventSettings = new HashMap<Integer, UserEventSettings>();
	eventSettings.put(1, new UserEventSettings("FFFFFF"));
	eventSettings.put(2, new UserEventSettings("000000"));
	eventSettings.put(5, new UserEventSettings("123456"));
	eventSettings.put(6, new UserEventSettings("654321"));
	UserSettings uS = new UserSettings(uGCMKeys, false, "english",
		eventSettings);
	try {
	    dbs.updateUserSetting(uS, 4);
	} catch (ObjectNotFoundException e1) {
	    // TODO Auto-generated catch block
	    e1.printStackTrace();
	} catch (IncorrectObjectException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	UserSettings res = null;
	try {
	    res = dbs.getUserSettings(4);
	} catch (ObjectNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	assertEquals(false, res.isEmailNotificationEnabled());
	assertEquals("english", res.getLanguage());
	assertEquals("[test1, test2]", res.getGCMKeys().toString());
    }

    @Test
    public void testGetUserID() {
	try {
	    assertEquals(7, dbs.getUserID("test2"));
	} catch (ObjectNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    @Test
    public void testGetUsersRoleForEvent() {
	try {
	    assertEquals(UserRole.ADMINISTRATOR, dbs.getUsersRoleForEvent(2, 6));
	    assertEquals(UserRole.RIGHTHOLDER, dbs.getUsersRoleForEvent(1, 1));
	    assertEquals(UserRole.DEPUTY, dbs.getUsersRoleForEvent(4, 2));
	    assertEquals(UserRole.OWNER, dbs.getUsersRoleForEvent(6, 2));
	    assertEquals(UserRole.USER, dbs.getUsersRoleForEvent(1, 2));
	} catch (ObjectNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

    }

    @Test
    public void testGetUserName() {
	try {
	    assertEquals("user123", dbs.getUserName(1));
	} catch (ObjectNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    @Test
    public void testInsertPerson1() {
	try {
	    dbs.insertPerson("A", "B", "C", "D", "E", "english", false);
	} catch (ObjectAlreadyExsistsException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IncorrectObjectException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    @Test(expected = ObjectAlreadyExsistsException.class)
    public void testInsertPerson2() throws Exception {
	dbs.insertPerson("A", "B", "C", "F", "E", "english", false);
    }

    @Test(expected = ObjectAlreadyExsistsException.class)
    public void testInsertPerson3() throws Exception {
	dbs.insertPerson("F", "B", "C", "D", "E", "english", false);
    }

    @Test
    public void testInsertSingleEventUpdate() {
	try {
	    dbs.insertSingleEventUpdate(new SingleEventUpdate(new SingleEvent(
		    0, 2, "HU 000", new Date(200000), "Mr.Supervise", 50), 8,
		    new Date(195000), "Mr. Creator", "A new Test update"));
	} catch (ObjectNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IncorrectObjectException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    @Test
    public void testInsertSingleEventUpdateNEW() {
	try {
	    dbs.insertSingleEventUpdate(new SingleEventUpdate(new SingleEvent(
		    0, 3, "HU 111", new Date(300000), "Mr.GanzNew", 50), 0,
		    new Date(295000), "Mr. New", "Added new SingleEvent"));
	} catch (ObjectNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IncorrectObjectException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    @Test
    public void testInsertSingleEventUpdateCANCEL() {
	try {
	    dbs.insertSingleEventUpdate(new SingleEventUpdate(null, 9,
		    new Date(195000), "Mr. Cancell", "SingleEvent Cancelled"));
	} catch (ObjectNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IncorrectObjectException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    @Test
    public void testInsertEvent() {
	List<Integer> list = new ArrayList<>();
	list.add(4);
	list.add(6);
	list.add(1);
	try {
	    dbs.insertEvent(new Event(0, 2, "FunnyStuff", list));
	} catch (ObjectNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IncorrectObjectException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    @Test
    public void testInsertCategoryTree() {
	CategoryNodeImpl category = new CategoryNodeImpl(-1, "Test");
	category.addChildNode(new CategoryNodeImpl(-1, "TestChild1"));
	category.addChildNode(new CategoryNodeImpl(-1, "TestChild2"));

	try {
	    dbs.insertCategoryTree(category, 4);
	} catch (IncorrectObjectException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (ObjectAlreadyExsistsException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (ObjectNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    @Test
    public void testInsertPrivilege() {
	try {
	    dbs.insertPrivilege(2, 2, 2, 1, new Date(1000));
	} catch (ObjectNotFoundException | IncorrectObjectException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    @Test
    public void testAddAdmin() {
	try {
	    dbs.addAdministrator(8);
	} catch (ObjectNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    @Test
    public void testRemoveAdmin() {
	try {
	    dbs.removeAdministrator(8);
	} catch (ObjectNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }
}
