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

/**
 * Holds import data to build an EventGroup.
 * 
 * @author Felix Wiemuth
 */
public class EventGroupImport {
    private String eventGroupName;
    private String eventGroupInfo;
    private List<EventImport> events;
    private Set<CategoryNode> categories;

    public EventGroupImport(String eventGroupName, String eventGroupInfo,
	    List<EventImport> events, Set<CategoryNode> categories) {
	this.eventGroupName = eventGroupName;
	this.eventGroupInfo = eventGroupInfo;
	this.events = events;
	this.categories = categories;
    }

    public String getEventGroupName() {
	return eventGroupName;
    }

    public String getEventGroupInfo() {
	return eventGroupInfo;
    }

    public List<EventImport> getEvents() {
	return events;
    }

    public Set<CategoryNode> getCategories() {
	return categories;
    }
}
