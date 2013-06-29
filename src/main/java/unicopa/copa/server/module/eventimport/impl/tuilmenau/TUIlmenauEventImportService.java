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
package unicopa.copa.server.module.eventimport.impl.tuilmenau;

import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import unicopa.copa.base.event.CategoryNode;
import unicopa.copa.base.event.CategoryNodeImpl;
import unicopa.copa.base.event.SingleEvent;
import unicopa.copa.server.module.eventimport.impl.tuilmenau.serialization.Serializer;
import unicopa.copa.server.module.eventimport.EventImportService;
import unicopa.copa.server.module.eventimport.model.EventImport;
import unicopa.copa.server.module.eventimport.model.EventGroupImport;
import unicopa.copa.server.module.eventimport.model.EventImportContainer;
import unicopa.copa.server.util.SimpleHttpsClient;

/**
 * The EventImportService for the TU Ilmenau.
 * 
 * @author Felix Wiemuth
 */
public class TUIlmenauEventImportService implements EventImportService {
    // TODO: own repository, parent CopaServer
    private SimpleHttpsClient client;
    private Properties settings = new Properties();
    private static final Logger LOG = Logger.getLogger(EventImportService.class
	    .getName());

    // URIs or Strings to build URIs for all requests
    private static URI REQ_GET_COURSES;
    private static String REQ_GET_COURSE_EVENTS;
    private static String REQ_GET_GROUP;

    private Map<Integer, Group> groupCache = new HashMap<>();
    private CategoryNodeImpl categoryTree = new CategoryNodeImpl(0, ""); // create
									 // the
									 // category
									 // tree
									 // (by
									 // specifiying
									 // root)
    private Map<Group, CategoryNode> categoryCache = new HashMap<>();

    /**
     * Create the EventImportService.
     * 
     * @param settingsStream
     * @param access
     * @throws IOException
     */
    public TUIlmenauEventImportService(InputStream settingsStream)
	    throws IOException {
	LOG.info("Initializing TUIlmenauEventImportService");
	settings.loadFromXML(settingsStream);
	REQ_GET_COURSE_EVENTS = settings.getProperty("uri_getCourseEvents");
	REQ_GET_GROUP = settings.getProperty("uri_getGroup");
	try {
	    REQ_GET_COURSES = new URI(settings.getProperty("uri_getCourses"));
	    client = new SimpleHttpsClient(new URI(
		    settings.getProperty("auth_uri")),
		    settings.getProperty("realm"));
	} catch (URISyntaxException ex) {
	    LOG.log(Level.SEVERE, null, ex);
	}
	client.authenticate(settings.getProperty("user"),
		settings.getProperty("password"));
	LOG.info("Successfully initialized TUIlmenauEventImportService");
    }

    @Override
    public EventImportContainer getSnapshot() throws URISyntaxException {
	// Start the client for this import session
	client.start();

	// Collect all EventGroups here to be finally returned inside an
	// EventImportContainer
	List<EventGroupImport> eventGroupImports = new LinkedList<>();

	// Get a list of all courses (EventGroups) from the server
	String jsonCourses = client.GET(REQ_GET_COURSES);
	List<Course> courses = Serializer.getGson().fromJson(jsonCourses,
		new TypeToken<Collection<Course>>() {
		}.getType());

	// Go through all the courses. One course will become one EventGroup,
	// its "dates" are distributed to events, depending on the type and the
	// group(!).
	for (Course course : courses) {
	    // Get a list of all dates for the current course (EventGroup) from
	    // the server
	    String jsonCourseEvents = client.GET(new URI(String.format(
		    REQ_GET_COURSE_EVENTS, course.getId())));
	    List<CourseEvent> courseEvents = Serializer.getGson().fromJson(
		    jsonCourseEvents, new TypeToken<Collection<CourseEvent>>() {
		    }.getType());

	    List<String> possibleOwners = new LinkedList<>();

	    for (String person : course.getLecturers()) {
		possibleOwners.add(person);
	    }

	    // Collect events for this course (EventGroup): an event is
	    // identified by the
	    // combination of groups
	    Map<Set<Group>, EventImport> eventsMap = new HashMap();

	    // Distribute "dates" as SingleEvents to Events
	    for (CourseEvent courseEvent : courseEvents) {
		SingleEvent singleEvent = new SingleEvent(0, 0,
			courseEvent.getLocation(), courseEvent.getDate(), "",
			courseEvent.getDuration());
		Set<Group> groups = new HashSet<>();
		for (Integer g : courseEvent.getGroups()) {
		    groups.add(getGroup(g));
		}
		EventImport eventImport = eventsMap.get(groups); // check
								 // if
								 // event
								 // already
								 // exists
		if (eventImport == null) { // create new Event
		    StringBuilder eventName = new StringBuilder();
		    eventName.append(courseEvent.getType()).append(" - ");
		    for (Group group : groups) {
			eventName.append(group.getCompactForm()).append(",");
		    }
		    eventName.deleteCharAt(eventName.length() - 1);

		    // Construct the Event

		    // Collect the categories for this event
		    List<CategoryNode> categories = new LinkedList<>();
		    for (Group group : groups) {
			categories.add(categoryCache.get(group));
		    }
		    eventImport = new EventImport(eventName.toString(),
			    new LinkedList<SingleEvent>(), possibleOwners,
			    categories);
		    eventsMap.put(groups, eventImport);
		}
		eventImport.getSingleEvents().add(singleEvent);
	    }

	    // The events for this EventGroup
	    List<EventImport> eventImports = new LinkedList<>(
		    eventsMap.values());

	    // The categories for the EventGroup
	    Set<CategoryNode> categories = new HashSet<>();
	    for (EventImport eventImport : eventImports) {
		categories.addAll(eventImport.getCategories());
	    }

	    // Construct the EventGroupImport with the collected data
	    EventGroupImport eventGroupImport = new EventGroupImport(
		    course.getName(), "", eventImports, categories);
	    // Add the Import to the final list which contains everything
	    // collected in this method
	    if (!eventGroupImport.getEvents().isEmpty()) {
		eventGroupImports.add(eventGroupImport);
	    } else {
		LOG.warning("Ignored course with no events: "
			+ eventGroupImport.getEventGroupName());
	    }
	}
	try {
	    client.stop();
	} catch (Exception ex) {
	    Logger.getLogger(TUIlmenauEventImportService.class.getName()).log(
		    Level.WARNING, "Could not stop HTTPS client.", ex);
	}
	return new EventImportContainer(categoryTree, eventGroupImports);
    }

    /**
     * Get the name of a group using the group cache. The group is also
     * automatically inserted into the category tree. The client must be
     * running.
     * 
     * @param id
     * @return
     */
    private Group getGroup(int id) throws URISyntaxException {
	Group group = groupCache.get(id);
	if (group == null) {
	    String jsonGroup = client.GET(new URI(String.format(REQ_GET_GROUP,
		    id)));
	    group = Serializer.getGson().fromJson(jsonGroup, Group.class);
	    groupCache.put(id, group); // add to cache
	    CategoryNode inserted = categoryTree.insertNode(group.toList()); // merge
									     // into
									     // category
									     // tree
	    categoryCache.put(group, inserted);
	}
	return group;
    }
}
