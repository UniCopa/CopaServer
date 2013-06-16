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
import unicopa.copa.base.com.request.AddSingleEventRequest;
import unicopa.copa.base.com.request.AddSingleEventUpdateRequest;
import unicopa.copa.base.com.request.CancelSingleEventRequest;
import unicopa.copa.base.com.request.GetAllDeputiesRequest;
import unicopa.copa.base.com.request.GetAllOwnersRequest;
import unicopa.copa.base.com.request.GetAllRightholdersRequest;
import unicopa.copa.base.com.request.GetCategoriesRequest;
import unicopa.copa.base.com.request.GetCurrentSingleEventsRequest;
import unicopa.copa.base.com.request.GetEventGroupRequest;
import unicopa.copa.base.com.request.GetEventGroupsRequest;
import unicopa.copa.base.com.request.GetEventRequest;
import unicopa.copa.base.com.request.GetEventsRequest;
import unicopa.copa.base.com.request.GetSingleEventUpdatesRequest;
import unicopa.copa.base.com.request.GetSubscribedSingleEventUpdatesRequest;
import unicopa.copa.base.com.request.GetUserSettingsRequest;
import unicopa.copa.base.com.request.SetUserSettingsRequest;
import unicopa.copa.base.com.request.TestRequest;
import unicopa.copa.base.com.serialization.ServerSerializer;
import unicopa.copa.server.com.requestHandler.RequestHandler;
import unicopa.copa.server.database.DatabaseService;
import unicopa.copa.server.database.ObjectNotFoundException;
import unicopa.copa.server.notification.Notifier;

/**
 * This class represents the core of the system. It receives client messages,
 * processes them and returns responses. It is to be used by a wrapper that
 * provides the communication with clients. Due to allow simple access by these
 * wrappers, the class is a singleton.
 * 
 * @author Felix Wiemuth
 */
public class CopaSystem {

    private static CopaSystem instance = new CopaSystem();
    private Properties systemProperties = new Properties(); // TODO use
    private CopaSystemContext context;
    private Map<Class<? extends AbstractRequest>, RequestHandler> requestHandlers = new HashMap<>();

    private CopaSystem() {
	try {
	    // TODO get database from system properties
	    context = new CopaSystemContext(new DatabaseService(new File(
		    "database")), new Notifier(), new File(
		    System.getProperty("user.home") + File.separator
			    + ".unicopa" + File.separator + "copa"));
	} catch (IOException ex) {
	    Logger.getLogger(CopaSystem.class.getName()).log(Level.SEVERE,
		    null, ex);
	    throw new RuntimeException();
	}
	// TODO add notification services
	loadProperties();
	loadRequestHandlers();
    }

    /**
     * Get the instance of the CopaSystem. Do not use this if not absolutely
     * necessary. Components of the system are provided with the needed
     * information by the system itself.
     * 
     * @return
     */
    public static CopaSystem getInstance() {
	return instance;
    }

    /**
     * Create the systems settings directory if necessary and load properties.
     */
    private void loadProperties() {
	if (!context.getSettingsDirectory().isDirectory()) {
	    context.getSettingsDirectory().mkdirs();
	    // TODO log
	    // TODO copy default files to the directory
	}
	// TODO load properties if necessary
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
		// sort alphabetically
		add(AddSingleEventRequest.class);
		add(AddSingleEventUpdateRequest.class);
		add(CancelSingleEventRequest.class);
		add(GetAllDeputiesRequest.class);
		add(GetAllOwnersRequest.class);
		add(GetAllRightholdersRequest.class);
		add(GetCategoriesRequest.class);
		add(GetCurrentSingleEventsRequest.class);
		add(GetEventGroupRequest.class);
		add(GetEventGroupsRequest.class);
		add(GetEventRequest.class);
		add(GetEventsRequest.class);
		add(GetSingleEventRequest.class);
		add(GetSingleEventUpdatesRequest.class);
		add(GetSubscribedSingleEventUpdatesRequest.class);
		add(GetUserSettingsRequest.class);
		add(SetUserSettingsRequest.class);
		add(TestRequest.class);
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
     * @param userName
     *            the unique user name of the user whose message to process
     * 
     * @return the message to be sent back to the client
     */
    public String processClientMessage(String json, String userName) {
	// TODO send back InternalErrorException or PermissionException if the
	// user cannot be found
	try {
	    int userID;
	    try {
		userID = context.getDbservice().getUserID(userName);
	    } catch (ObjectNotFoundException ex) {
		throw new InternalErrorException(
			"Fatal: Cannot process client message because of missing user ID: "
				+ ex.getMessage());
	    }
	    AbstractRequest request = AbstractRequest.deserialize(json);
	    AbstractResponse response = getRequestHandler(request.getClass())
		    .handleRequest(request, userID);
	    if (response == null) {
		throw new InternalErrorException(
			"The request handler for the request returned a null object.");
	    }
	    return response.serialize();
	} catch (InternalErrorException | APIException | PermissionException
		| RequestNotPracticableException ex) {
	    // Logger.getLogger(CopaSystem.class.getName()).log(Level.SEVERE,
	    // null, ex);
	    // TODO log in appropriate way
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

    public CopaSystemContext getContext() {
	return context;
    }
}
