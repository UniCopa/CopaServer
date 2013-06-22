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
package unicopa.copa.server.email;

import java.util.Date;

/**
 * The update information that is passed to the E-Mail service.
 * 
 * @author Philip Wendland
 */
public class UpdateInformation {
    private String eventGroupName;
    private String eventName;
    private Date updateDate;
    private String creatorName;
    private String comment;
    private String location;
    private Date date;
    private String supervisor;

    /**
     * @param eventGroupName
     * @param eventName
     * @param updateDate
     * @param creatorName
     * @param comment
     * @param location
     * @param date
     * @param supervisor
     */
    public UpdateInformation(String eventGroupName, String eventName,
	    Date updateDate, String creatorName, String comment,
	    String location, Date date, String supervisor) {
	this.eventGroupName = eventGroupName;
	this.eventName = eventName;
	this.updateDate = updateDate;
	this.creatorName = creatorName;
	this.comment = comment;
	this.location = location;
	this.date = date;
	this.supervisor = supervisor;
    }

    /**
     * @return the eventGroupName
     */
    public String getEventGroupName() {
	return eventGroupName;
    }

    /**
     * @return the eventName
     */
    public String getEventName() {
	return eventName;
    }

    /**
     * @return the updateDate
     */
    public Date getUpdateDate() {
	return updateDate;
    }

    /**
     * @return the creatorName
     */
    public String getCreatorName() {
	return creatorName;
    }

    /**
     * @return the comment
     */
    public String getComment() {
	return comment;
    }

    /**
     * @return the location
     */
    public String getLocation() {
	return location;
    }

    /**
     * @return the date
     */
    public Date getDate() {
	return date;
    }

    /**
     * @return the supervisor
     */
    public String getSupervisor() {
	return supervisor;
    }

}
