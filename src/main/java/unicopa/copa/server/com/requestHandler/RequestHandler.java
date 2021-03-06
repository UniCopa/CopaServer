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
import unicopa.copa.base.com.request.AbstractRequest;
import unicopa.copa.base.com.request.AbstractResponse;
import unicopa.copa.base.com.exception.InternalErrorException;
import unicopa.copa.base.com.exception.PermissionException;
import unicopa.copa.base.com.exception.RequestNotPracticableException;
import unicopa.copa.server.CopaSystemContext;
import unicopa.copa.server.database.ObjectNotFoundException;

/**
 * A RequestHandler handles a specific Request (subclass of AbstractRequest).
 * 
 * @author Felix Wiemuth
 */
public abstract class RequestHandler {

    private final CopaSystemContext context;

    public RequestHandler(CopaSystemContext context) {
	this.context = context;
    }

    public CopaSystemContext getContext() {
	return context;
    }

    /**
     * Execute the request of the user. When this method is called, it is
     * guaranteed that the user satisfies
     * 
     * @param request
     *            the request to be processed
     * @param userID
     *            the ID of the user who sent the request
     * @return
     * @throws PermissionException
     * @throws RequestNotPracticableException
     * @throws InternalErrorException
     */
    public abstract AbstractResponse handleRequest(AbstractRequest request,
	    int userID) throws PermissionException,
	    RequestNotPracticableException, InternalErrorException;

    /**
     * Check whether a user holds a specific role for a specific event.
     * 
     * @param userID
     *            the ID of the user
     * @param eventID
     *            the ID of the event
     * @param role
     *            the role for which to check
     * @throws RequestNotPracticableException
     * @throws PermissionException
     *             if the user doesn´t hold the specified role
     */
    public void checkEventPermission(int userID, int eventID,
	    UserRole requiredRole) throws RequestNotPracticableException,
	    PermissionException {
	UserRole hasRole;
	try {
	    hasRole = context.getDbservice().getUsersRoleForEvent(userID,
		    eventID);
	    if (hasRole.level() < requiredRole.level()) {
		StringBuilder text = new StringBuilder(
			"Not enough permissions to perform this operation at event with ID ")
			.append(eventID).append("(required: ")
			.append(requiredRole.toString()).append(" present: ")
			.append(hasRole.toString()).append(")");
		throw new PermissionException(text.toString());
	    }
	} catch (ObjectNotFoundException ex) {
	    throw new RequestNotPracticableException(ex.getMessage());
	}
    }

    public void checkPermission(int userID, UserRole requiredRole)
	    throws PermissionException, RequestNotPracticableException {
	try {
	    UserRole hasRole = context.getDbservice().getUserRole(userID);
	    if (hasRole.level() < requiredRole.level()) {
		StringBuilder text = new StringBuilder(
			"Not enough permissions to perform this operation (required: ")
			.append(requiredRole.toString()).append(" present: ")
			.append(hasRole.toString()).append(")");
		throw new PermissionException(text.toString());
	    }
	} catch (ObjectNotFoundException ex) {
	    throw new RequestNotPracticableException(ex.getMessage());
	}
    }
}
