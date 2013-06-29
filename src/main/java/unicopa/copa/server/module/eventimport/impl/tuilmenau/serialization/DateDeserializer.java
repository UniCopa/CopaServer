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
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * @author Felix Wiemuth
 */
public class DateDeserializer implements JsonDeserializer<Date> {
    private static final SimpleDateFormat SDF = new SimpleDateFormat(
	    "yyyy-MM-dd HH:mm:ss");

    @Override
    public Date deserialize(JsonElement json, Type typeOfT,
	    JsonDeserializationContext context) throws JsonParseException {
	try {
	    return SDF.parse(json.getAsString());
	} catch (ParseException ex) {
	    throw new RuntimeException(ex);
	}
    }
}
