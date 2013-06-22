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

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import unicopa.copa.base.event.SingleEventUpdate;
import unicopa.copa.server.CopaSystemContext;

/**
 * A service which notifies clients about an update on a SingleEvent.
 * 
 * @author Felix Wiemuth
 */
public abstract class NotificationService {

    /**
     * Events clients can be notified about. This are only events where no
     * special data has to be sent.
     */
    public enum NotificationEvent {
	USER_SETTINGS_CHANGED, // Inform the user that his settings have changed
	SERVER_STATUS_NOTE, // Inform the user that there is a new server status
			    // note
	USER_EVENT_PERMISSIONS_CHANGED; // Inform the user that his permissons
					// for events have changed
    }

    private CopaSystemContext context;
    private static final Logger LOG = Logger
	    .getLogger("unicopa.copa.server.notification");

    /**
     * Create a new instance of a notification service.
     * 
     * @param dbservice
     *            the database service the notification service should use to
     *            obtain additional information
     */
    public NotificationService(CopaSystemContext context) {
	this.context = context;
	try {
	    LOG.addHandler(new FileHandler(context.getLogDirectory()
		    .getCanonicalPath() + "/copa-notificationService.log",
		    10000000, 1, true));
	} catch (IOException ex) {
	    LOG.log(Level.SEVERE, null, ex);
	}
    }

    /**
     * Obtain the system context.
     * 
     * @see unicopa.copa.server.CopaSystemContext
     * @return
     */
    protected CopaSystemContext getContext() {
	return context;
    }

    /**
     * Notify all clients that registered for this kind of notification and for
     * receiving this update.
     * 
     * @param update
     *            the SingleEventUpdate to inform about
     */
    public abstract void notifyClients(SingleEventUpdate update);

    /**
     * Notify a specific client.
     * 
     * @param event
     *            the notification event to inform about
     * @param userID
     *            the recipient of the notification event
     */
    public abstract void notifyClient(NotificationEvent event, int userID);
}
