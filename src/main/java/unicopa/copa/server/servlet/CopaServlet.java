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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import unicopa.copa.base.com.exception.APIException;
import unicopa.copa.base.com.exception.InternalErrorException;
import unicopa.copa.base.com.serialization.ServerSerializer;
import unicopa.copa.server.CopaSystem;
import unicopa.copa.server.GeneralUserPermission;

/**
 * This servlet receives all requests (AbstractRequest) from the clients, passes
 * them to the CopaSystem and sends the reponses back.
 * 
 * @author Felix Wiemuth
 */
public class CopaServlet extends HttpServlet implements Filter {

    private CopaSystem system;
    private List<String> permissions;
    private static final Logger LOG = Logger.getLogger(CopaServlet.class
	    .getName());
    private static final String PARAM_REQUEST = "req"; // the parameter name of
						       // the HTTP POST method
						       // which includes the
						       // client request
    private static final String CONTENT_TYPE = "text/plain;charset=UTF-8";

    @Override
    public void init() throws ServletException {
	system = CopaSystem.getInstance();
	try {
	    LOG.addHandler(new FileHandler(system.getContext()
		    .getLogDirectory().getCanonicalPath()
		    + "/copa-servlet.log", 10000000, 1, true));
	} catch (IOException | SecurityException ex) {
	    Logger.getLogger(CopaServlet.class.getName()).log(Level.SEVERE,
		    null, ex);
	}

	// determine permissions config
	File settingsDirectory = new File(this.system.getContext()
		.getSettingsDirectory(), "permissions");
	settingsDirectory.mkdirs();
	File permissionsFile = new File(settingsDirectory, "permissions.txt");
	try {
	    this.permissions = new ArrayList<>();
	    if (!permissionsFile.exists()) {
		File src = new File(this.getClass()
			.getResource("externalAddresses.txt").toURI());
		unicopa.copa.server.util.IOutils.copyFile(src, permissionsFile);
	    }
	    FileInputStream extAddrs = new FileInputStream(permissionsFile);
	    Scanner scn = new Scanner(new BufferedInputStream(extAddrs));
	    if (scn.hasNextLine())
		scn.nextLine();
	    if (scn.hasNextLine())
		scn.nextLine();
	    if (scn.hasNextLine())
		scn.nextLine(); // ignore first three lines
	    while (scn.hasNextLine()) {
		String nextRole = scn.nextLine();
		this.permissions.add(nextRole);
	    }

	} catch (FileNotFoundException ex) {
	    LOG.log(Level.SEVERE, null, ex);
	} catch (IOException | URISyntaxException ex) {
	    LOG.log(Level.SEVERE, null, ex);
	}

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
	String userName = request.getUserPrincipal().getName();
	String req = request.getParameter(PARAM_REQUEST);
	if (req == null) {
	    resp = ServerSerializer.serialize(new APIException(
		    "The HTTP POST request does not contain"
			    + " the required parameter \"req\"."
			    + " The request to the system must "
			    + " be the value of this parameter."));
	} else {
	    GeneralUserPermission userPermission = determineGeneralPermission(request);
	    resp = system.processClientMessage(req, userName, userPermission);
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

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
	    FilterChain chain) throws IOException, ServletException {
	try {
	    chain.doFilter(request, response);
	} catch (IOException | ServletException e) {
	    LOG.log(Level.SEVERE, "Internal Error.", e);
	    HttpServletResponse httpResponse = (HttpServletResponse) response;
	    httpResponse.getWriter().print(
		    ServerSerializer.serialize(new InternalErrorException(
			    "The server failed to handle the request: "
				    + e.getMessage())));
	}
    }

    public GeneralUserPermission determineGeneralPermission(
	    HttpServletRequest request) {
	for (String role : this.permissions) {
	    if (request.isUserInRole(role)) {
		return GeneralUserPermission.POSSIBLE_OWNER;
	    }
	}
	return GeneralUserPermission.NONE;
    }
}
