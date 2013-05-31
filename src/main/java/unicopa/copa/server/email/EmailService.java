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

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

/**
 * \brief With the E-Mail Service it is possible to send E-Mails i.e. for
 * notifications of updates.
 * 
 * In order to be able to send E-Mails you have to provide login data for an
 * (sending) SMTP-Server in the config file.
 * 
 * @author Philip Wendland
 */
public class EmailService {

    private static Properties smtpProps = null;
    private static Authenticator auth = null;

    /**
     * \brief Initializes the EmailService.
     * 
     * This static method reads the desired configuration from the configuration
     * file and initializes the EmailService.
     */
    public static void initEmail() {

	// TODO read from config

	// the properties of the SMTP-Server
	smtpProps = new Properties();
	smtpProps.put("mail.smtp.host", "");
	smtpProps.put("mail.smtp.auth", "");
	smtpProps.put("mail.smtp.port", "");

	// the username and password to log in to the SMTP-Server
	final String username; // Uni-RZ Login
	username = "";
	final String password; // Uni-RZ PW
	password = "";

	auth = new javax.mail.Authenticator() {
	    @Override
	    protected PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(username, password);
	    }
	};
    }

    /**
     * \brief Gets the E-Mail-Addresses of the users if E-Mail Notification is
     * wanted.
     * 
     * This method reads the E-Mail-Address and checks if the particular user
     * wishes to be notified by E-Mail for every UserID. If the user wants to be
     * notified, the E-Address is put into the List of Addresses which is then
     * returned.
     * 
     * @param userIDs
     *            a List of UserIDs
     * @return the addresses the E-Mail should be sent to in a List.
     */
    private static List<InternetAddress> getRecipients(List userIDs) {
	List<InternetAddress> addrs;
	addrs = new ArrayList<>();

	// TODO get the E-Mail-addresses from the DB and check if user wants to
	// be notified

	return addrs;
    }

    /**
     * \brief Sends a plain-text E-Mail.
     * 
     * Note: the EmailService must already be initialized using initEmail(). *
     * 
     * @param recipient
     *            the reciptient the E-Mail should be sent to, i.e. x@y.com
     * @param subject
     *            the subject of the E-Mail
     * @param message
     *            the plain-text body of the E-Mail
     * @param from
     *            the desired name of the sender
     * @throws MessagingException
     */
    public static void postMail(String recipient, String subject,
	    String message, String from) throws MessagingException {
	// initiate session with the (sending) smtp server
	Session session = Session.getInstance(smtpProps, auth);

	// create and configure message
	Message msg = new MimeMessage(session);
	InternetAddress addressFrom = new InternetAddress(from);
	msg.setFrom(addressFrom);
	InternetAddress addressTo = new InternetAddress(recipient);
	msg.setRecipient(Message.RecipientType.TO, addressTo);
	msg.setSubject(subject);
	msg.setContent(message, "text/plain");

	// send the E-Mail
	Transport.send(msg);
    }

}
