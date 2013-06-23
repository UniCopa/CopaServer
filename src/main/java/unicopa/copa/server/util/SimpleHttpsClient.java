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
package unicopa.copa.server.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.AuthenticationStore;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.util.BasicAuthentication;
import org.eclipse.jetty.client.util.DigestAuthentication;
import org.eclipse.jetty.util.ssl.SslContextFactory;

/**
 * A simple HTTPS client based on Jetty.
 * 
 * @author Felix Wiemuth
 */
public class SimpleHttpsClient {

    private HttpClient client;
    private URI authURI;

    // private String loginURL;
    // private String reqURL;

    public SimpleHttpsClient(URI authURI) {
	this.authURI = authURI;
	// Configure HttpClient
	// Instantiate and configure the SslContextFactory
	SslContextFactory sslContextFactory = new SslContextFactory();
	client = new HttpClient(sslContextFactory);
	client.setFollowRedirects(false);
    }

    public void start() {
	try {
	    client.start();
	} catch (Exception ex) {
	    Logger.getLogger(SimpleHttpsClient.class.getName()).log(
		    Level.SEVERE, null, ex);
	}
    }

    /**
     * Authenticate at the given login URL using HTTP basic access
     * authentication.
     * 
     * @param user
     * @param pwd
     */
    public void authenticate(String user, String pwd) {
	// Add authentication credentials
	AuthenticationStore auth = client.getAuthenticationStore();
	// TODO what to do with realm?
	// TODO URI: Required? How to specifiy to allow access to everything?
	System.out.println("HttpsClient: Add authentication for URI: "
		+ authURI);
	// TODO use default realm / correct realm!
	auth.addAuthentication(new DigestAuthentication(authURI, "", user, pwd));
	// auth.addAuthentication(new BasicAuthentication(authURI, "sPi API",
	// user, pwd));

    }

    /**
     * 
     * @param text
     * @return
     */
    public String GET(URI uri) {
	try {
	    ContentResponse response = client.GET(uri);
	    System.out.println("Status: " + response.getStatus());
	    return response.getContentAsString();
	} catch (InterruptedException | TimeoutException | ExecutionException ex) {
	    Logger.getLogger(SimpleHttpsClient.class.getName()).log(
		    Level.SEVERE, null, ex);
	}
	return null;
    }

    public void stop() throws Exception {
	client.stop();
    }
}
