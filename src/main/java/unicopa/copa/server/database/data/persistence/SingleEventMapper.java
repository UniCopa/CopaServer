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
package unicopa.copa.server.database.data.persistence;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import unicopa.copa.base.event.SingleEvent;

public interface SingleEventMapper {

    public SingleEvent getSingleEvent(@Param("singleEventID") int singleEventID);

    public List<SingleEvent> getCurrentSingleEvent(
	    @Param("eventID") int eventID, @Param("since") long since);

    public void insertSingleEvent(@Param("singleEvent") SingleEvent se,
	    @Param("sEventDate") long sEventDate,
	    @Param("isRecent") boolean isRecent);

    public void updateSingleEventStatus(@Param("isRecent") boolean isRecent,
	    @Param("singleEventID") int singleEventID);

    public boolean getSingleEventStatus(
	    @Param("singleEventID") int singleEventID);

    public int singleEventExists(@Param("singleEventID") int singleEventID);

    public void deleteSingleEventUpdates();

    public void deleteSingleEvent();
}
