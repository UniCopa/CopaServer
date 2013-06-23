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
import unicopa.copa.base.event.CategoryNodeImpl;
import unicopa.copa.base.event.EventGroup;
import unicopa.copa.base.event.Event;
import unicopa.copa.base.event.SingleEvent;

/**
 * Contains the complete data imported to be integrated into the system.
 * @author Felix Wiemuth
 */
public class EventImportContainer {
    private CategoryNodeImpl categoryTree;
    private List<EventGroup> eventGroups;
    private List<Event> events;
    private List<SingleEvent> singleEvents;
    
}
