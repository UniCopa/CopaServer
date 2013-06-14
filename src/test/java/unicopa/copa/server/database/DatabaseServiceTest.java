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
    public void testGetEventGroups() {
	List<Integer> res = new ArrayList<Integer>();
	res.add(5);
	EventGroup eG = new EventGroup(1, "TestEvent1",
		"This is the first TestEvent", res);
	EventGroup reseG = dbs.getEventGroups(5, "TEST").get(0);
	assertEquals(eG.getEventGroupName(), reseG.getEventGroupName());
	assertEquals(eG.getEventGroupInfo(), reseG.getEventGroupInfo());
	assertEquals(eG.getEventGroupID(), reseG.getEventGroupID());
	assertEquals(eG.getCategories(), reseG.getCategories());
    }

    @Test
    public void testGetEvents() {
	ArrayList<Integer> res = new ArrayList<Integer>();
	res.add(6);
	Event e = new Event(9, 2, "Vorlesung", res);
	Event resE = dbs.getEvents(2, 1).get(0);
	assertEquals(e.getCategories(), resE.getCategories());
	assertEquals(e.getEventGroupID(), resE.getEventGroupID());
	assertEquals(e.getEventID(), resE.getEventID());
	assertEquals(e.getEventName(), resE.getEventName());
    }

    @Test
    public void testGetSingleEvent() {
	SingleEvent sE = new SingleEvent(10, 15, "test", new Date(21024000),
		"Prof. Test", 14);
	SingleEvent ressE = dbs.getSingleEvent(10);
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
	res.add(5);
	res.add(7);
	res.add(9);
	EventGroup eG = new EventGroup(1, "TestEvent1",
		"This is the first TestEvent", res);
	EventGroup resG = dbs.getEventGroup(1);
	assertEquals(eG.getEventGroupName(), resG.getEventGroupName());
	assertEquals(eG.getEventGroupInfo(), resG.getEventGroupInfo());
	assertEquals(eG.getEventGroupID(), resG.getEventGroupID());
	assertEquals(eG.getCategories(), resG.getCategories());
    }

    @Test
    public void testGetEvent() {
	ArrayList<Integer> res = new ArrayList<Integer>();
	res.add(5);
	res.add(6);
	Event e = new Event(2, 1, "Uebung", res);
	Event resE = dbs.getEvent(2);
	assertEquals(e.getCategories(), resE.getCategories());
	assertEquals(e.getEventGroupID(), resE.getEventGroupID());
	assertEquals(e.getEventID(), resE.getEventID());
	assertEquals(e.getEventName(), resE.getEventName());
    }

    @Test
    public void testGetEmailAddress() {
	assertEquals("derp@Derpenson.com", dbs.getEmailAddress(2));
    }

    @Test
    public void testGetSubscribedUserIDs() {
	ArrayList<Integer> res = new ArrayList<Integer>();
	res.add(2);
	res.add(5);
	res.add(7);
	assertEquals(res, dbs.getSubscribedUserIDs(1));
    }

    @Test
    public void testGetRightholders() {
	List<String> resN = dbs.getRightholders(1, 2);
	ArrayList<String> nList = new ArrayList<String>();
	nList.add("Max Mustermann");
	nList.add("Max1 Mustermann4");
	assertEquals(nList.get(0), resN.get(0));
	assertEquals(nList.get(1), resN.get(1));
    }

    @Test
    public void testGetRightholders2() {
	List<String> resN = dbs.getRightholders(1);
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
	List<String> resN = dbs.getDeputies(2, 2);
	ArrayList<String> nList = new ArrayList<String>();
	nList.add("Max1 Mustermann4");
	assertEquals(nList.get(0), resN.get(0));
    }

    @Test
    public void testGetDeputies2() {
	List<String> resN = dbs.getDeputies(2);
	ArrayList<String> nList = new ArrayList<String>();
	nList.add("Max1 Mustermann4");
	nList.add("Max2 Mustermann3");
	assertEquals(nList.get(0), resN.get(0));
	assertEquals(nList.get(1), resN.get(1));
    }

    @Test
    public void testGetOwners() {
	List<String> resN = dbs.getOwners(2);
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
	UserSettings resUs = dbs.getUserSettings(2);
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
	CategoryNodeImpl resCNI = dbs.getCategoryTree(2);
	CategoryNodeImpl cNI1 = new CategoryNodeImpl(2, "BA");
	CategoryNodeImpl cNI2 = new CategoryNodeImpl(4, "INF");
	cNI2.addChildNode(new CategoryNodeImpl(7, "S2"));
	cNI1.addChildNode(cNI2);
	cNI1.addChildNode(new CategoryNodeImpl(5, "WI"));
	cNI1.addChildNode(new CategoryNodeImpl(6, "MN"));

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
	dbs.updateUserSetting(uS, 4);
	UserSettings res = dbs.getUserSettings(4);
	assertEquals(false, res.isEmailNotificationEnabled());
	assertEquals("english", res.getLanguage());
	assertEquals("[test1, test2]", res.getGCMKeys().toString());
    }

    @Test
    public void testGetUserID() {
	assertEquals(7, dbs.getUserID("test2"));
    }

    @Test
    public void testGetUsersRoleForEvent() {
	assertEquals(UserRole.ADMINISTRATOR, dbs.getUsersRoleForEvent(2, 6));
	assertEquals(UserRole.RIGHTHOLDER, dbs.getUsersRoleForEvent(1, 1));
	assertEquals(UserRole.DEPUTY, dbs.getUsersRoleForEvent(4, 2));
	assertEquals(UserRole.OWNER, dbs.getUsersRoleForEvent(6, 2));
	assertEquals(UserRole.USER, dbs.getUsersRoleForEvent(1, 2));
    }
}
