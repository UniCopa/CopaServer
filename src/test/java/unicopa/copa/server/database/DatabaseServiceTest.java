package unicopa.copa.server.database;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import unicopa.copa.base.event.Event;
import unicopa.copa.base.event.EventGroup;
import unicopa.copa.base.event.SingleEvent;

public class DatabaseServiceTest {
    private static String driver = "org.apache.derby.jdbc.EmbeddedDriver";
    private static String dbURL = "c:/squirrel/TestDB";
    private static String protocol = "jdbc:derby:";
    private static Connection conn = null;
    private DatabaseService dbs;

    @Before
    public void connect() {
	try {
	    Class.forName(driver).newInstance();
	    if (conn == null)
		conn = DriverManager.getConnection(protocol + dbURL);
	} catch (Exception except) {
	    except.printStackTrace();
	}
	File db = new File(dbURL);
	try {
	    dbs = new DatabaseService(db, "", "");
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    @After
    public void disconnect() {
	try {
	    conn.close();
	} catch (SQLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
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
	// TODO correct values for SingleEvent
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
