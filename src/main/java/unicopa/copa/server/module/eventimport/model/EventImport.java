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
import unicopa.copa.base.event.CategoryNode;
import unicopa.copa.base.event.SingleEvent;

/**
 * Holds import data to build an Event.
 * 
 * @author Felix Wiemuth
 */
public class EventImport {
    private String eventName;
    private List<SingleEvent> singleEvents;
    private List<String> possibleOwners;
    private List<CategoryNode> categories;

    public EventImport(String eventName, List<SingleEvent> singleEvents,
	    List<String> possibleOwners, List<CategoryNode> categories) {
	this.eventName = eventName;
	this.singleEvents = singleEvents;
	this.possibleOwners = possibleOwners;
	this.categories = categories;
    }

    public String getEventName() {
	return eventName;
    }

    /**
     * Get the SingleEvents belonging to this event. Note: SingleEventID and
     * EventID are not valid.
     * 
     * @return
     */
    public List<SingleEvent> getSingleEvents() {
	return singleEvents;
    }

    public List<String> getPossibleOwners() {
	return possibleOwners;
    }

    public List<CategoryNode> getCategories() {
	return categories;
    }
}
