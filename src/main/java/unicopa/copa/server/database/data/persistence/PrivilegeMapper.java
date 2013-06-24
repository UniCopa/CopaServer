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

import unicopa.copa.server.database.data.db.DBUserData;

public interface PrivilegeMapper {

    public List<String> getPrivileged(@Param("eventID") int eventID,
	    @Param("appointedByUserID") int appointedByUserID,
	    @Param("kindOfPriv") int kindOfPriv);

    public void removePrivilege(@Param("userID") int userID,
	    @Param("eventID") int eventID);

    public void insertPrivilege(@Param("personID") int personID,
	    @Param("eventID") int eventID,
	    @Param("kindOfPrivilege") int kindOfPrivilege,
	    @Param("gavePrivilege") int gavePrivilege,
	    @Param("privDate") long privDate);

    public void insertAdmin(@Param("userID") int userID,
	    @Param("date") long date);

    public void deleteAdmin(@Param("userID") int userID);

    public Integer isAppointedBy(@Param("userID") int userID,
	    @Param("gavePrivilege") int gavePrivilege,
	    @Param("eventID") int eventID,
	    @Param("kindOfPrivilege") int kindOfPrivilege);

    public List<Map<String, Integer>> getUsersPriviligedEvents(
	    @Param("userID") int userID);

    public List<DBUserData> getUsersAppointedUsers(
	    @Param("gavePrivilege") int gavePrivilege);

    public Integer hasPrivFor(@Param("userID") int userID,
	    @Param("eventID") int eventID);

    public void deletePrivilege();
}
