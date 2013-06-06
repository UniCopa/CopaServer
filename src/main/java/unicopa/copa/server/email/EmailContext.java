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
package unicopa.copa.server.email;

import javax.mail.internet.InternetAddress;

/**
 * \brief The Context which is passed as a list to the E-Mail service.
 * 
 * This class contains the E-Mail address and the ID of the Text Template for
 * the E-Mail body and subject.
 * 
 * @see unicopa.copa.server.email.EmailService
 * @see unicopa.copa.server.email.EmailService.notifySingleEventUpdate
 * @see unicopa.copa.server.notification.EmailNotificationService
 * 
 * @author Philip Wendland
 */
public class EmailContext {
    private InternetAddress emailAddress;
    private String textID;

    public EmailContext(InternetAddress addr, String textID) {
	this.emailAddress = addr;
	this.textID = textID;
    }

    public InternetAddress getEmailAddress() {
	return emailAddress;
    }

    public String getTextID() {
	return textID;
    }

}
