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
package unicopa.copa.server;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import unicopa.copa.base.com.exception.APIException;
import unicopa.copa.base.com.request.AbstractRequest;
import unicopa.copa.base.com.request.AbstractResponse;
import unicopa.copa.base.com.request.GetSingleEventRequest;
import unicopa.copa.base.com.exception.InternalErrorException;
import unicopa.copa.base.com.exception.PermissionException;
import unicopa.copa.base.com.exception.RequestNotPracticableException;
import unicopa.copa.base.com.serialization.ServerSerializer;
import unicopa.copa.server.com.requestHandler.RequestHandler;
import unicopa.copa.server.database.DatabaseService;
import unicopa.copa.server.notification.Notifier;

/**
 * 
 * @author Felix Wiemuth
 */
public class CopaSystem {

    private Properties systemProperties = new Properties(); // TODO use
    private CopaSystemContext context;
    private Map<Class<? extends AbstractRequest>, RequestHandler> requestHandlers = new HashMap<>();

    public CopaSystem() {
	try {
	    // TODO get database from system properties
	    context = new CopaSystemContext(new DatabaseService(new File(
		    "database")), new Notifier());
	} catch (IOException ex) {
	    Logger.getLogger(CopaSystem.class.getName()).log(Level.SEVERE,
		    null, ex);
	    throw new RuntimeException();
	}
	loadRequestHandlers();
    }

    /**
     * Load the request handlers for the specified requests. The requests for
     * which handlers should be loaded must be entered below.
     */
    private void loadRequestHandlers() {
	// TODO (improvement) load class files from directory
	// Enable requests (select those for which a RequestHandler should be
	// registered below)
	List<Class<? extends AbstractRequest>> requests = new ArrayList<Class<? extends AbstractRequest>>() {
	    {
		add(GetSingleEventRequest.class);
	    }
	};

	// Map Requests to appropriate RequestHandlers
	for (Class req : requests) {
	    StringBuilder handlerName = new StringBuilder();
	    handlerName.append(RequestHandler.class.getPackage().getName());
	    handlerName.append('.');
	    handlerName.append(req.getSimpleName());
	    handlerName.append("Handler");
	    Class reqHandlerClass = null;
	    try {
		reqHandlerClass = Class.forName(handlerName.toString());
	    } catch (ClassNotFoundException ex) {
		Logger.getLogger(CopaSystem.class.getName())
			.log(Level.SEVERE,
				"The request hanlder for \""
					+ req.getSimpleName()
					+ "\" could not be found! This will result in the system not being able to handle this request and return InternatlErrorException instead.",
				ex);
		continue;
	    }
	    RequestHandler reqHandler = null;
	    try {
		reqHandler = (RequestHandler) reqHandlerClass.getConstructor(
			CopaSystemContext.class).newInstance(context);
	    } catch (NoSuchMethodException | SecurityException
		    | InstantiationException | IllegalAccessException
		    | IllegalArgumentException | InvocationTargetException ex) {
		Logger.getLogger(CopaSystem.class.getName()).log(Level.SEVERE,
			null, ex);
		continue;
	    }
	    requestHandlers.put(req, reqHandler);
	}
    }

    /**
     * Process a message sent from a client.
     * 
     * @param json
     *            the message from the client (expected in JSON format)
     * @return the message to be sent back to the client
     */
    public String processClientMessage(String json) {
	try {
	    AbstractRequest request = AbstractRequest.deserialize(json);
	    AbstractResponse response = getRequestHandler(request.getClass())
		    .handleRequest(request);
	    return response.serialize();
	} catch (InternalErrorException | APIException | PermissionException
		| RequestNotPracticableException ex) {
//	    Logger.getLogger(CopaSystem.class.getName()).log(Level.SEVERE,
//		    null, ex);
            //TODO log in appropriate way
	    return ServerSerializer.serialize(ex); // pass exception to client
	}
    }

    private RequestHandler getRequestHandler(Class requestClass)
	    throws InternalErrorException {
	RequestHandler requestHandler = requestHandlers.get(requestClass);
	if (requestHandler == null) {
	    throw new InternalErrorException(
		    "There is no request handler available for request "
			    + requestClass.getSimpleName() + ".");
	}
	return requestHandler;
    }
}
