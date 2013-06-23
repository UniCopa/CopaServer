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
//TODO own repository!
package unicopa.copa.server.module.eventimport.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import unicopa.copa.server.module.eventimport.EventImportService;
import unicopa.copa.server.module.eventimport.model.EventImportContainer;
import unicopa.copa.server.util.SimpleHttpsClient;

/**
 * 
 * @author Felix Wiemuth
 */
public class TUIlmenauEventImportService implements EventImportService {
    private SimpleHttpsClient client;
    private Properties settings = new Properties();
    private static final Logger LOG = Logger.getLogger(TUIlmenauEventImportService.class.getName());
    
    private static URI REQ_GET_COURSES;

    public TUIlmenauEventImportService(InputStream settingsStream) throws IOException {
        settings.load(settingsStream);
        try {
            REQ_GET_COURSES = new URI(settings.getProperty("uri_getCourses"));
            client = new SimpleHttpsClient(new URI(settings.getProperty("auth_uri")));
        } catch (URISyntaxException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        client.authenticate(settings.getProperty("user"), settings.getProperty("password"));
    }

    @Override
    public EventImportContainer getSnapshot() throws Exception {
        client.start();
        String json = client.GET(REQ_GET_COURSES);
        client.stop();
        return null;
    }
    
    public EventImportContainer getSnapshotTest() throws Exception {
        client.start();
        System.out.println("Do request to URI " + REQ_GET_COURSES.toString());
        System.out.println("Response: " + client.GET(REQ_GET_COURSES));
        client.stop();
        return null;
    }
    
}
