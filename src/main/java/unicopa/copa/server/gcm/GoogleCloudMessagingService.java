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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.util.ssl.SslContextFactory;

/**
 *
 * @author Philip Wendland
 */
public class GoogleCloudMessagingService {
    private static GoogleCloudMessagingService instance = null;
    private HttpClient client;
    private Properties gcmProps;
    
    
    private GoogleCloudMessagingService(){
        this.gcmProps = new Properties();
	try (BufferedInputStream stream = new BufferedInputStream(
		new FileInputStream("/gcm/googleCloudMessaging.properties"))) {
	    this.gcmProps.load(stream);
	} catch (FileNotFoundException ex) {
            Logger.getLogger(GoogleCloudMessagingService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GoogleCloudMessagingService.class.getName()).log(Level.SEVERE, null, ex);
        }
        // Instantiate and configure the SslContextFactory
        SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setTrustAll(true);        
        // Instantiate HttpClient with the SslContextFactory
        this.client = new HttpClient(sslContextFactory);
        // Configure HttpClient
        //client.setFollowRedirects(false);        
        // Start HttpClient        
        try {
            client.start();           
        } catch (Exception ex) {
            Logger.getLogger(GoogleCloudMessagingService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static synchronized GoogleCloudMessagingService getInstance() {
        if (instance == null) {
            instance = new GoogleCloudMessagingService();
        }
        return instance;
    }
    
    public void notify(Set<String> gcmKeys, String msg){        
        for (String key : gcmKeys) {       
                //async POST
                this.client.POST(gcmProps.getProperty("gcmURL"))
                        .header("Authorization", "key="+gcmProps.getProperty("senderAuthToken"))
                        .header("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
                        .param("registration_id", key)
                        .param("data.msg", msg)
                        .send(new Response.Listener.Empty()
                            {
                                @Override
                                public void onContent(Response response, ByteBuffer buffer)
                                {
                                    if(response.getStatus() != 200){
                                        Logger.getLogger(GoogleCloudMessagingService.class.getName())
                                                .log(Level.INFO, "Google Cloud Messaging Server rejected request. Response code: {0}", response.getStatus());
                                    }                                   
                                }
                            });           
        }
    }   
          
}
