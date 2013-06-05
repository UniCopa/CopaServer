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
 * This NotificationService informs users about updates by sending emails.
 * 
 * @author Felix Wiemuth
 */
public class EmailNotificationService extends NotificationService {

    public EmailNotificationService(DatabaseService dbservice) {
	super(dbservice);
    }

    /**
     * Notify all users that have subscribed to get the specified
     * SingleEventUpdate by sending an email to their registered email address.
     * The content of the email will be in the language the user specified in
     * his settings (if available).
     * 
     * @param update
     *            the SingleEventUpdate to inform about
     */
    @Override
    public void notifyClients(SingleEventUpdate update) {
	throw new UnsupportedOperationException("Not supported yet.");
	// TODO search for clients who want to be notified about this update
	// use EmailService to send emails
	// TODO add required methods to DatabaseService
    }

}
