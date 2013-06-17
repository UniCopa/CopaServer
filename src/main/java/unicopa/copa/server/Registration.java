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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Hashtable;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import unicopa.copa.server.database.IncorrectObjectException;
import unicopa.copa.server.database.ObjectAlreadyExsistsException;

/**
 * This class handles the registration of users to the system.
 * 
 * @author Felix Wiemuth, Philip Wendland
 */
public class Registration {
    private CopaSystemContext copaCtx;

    private Properties ldapProps;

    public Registration(CopaSystemContext copaCtx) {
	this.copaCtx = copaCtx;

	// create/navigate to settings directoy
	File settingsDirectory = new File(this.copaCtx.getSettingsDirectory(),
		"ldap");
	settingsDirectory.mkdirs();

	this.ldapProps = new Properties();
	File ldapFile = new File(settingsDirectory, "registration.properties");
	if (!ldapFile.exists()) {
	    File src;
	    try {
		src = new File(this.getClass()
			.getResource("/ldap/registration.properties").toURI());
		unicopa.copa.server.util.IOutils.copyFile(src, ldapFile);
	    } catch (URISyntaxException | IOException ex) {
		Logger.getLogger(Registration.class.getName()).log(
			Level.SEVERE, null, ex);
	    }
	}
	try (BufferedInputStream stream = new BufferedInputStream(
		new FileInputStream(ldapFile))) {
	    ldapProps.load(stream);
	} catch (FileNotFoundException ex) {
	    Logger.getLogger(Registration.class.getName()).log(Level.SEVERE,
		    null, ex);
	} catch (IOException ex) {
	    Logger.getLogger(Registration.class.getName()).log(Level.SEVERE,
		    null, ex);
	}
    }

    /**
     * Register a client to the system.
     * 
     * @param username
     *            the username of the user to register
     * @return a message to be sent back to the client, indicating success
     *         ("OK") or failure
     */
    public String register(String username) {
	String userIdAttribute = this.ldapProps.getProperty("userIdAttribute");
	String userBaseDn = this.ldapProps.getProperty("userBaseDn");
	String url = this.ldapProps.getProperty("url");
	String initialCtxFactory = this.ldapProps
		.getProperty("initialCtxFactory");
	String emailAttrName = this.ldapProps.getProperty("emailAttrName");
	String givenNameAttrName = this.ldapProps
		.getProperty("givenNameAttrName");
	String surNameAttrName = this.ldapProps.getProperty("surNameAttrName");

	Hashtable env = new Hashtable(11);
	env.put(Context.INITIAL_CONTEXT_FACTORY, initialCtxFactory);
	env.put(Context.PROVIDER_URL, url);
	env.put(Context.SECURITY_PRINCIPAL, userBaseDn);
	try {
	    // Create initial context
	    DirContext ctx = new InitialDirContext(env);
	    String attrIDs[] = { emailAttrName, givenNameAttrName,
		    surNameAttrName };
	    StringBuilder objName = new StringBuilder();
	    objName.append(userIdAttribute).append("=").append(username)
		    .append(",");
	    objName.append(userBaseDn);

	    Attributes attr = ctx.getAttributes(objName.toString(), attrIDs);
	    NamingEnumeration ne = attr.getAll();

	    // we have to cut the first characters as we do not want the
	    // identifiers
	    String mailString = attr.get(emailAttrName).toString()
		    .substring(emailAttrName.length() + 2);
	    String gnString = attr.get(givenNameAttrName).toString()
		    .substring(givenNameAttrName.length() + 2);
	    String snString = attr.get(surNameAttrName).toString()
		    .substring(surNameAttrName.length() + 2);

	    // create user
	    this.copaCtx.getDbservice().insertPerson(username, gnString,
		    snString, mailString, "", "english", true);

	    // Close the context when we're done
	    ctx.close();
	    return "OK";
	} catch (NamingException ex) {
	    Logger.getLogger(Registration.class.getName()).log(Level.SEVERE,
		    null, ex);
	    return "FAILURE: LDAP error";
	} catch (ObjectAlreadyExsistsException ex) {
	    Logger.getLogger(Registration.class.getName()).log(Level.INFO,
		    null, ex);
	    return "FAILURE: user already existent";
	} catch (IncorrectObjectException ex) {
	    Logger.getLogger(Registration.class.getName()).log(Level.SEVERE,
		    null, ex);
	    return "FAILURE: incorrect data";
	}

    }

}
