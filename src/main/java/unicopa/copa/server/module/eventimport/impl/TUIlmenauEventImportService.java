/*
 * Copyright (C) 2013 UniCopa
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

import java.util.List;
import unicopa.copa.server.module.eventimport.EventImportService;
import unicopa.copa.server.module.eventimport.model.Event;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;

/**
 * 
 * @author Felix Wiemuth
 */
public class TUIlmenauEventImportService implements EventImportService {
    // private final URI host;
    private HttpClient client;

    public TUIlmenauEventImportService() throws Exception { // TODO specific
							    // exception
	// try {
	// host = new URI("http://domain.com/"); //TODO read from configuration
	// file
	// } catch (URISyntaxException ex) {
	// Logger.getLogger(TUIlmenauEventImportService.class.getName()).log(Level.SEVERE,
	// null, ex);
	// throw new Exception();
	// }
	client = new HttpClient();
	client.start();
    }

    public List<Event> getEvents() throws Exception {
	ContentResponse response = client.GET("URL"); // TODO set URL
	return Event.fromJsonList(response.getContentAsString());
    }
}
