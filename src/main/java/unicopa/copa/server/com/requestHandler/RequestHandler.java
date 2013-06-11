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
     * @return
     * @throws PermissionException
     * @throws RequestNotPracticableException
     * @throws InternalErrorException
     */
    public abstract AbstractResponse handleRequest(AbstractRequest request)
	    throws PermissionException, RequestNotPracticableException,
	    InternalErrorException;

    /**
     * Check whether a user holds a specific role for a specific event.
     * 
     * @param userID
     *            the ID of the user
     * @param eventID
     *            the ID of the event
     * @param role
     *            the role for which to check
     * @return true if the user holds the specified or any higher role
     */
    public boolean checkEventPermission(int userID, int eventID, UserRole role) {
	throw new UnsupportedOperationException();
    }
}
