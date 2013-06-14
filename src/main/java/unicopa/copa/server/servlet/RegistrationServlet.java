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
package unicopa.copa.server.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import unicopa.copa.server.CopaSystem;
import unicopa.copa.server.Registration;

/**
 * This servlet allows clients to register to the system to use the whole
 * functionality.
 * 
 * @author Felix Wiemuth
 */
public class RegistrationServlet extends HttpServlet {

    private Registration registration;
    private CopaSystem system;

    private static final String PARAM_PASSWORD = "password"; // the parameter
							     // name of the HTTP
							     // POST method
							     // which includes
							     // the password of
							     // the
							     // authenticated
							     // client which to
							     // register

    private static final String CONTENT_TYPE = "text/plain;charset=UTF-8";

    @Override
    public void init() throws ServletException {
	system = CopaSystem.getInstance();
	registration = new Registration(system.getContext().getDbservice());
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     * 
     * @param request
     *            servlet request
     * @param response
     *            servlet response
     * @throws ServletException
     *             if a servlet-specific error occurs
     * @throws IOException
     *             if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request,
	    HttpServletResponse response) throws ServletException, IOException {
	String resp;
	response.setContentType(CONTENT_TYPE);
	String username = request.getUserPrincipal().getName();
	String password = request.getParameter(PARAM_PASSWORD);
	if (password == null) {
	    resp = "Registration error: The HTTP POST request does not contain"
		    + " the required parameter \"password\".";
	} else {
	    resp = registration.register(username, password);
	}
	response.getWriter().print(resp);
    }

    /**
     * Returns a short description of the servlet.
     * 
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
	return "Short description";
    }
}
