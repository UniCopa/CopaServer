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
 * This is a Helperclass to simplefy the getUsersAppointedUsers Methode
 * 
 * @author Nintaro
 * 
 */
public class DBUserData {
    private String firstName;
    private String familyName;
    private String email;
    private int kindOfPrivilege;

    public String getFirstname() {
	return firstName;
    }

    public void setfirstName(String firstname) {
	this.firstName = firstname;
    }

    public String getFamilyname() {
	return familyName;
    }

    public void setfamilyName(String familyname) {
	this.familyName = familyname;
    }

    public String getEmail() {
	return email;
    }

    public void setEmail(String email) {
	this.email = email;
    }

    public int getKindOfPrivilege() {
	return kindOfPrivilege;
    }

    public void setKindOfPrivilege(int kindOfPrivilege) {
	this.kindOfPrivilege = kindOfPrivilege;
    }

}
