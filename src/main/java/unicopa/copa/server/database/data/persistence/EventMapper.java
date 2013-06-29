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

import java.util.List;
import org.apache.ibatis.annotations.Param;

import unicopa.copa.base.event.Event;

public interface EventMapper {

    public List<Event> getEvents(@Param("eventGroupID") int eventGroupID,
	    @Param("categoryNodeID") List<Integer> categoryNodeID);

    public Event getEvent(@Param("eventID") int eventID);

    public Integer eventExists(@Param("eventID") int eventID);

    public void insertEvent(@Param("event") Event event);

    public void insertEventCategorie(@Param("eventID") int eventID,
	    @Param("categoryID") List<Integer> categoryList);

    public void deleteEventHasCategories();

    public void deleteEvent();

    public void deletePossibleOwners();

    public List<String> getPossibleOwners(@Param("eventID") int eventID);

    public void insertPossibleOwners(@Param("eventID") int eventID,
	    @Param("ownerList") List<String> ownerList);

    public List<Integer> getPossibleOwnerMatches(@Param("userID") int userID);
}
