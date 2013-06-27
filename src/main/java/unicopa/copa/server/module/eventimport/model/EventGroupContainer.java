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
package unicopa.copa.server.module.eventimport.model;

import java.util.List;
import java.util.Set;
import unicopa.copa.base.event.CategoryNode;
import unicopa.copa.base.event.EventGroup;

/**
 * Encapsulates an EventGroup with related data.
 * 
 * @author Felix Wiemuth
 */
public class EventGroupContainer {
    private EventGroup eventGroup;
    private List<EventContainer> events;
    private Set<CategoryNode> categories;

    public EventGroupContainer(EventGroup eventGroup,
	    List<EventContainer> events, Set<CategoryNode> categories) {
	this.eventGroup = eventGroup;
	this.events = events;
	this.categories = categories;
    }

    public EventGroup getEventGroup() {
	return eventGroup;
    }

    public List<EventContainer> getEvents() {
	return events;
    }

    public Set<CategoryNode> getCategories() {
	return categories;
    }
}
