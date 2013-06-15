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
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface PersonMapper {

    public String getEmailAddress(@Param("userID") int userID);

    public List<Integer> getSubscribedUserIDs(@Param("eventID") int eventID);

    public Integer getUserID(@Param("userName") String userName);

    public Map<String, Integer> isAdmin(@Param("userID") int userID);

    public Integer getPrivilege(@Param("userID") int userID,
	    @Param("eventID") int eventID);

    public String getUserName(@Param("userID") int userID);

    public void insertPerson(@Param("userName") String userName,
	    @Param("firstName") String firstName,
	    @Param("familyName") String familyName,
	    @Param("email") String email, @Param("titel") String titel,
	    @Param("language") String language,
	    @Param("eMailNotification") boolean eMailNotification);

    public Integer userNameExsists(@Param("userName") String userName);

    public Integer userIDExsists(@Param("userID") int userID);

    public Integer emailExsists(@Param("email") String email);
}
