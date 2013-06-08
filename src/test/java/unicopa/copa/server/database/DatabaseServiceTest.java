package unicopa.copa.server.database;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import unicopa.copa.base.event.Event;
import unicopa.copa.base.event.EventGroup;
import unicopa.copa.base.event.SingleEvent;
import unicopa.copa.server.database.util.DatabaseUtil;

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
	    runner.runScript(reader);

	    reader = new BufferedReader(new InputStreamReader(
		    DatabaseServiceTest.class
			    .getResourceAsStream(RESOURCE_SQL_INSERTS)));
	    runner = new ScriptRunner(conn);
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
	runner.runScript(reader);
    }

    @Test
    public void testGetEventGroups() {
	List<Integer> res = new ArrayList<Integer>();
	res.add(5);
	EventGroup resG = new EventGroup(1, "TestEvent1",
		"This is the first TestEvent", res);
	assertEquals(resG.getEventGroupName(), dbs.getEventGroups(5, "TEST")
		.get(0).getEventGroupName());
	assertEquals(resG.getEventGroupInfo(), dbs.getEventGroups(5, "TEST")
		.get(0).getEventGroupInfo());
	assertEquals(resG.getEventGroupID(),
		dbs.getEventGroups(5, "TEST").get(0).getEventGroupID());
	assertEquals(resG.getCategories(), dbs.getEventGroups(5, "TEST").get(0)
		.getCategories());
    }

    @Test
    public void testGetEvents() {
	ArrayList<Integer> res = new ArrayList<Integer>();
	res.add(6);
	Event resE = new Event(9, 2, "Vorlesung", res);
	assertEquals(resE.getCategories(), dbs.getEvents(2, 1).get(0)
		.getCategories());
	assertEquals(resE.getEventGroupID(), dbs.getEvents(2, 1).get(0)
		.getEventGroupID());
	assertEquals(resE.getEventID(), dbs.getEvents(2, 1).get(0).getEventID());
	assertEquals(resE.getEventName(), dbs.getEvents(2, 1).get(0)
		.getEventName());
    }

    @Test
    public void testGetSingleEvent() {
	SingleEvent resS = new SingleEvent(10, 15, "Raumtest", new Date(
		21024000), "Prof. Test", 14);
	assertEquals("SEID", resS.getSingleEventID(), dbs.getSingleEvent(10)
		.getSingleEventID());
	assertEquals("Duration", resS.getDurationMinutes(),
		dbs.getSingleEvent(10).getDurationMinutes());
	assertEquals("EID", resS.getEventID(), dbs.getSingleEvent(10)
		.getEventID());
	assertEquals("Location", resS.getLocation(), dbs.getSingleEvent(10)
		.getLocation());
	assertEquals("Supervisor", resS.getSupervisor(), dbs.getSingleEvent(10)
		.getSupervisor());
	assertEquals("Date", resS.getDate(), dbs.getSingleEvent(10).getDate());
    }

}
