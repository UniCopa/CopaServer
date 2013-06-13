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

import unicopa.copa.base.com.request.AbstractRequest;
import unicopa.copa.base.com.request.AbstractResponse;
import unicopa.copa.base.com.exception.InternalErrorException;
import unicopa.copa.base.com.exception.PermissionException;
import unicopa.copa.base.com.exception.RequestNotPracticableException;
import unicopa.copa.base.com.request.TestRequest;
import unicopa.copa.base.com.request.TestResponse;
import unicopa.copa.server.CopaSystemContext;

/**
 * 
 * @author Felix Wiemuth
 */
public class TestRequestHandler extends RequestHandler {

    public TestRequestHandler(CopaSystemContext context) {
	super(context);
    }

    @Override
    public AbstractResponse handleRequest(AbstractRequest request, int userID)
	    throws RequestNotPracticableException, InternalErrorException,
	    PermissionException {
	TestRequest req = (TestRequest) request;
	if (req.getMagicNumber() == 42) {
	    throw new RequestNotPracticableException(
		    "Don´t know what to do with 42!?");
	} else if (req.getMagicNumber() == 0) {
	    throw new InternalErrorException(
		    "Haha maybe I try to devide by zero...");
	} else if (req.getMagicNumber() < 0) {
	    throw new PermissionException(
		    "You are not allowed to ask for negative events!");
	} else if (req.getMagicNumber() == 400) {
	    return null;
	}
	String answer;
	if (req.getMessage().equals("Who am I?")) {
	    answer = "You are ID " + userID;
	} else {
	    answer = "Your message was: " + req.getMessage();
	}
	return new TestResponse(req, answer);
    }
}
