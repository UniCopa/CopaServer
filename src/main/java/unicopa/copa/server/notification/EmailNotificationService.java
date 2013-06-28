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
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import unicopa.copa.base.UserSettings;
import unicopa.copa.base.event.Event;
import unicopa.copa.base.event.SingleEventUpdate;
import unicopa.copa.base.event.SingleEvent;
import unicopa.copa.server.CopaSystemContext;
import unicopa.copa.server.database.ObjectNotFoundException;
import unicopa.copa.server.email.EmailContext;
import unicopa.copa.server.email.EmailService;
import unicopa.copa.server.email.UpdateInformation;

/**
 * This NotificationService informs users about updates by sending E-Mails.
 * 
 * @author Felix Wiemuth, Philip Wendland
 */
public class EmailNotificationService extends NotificationService {
    private EmailService emailService;
    private Map<InternetAddress, String> centralInstances;
    public static final Logger LOG = Logger
	    .getLogger(GoogleCloudNotificationService.class.getName());

    /**
     * Create a new EmailNotificationService.
     * 
     * Reads the configuration and text templates from /email and gets an
     * EmailService object.
     * 
     * @param context
     *            The system context
     */
    public EmailNotificationService(CopaSystemContext context) {
	super(context);

	File settingsDirectory = new File(super.getContext()
		.getSettingsDirectory(), "email");
	settingsDirectory.mkdirs();

	// obtain configuration for the SMTP server
	Properties smtpProps = new Properties();
	File smtpConfig = new File(settingsDirectory, "smtp.properties");
	if (!smtpConfig.exists()) {
	    File src;
	    try {
		src = new File(this.getClass()
			.getResource("/email/smtp.properties").toURI());
		unicopa.copa.server.util.IOutils.copyFile(src, smtpConfig);
	    } catch (URISyntaxException | IOException ex) {
		LOG.log(Level.SEVERE,
			"Error copying smtp.properties file to user settings directory",
			ex);
	    }
	}
	try (BufferedInputStream stream = new BufferedInputStream(
		new FileInputStream(smtpConfig))) {
	    smtpProps.load(stream);
	} catch (FileNotFoundException ex) {
	    LOG.log(Level.SEVERE, "Did not find smtp.properties.", ex);
	} catch (IOException ex) {
	    LOG.log(Level.SEVERE, null, ex);
	}

	// obtain text templates
	Map<String, InputStream> texts = new HashMap<>();
	File templates = new File(settingsDirectory, "/templates");
	templates.mkdirs();
	File[] files = unicopa.copa.server.util.IOutils.FileFinder(templates);
	for (File file : files) {
	    FileInputStream fis;
	    try {
		fis = new FileInputStream(file);
		texts.put(file.getName(), fis);
	    } catch (FileNotFoundException ex) {
		LOG.log(Level.SEVERE, null, ex);
	    }
	}

	// gather external E-Mail addresses and language settigns from
	// configuration
	centralInstances = new HashMap<>();
	File externalAddrs = new File(settingsDirectory,
		"externalAddresses.txt");
	try {
	    if (!externalAddrs.exists()) {
		File src = new File(this.getClass()
			.getResource("externalAddresses.txt").toURI());
		unicopa.copa.server.util.IOutils.copyFile(src, smtpConfig);
	    }
	    FileInputStream extAddrs = new FileInputStream(externalAddrs);
	    Scanner scn = new Scanner(new BufferedInputStream(extAddrs));
	    if (scn.hasNextLine())
		scn.nextLine();
	    if (scn.hasNextLine())
		scn.nextLine();
	    if (scn.hasNextLine())
		scn.nextLine(); // ignore first three lines
	    while (scn.hasNextLine()) {
		String nextAddrStr = scn.useDelimiter(":").next();
		String nextLanguage = scn.skip(":").nextLine();
		InternetAddress nextAddr = new InternetAddress(nextAddrStr);
		this.centralInstances.put(nextAddr, nextLanguage);

	    }
	} catch (IOException | AddressException ex) {
	    LOG.log(Level.SEVERE,
		    "Possibly format errors in  externalAddresses.txt.", ex);
	} catch (URISyntaxException ex) {
	    LOG.log(Level.SEVERE, null, ex);
	}
	// get a EmailService object
	FileHandler logFH;
	try {
	    logFH = new FileHandler(context.getLogDirectory()
		    .getCanonicalPath() + "/email.log", 10000000, 1, true);
	    this.emailService = new EmailService(smtpProps, texts, logFH);
	} catch (IOException | SecurityException ex) {
	    LOG.log(Level.SEVERE, "Error creating FileHandler for logging.", ex);
	}
    }

    /**
     * Notify all users that have subscribed to get the specified
     * SingleEventUpdate by sending an E-Mail to their registered E-Mail
     * address.
     * 
     * The content of the E-Mail will be in the language the user specified in
     * his settings (if available).
     * 
     * @param update
     *            the SingleEventUpdate to inform about
     */
    @Override
    public void notifyClients(SingleEventUpdate update) {
	try {
	    // determine kind of update
	    String kindOfUpd;
	    if (update.isSingleEventCreation()) {
		kindOfUpd = "new_";
	    } else if (update.isCancellation()) {
		kindOfUpd = "cncld_";
	    } else {
		kindOfUpd = "upd_";
	    }
	    // === gather the update information ===
	    // obtain EventGroupName, EventName from db.
	    // Note: We have to get the Event-ID from the old single event,
	    // because the new single event might be null
	    int eventID;
	    if (!update.isSingleEventCreation()) {
		int oldID = update.getOldSingleEventID();
		SingleEvent oldSingleEvent = super.getContext().getDbservice()
			.getSingleEvent(oldID);
		eventID = oldSingleEvent.getEventID();
	    } else {
		eventID = update.getUpdatedSingleEvent().getEventID();
	    }
	    Event event = super.getContext().getDbservice().getEvent(eventID);
	    int eventGroupID = event.getEventGroupID();
	    String eventGroupName = super.getContext().getDbservice()
		    .getEventGroup(eventGroupID).getEventGroupName();
	    String eventName = event.getEventName();
	    Date updateDate = update.getUpdateDate();
	    String creatorName = update.getCreatorName();
	    String comment = update.getComment();
	    String location;
	    Date date;
	    String supervisor;
	    // if the update is _no_ cancellation use data from new single event
	    // else use data from old single event
	    if (!update.isCancellation()) {
		location = update.getUpdatedSingleEvent().getLocation();
		date = update.getUpdatedSingleEvent().getDate();
		supervisor = update.getUpdatedSingleEvent().getSupervisor();
	    } else {
		int oldID = update.getOldSingleEventID();
		SingleEvent oldSingleEvent = super.getContext().getDbservice()
			.getSingleEvent(oldID);
		location = oldSingleEvent.getLocation();
		date = oldSingleEvent.getDate();
		supervisor = oldSingleEvent.getSupervisor();
	    }
	    UpdateInformation info = new UpdateInformation(eventGroupName,
		    eventName, updateDate, creatorName, comment, location,
		    date, supervisor);

	    // search for clients who want to be notified about this update
	    List<Integer> subUsers = super.getContext().getDbservice()
		    .getSubscribedUserIDs(eventID);

	    // get the Email addresses and user settings for those users
	    Map<Integer, String> emailStrings = new HashMap<>();
	    Map<Integer, UserSettings> usrSettings = new HashMap<>();
	    for (int usrID : subUsers) {
		try {
		    String addr = super.getContext().getDbservice()
			    .getEmailAddress(usrID);
		    emailStrings.put(usrID, addr);
		    UserSettings settings = super.getContext().getDbservice()
			    .getUserSettings(usrID);
		    usrSettings.put(usrID, settings);
		} catch (ObjectNotFoundException ex) {
		    LOG.log(Level.SEVERE,
			    "Error obtaining information from database.", ex);
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
			LOG.log(Level.WARNING, "E-Mail address of user"
				+ userID + "propably malformated.", ex);
		    }

		    // Text-ID
		    StringBuilder textID = new StringBuilder();
		    textID.append("default_");
		    textID.append(kindOfUpd);
		    textID.append(usrSet.getLanguage());
		    textID.append(".txt");

		    // add EmailContext to list
		    if (EmailAddr != null) {
			EmailContext ctx = new EmailContext(EmailAddr,
				textID.toString());
			emailContexts.add(ctx);
		    }
		}
	    }

	    // next we have to add the contexts for external users.
	    // The E-Mail addresses of those users are located in a
	    // configuration file and have already been gathered by the
	    // constructor.
	    for (Map.Entry<InternetAddress, String> entry : centralInstances
		    .entrySet()) {
		// Text-ID
		String lang = entry.getValue();
		StringBuilder textID = new StringBuilder();
		textID.append("general_");
		textID.append(kindOfUpd);
		textID.append(lang);
		textID.append(".txt");
		EmailContext ctx = new EmailContext(entry.getKey(),
			textID.toString());
		emailContexts.add(ctx);
	    }

	    // use EmailService to send emails

	    this.emailService.notifySingleEventUpdate(emailContexts, info);

	} catch (ObjectNotFoundException ex) {
	    LOG.log(Level.SEVERE, null, ex);
	}
    }

    /**
     * The E-Mail service ignores those notification events.
     * 
     * @param event
     * @param userID
     */
    @Override
    public void notifyClient(NotificationEvent event, int userID) {
    }
}
