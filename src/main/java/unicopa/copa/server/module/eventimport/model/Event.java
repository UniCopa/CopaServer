//TODO LICENSE
package unicopa.copa.server.module.eventimport.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author Felix Wiemuth
 */
public class Event {

    private final static Gson gson = new Gson();
    private int id;
    private String lecturer;
    private String name;
    private List<Integer> groups;

    public Event(String json) {
    }

    public int getId() {
	return id;
    }

    public String getLecturer() {
	return lecturer;
    }

    public String getName() {
	return name;
    }

    public List<Integer> getGroups() {
	return groups;
    }

    @Override
    public int hashCode() {
	int hash = 7;
	hash = 19 * hash + this.id;
	hash = 19 * hash
		+ (this.lecturer != null ? this.lecturer.hashCode() : 0);
	hash = 19 * hash + (this.name != null ? this.name.hashCode() : 0);
	hash = 19 * hash + (this.groups != null ? this.groups.hashCode() : 0);
	return hash;
    }

    /**
     * NOTE: Not sure if Integer List comparison is correct. Research says it is
     * ok, extra test for large numbers, too.
     * 
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	final Event other = (Event) obj;
	if (this.id != other.id) {
	    return false;
	}
	if ((this.lecturer == null) ? (other.lecturer != null) : !this.lecturer
		.equals(other.lecturer)) {
	    return false;
	}
	if ((this.name == null) ? (other.name != null) : !this.name
		.equals(other.name)) {
	    return false;
	}
	if (this.groups != other.groups
		&& (this.groups == null || !this.groups.equals(other.groups))) {
	    return false;
	}
	return true;
    }

    public static Event fromJson(String json) {
	return gson.fromJson(json, Event.class);
    }

    public static List<Event> fromJsonList(String json) {
	Type collectionType = new TypeToken<Collection<Event>>() {
	}.getType();
	return gson.fromJson(json, collectionType);
    }
}
