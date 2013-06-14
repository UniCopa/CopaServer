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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import unicopa.copa.base.com.request.AbstractRequest;
import unicopa.copa.base.com.request.AbstractResponse;
import unicopa.copa.base.com.exception.APIException;
import unicopa.copa.base.com.exception.InternalErrorException;
import unicopa.copa.base.com.exception.PermissionException;
import unicopa.copa.base.com.exception.RequestNotPracticableException;
import unicopa.copa.base.com.request.GetSingleEventRequest;
import unicopa.copa.base.com.request.GetSingleEventResponse;

/**
 * Simulate client requests to the service. All requests should be tested here.
 * 
 * @author Felix Wiemuth
 */
public class ClientSimulationTest {

    private CopaSystem system;

    public ClientSimulationTest() {
    }

    @Before
    public void setUp() {
	system = CopaSystem.getInstance();
	// TODO set up test database (or just use test database below,
	// but content must be known and guaranteed)
    }

    @After
    public void tearDown() {
    }

    /**
     * Test GetSingleEventRequest.
     * 
     * @throws Exception
     */
    @Test
    public void testGetSingleEventRequest() throws Exception {
	AbstractRequest req = new GetSingleEventRequest(1);
	String send = req.serialize();
	String recv = system.processClientMessage(send, "");
	AbstractResponse resp = AbstractResponse.deserialize(recv);
	GetSingleEventResponse response = (GetSingleEventResponse) resp;
	// TODO assert equals
    }

    @Test(expected = APIException.class)
    public void testWrongAPIUsage() throws Exception {
	String send = "Send something strange...";
	String recv = system.processClientMessage(send, "");
	AbstractResponse resp = AbstractResponse.deserialize(recv);
    }

    @Test(expected = PermissionException.class)
    public void testGetSingleEventRequestInsufficientPermission()
	    throws Exception {
	AbstractRequest req = new GetSingleEventRequest(-1);
	String send = req.serialize();
	String recv = system.processClientMessage(send, "");
	AbstractResponse resp = AbstractResponse.deserialize(recv);
	GetSingleEventResponse response = (GetSingleEventResponse) resp;
    }

    @Test(expected = RequestNotPracticableException.class)
    public void testGetSingleEventRequestWrongParameter() throws Exception {
	AbstractRequest req = new GetSingleEventRequest(42);
	String send = req.serialize();
	String recv = system.processClientMessage(send, "");
	AbstractResponse resp = AbstractResponse.deserialize(recv);
	GetSingleEventResponse response = (GetSingleEventResponse) resp;
    }

    @Test(expected = InternalErrorException.class)
    public void testGetSingleEventRequestInternalError() throws Exception {
	AbstractRequest req = new GetSingleEventRequest(0);
	String send = req.serialize();
	String recv = system.processClientMessage(send, "");
	AbstractResponse resp = AbstractResponse.deserialize(recv);
	GetSingleEventResponse response = (GetSingleEventResponse) resp;
    }
}