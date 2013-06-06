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

import java.io.InputStream;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import unicopa.copa.base.event.SingleEventUpdate;

/**
 * \brief With the E-Mail Service it is possible to send E-Mails for
 * notifications of updates.
 * 
 * @author Philip Wendland
 */
public class EmailService {

    private Properties smtpProps;
    private Authenticator auth;
    private Map<String, String> bodies;
    private Map<String, String> subjects;

    /**
     * \brief Create a new EmailService.
     * 
     * @param smtpProps
     *            the environment properties used by the JavaMail API. The path
     *            to the file is "/email/smtp.properties". You should provide
     *            mail.smtp.host - specifies the host address of the SMTP-Server
     *            you wish to use, e.g. smtp.example.com mail.smtp.auth -
     *            specifies, whether authentication is necessary in order to be
     *            able to connect to the SMTP-Server (true/false - true in most
     *            cases) mail.smtp.port - specifies the port of the SMTP-Server
     *            to connect to, e.g. 587 mail.from - specifies the return
     *            E-Mail address of the sent E-Mail, i.e. info@example.com
     *            username - the username of the user (if mail.smtp.auth=true)
     *            password - the password of the user (if mail.smtp.auth=true)
     *            for the EmailService to work properly.
     * @param texts
     *            a Map of <String, InputStream> where the key is a
     *            (String-)identifier for the specific template which is read
     *            from the InputStream. The path to the files is
     *            "/email/templates/". The name of the file is for example
     *            "default_newAppointment_english.txt" (see the email
     *            configuration folder for more information) The following
     *            example shows the format of the text template: The first line
     *            is the E-Mail subject line The message body begins with the
     *            second line. You can add expressions from
     *            unicopa.copa.server.email.TextPatterns (i.e. _UPDATE_DATE,
     *            _CREATOR_NAME, _COMMENT, _LOCATION, _DATE, _SUPERVISOR,
     *            _EVENTGROUP_NAME, _EVENT_NAME). These expressions will be
     *            replaced by the data from the SingleEventUpdate before sending
     *            E-Mails with the method notifySingleEventUpdate. This will
     *            also work in the subject line!
     * 
     * @see unicopa.copa.server.email.TextPattern
     */
    public EmailService(Properties smtpProps, Map<String, InputStream> texts) {
	// the properties of the SMTP-Server
	this.smtpProps = smtpProps;
	// the username and password to log in to the SMTP-Server
	final String username;
	username = smtpProps.getProperty("username");
	smtpProps.remove("username");
	final String password;
	password = smtpProps.getProperty("password");
	smtpProps.remove("password");
	this.auth = new javax.mail.Authenticator() {
	    @Override
	    protected PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(username, password);
	    }
	};

	// the texts
	this.bodies = new HashMap<>();
	this.subjects = new HashMap<>();
	for (Map.Entry<String, InputStream> entry : texts.entrySet()) {
	    String key = entry.getKey();
	    try (Scanner scn = new Scanner(entry.getValue(), "UTF-8")) {
		// the first line of the text is the subject of the E-Mail
		String subject = scn.nextLine();
		this.subjects.put(key, subject);
		// the following text is the message body
		String msgBody = scn.useDelimiter("//A").next();
		this.bodies.put(key, msgBody);
	    }
	}
    }

    /**
     * \brief Sends a simple plain-text E-Mail.
     * 
     * @param recipient
     *            the reciptient the E-Mail should be sent to, i.e. x@y.com
     * @param subject
     *            the subject of the E-Mail
     * @param message
     *            the plain-text body of the E-Mail
     * @throws MessagingException
     */
    public void postMail(String recipient, String subject, String message)
	    throws MessagingException {
	// initiate session with the (sending) smtp server
	Session session = Session.getInstance(smtpProps, auth);

	// create and configure message
	Message msg = new MimeMessage(session);
	InternetAddress addressTo = new InternetAddress(recipient);
	msg.setRecipient(Message.RecipientType.TO, addressTo);
	msg.setSubject(subject);
	msg.setContent(message, "text/plain");

	// send the E-Mail
	Transport.send(msg);

    }

    /**
     * \brief Sends E-Mails to notify the given recipients of the
     * SingelEventUpdate.
     * 
     * Note: The format of the E-Mail depents on the data obtained from the
     * parameter "update" and the "TextID" obtained from the parameter contexts.
     * For each TextID there should be one entry in the "texts" parameter which
     * was passed to the constructor when you created your EmailContext object.
     * 
     * @param contexts
     *            a list of EmailContexts (Containing E-Mail-Addresses and
     *            TextID's)
     * @param update
     *            a SingleEventUpdate
     * @throws MessagingException
     * @see unicopa.copa.base.event.SingleEventUpdate
     * @see unicopa.copa.base.event.SingleEvent
     * @see unicopa.copa.server.email.EmailContext
     */
    public void notifySingleEventUpdate(List<EmailContext> contexts,
	    SingleEventUpdate update, String eventGroupName, String eventName)
	    throws MessagingException {
	// compose texts
	Map<String, String> processedBodies = replaceTextPatterns(this.bodies,
		update, eventGroupName, eventName);
	Map<String, String> processedSubjects = replaceTextPatterns(
		this.subjects, update, eventGroupName, eventName);

	// send E-Mails
	Session session = Session.getInstance(smtpProps, auth);
	for (EmailContext ctx : contexts) {
	    InternetAddress addr = ctx.getEmailAddress();
	    String textID = ctx.getTextID();
	    String msgBody = processedBodies.get(textID);
	    String subject = processedSubjects.get(textID);

	    Message msg = new MimeMessage(session);
	    msg.setRecipient(Message.RecipientType.TO, addr);
	    msg.setSubject(subject);
	    msg.setContent(msgBody, "text/plain");
	    Transport.send(msg);
	}
    }

    /**
     * \brief This method returns a Map with the TextPatterns being replaced by
     * the data obtained from the parameters.
     * 
     * The parameters SingleEventUpdate, eventGroupName and eventName define the
     * replacements for the Testpatterns.
     * 
     * @param inputMap
     *            The map where the values contain the TextPatterns that should
     *            be replaced.
     * @param update
     *            The SingleEventUpdate to obtain data from. _UPDATE_DATE,
     *            _CREATOR_NAME and _COMMENT will be directly obtained from
     *            SingleEventUpdate. _LOCATION, _DATE and _SUPERVISOR will be
     *            obtained from the new SingleEvent which is encapsulated in the
     *            SingleEventUpdate.
     * @param eventGroupName
     *            The Name of the EventGroup for the new SingleEvent.
     *            _EVENTGROUP_NAME will be replaced by this.
     * @param eventName
     *            The Name of the Event for the new SingleEvent. _EVENT_NAME
     *            will be replaced by this.
     * @return a processed copy of the parameter inputMap
     * 
     * @see unicopa.copa.server.email.TextPatterns for the TextPatterns that
     *      should be replaced
     */

    private static Map<String, String> replaceTextPatterns(
	    final Map<String, String> inputMap, final SingleEventUpdate update,
	    final String eventGroupName, final String eventName) {
	Map<String, String> messages = new HashMap<>();
	messages.putAll(inputMap);

	String updateDate = update.getUpdateDate().toString();
	String creatorName = update.getCreatorName();
	String comment = update.getComment();
	String newLocation = update.getUpdatedSingleEvent().getLocation();
	String newDate = update.getUpdatedSingleEvent().getDate().toString();
	String newSupervisor = update.getUpdatedSingleEvent().getSupervisor();

	for (Map.Entry<String, String> entry : messages.entrySet()) {
	    String text = entry.getValue();
	    text = text.replaceAll(TextPatterns._UPDATE_DATE.toString(),
		    updateDate);
	    text = text.replaceAll(TextPatterns._CREATOR_NAME.toString(),
		    creatorName);
	    text = text.replaceAll(TextPatterns._COMMENT.toString(), comment);
	    text = text.replaceAll(TextPatterns._LOCATION.toString(),
		    newLocation);
	    text = text.replaceAll(TextPatterns._DATE.toString(), newDate);
	    text = text.replaceAll(TextPatterns._SUPERVISOR.toString(),
		    newSupervisor);
	    text = text.replaceAll(TextPatterns._EVENTGROUP_NAME.toString(),
		    eventGroupName);
	    text = text.replaceAll(TextPatterns._EVENT_NAME.toString(),
		    eventName);
	    entry.setValue(text);
	}

	return messages;
    }
}
