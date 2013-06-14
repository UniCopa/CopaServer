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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import unicopa.copa.base.UserSettings;
import unicopa.copa.base.event.Event;
import unicopa.copa.base.event.SingleEventUpdate;
import unicopa.copa.base.event.SingleEvent;
import unicopa.copa.server.database.DatabaseService;
import unicopa.copa.server.database.ObjectNotFoundException;
import unicopa.copa.server.email.EmailContext;
import unicopa.copa.server.email.EmailService;

/**
 * This NotificationService informs users about updates by sending E-Mails.
 * 
 * @author Felix Wiemuth, Philip Wendland
 */
public class EmailNotificationService extends NotificationService {
    private EmailService emailService;
    private Map<InternetAddress, String> centralInstances;

    /**
     * Create a new EmailNotificationService.
     * 
     * Reads the configuration and text templates from /email and gets an
     * EmailService object.
     * 
     * @param dbservice
     *            The DatabaseService object used by the system.
     * @throws FileNotFoundException
     * @throws IOException
     */
    public EmailNotificationService(DatabaseService dbservice)
	    throws FileNotFoundException, IOException {
	super(dbservice);

	// obtain configuration for the SMTP server
	Properties smtpProps = new Properties();
	try (BufferedInputStream stream = new BufferedInputStream(
		new FileInputStream("/email/smtp.properties"))) {
	    smtpProps.load(stream);
	}

	// obtain text templates
	Map<String, InputStream> texts = new HashMap<>();
	File[] files = FileFinder("/email/templates/");
	for (File file : files) {
	    FileInputStream fis = new FileInputStream(file);
	    texts.put(file.getName(), fis);
	}

	// gather external E-Mail addresses and language settigns from
	// configuration
	centralInstances = new HashMap<>();
	FileInputStream extAddrs = new FileInputStream(
		"/email/externalAddresses.txt");
	Scanner scn = new Scanner(new BufferedInputStream(extAddrs));
	scn.nextLine();
	scn.nextLine();
	scn.nextLine(); // ignore first three lines
	while (scn.hasNextLine()) {
	    String nextAddrStr = scn.useDelimiter(":").next();
	    String nextLanguage = scn.nextLine();
	    try {
		InternetAddress nextAddr = new InternetAddress(nextAddrStr);
		this.centralInstances.put(nextAddr, nextLanguage);
	    } catch (AddressException ex) {
		Logger.getLogger(EmailNotificationService.class.getName()).log(
			Level.SEVERE, null, ex);
	    }
	}

	// get a EmailService object
	this.emailService = new EmailService(smtpProps, texts);
    }

    /**
     * \brief Returns an array of Files ending with .txt from the given path
     * 
     * @param dirName
     *            The path to the directory
     * @return an array of Files from that directory ending with ".txt"
     */
    public static File[] FileFinder(String dirName) {
	File dir = new File(dirName);

	return dir.listFiles(new FilenameFilter() {
	    @Override
	    public boolean accept(File dir, String filename) {
		return filename.endsWith(".txt");
	    }
	});
    }

    /**
     * Notify all users that have subscribed to get the specified
     * SingleEventUpdate by sending an E-Mail to their registered E-Mail
     * address.
     * 
     * The content of the E-Mail will be in the language the user specified in
     * his settings (if available).
     * 
     * TODO obtain E-Mail addresses for general text templates
     * 
     * @param update
     *            the SingleEventUpdate to inform about
     */
    @Override
    public void notifyClients(SingleEventUpdate update) {
	// determine kind of update
	String kindOfUpd;
	if (update.isSingleEventCreation()) {
	    kindOfUpd = "new_";
	} else if (update.isCancellation()) {
	    kindOfUpd = "cncld_";
	} else {
	    kindOfUpd = "upd_";
	}

	// obtain EventGroupName, EventName from db.
	// Note: We have to get the Event-ID from the old single event, because
	// the new single event might be null
	int oldID = update.getOldSingleEventID();
	SingleEvent oldSingleEvent = super.dbservice().getSingleEvent(oldID);
	int eventID = oldSingleEvent.getEventID();
	Event event = super.dbservice().getEvent(eventID);
	int eventGroupID = event.getEventGroupID();
	String eventName = event.getEventName();
	String eventGroupName = super.dbservice().getEventGroup(eventGroupID)
		.getEventGroupName();

	// search for clients who want to be notified about this update
	List<Integer> subUsers = super.dbservice()
		.getSubscribedUserIDs(eventID);

	// get the Email addresses and user settings for those users
	Map<Integer, String> emailStrings = new HashMap<>();
	Map<Integer, UserSettings> usrSettings = new HashMap<>();
	for (int usrID : subUsers) {
	    try {
		String addr = super.dbservice().getEmailAddress(usrID);
		emailStrings.put(usrID, addr);
		UserSettings settings = super.dbservice()
			.getUserSettings(usrID);
		usrSettings.put(usrID, settings);
	    } catch (ObjectNotFoundException ex) {
		Logger.getLogger(EmailNotificationService.class.getName()).log(
			Level.SEVERE, null, ex);
	    }
	}

	// === create the list of EmailContext ===

	// begin with default users from db
	List<EmailContext> emailContexts = new ArrayList<>();
	for (int userID : subUsers) {
	    UserSettings usrSet = usrSettings.get(userID);
	    if (usrSet.isEmailNotificationEnabled()) {
		// E-Mail address
		String emailString = emailStrings.get(userID);
		InternetAddress EmailAddr = null;
		try {
		    EmailAddr = new InternetAddress(emailString);
		} catch (AddressException ex) {
		    Logger.getLogger(EmailNotificationService.class.getName())
			    .log(Level.SEVERE, null, ex);
		}

		// Text-ID
		StringBuilder textID = new StringBuilder();
		textID.append("default_");
		textID.append(kindOfUpd);
		textID.append(usrSet.getLanguage());

		// add EmailContext to list
		if (EmailAddr != null) {
		    EmailContext ctx = new EmailContext(EmailAddr,
			    textID.toString());
		    emailContexts.add(ctx);
		}
	    }
	}

	// next we have to add the contexts for external users.
	// The E-Mail addresses of those users are located in a configuration
	// file and have already been gathered by the constructor.
	for (Map.Entry<InternetAddress, String> entry : centralInstances
		.entrySet()) {
	    // Text-ID
	    String lang = entry.getValue();
	    StringBuilder textID = new StringBuilder();
	    textID.append("general_");
	    textID.append(kindOfUpd);
	    textID.append(lang);
	    EmailContext ctx = new EmailContext(entry.getKey(),
		    textID.toString());
	    emailContexts.add(ctx);
	}

	// use EmailService to send emails
	try {
	    this.emailService.notifySingleEventUpdate(emailContexts, update,
		    eventGroupName, eventName);
	} catch (MessagingException ex) {
	    Logger.getLogger(EmailNotificationService.class.getName()).log(
		    Level.SEVERE, null, ex);
	}
    }

    @Override
    public void notifyClient(NotificationEvent event, int userID) {
    }
}
