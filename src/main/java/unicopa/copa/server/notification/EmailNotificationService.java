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
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import unicopa.copa.base.event.SingleEventUpdate;
import unicopa.copa.server.database.DatabaseService;
import unicopa.copa.server.email.EmailService;

/**
 * This NotificationService informs users about updates by sending E-Mails.
 * 
 * @author Felix Wiemuth, Philip Wendland
 */
public class EmailNotificationService extends NotificationService {
    private EmailService emailService;

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

	// get a EmailService object
	emailService = new EmailService(smtpProps, texts);
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
     * address. The content of the E-Mail will be in the language the user
     * specified in his settings (if available).
     * 
     * @param update
     *            the SingleEventUpdate to inform about
     */
    @Override
    public void notifyClients(SingleEventUpdate update) {
	throw new UnsupportedOperationException("Not supported yet.");
	// TODO search for clients who want to be notified about this update
	// compose the textIDs for the text template for every user
	// obtain EventGroupName, EventName from db
	// use EmailService to send emails
	// add required methods to DatabaseService
    }

}
