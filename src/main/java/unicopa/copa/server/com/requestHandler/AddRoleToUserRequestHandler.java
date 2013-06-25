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
package unicopa.copa.server.com.requestHandler;

import unicopa.copa.base.UserRole;
import unicopa.copa.base.UserSettings;
import unicopa.copa.base.com.exception.InternalErrorException;
import unicopa.copa.base.com.exception.PermissionException;
import unicopa.copa.base.com.exception.RequestNotPracticableException;
import unicopa.copa.base.com.request.AbstractRequest;
import unicopa.copa.base.com.request.AbstractResponse;
import unicopa.copa.base.com.request.AddRoleToUserRequest;
import unicopa.copa.base.com.request.AddRoleToUserResponse;
import unicopa.copa.server.CopaSystemContext;
import unicopa.copa.server.database.IncorrectObjectException;
import unicopa.copa.server.database.ObjectAlreadyExsistsException;
import unicopa.copa.server.database.ObjectNotFoundException;
import unicopa.copa.server.notification.NotificationService;

/**
 * 
 * @author Felix Wiemuth
 */
public class AddRoleToUserRequestHandler extends RequestHandler {

    public AddRoleToUserRequestHandler(CopaSystemContext context) {
	super(context);
    }

    @Override
    public AbstractResponse handleRequest(AbstractRequest request, int userID)
	    throws PermissionException, RequestNotPracticableException,
	    InternalErrorException {
	AddRoleToUserRequest req = (AddRoleToUserRequest) request;

	int userToAdd;
	try {
	    userToAdd = getContext().getDbservice().getUserIDByEmail(
		    req.getUserEmail());
	} catch (ObjectNotFoundException ex) {
	    throw new RequestNotPracticableException(ex.getMessage());
	}

	if (userToAdd == userID) {
	    throw new RequestNotPracticableException(
		    "A user cannot add roles for himself.");
	}

	UserRole currentRoleUserToAdd;
	try {
	    currentRoleUserToAdd = getContext().getDbservice()
		    .getUsersRoleForEvent(userToAdd, userID);
	} catch (ObjectNotFoundException ex) {
	    throw new RequestNotPracticableException(ex.getMessage());
	}

	// Check permissions
	UserRole required;
	switch (req.getRole()) {
	case RIGHTHOLDER:
	    required = UserRole.DEPUTY;
	    break;
	case DEPUTY:
	    required = UserRole.OWNER;
	    break;
	case OWNER:
	    required = UserRole.ADMINISTRATOR;
	    break;
	default:
	    throw new RequestNotPracticableException(
		    "The role to add must either be " + UserRole.RIGHTHOLDER
			    + ", " + UserRole.DEPUTY + " or " + UserRole.OWNER
			    + ".");
	}
	checkEventPermission(userID, req.getEventID(), required);

	// Check whether the user already got an equal or higher role
	if (req.getRole().level() < currentRoleUserToAdd.level()) {
	    throw new RequestNotPracticableException(
		    "The user specified already has a higher role then the role to add.");
	} else if (req.getRole().level() == currentRoleUserToAdd.level()) {
	    throw new RequestNotPracticableException(
		    "The user specified already has the given role.");
	}
	try {
	    // Set the role
	    getContext().getDbservice().setUserRoleForEvent(userToAdd,
		    req.getEventID(), req.getRole(), userID);
	    // notify
	    getContext()
		    .getNotifier()
		    .notifyClient(
			    NotificationService.NotificationEvent.USER_EVENT_PERMISSIONS_CHANGED,
			    userID);
	    // add subscription
	    UserSettings userSettings = getContext().getDbservice()
		    .getUserSettings(userID);
	    userSettings.addSubscription(req.getEventID());
	    getContext().getDbservice().updateUserSetting(userSettings, userID);
	} catch (IncorrectObjectException | ObjectNotFoundException
		| ObjectAlreadyExsistsException ex) {
	    throw new RequestNotPracticableException(ex.getMessage());
	}

	return new AddRoleToUserResponse();
    }
}
