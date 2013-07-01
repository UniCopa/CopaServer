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
package unicopa.copa.server.module.eventimport.impl.tuilmenau.serialization;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import unicopa.copa.server.module.eventimport.impl.tuilmenau.CourseEvent;

/**
 * 
 * @author Felix Wiemuth
 */
public class CourseEventDeserializer implements JsonDeserializer<CourseEvent> {
    private static final SimpleDateFormat inputTimeFormat = new SimpleDateFormat(
	    "HH:mm:ss");

    @Override
    public CourseEvent deserialize(JsonElement json, Type typeOfT,
	    JsonDeserializationContext context) throws JsonParseException {
	JsonObject jo = json.getAsJsonObject();

	// standard deserialization
	String type = context.deserialize(jo.get("type"), String.class);
	Date date = context.deserialize(jo.get("date"), Date.class);
	String location = context.deserialize(jo.get("location"), String.class);
	List<Integer> groups = context.deserialize(jo.get("groups"),
		new TypeToken<List<Integer>>() {
		}.getType());

	// special deserialization
	Date durationAsDate;
	try {
	    durationAsDate = inputTimeFormat.parse(jo.get("duration")
		    .getAsString());
	} catch (ParseException ex) {
	    throw new RuntimeException(ex);
	}
	Calendar cal = Calendar.getInstance();
	cal.setTime(durationAsDate);
	int duration = cal.get(Calendar.MINUTE) + 60
		* cal.get(Calendar.HOUR_OF_DAY);
	return new CourseEvent(type, date, duration, location, groups);
    }
}
