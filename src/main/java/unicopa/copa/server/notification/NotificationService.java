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
package unicopa.copa.server.notification;

import unicopa.copa.base.event.SingleEventUpdate;
import unicopa.copa.server.database.DatabaseService;

/**
 * A service which notifies clients about an update on a SingleEvent.
 * 
 * @author Felix Wiemuth
 */
public abstract class NotificationService {
    private DatabaseService dbservice;

    /**
     * Create a new instance of a notification service.
     * 
     * @param dbservice
     *            the database service the notification service should use to
     *            obtain additional information
     */
    public NotificationService(DatabaseService dbservice) {
	this.dbservice = dbservice;
    }

    /**
     * Obtain the database service where to get information from.
     * 
     * @return
     */
    protected DatabaseService dbservice() {
	return dbservice;
    }

    /**
     * Notify all clients that registered for this kind of notification and for
     * receiving this update.
     * 
     * @param update
     *            the SingleEventUpdate to inform about
     */
    public abstract void notifyClients(SingleEventUpdate update);

}