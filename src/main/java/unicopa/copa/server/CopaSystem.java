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
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;
import unicopa.copa.base.ServerInfo;
import unicopa.copa.base.com.exception.APIException;
import unicopa.copa.base.com.request.AbstractRequest;
import unicopa.copa.base.com.request.AbstractResponse;
import unicopa.copa.base.com.request.GetSingleEventRequest;
import unicopa.copa.base.com.exception.InternalErrorException;
import unicopa.copa.base.com.exception.PermissionException;
import unicopa.copa.base.com.exception.RequestNotPracticableException;
import unicopa.copa.base.com.request.AddRoleToUserRequest;
import unicopa.copa.base.com.request.AddSingleEventRequest;
import unicopa.copa.base.com.request.AddSingleEventUpdateRequest;
import unicopa.copa.base.com.request.CancelSingleEventRequest;
import unicopa.copa.base.com.request.DropRoleRequest;
import unicopa.copa.base.com.request.GetAllDeputiesRequest;
import unicopa.copa.base.com.request.GetAllOwnersRequest;
import unicopa.copa.base.com.request.GetAllRightholdersRequest;
import unicopa.copa.base.com.request.GetCategoriesRequest;
import unicopa.copa.base.com.request.GetCurrentSingleEventsRequest;
import unicopa.copa.base.com.request.GetEventGroupRequest;
import unicopa.copa.base.com.request.GetEventGroupsRequest;
import unicopa.copa.base.com.request.GetEventRequest;
import unicopa.copa.base.com.request.GetEventsRequest;
import unicopa.copa.base.com.request.GetMyAppointedUsersRequest;
import unicopa.copa.base.com.request.GetMyEventsRequest;
import unicopa.copa.base.com.request.GetServerInfoRequest;
import unicopa.copa.base.com.request.GetServerStatusNotesRequest;
import unicopa.copa.base.com.request.GetSingleEventUpdatesRequest;
import unicopa.copa.base.com.request.GetSubscribedSingleEventUpdatesRequest;
import unicopa.copa.base.com.request.GetUserDataRequest;
import unicopa.copa.base.com.request.GetUserSettingsRequest;
import unicopa.copa.base.com.request.RemoveRoleFromUserRequest;
import unicopa.copa.base.com.request.SetUserSettingsRequest;
import unicopa.copa.base.com.request.TestRequest;
import unicopa.copa.base.com.serialization.ServerSerializer;
import unicopa.copa.server.com.requestHandler.RequestHandler;
import unicopa.copa.server.database.DatabaseService;
import unicopa.copa.server.database.IncorrectObjectException;
import unicopa.copa.server.database.ObjectAlreadyExsistsException;
import unicopa.copa.server.database.ObjectNotFoundException;
import unicopa.copa.server.database.util.DatabaseUtil;
import unicopa.copa.server.module.eventimport.EventImportService;
import unicopa.copa.server.module.eventimport.impl.tuilmenau.TUIlmenauEventImportService;
import unicopa.copa.server.module.eventimport.model.EventImportContainer;
import unicopa.copa.server.notification.EmailNotificationService;
import unicopa.copa.server.notification.GoogleCloudNotificationService;
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

    private static final boolean DEBUG_MODE = true; // this enables debug
						    // logging - attention, may
						    // result in big log files
    private static final Logger LOG = Logger.getLogger(CopaSystem.class
	    .getName());
    private static final Logger DEBUG_LOG = Logger.getLogger("debug");
    private static final File databaseDirectory = new File("database");
    private static CopaSystem instance = new CopaSystem();
    private Properties systemProperties = new Properties(); // TODO use
    private CopaSystemContext context;
    private Registration registration;
    private EventImportService eventImportService;
    private Map<Class<? extends AbstractRequest>, RequestHandler> requestHandlers = new HashMap<>();

    private boolean doEventImport = false;

    private CopaSystem() {
	try {
	    // TODO get database from system properties
	    File baseDir = new File(System.getProperty("user.home")
		    + File.separator + ".unicopa" + File.separator + "copa");
	    baseDir.mkdirs();

	    File logDirectory = new File(baseDir, "logs");
	    logDirectory.mkdirs();

	    // Activate logging to files first

	    LOG.addHandler(new FileHandler(logDirectory.getCanonicalPath()
		    + "/copa-system.log", 10000000, 1, true));

	    if (DEBUG_MODE) {
		DEBUG_LOG.addHandler(new FileHandler(logDirectory
			.getCanonicalPath() + "/copa-debug.log", 10000000, 1,
			true));
	    }

	    // Logger for EventImport module
	    Logger.getLogger(EventImportService.class.getName()).addHandler(
		    new FileHandler(logDirectory.getCanonicalPath()
			    + "/event-import.log", 10000000, 1, true));

	    DatabaseService dbservice = initializeDatabase();

	    Properties serverInfoProperties = new Properties();
	    serverInfoProperties.load(getClass().getResourceAsStream(
		    "/system/version.properties"));

	    Notifier notifier = new Notifier();

	    context = new CopaSystemContext(dbservice, notifier, baseDir,
		    logDirectory, DEBUG_LOG, null); // serverInfo is initialized
						    // below because of cyclic
						    // dependency

	    List<String> availableRequests = loadRequestHandlers(); // needs
								    // context
								    // to be
								    // initialized

	    Date startDate = new Date();
	    ServerInfo serverInfo = new ServerInfo(serverInfoProperties,
		    startDate, availableRequests);
	    context.setServerInfo(serverInfo);

	    loadSystemProperties();
	    initializeEventImportService();

	    registration = new Registration(context);
	    EmailNotificationService emailNotificationService = new EmailNotificationService(
		    context);
	    GoogleCloudNotificationService googleCloudNotificationService = new GoogleCloudNotificationService(
		    context);
	    notifier.addNotificationService(emailNotificationService);
	    notifier.addNotificationService(googleCloudNotificationService);
	    LOG.log(Level.INFO,
		    "System fully initialized, started {0}.\nversion: {1}\nAPI version: {2}\ncommit ID: {3}",
		    new Object[] { startDate, serverInfo.getVersion(),
			    serverInfo.getApiVersion(),
			    serverInfo.getCommitID() });
	} catch (Exception ex) {
	    LOG.log(Level.SEVERE, null, ex);
	    throw new RuntimeException(
		    "Could not fully initialize the system - safety abort.", ex); // cannot
										  // continue
										  // with
										  // this
										  // failure
	}
	if (doEventImport) {
	    LOG.info("Starting event import because database was created...");
	    try {
		EventImportContainer snapshot = eventImportService
			.getSnapshot();
		context.getDbservice().importEvents(snapshot);
	    } catch (Exception ex) {
		LOG.log(Level.SEVERE, "Event import failed", ex);
	    }
	}
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

    private void initializeEventImportService() throws Exception {
	File eventImportSettings = new File(context.getSettingsDirectory(),
		"eventImport.xml");
	if (!eventImportSettings.exists()) {
	    throw new Exception(
		    "The configuration file for the event import module does not exist - expected: "
			    + eventImportSettings.getAbsolutePath());
	}
	// TODO generalize: load class from file
	eventImportService = new TUIlmenauEventImportService(
		new FileInputStream(eventImportSettings));
    }

    /**
     * Initialize DatabaseService and create database if it doesn´t exist.
     * 
     * @return
     */
    private DatabaseService initializeDatabase() throws Exception {
	DatabaseService databaseService;
	if (!databaseDirectory.isDirectory()) {
	    if (databaseDirectory.exists()) {
		throw new Exception(
			String.format(
				"The path {0} should be either the database directory or nonexistent. Delete it for new database initialization.",
				databaseDirectory.getAbsolutePath()));
	    } else {
		DatabaseUtil.createNewDatabase(databaseDirectory);
		LOG.log(Level.WARNING, "Created new Database in {0}",
			databaseDirectory.getAbsolutePath());
		databaseService = new DatabaseService(databaseDirectory);
		if (systemProperties.getProperty(
			"runEventImportOnInitialization").equals("true")) {
		    doEventImport = true;
		}
	    }
	} else {
	    databaseService = new DatabaseService(databaseDirectory);
	}
	return databaseService;
    }

    /**
     * Create the systems settings directory if necessary and load properties.
     */
    private void loadSystemProperties() throws URISyntaxException, IOException {
	File systemPropertiesFile = new File(context.getSettingsDirectory(),
		"system.properties");
	if (!systemPropertiesFile.isFile()) {
	    LOG.warning("System configuration file "
		    + systemPropertiesFile.getAbsolutePath()
		    + " does not exist, creating default configuration.");
	    File src = new File(this.getClass()
		    .getResource("/system/system.properties").toURI());
	    unicopa.copa.server.util.IOutils
		    .copyFile(src, systemPropertiesFile);
	}
	systemProperties = new Properties();
	systemProperties.load(new FileInputStream(systemPropertiesFile));
    }

    /**
     * Load the request handlers for the specified requests. The requests for
     * which handlers should be loaded must be entered below.
     * 
     * @return a list with the simple names of all Request classes loaded (where
     *         a handler was found)
     */
    private List<String> loadRequestHandlers() {
	// TODO (improvement) load class files from directory
	// Enable requests (select those for which a RequestHandler should be
	// registered below)
	List<Class<? extends AbstractRequest>> requests = new ArrayList<Class<? extends AbstractRequest>>() {
	    {
		// sort alphabetically
		add(AddRoleToUserRequest.class);
		add(AddSingleEventRequest.class);
		add(AddSingleEventUpdateRequest.class);
		add(CancelSingleEventRequest.class);
		add(DropRoleRequest.class);
		add(GetAllDeputiesRequest.class);
		add(GetAllOwnersRequest.class);
		add(GetAllRightholdersRequest.class);
		add(GetCategoriesRequest.class);
		add(GetCurrentSingleEventsRequest.class);
		add(GetEventGroupRequest.class);
		add(GetEventGroupsRequest.class);
		add(GetEventRequest.class);
		add(GetEventsRequest.class);
		add(GetMyAppointedUsersRequest.class);
		add(GetMyEventsRequest.class);
		add(GetServerInfoRequest.class);
		add(GetServerStatusNotesRequest.class);
		add(GetSingleEventRequest.class);
		add(GetSingleEventUpdatesRequest.class);
		add(GetSubscribedSingleEventUpdatesRequest.class);
		add(GetUserDataRequest.class);
		add(GetUserSettingsRequest.class);
		add(RemoveRoleFromUserRequest.class);
		add(SetUserSettingsRequest.class);
		add(TestRequest.class);
	    }
	};

	List<String> availableRequests = new LinkedList<>();
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
		LOG.log(Level.SEVERE,
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
		LOG.log(Level.SEVERE, null, ex);
		continue;
	    }
	    requestHandlers.put(req, reqHandler);
	    availableRequests.add(req.getSimpleName());
	}
	return availableRequests;
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
    public String processClientMessage(String json, String userName,
	    GeneralUserPermission userPermission) {
	try {
	    int userID;
	    try {
		userID = context.getDbservice().getUserID(userName);
	    } catch (ObjectNotFoundException ex) {
		// user does not exist in database - register and try again
		try {
		    registration.register(userName, userPermission);
		    userID = context.getDbservice().getUserID(userName);
		    // new user registered - make him owner at events where he
		    // is eligible
		    context.getDbservice().matchOwners(userID);
		} catch (NamingException | ObjectAlreadyExsistsException
			| IncorrectObjectException ex1) {
		    throw new InternalErrorException(
			    "Fatal: the user could not be registered to the system (which is required to process requests): "
				    + ex.getMessage());
		} catch (ObjectNotFoundException ex1) {
		    throw new InternalErrorException(
			    "Fatal: Registration was performed but user still cannot be found in the database: "
				    + ex.getMessage());
		}
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
	    LOG.log(Level.SEVERE, "Exception while processing client message:",
		    ex);
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
