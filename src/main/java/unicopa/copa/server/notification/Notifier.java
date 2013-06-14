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

import java.util.LinkedList;
import java.util.List;
import unicopa.copa.base.event.SingleEventUpdate;
import unicopa.copa.server.notification.NotificationService.NotificationEvent;

/**
 * This class holds all notification services available to the system. If
 * notifications should be sent, it is enought to pass it to the notifier and it
 * delegates the task to the different notification services.
 * 
 * @author Felix Wiemuth
 */
public class Notifier {
    private List<NotificationService> services = new LinkedList<>();

    /**
     * For each notification service, use their 'notifyClients' interface with
     * this update to inform clients about this update.
     * 
     * @param update
     *            the SingleEventUpdate to inform about
     */
    public void notifyClients(SingleEventUpdate update) {
	for (NotificationService service : services) {
	    service.notifyClients(update);
	}
    }

    /**
     * For each notification service, use their 'notifyClients' interface with
     * this notification event to inform clients about the event.
     * 
     * @param event
     *            the notification event to inform about
     */
    public void notifyClient(NotificationEvent event, int userID) {
	for (NotificationService service : services) {
	    service.notifyClient(event, userID);
	}
    }

    /**
     * Add a new notification service to the notifier.
     * 
     * @param service
     */
    public void addNotificationService(NotificationService service) {
	services.add(service);
    }
}
