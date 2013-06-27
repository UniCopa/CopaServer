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
package unicopa.copa.server.module.eventimport.impl;

import java.util.ArrayList;
import java.util.List;

import unicopa.copa.server.database.IncorrectObjectException;
import unicopa.copa.server.database.ObjectNotFoundException;
import unicopa.copa.server.CopaSystemContext;
import unicopa.copa.server.GeneralUserPermission;
import unicopa.copa.server.module.eventimport.LimitedDatabaseAccess;

/**
 * TODO : proper doku
 * 
 * @author Nintaro
 * 
 */
public class TUIlmenauLimitedDatabaseAccess implements LimitedDatabaseAccess {

    private final CopaSystemContext context;

    public TUIlmenauLimitedDatabaseAccess(CopaSystemContext context) {
	this.context = context;
    }

    @Override
    public List<Integer> matchName(String personName,
	    GeneralUserPermission minimumRequiredPermission)
	    throws ObjectNotFoundException, IncorrectObjectException {
	if (personName.contains("Mitarbeiter"))
	    throw new ObjectNotFoundException("The given name " + personName
		    + " is ambiguos");
	String familyName = clearName(personName);
	List<Integer> usersWithPermission = context.getDbservice()
		.getUserByFamilyNameWithPermission(familyName,
			minimumRequiredPermission);
	return usersWithPermission;
    }

    /**
     * removes all prefix of the given name
     * 
     * @param personName
     * @return the familyName part of the given string
     */
    private String clearName(String personName) {
	String clearName = personName;
	List<String> prefix = new ArrayList<>();
	prefix.add("Frau ");
	prefix.add("Herr ");
	prefix.add("Dr. ");
	prefix.add("Priv.-Doz. ");
	prefix.add("Dipl.-Designer ");
	prefix.add("Prof. ");
	prefix.add("Dipl.-Art. ");
	prefix.add("M. Sc. ");
	prefix.add("Dipl.-Kfm. ");
	prefix.add("M. A. ");

	for (String pref : prefix) {
	    if (clearName.contains(pref))
		clearName = clearName.substring(clearName.lastIndexOf(pref)
			+ pref.length());
	}
	return clearName;
    }

}
