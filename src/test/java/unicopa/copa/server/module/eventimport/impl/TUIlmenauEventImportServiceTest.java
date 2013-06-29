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

import unicopa.copa.server.module.eventimport.impl.tuilmenau.CourseEventType;
import unicopa.copa.server.module.eventimport.impl.tuilmenau.CourseEvent;
import unicopa.copa.server.module.eventimport.impl.tuilmenau.TUIlmenauEventImportService;
import com.google.gson.reflect.TypeToken;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import unicopa.copa.server.module.eventimport.model.EventImportContainer;
import unicopa.copa.server.module.eventimport.impl.tuilmenau.serialization.Serializer;

/**
 * 
 * @author Felix Wiemuth
 */
public class TUIlmenauEventImportServiceTest {
    TUIlmenauEventImportService service;

    public TUIlmenauEventImportServiceTest() {
    }

    @Before
    public void setUp() throws IOException {
	service = new TUIlmenauEventImportService(new FileInputStream(
		"eventImport.xml"));
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testSerialization() {
	List<Integer> groups = new LinkedList<>();
	groups.add(2);
	groups.add(4);
	CourseEvent event = new CourseEvent(CourseEventType.Vorlesungen,
		new Date(42), 20, "Hs2", groups);
	System.out.println("Serialized: " + Serializer.getGson().toJson(event));
    }

    @Test
    @Ignore
    public void testDeserializationOld() {
	CourseEvent event = Serializer
		.getGson()
		.fromJson(
			"{\"type\":\"Vorlesungen\",\"date\":{\"millis\":42000},\"duration\":30,\"location\":\"Sr K 2026\",\"groups\":[488]}",
			CourseEvent.class);

	String jsonList = "[{\"type\":\"Vorlesungen\",\"date\":{\"millis\":42000},\"duration\":30,\"location\":\"Sr K 2026\",\"groups\":[488]},{\"type\":\"Vorlesungen\",\"date\":{\"millis\":42000},\"duration\":10,\"location\":\"Sr K 2032\",\"groups\":[]},{\"type\":\"Vorlesungen\",\"date\":{\"millis\":420000},\"duration\":90,\"location\":\"Sr K 2026\",\"groups\":[433,323,12123123,88]}]";
	Type collectionType = new TypeToken<Collection<CourseEvent>>() {
	}.getType();
	List<CourseEvent> events = Serializer.getGson().fromJson(jsonList,
		collectionType);
    }

    @Test
    public void testDateParsing() throws ParseException {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	System.out.println(sdf.parse("2013-06-12 09:00:00"));
    }

    @Test
    public void testDeserialization() {
	CourseEvent event = Serializer
		.getGson()
		.fromJson(
			"{\"type\":\"Vorlesungen\",\"date\":\"2013-06-12 09:00:00\",\"duration\":\"01:30:00\",\"location\":\"R-Hs\",\"groups\":[648]}",
			CourseEvent.class);

	String jsonList = "[{\"type\":\"Vorlesungen\",\"date\":\"2013-05-24 11:00:00\",\"duration\":\"01:30:00\",\"location\":\"HU-Hs\",\"groups\":[352,387,456,457,458,581]},{\"type\":\"Vorlesungen\",\"date\":\"2013-04-26 11:00:00\",\"duration\":\"01:30:00\",\"location\":\"HU-Hs\",\"groups\":[352,387,456,457,458,581]},{\"type\":\"Vorlesungen\",\"date\":\"2013-05-10 11:00:00\",\"duration\":\"01:30:00\",\"location\":\"HU-Hs\",\"groups\":[352,387,456,457,458,581]},{\"type\":\"Vorlesungen\",\"date\":\"2013-07-12 11:00:00\",\"duration\":\"01:30:00\",\"location\":\"HU-Hs\",\"groups\":[352,387,456,457,458,581]},{\"type\":\"Vorlesungen\",\"date\":\"2013-04-05 11:00:00\",\"duration\":\"01:30:00\",\"location\":\"HU-Hs\",\"groups\":[352,387,456,457,458,581]},{\"type\":\"Vorlesungen\",\"date\":\"2013-06-21 11:00:00\",\"duration\":\"01:30:00\",\"location\":\"HU-Hs\",\"groups\":[352,387,456,457,458,581]},{\"type\":\"Vorlesungen\",\"date\":\"2013-05-03 11:00:00\",\"duration\":\"01:30:00\",\"location\":\"HU-Hs\",\"groups\":[352,387,456,457,458,581]},{\"type\":\"Vorlesungen\",\"date\":\"2013-07-05 11:00:00\",\"duration\":\"01:30:00\",\"location\":\"HU-Hs\",\"groups\":[352,387,456,457,458,581]},{\"type\":\"Vorlesungen\",\"date\":\"2013-04-12 11:00:00\",\"duration\":\"01:30:00\",\"location\":\"HU-Hs\",\"groups\":[352,387,456,457,458,581]},{\"type\":\"Vorlesungen\",\"date\":\"2013-05-17 11:00:00\",\"duration\":\"01:30:00\",\"location\":\"HU-Hs\",\"groups\":[352,387,456,457,458,581]},{\"type\":\"Vorlesungen\",\"date\":\"2013-06-14 11:00:00\",\"duration\":\"01:30:00\",\"location\":\"HU-Hs\",\"groups\":[352,387,456,457,458,581]},{\"type\":\"Vorlesungen\",\"date\":\"2013-04-19 11:00:00\",\"duration\":\"01:30:00\",\"location\":\"HU-Hs\",\"groups\":[352,387,456,457,458,581]},{\"type\":\"Vorlesungen\",\"date\":\"2013-06-07 11:00:00\",\"duration\":\"01:30:00\",\"location\":\"HU-Hs\",\"groups\":[352,387,456,457,458,581]},{\"type\":\"Vorlesungen\",\"date\":\"2013-06-28 11:00:00\",\"duration\":\"01:30:00\",\"location\":\"HU-Hs\",\"groups\":[352,387,456,457,458,581]},{\"type\":\"Vorlesungen\",\"date\":\"2013-05-31 11:00:00\",\"duration\":\"01:30:00\",\"location\":\"HU-Hs\",\"groups\":[352,387,456,457,458,581]}]";
	Type collectionType = new TypeToken<Collection<CourseEvent>>() {
	}.getType();
	List<CourseEvent> events = Serializer.getGson().fromJson(jsonList,
		collectionType);
    }

    /**
     * Test of getSnapshot method, of class TUIlmenauEventImportService.
     */
    @Test
    public void testGetSnapshot() throws Exception {
	TUIlmenauEventImportService service = new TUIlmenauEventImportService(
		new FileInputStream("eventImport.xml"));
	EventImportContainer snapshot = service.getSnapshot();
	System.out.println("Imported all events.");
    }
}