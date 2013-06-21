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
package unicopa.copa.server.gcm;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.Properties;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import unicopa.copa.server.CopaSystemContext;

/**
 * With the GoogleCloudMessagingSerive it is possible to send push messages to
 * Android devices.
 * 
 * @author Philip Wendland
 */
public class GoogleCloudMessagingService {
    public static final Logger LOG = Logger.getLogger(GoogleCloudMessagingService.class
	    .getName());    
    private HttpClient client;
    private Properties gcmProps;

    /**
     * Creates an object of GoogleCloudMessagingService.
     * 
     * Note: You have to provide configuration in
     * /gcm/googleCloudMessaging.properties for the GoogleCloudMessagingService
     * to work.
     * 
     */
    public GoogleCloudMessagingService(CopaSystemContext context) {
        try {
            LOG.addHandler(new FileHandler(context
                        .getLogDirectory().getCanonicalPath()
                        + "/GCM.log", 10000000, 1));
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
	this.gcmProps = new Properties();
	File settingsDirectory = new File(context.getSettingsDirectory(), "gcm");
	settingsDirectory.mkdirs();
	File gcmConfig = new File(settingsDirectory,
		"googleCloudMessaging.properties");

	if (!gcmConfig.exists()) {
	    File src;
	    try {
		src = new File(this.getClass()
			.getResource("/gcm/googleCloudMessaging.properties")
			.toURI());
		unicopa.copa.server.util.IOutils.copyFile(src, gcmConfig);
	    } catch (URISyntaxException | IOException ex) {
		LOG.log(Level.SEVERE, null, ex);
	    }
	}
	BufferedInputStream stream;
	try {
	    stream = new BufferedInputStream(new FileInputStream(gcmConfig));
	    this.gcmProps.load(stream);
	} catch (FileNotFoundException ex) {
	    LOG.log(
		    Level.SEVERE, null, ex);
	} catch (IOException ex) {
	    LOG.log(
		    Level.SEVERE, null, ex);
	}

	// Instantiate and configure the SslContextFactory
	SslContextFactory sslContextFactory = new SslContextFactory();
	sslContextFactory.setTrustAll(true);
	// Instantiate HttpClient with the SslContextFactory
	this.client = new HttpClient(sslContextFactory);
	// Start HttpClient
	try {
	    client.start();
	} catch (Exception ex) {
	    LOG.log(
		    Level.SEVERE, null, ex);
	}
    }

    /**
     * Send Push-messages to Android devices.
     * 
     * @param gcmKeys
     *            the keys to identify the recipients
     * @param msg
     */
    public void notify(Set<String> gcmKeys, String msg) {
	for (String key : gcmKeys) {
	    // async POST
	    this.client
		    .POST(gcmProps.getProperty("gcmURL"))
		    .header("Authorization",
			    "key=" + gcmProps.getProperty("senderAuthToken"))
		    .header("Content-Type",
			    "application/x-www-form-urlencoded;charset=UTF-8")
		    .param("registration_id", key).param("data.msg", msg)
		    .send(new Response.Listener.Empty() {
			@Override
			public void onContent(Response response,
				ByteBuffer buffer) {
			    if (response.getStatus() != 200) {
				LOG
					.log(Level.WARNING,
						"Google Cloud Messaging Server rejected request. Response code: {0}",
						response.getStatus());
			    }
			}
		    });
	}
    }

}
