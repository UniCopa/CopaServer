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
package unicopa.copa.server;

import unicopa.copa.server.database.DatabaseService;

/**
 * This class handles the registration of users to the system.
 * 
 * @author Felix Wiemuth
 */
public class Registration {
    private DatabaseService dbservice;

    public Registration(DatabaseService dbservice) {
	this.dbservice = dbservice;
    }

    /**
     * Register a client to the system.
     * 
     * @param username
     *            the username of the user to register
     * @param password
     *            the password of the user to register
     * @return a message to be sent back to the client, indicating success
     *         ("OK") or failure
     */
    public String register(String username, String password) {
	throw new UnsupportedOperationException();
    }

}
