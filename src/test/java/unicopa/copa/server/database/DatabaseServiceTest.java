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
import org.junit.Ignore;
import org.junit.Test;

import unicopa.copa.base.ServerStatusNote;
import unicopa.copa.base.UserData;
import unicopa.copa.base.UserEventSettings;
import unicopa.copa.base.UserRole;
import unicopa.copa.base.UserSettings;
import unicopa.copa.base.event.CategoryNode;
import unicopa.copa.base.event.CategoryNodeImpl;
import unicopa.copa.base.event.Event;
import unicopa.copa.base.event.EventGroup;
import unicopa.copa.base.event.SingleEvent;
import unicopa.copa.base.event.SingleEventUpdate;
import unicopa.copa.server.GeneralUserPermission;
import unicopa.copa.server.database.util.DatabaseUtil;
import unicopa.copa.server.module.eventimport.model.EventGroupImport;
import unicopa.copa.server.module.eventimport.model.EventImport;
import unicopa.copa.server.module.eventimport.model.EventImportContainer;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
/**
 * 
 * @author Nintaro
 *
 */
public class DatabaseServiceTest {
    private static String dbURL = "database";
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
    public void testGetUserData() {
	try {
	    UserData res = dbs.getUserData(2);
	    assertEquals("Derp Derpenson", res.getName());
	    assertEquals("derp@Derpenson.com", res.getEmail());
	} catch (ObjectNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    @Test
    public void testUpdateUserSettingTest2() {
	Set<String> gcmKeys = new HashSet<>();
	gcmKeys.add("TestKey1");
	gcmKeys.add("TestKey2");
	Map<Integer, UserEventSettings> eventSettings = new HashMap<Integer, UserEventSettings>();
	eventSettings.put(3, new UserEventSettings("F0F0F0"));

	UserSettings setting = new UserSettings(gcmKeys, true, "english",
		eventSettings);
	try {
	    dbs.updateUserSetting(setting, dbs.getUserID("derp"));
	    dbs.updateUserSetting(setting, dbs.getUserID("derp"));
	} catch (ObjectNotFoundException | IncorrectObjectException
		| ObjectAlreadyExsistsException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    @Test
    public void testGetUserRole() {
	try {
	    assertEquals(UserRole.USER, dbs.getUserRole(1));
	    assertEquals(UserRole.USER, dbs.getUserRole(2));
	} catch (ObjectNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    @Test
    public void testInsertStupidFunktionnameHere() {
	try {
	    dbs.getUsersPriviligedEvents(4);
	} catch (ObjectNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    @Test
    public void testGetCurrentSingleEvents() {
	try {
	    List<SingleEvent> res = dbs.getCurrentSingleEvents(4, new Date());
	    assertEquals(1, res.size());
	    // TODO proper test
	} catch (ObjectNotFoundException | IncorrectObjectException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    @Test
    public void testGetSingleEventUpdates() {
	try {
	    dbs.getSingleEventUpdates(1, new Date());
	} catch (ObjectNotFoundException | IncorrectObjectException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    @Test
    public void testGetSubscribedSingleEventUpdates() {
	SingleEventUpdate sEU1 = new SingleEventUpdate(new SingleEvent(0, 0,
		"DUMMY", new Date(0), "DUMMY", 0), 25,
		new Date(1382471714176L), "Deleter", "Muss net");
	List<SingleEventUpdate> sEUList = new ArrayList<>();
	sEUList.add(sEU1);
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
	assertEquals(null, resSEUList.get(0).getUpdatedSingleEvent());
    }

    @Test
    public void testGetEventGroups() {
	List<Integer> res = new ArrayList<Integer>();
	res.add(13);
	EventGroup eG = new EventGroup(2, "Telematik 1(Test)", "Ilf", res);
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
	res.add(6);
	res.add(13);
	Event e = new Event(4, 2, "Uebung 1", res);
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
	SingleEvent sE = new SingleEvent(10, 5, "HU 754", new Date(
		1394471714176L), "Dr. Test", 20);
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
	res.add(10);
	EventGroup eG = new EventGroup(1, "Mathe(Test)",
		"Hier könnte ihr Werbung stehen", res);
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
	res.add(10);
	Event e = new Event(2, 1, "Uebung 2", res);
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
	res.add(10);
	res.add(4);
	res.add(2);
	res.add(1);
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
	    resN = dbs.getRightholders(12, 4);
	} catch (ObjectNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	ArrayList<String> nList = new ArrayList<String>();
	nList.add("Derp Derpenson");
	nList.add("Max1 Mustermann4");
	assertEquals(nList.get(0), resN.get(0));
    }

    @Test
    public void testGetRightholders2() {
	List<String> resN = null;
	try {
	    resN = dbs.getRightholders(12);
	} catch (ObjectNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	ArrayList<String> nList = new ArrayList<String>();
	nList.add("Derp Derpenson");
	assertEquals(nList.get(0), resN.get(0));
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
	nList.add("Dep uty");
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
	nList.add("Dep uty");
	nList.add("Max2 Mustermann3");
	assertEquals(nList.get(0), resN.get(0));
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
	nList.add("Own er");
	assertEquals(nList.get(0), resN.get(0));
    }

    @Test
    public void testGetUserSettings() {
	Set<String> uGCMKeys = new HashSet<String>();
	uGCMKeys.add("refgsfb");
	uGCMKeys.add("dsfbsdb");
	uGCMKeys.add("snfdggd");
	Map<Integer, UserEventSettings> eventSettings = new HashMap<Integer, UserEventSettings>();
	eventSettings.put(1, new UserEventSettings("00FFF0"));
	eventSettings.put(2, new UserEventSettings("000FF0"));
	eventSettings.put(3, new UserEventSettings("000000"));
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
	assertEquals(uS.isEmailNotificationEnabled(),
		resUs.isEmailNotificationEnabled());
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
	} catch (ObjectAlreadyExsistsException e) {
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
	    assertEquals(2, dbs.getUserID("derp"));
	} catch (ObjectNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    @Test
    public void testGetUsersRoleForEvent() {
	try {
	    assertEquals(UserRole.USER, dbs.getUsersRoleForEvent(2, 6));
	    assertEquals(UserRole.RIGHTHOLDER, dbs.getUsersRoleForEvent(1, 1));
	    assertEquals(UserRole.OWNER, dbs.getUsersRoleForEvent(4, 2));
	    assertEquals(UserRole.USER, dbs.getUsersRoleForEvent(6, 2));
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
	    dbs.insertPerson("A", "B", "C", "D", "E", "english", false,
		    GeneralUserPermission.NONE);
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
	dbs.insertPerson("A", "B", "C", "F", "E", "english", false,
		GeneralUserPermission.NONE);
    }

    @Test(expected = ObjectAlreadyExsistsException.class)
    public void testInsertPerson3() throws Exception {
	dbs.insertPerson("F", "B", "C", "D", "E", "english", false,
		GeneralUserPermission.NONE);
    }

    @Test
    public void testInsertSingleEventUpdate() {
	try {
	    SingleEvent se = new SingleEvent(0, 2, "HU 000", new Date(200000),
		    "Mr.Supervise", 50);
	    dbs.insertSingleEventUpdate(new SingleEventUpdate(se, 8, new Date(
		    195000), "Mr. Creator", "A new Test update"));
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

    @Test(expected = IncorrectObjectException.class)
    public void testInsertSingleEventUpdateCANCEL2()
	    throws ObjectNotFoundException, IncorrectObjectException {
	dbs.insertSingleEventUpdate(new SingleEventUpdate(null, 9, new Date(
		195000), "Mr. Cancell", "SingleEvent Cancelled"));
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
	    dbs.insertPrivilege(2, 4, 2, 1, new Date(1000));
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

    @Test
    public void testAddServerStatusNote() {
	try {
	    dbs.addServerStatusNote("TestNote...huhu");
	    dbs.addServerStatusNote("Another Test...YAY");
	} catch (IncorrectObjectException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    @Test
    public void testGetServerStatusNote() {
	try {
	    List<ServerStatusNote> notes = dbs.getServerStatusNote(new Date(
		    new Date().getTime() - 100000));
	    assertEquals("TestNote...huhu", notes.get(0).getNote());
	    assertEquals("Another Test...YAY", notes.get(1).getNote());
	} catch (IncorrectObjectException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    @Test
    public void testGetUsersAppointedUsers() {
	try {
	    dbs.getUsersAppointedUsers(2);
	} catch (ObjectNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    @Test
    public void testIsAppointedByUser() {
	try {
	    assertEquals(false,
		    dbs.isAppointedBy(1, 2, 1, UserRole.RIGHTHOLDER));
	    assertEquals(true, dbs.isAppointedBy(1, 7, 1, UserRole.RIGHTHOLDER));
	} catch (ObjectNotFoundException | IncorrectObjectException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    @Test
    public void testInsertEventGroup() {
	List<Integer> cat = new ArrayList<Integer>();
	cat.add(4);
	cat.add(5);
	try {
	    dbs.insertEventGroup(new EventGroup(0, "insertTestEG",
		    "This is a TestEG", cat));
	} catch (ObjectNotFoundException | IncorrectObjectException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    @Test
    public void testPossibleOwners1() {
	try {
	    assertEquals("Dr. Test", dbs.getPossibleOwners(1).get(0));
	    assertEquals("Herr Prof. Owner", dbs.getPossibleOwners(1).get(1));
	} catch (ObjectNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    @Test
    public void testPossibleOwners2() {
	try {
	    List<String> ownerList = new ArrayList<>();
	    ownerList.add("Derpy");
	    ownerList.add("Owner2");
	    dbs.insertPossibleOwners(4, ownerList);
	} catch (ObjectNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IncorrectObjectException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    @Test
    public void testPossibleOwners3() {
	try {
	    assertEquals("Dr. Dr. Prof. Derp", dbs.getPossibleOwners(4).get(0));
	    assertEquals("Prof. Dr. Mustermann", dbs.getPossibleOwners(4)
		    .get(1));
	    assertEquals("Derpy", dbs.getPossibleOwners(4).get(2));
	    assertEquals("Owner2", dbs.getPossibleOwners(4).get(3));
	} catch (ObjectNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    @Test
    public void testZMatchOwners() {
	Map<Integer, List<Integer>> ownerEventMap = dbs.matchOwners();
	List<Integer> ownerListEmpty = new ArrayList<>();
	List<Integer> ownerListWith4 = new ArrayList<>();
	ownerListWith4.add(4);
	assertEquals(ownerListWith4, ownerEventMap.get(1));
	assertEquals(ownerListWith4, ownerEventMap.get(7));
	assertEquals(ownerListWith4, ownerEventMap.get(12));
	assertEquals(ownerListEmpty, ownerEventMap.get(2));

    }

    @Test
    public void testzzzzImportEvents() {
	CategoryNodeImpl categoryTree = new CategoryNodeImpl(0, "root");
	CategoryNodeImpl categoryTreeC1 = new CategoryNodeImpl(0, "child1");
	CategoryNodeImpl categoryTreeC2 = new CategoryNodeImpl(0, "child2");
	CategoryNodeImpl categoryTreeC11 = new CategoryNodeImpl(0, "child11");
	CategoryNodeImpl categoryTreeC12 = new CategoryNodeImpl(0, "child12");
	categoryTree.addChildNode(categoryTreeC1);
	categoryTree.addChildNode(categoryTreeC2);
	categoryTreeC1.addChildNode(categoryTreeC11);
	categoryTreeC1.addChildNode(categoryTreeC12);

	Set<CategoryNode> eG1Cat = new HashSet<>();
	eG1Cat.add(categoryTreeC11);
	eG1Cat.add(categoryTreeC2);

	Set<CategoryNode> eG2Cat = new HashSet<>();
	eG1Cat.add(categoryTreeC12);

	List<CategoryNode> e1Cat = new ArrayList<>();
	e1Cat.add(categoryTreeC11);
	e1Cat.add(categoryTreeC2);

	List<CategoryNode> e2Cat = new ArrayList<>();
	e2Cat.add(categoryTreeC2);

	List<CategoryNode> e3Cat = new ArrayList<>();
	e3Cat.add(categoryTreeC12);

	SingleEvent se1 = new SingleEvent(0, 0, "HU 102", new Date(),
		"Mr. Super", 10);
	SingleEvent se2 = new SingleEvent(0, 0, "HU 120", new Date(),
		"Superviser", 20);
	SingleEvent se3 = new SingleEvent(0, 0, "LdV1", new Date(),
		"Uffpasser", 90);
	SingleEvent se4 = new SingleEvent(0, 0, "Mensa", new Date(), "Derp", 45);

	List<SingleEvent> sEL1 = new ArrayList<>();
	sEL1.add(se1);
	sEL1.add(se2);

	List<SingleEvent> sEL2 = new ArrayList<>();
	sEL2.add(se3);

	List<SingleEvent> sEL3 = new ArrayList<>();
	sEL3.add(se4);

	List<String> possibleOwner1 = new ArrayList<>();
	possibleOwner1.add("Prof. Derp");

	List<String> possibleOwner2 = new ArrayList<>();
	possibleOwner2.add("Prof. Derp");
	possibleOwner2.add("Dr. Test");

	List<String> possibleOwner3 = new ArrayList<>();
	possibleOwner3.add("Prof. Dr. Admin");

	EventImport eI1 = new EventImport("event1", sEL1, possibleOwner1, e1Cat);
	EventImport eI2 = new EventImport("event2", sEL2, possibleOwner2, e2Cat);
	EventImport eI3 = new EventImport("event3", sEL3, possibleOwner3, e3Cat);

	List<EventImport> eventImport1 = new ArrayList<>();
	eventImport1.add(eI1);
	eventImport1.add(eI2);

	List<EventImport> eventImport2 = new ArrayList<>();
	eventImport2.add(eI3);

	EventGroupImport eventGroupImp1 = new EventGroupImport("TestEG1",
		"No Info", eventImport1, eG1Cat);

	EventGroupImport eventGroupImp2 = new EventGroupImport("TestEG2", "",
		eventImport2, eG2Cat);

	List<EventGroupImport> eventGroupContainers = new ArrayList<>();
	eventGroupContainers.add(eventGroupImp1);
	eventGroupContainers.add(eventGroupImp2);
	EventImportContainer container = new EventImportContainer(categoryTree,
		eventGroupContainers);
	try {
	    dbs.importEvents(container);
	} catch (ObjectNotFoundException | IncorrectObjectException
		| ObjectAlreadyExsistsException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    // @Test
    // public void dropCheck() {
    // try {
    // System.out.println("Name:" + dbs.getUserName(2));
    // System.out.println(">" + dbs.getRightholders(12));
    // dbs.setUserRoleForEvent(2, 12, UserRole.USER, 2);
    // System.out.println(">>" + dbs.getRightholders(12));
    // } catch (ObjectNotFoundException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // } catch (IncorrectObjectException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // }
}
