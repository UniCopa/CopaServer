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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import unicopa.copa.base.UserSettings;
import unicopa.copa.base.event.SingleEvent;
import unicopa.copa.base.event.SingleEventUpdate;
import unicopa.copa.server.database.DatabaseService;
import unicopa.copa.server.database.ObjectNotFoundException;
import unicopa.copa.server.gcm.GoogleCloudMessagingService;

/**
 * This NotificationService informs Android devices about updates by sending
 * Google Cloud Messages.
 * 
 * @author Felix Wiemuth, Philip Wendland
 */
public class GoogleCloudNotificationService extends NotificationService {
    private GoogleCloudMessagingService gcmService;

    public GoogleCloudNotificationService(DatabaseService dbservice) {
	super(dbservice);
	this.gcmService = GoogleCloudMessagingService.getInstance();
    }

    /**
     * Notify all users on their Android devices where the user has subscribed
     * to get the specified SingleEventUpdate and has added the GCM key for the
     * device to his UserSettings.
     * 
     * @param update
     *            the SingleEventUpdate to inform about
     */
    @Override
    public void notifyClients(SingleEventUpdate update) {
	try {
	    // determine the single event ID
	    // Note: We have to get the Event-ID from the old single event,
	    // because
	    // the new single event might be null
	    int seID = update.getOldSingleEventID();
	    SingleEvent oldSingleEvent = super.dbservice().getSingleEvent(seID);
	    int eventID = oldSingleEvent.getEventID();
	    // determine the users that should be informed about the update
	    List<Integer> subUsers = super.dbservice().getSubscribedUserIDs(
		    eventID);
	    // determine GCM keys of the users
	    Set<String> gcmKeys = new HashSet<>();
	    for (Integer userID : subUsers) {
		UserSettings settings = super.dbservice().getUserSettings(
			userID);
		Set<String> usrKeys = settings.getGCMKeys();
		gcmKeys.addAll(usrKeys);
	    }
	    // send using service
	    this.gcmService.notify(gcmKeys, "SINGLE_EVENT_UPDATE");
	} catch (ObjectNotFoundException ex) {
	    Logger.getLogger(GoogleCloudNotificationService.class.getName())
		    .log(Level.SEVERE, null, ex);
	}
    }

    /**
     * Uses GoogleCloudMessagingService to notify the client.
     * 
     * The message will be sent to every device which is registered.
     * 
     * @param event
     * @param userID
     */
    @Override
    public void notifyClient(NotificationEvent event, int userID) {
	try {
	    UserSettings settings = super.dbservice().getUserSettings(userID);
	    Set<String> usrKeys = settings.getGCMKeys();
	    // send using service
	    this.gcmService.notify(usrKeys, event.toString());
	} catch (ObjectNotFoundException ex) {
	    Logger.getLogger(GoogleCloudNotificationService.class.getName())
		    .log(Level.SEVERE, null, ex);
	}
    }

}
