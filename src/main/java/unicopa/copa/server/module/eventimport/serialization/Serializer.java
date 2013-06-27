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
package unicopa.copa.server.module.eventimport.serialization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Date;
import unicopa.copa.base.com.serialization.DateDeserializer;
import unicopa.copa.base.com.serialization.DateSerializer;

/**
 * 
 * @author Felix Wiemuth
 */
public class Serializer {
    private static final Gson gson = new GsonBuilder()
	    .registerTypeAdapter(Date.class, new DateSerializer())
	    .registerTypeAdapter(Date.class, new DateDeserializer()).create();

    // public static String serialize(Object object) {
    // return gson.toJson(object);
    // }

    public static Gson getGson() {
	return gson;
    }

}
