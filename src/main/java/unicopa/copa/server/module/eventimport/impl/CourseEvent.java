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

import java.util.Date;
import java.util.List;

/**
 * 
 * @author Felix Wiemuth
 */
public class CourseEvent {
    private CourseEventType type;
    private Date date;
    private int duration;
    private String location;
    private List<Integer> groups;

    public CourseEvent(CourseEventType type, Date date, int duration,
	    String location, List<Integer> groups) {
	this.type = type;
	this.date = date;
	this.duration = duration;
	this.location = location;
	this.groups = groups;
    }

    public CourseEventType getType() {
	return type;
    }

    public Date getDate() {
	return date;
    }

    public int getDuration() {
	return duration;
    }

    public String getLocation() {
	return location;
    }

    public List<Integer> getGroups() {
	return groups;
    }
}
