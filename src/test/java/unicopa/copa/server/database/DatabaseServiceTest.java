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
	    runner.setLogWriter(null);
	    runner.setErrorLogWriter(null);
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
	runner.setErrorLogWriter(null);
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
	SingleEvent sE = new SingleEvent(10, 15, "Raumtest",
		new Date(21024000), "Prof. Test", 14);
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
    public void testgetEventGroup() {
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

}
