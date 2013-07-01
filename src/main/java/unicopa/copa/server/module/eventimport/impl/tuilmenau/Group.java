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
package unicopa.copa.server.module.eventimport.impl.tuilmenau;

import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author Felix Wiemuth
 */
public class Group {
    private String degree;
    private String course;
    private int semester; // may be 0
    private String subgroup; // may be empty

    public Group() {
    }

    public List<String> toList() {
	List<String> path = new LinkedList<>();
	if (degree == "") {
	    path.add("Others");
	} else {
	    path.add(degree);
	}
	if (course == "") {
	    path.add("Others");
	} else {
	    path.add(course);
	}
	if (semester == 0) {
	    path.add("Others");
	} else {
	    path.add("Semester " + semester);
	}
	if (!(subgroup == null || subgroup.isEmpty())) {
	    path.add(subgroup);
	}
	return path;
    }

    public String getCompactForm() {
	StringBuilder sb = new StringBuilder();
	sb.append(degree).append("-").append(course).append("-")
		.append(semester);
	if (!subgroup.isEmpty()) {
	    sb.append("-").append(subgroup);
	}
	return sb.toString();
    }

    public String getDegree() {
	return degree;
    }

    public String getCourse() {
	return course;
    }

    public int getSemester() {
	return semester;
    }

    public String getSubgroup() {
	return subgroup;
    }
}
