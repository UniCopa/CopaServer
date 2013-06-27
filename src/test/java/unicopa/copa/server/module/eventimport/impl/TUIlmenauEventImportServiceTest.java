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

import com.google.gson.reflect.TypeToken;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import unicopa.copa.server.module.eventimport.model.EventImportContainer;
import unicopa.copa.server.module.eventimport.serialization.Serializer;

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
		"eventImport.xml"), null);
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
    public void testDeserialization() {
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

    /**
     * Test of getSnapshot method, of class TUIlmenauEventImportService.
     */
    @Test
    public void testGetSnapshot() throws Exception {
	TUIlmenauEventImportService service = new TUIlmenauEventImportService(
		new FileInputStream("eventImport.xml"), null);
	EventImportContainer snapshot = service.getSnapshot();
    }
}