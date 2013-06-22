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
package unicopa.copa.server.database.data.db;

/**
 * This is a Helperclass to simplefy the work with the singleEventUpdate-table
 * in the database
 * 
 * @author Nintaro
 * 
 */
public class DBSingleEventUpdate {

    private int updatedSingleEvent;
    private int oldSingleEventID;
    private long updateDate;
    private String creatorName;

    /**
     * 
     * @param updatedSingleEvent
     * @param oldSingleEventID
     * @param updateDate
     * @param creatorName
     * @param comment
     */
    public DBSingleEventUpdate(int updatedSingleEvent, int oldSingleEventID,
	    long updateDate, String creatorName, String comment) {
	this.updatedSingleEvent = updatedSingleEvent;
	this.oldSingleEventID = oldSingleEventID;
	this.updateDate = updateDate;
	this.creatorName = creatorName;
	this.comment = comment;
    }

    private String comment;

    /**
     * The default constructor
     */
    public DBSingleEventUpdate() {

    }

    /**
     * Get the updated SingleEvent or null if the SingleEvent was cancelled.
     * Compare attributes to the old object to see changes.
     * 
     * @return the new SingleEvent object, or null if the fixed date was
     *         cancelled and there is no new SingelEvent
     */
    public int getUpdatedSingleEvent() {
	return updatedSingleEvent;
    }

    /**
     * Get the ID of the SingleEvent that was updated and replaced by the new
     * one.
     * 
     * @return the ID of the SingleEvent that was updated or 0 if this update
     *         indicates a creation of a new SingleEvent
     */
    public int getOldSingleEventID() {
	return oldSingleEventID;
    }

    /**
     * Get the date of when the update was submitted.
     * 
     * @return
     */
    public long getUpdateDate() {
	return updateDate;
    }

    public void setUpdateDate(long updateDate) {
	this.updateDate = updateDate;
    }

    /**
     * Get the name of the person who created the update.
     * 
     * @return
     */
    public String getCreatorName() {
	return creatorName;
    }

    /**
     * Get the comment the creator of the update added to give further
     * information.
     * 
     * @return
     */
    public String getComment() {
	return comment;
    }
}