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
package unicopa.copa.server.module.eventimport;

import unicopa.copa.server.GeneralUserPermission;
import unicopa.copa.server.database.ObjectNotFoundException;

/**
 * 
 * @author Felix Wiemuth
 */
public interface LimitedDatabaseAccess {
    /**
     * Try to find a user in the database whose name data does a good match to
     * the given name.
     * 
     * @param personName
     * @return the ID of the user found
     */
    public int matchName(String personName,
	    GeneralUserPermission minimumRequiredPermission)
	    throws ObjectNotFoundException;

}
