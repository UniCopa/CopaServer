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

import java.util.logging.Level;
import java.util.logging.Logger;
import unicopa.copa.base.UserRole;
import unicopa.copa.base.com.exception.InternalErrorException;
import unicopa.copa.base.com.exception.PermissionException;
import unicopa.copa.base.com.exception.RequestNotPracticableException;
import unicopa.copa.base.com.request.AbstractRequest;
import unicopa.copa.base.com.request.AbstractResponse;
import unicopa.copa.base.com.request.RemoveRoleFromUserRequest;
import unicopa.copa.base.com.request.RemoveRoleFromUserResponse;
import unicopa.copa.server.CopaSystemContext;
import unicopa.copa.server.database.IncorrectObjectException;
import unicopa.copa.server.database.ObjectNotFoundException;

/**
 * 
 * @author Felix Wiemuth
 */
public class RemoveRoleFromUserRequestHandler extends RequestHandler {

    public RemoveRoleFromUserRequestHandler(CopaSystemContext context) {
	super(context);
    }

    @Override
    public AbstractResponse handleRequest(AbstractRequest request, int userID)
	    throws PermissionException, RequestNotPracticableException,
	    InternalErrorException {
	RemoveRoleFromUserRequest req = (RemoveRoleFromUserRequest) request;

	int userToRemove;
	try {
	    userToRemove = getContext().getDbservice().getUserIDByEmail(
		    req.getUserEmail());
	} catch (ObjectNotFoundException ex) {
	    throw new RequestNotPracticableException(ex.getMessage());
	}

	if (userToRemove == userID) {
	    throw new RequestNotPracticableException(
		    "A user cannot remove a role from himself with this request.");
	}

	UserRole userToRemoveRole;
	try {
	    userToRemoveRole = getContext().getDbservice()
		    .getUsersRoleForEvent(userToRemove, req.getEventID());
	} catch (ObjectNotFoundException ex) {
	    throw new RequestNotPracticableException(ex.getMessage());
	}

	// Check whether the action to perform is a withdrawal (which it must
	// be)
	try {
	    if (!getContext().getDbservice().isAppointedBy(userToRemove,
		    userID, req.getEventID(), userToRemoveRole)) {
		throw new RequestNotPracticableException(
			"The user specified is not appointed with the given role.");
	    }
	} catch (ObjectNotFoundException ex) {
	    throw new RequestNotPracticableException(ex.getMessage());
	}

	// Set the role
	try {
	    getContext().getDbservice().setUserRoleForEvent(userToRemove,
		    req.getEventID(), UserRole.USER, userID);
	} catch (IncorrectObjectException | ObjectNotFoundException ex) {
	    throw new RequestNotPracticableException(ex.getMessage());
	}

	return new RemoveRoleFromUserResponse();
    }
}
