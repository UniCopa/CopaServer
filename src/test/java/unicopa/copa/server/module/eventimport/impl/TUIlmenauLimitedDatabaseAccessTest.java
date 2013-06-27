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
package unicopa.copa.server.module.eventimport.impl;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import unicopa.copa.base.UserRole;
import unicopa.copa.server.CopaSystem;
import unicopa.copa.server.GeneralUserPermission;
import unicopa.copa.server.database.DatabaseService;
import unicopa.copa.server.database.DatabaseServiceTest;
import unicopa.copa.server.database.IncorrectObjectException;
import unicopa.copa.server.database.ObjectNotFoundException;
import unicopa.copa.server.database.util.DatabaseUtil;

public class TUIlmenauLimitedDatabaseAccessTest {

    private TUIlmenauLimitedDatabaseAccess access;
    private CopaSystem system;
    private static String dbURL = "database";
    private static DatabaseService dbs;
    private static final String RESOURCE_SQL_INSERTS = "/sql/inserts.sql";
    private static final String RESOURCE_SQL_DROP = "/sql/drop.sql";
    private static final String RESOURCE_SQL_INITDB = "/sql/initializeDB.sql";
    public static String driver = "org.apache.derby.jdbc.EmbeddedDriver";
    public static String protocol = "jdbc:derby:";
    private static File db = new File(dbURL);
    private static Connection conn = null;

    public TUIlmenauLimitedDatabaseAccessTest() {

    }

    @Before
    public void setUp() {
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
	system = CopaSystem.getInstance();
	access = new TUIlmenauLimitedDatabaseAccess(system.getContext());
    }

    @AfterClass
    public static void disconnect() {
	Reader reader = new BufferedReader(new InputStreamReader(
		DatabaseServiceTest.class
			.getResourceAsStream(RESOURCE_SQL_DROP)));
	ScriptRunner runner = new ScriptRunner(conn);
	runner.setLogWriter(null);
	runner.runScript(reader);
    }

    @Test
    public void testMatchName() {
	try {
	    List<Integer> list1 = new ArrayList<>();
	    List<Integer> list2 = new ArrayList<>();
	    list1.add(1);
	    list2.add(1);
	    list1.add(7);
	    list2.add(7);
	    list1.add(18);
	    List<Integer> res1 = access.matchName("Herr Dr. Prof. Mustermann",
		    GeneralUserPermission.NONE);
	    List<Integer> res2 = access.matchName("Herr Dr. Prof. Mustermann",
		    GeneralUserPermission.POSSIBLE_OWNER);
	    assertEquals(list1, res1);
	    assertEquals(list2, res2);
	} catch (ObjectNotFoundException | IncorrectObjectException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }
}