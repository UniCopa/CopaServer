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
import unicopa.copa.base.event.Event;
import unicopa.copa.base.event.EventGroup;
import unicopa.copa.base.event.SingleEvent;
import unicopa.copa.server.GeneralUserPermission;
import unicopa.copa.server.database.ObjectNotFoundException;
import unicopa.copa.server.module.eventimport.serialization.Serializer;
import unicopa.copa.server.module.eventimport.EventImportService;
import unicopa.copa.server.module.eventimport.LimitedDatabaseAccess;
import unicopa.copa.server.module.eventimport.model.EventContainer;
import unicopa.copa.server.module.eventimport.model.EventGroupContainer;
import unicopa.copa.server.module.eventimport.model.EventImportContainer;
import unicopa.copa.server.util.SimpleHttpsClient;

/**
 * TODO: own repository, parent CopaServer TODO: use in the following way: get
 * EventImportContainer from here, let database insert data and set the IDs!
 * container also contains map from event to {role,user} => hand container over
 * to permission import system
 * 
 * @author Felix Wiemuth
 */
public class TUIlmenauEventImportService implements EventImportService {
    private SimpleHttpsClient client;
    private Properties settings = new Properties();
    private static final Logger LOG = Logger
	    .getLogger(TUIlmenauEventImportService.class.getName());

    // URIs or Strings to build URIs for all requests
    private static URI REQ_GET_COURSES;
    private static String REQ_GET_COURSE_EVENTS;
    private static String REQ_GET_GROUP;
    private final LimitedDatabaseAccess access;

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
    public TUIlmenauEventImportService(InputStream settingsStream,
	    LimitedDatabaseAccess access) throws IOException {
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
	this.access = access;
    }

    @Override
    public EventImportContainer getSnapshot() throws URISyntaxException {
	// Start the client for this import session
	client.start();

	// Collect all EventGroups here to be finally returned inside an
	// EventImportContainer
	List<EventGroupContainer> eventGroupContainers = new LinkedList<>();

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

	    List<Integer> owners = new LinkedList<>(); // ID of users to become
						       // owners of the events
						       // from this group

	    for (String person : course.getLecturers()) {
		try {
		    owners.add(access.matchName(person,
			    GeneralUserPermission.POSSIBLE_OWNER));
		} catch (ObjectNotFoundException ex) {
		    // Did not find a match //TODO log warning
		}
	    }

	    // Collect events for this course (EventGroup): an event is
	    // identified by the
	    // combination of groups
	    Map<Set<Group>, EventContainer> eventsMap = new HashMap();

	    // TODO note that categories at eventgroup are the ones unioned from
	    // the events

	    // Distribute "dates" as SingleEvents to Events
	    for (CourseEvent courseEvent : courseEvents) {
		SingleEvent singleEvent = new SingleEvent(0, 0,
			courseEvent.getLocation(), courseEvent.getDate(), "",
			courseEvent.getDuration());
		Set<Group> groups = new HashSet<>();
		for (Integer g : courseEvent.getGroups()) {
		    groups.add(getGroup(g));
		}
		EventContainer eventContainer = eventsMap.get(groups); // check
								       // if
								       // event
								       // already
								       // exists
		if (eventContainer == null) { // create new Event
		    StringBuilder eventName = new StringBuilder();
		    eventName.append(courseEvent.getType()).append(" - ");
		    for (Group group : groups) {
			eventName.append(group.getCompactForm()).append(",");
		    }
		    eventName.deleteCharAt(eventName.length() - 1);

		    // Construct the Event
		    Event event = new Event(0, 0, eventName.toString(), null); // IDs
									       // must
									       // be
									       // set
									       // by
									       // the
									       // database

		    // Collect the categories for this event
		    List<CategoryNode> categories = new LinkedList<>();
		    for (Group group : groups) {
			categories.add(categoryCache.get(group));
		    }
		    eventContainer = new EventContainer(event,
			    new LinkedList<SingleEvent>(), owners, categories);
		    eventsMap.put(groups, eventContainer);
		}
		eventContainer.getSingleEvents().add(singleEvent);
	    }

	    // TODO sort to events after generating SingleEvents?

	    // TODO construct these at the end when all information is there...

	    // The events for this EventGroup
	    List<EventContainer> eventContainers = new LinkedList<>(
		    eventsMap.values());

	    // The categories for the EventGroup
	    Set<CategoryNode> categories = new HashSet<>();
	    for (EventContainer eventContainer : eventContainers) {
		categories.addAll(eventContainer.getCategories());
	    }

	    // Construct the EventGroup with the collected data
	    EventGroup eventGroup = new EventGroup(0, course.getName(), "",
		    null); // IDs for EventGroup and categories must be set by
			   // database

	    // Construct the EventGroupContainer with the collected data
	    EventGroupContainer eventGroupConainer = new EventGroupContainer(
		    eventGroup, eventContainers, categories);
	    // Add the container to the final list which contains everything
	    // collected in this method
	    eventGroupContainers.add(eventGroupConainer);
	}
	try {
	    client.stop();
	} catch (Exception ex) {
	    Logger.getLogger(TUIlmenauEventImportService.class.getName()).log(
		    Level.WARNING, "Could not stop HTTPS client.", ex);
	}
	return new EventImportContainer(categoryTree, eventGroupContainers);
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
