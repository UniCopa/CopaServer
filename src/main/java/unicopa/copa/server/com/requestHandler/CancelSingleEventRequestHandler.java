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
import unicopa.copa.base.com.exception.InternalErrorException;
import unicopa.copa.base.com.exception.PermissionException;
import unicopa.copa.base.com.exception.RequestNotPracticableException;
import unicopa.copa.base.com.request.AbstractRequest;
import unicopa.copa.base.com.request.AbstractResponse;
import unicopa.copa.base.com.request.CancelSingleEventRequest;
import unicopa.copa.base.com.request.CancelSingleEventResponse;
import unicopa.copa.server.CopaSystemContext;
import unicopa.copa.server.com.requestHandler.executor.AddSingleEventUpdateExecutor;
import unicopa.copa.server.database.IncorrectObjectException;
import unicopa.copa.server.database.ObjectNotFoundException;

/**
 * 
 * @author Felix Wiemuth
 */
public class CancelSingleEventRequestHandler extends RequestHandler {
    private AddSingleEventUpdateExecutor executor = new AddSingleEventUpdateExecutor(
	    getContext());

    public CancelSingleEventRequestHandler(CopaSystemContext context) {
	super(context);
    }

    @Override
    public AbstractResponse handleRequest(AbstractRequest request, int userID)
	    throws PermissionException, RequestNotPracticableException,
	    InternalErrorException {
	CancelSingleEventRequest req = (CancelSingleEventRequest) request;
	try {
	    checkEventPermission(userID, getContext().getDbservice()
		    .getSingleEvent(req.getSingleEventID()).getEventID(),
		    UserRole.RIGHTHOLDER);
	} catch (ObjectNotFoundException ex) {
	    throw new RequestNotPracticableException(ex.getMessage());
	}
	try {
	    executor.cancelSingleEvent(req.getSingleEventID(),
		    req.getComment(), userID);
	} catch (ObjectNotFoundException | IncorrectObjectException ex) {
	    throw new RequestNotPracticableException(ex.getMessage());
	}
	return new CancelSingleEventResponse();
    }
}
