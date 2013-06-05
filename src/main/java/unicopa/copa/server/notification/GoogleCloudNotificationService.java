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
 * This NotificationService informs Android devices about updates by sending
 * Google Cloud Messages.
 * 
 * @author Felix Wiemuth
 */
public class GoogleCloudNotificationService extends NotificationService {

    public GoogleCloudNotificationService(DatabaseService dbservice) {
	super(dbservice);
    }

    /**
     * Notify all users on their Android devices where the user has subscribed
     * to get the sepcified SingleEventUpdate and has added the GCM key for the
     * device to his UserSettings.
     * 
     * @param update
     *            the SingleEventUpdate to inform about
     */
    @Override
    public void notifyClients(SingleEventUpdate update) {
	throw new UnsupportedOperationException("Not supported yet.");
	// TODO send GCM messages to the registered GCM keys, regarding the
	// communication interface specifications
	// TODO add required methods to DatabaseService
    }
}
