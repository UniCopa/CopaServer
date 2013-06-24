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

import unicopa.copa.base.event.EventGroup;

public interface EventGroupMapper {

    public List<EventGroup> getEventGroups(
	    @Param("categoryNodeID") List<Integer> categoryNodeID,
	    @Param("searchTerm") String searchTerm);

    public EventGroup getEventGroup(@Param("eventGroupID") int eventGroupID);

    public void insertEventGroup(@Param("eventGroup") EventGroup eventGroup);

    public void insertEventGroupCategory(
	    @Param("eventGroupID") int eventGroupID,
	    @Param("categoryList") List<Integer> categoryList);

    public Integer eventGroupExists(@Param("eventGroupID") int eventGroupID);

    public void deleteEventGroup();

    public void deleteEventGroupHasCategories();
}
