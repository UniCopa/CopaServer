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

import unicopa.copa.base.com.exception.InternalErrorException;
import unicopa.copa.base.com.exception.PermissionException;
import unicopa.copa.base.com.exception.RequestNotPracticableException;
import unicopa.copa.base.com.request.AbstractRequest;
import unicopa.copa.base.com.request.AbstractResponse;

import unicopa.copa.base.com.request.GetCategoriesResponse;

import unicopa.copa.base.event.CategoryNodeImpl;
import unicopa.copa.server.CopaSystemContext;
import unicopa.copa.server.database.ObjectNotFoundException;

/**
 * 
 * @author Philip
 */
public class GetCategoriesRequestHandler extends RequestHandler {

    public GetCategoriesRequestHandler(CopaSystemContext context) {
	super(context);
    }

    @Override
    public AbstractResponse handleRequest(AbstractRequest request, int userID)
	    throws PermissionException, RequestNotPracticableException,
	    InternalErrorException {
	try {
	    CategoryNodeImpl ct = super.getContext().getDbservice()
		    .getCategoryTree(0);
	    return new GetCategoriesResponse(ct);
	} catch (ObjectNotFoundException ex) {
	    throw new RequestNotPracticableException(ex.getMessage());

	}
    }

}
