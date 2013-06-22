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
package unicopa.copa.server.email;

/**
 * The Text-Patterns of the Template for E-Mail message bodies that should be
 * replaced by actual data.
 * 
 * @see unicopa.copa.server.email.EmailService.replaceTextPatterns
 * 
 * @author Philip Wendland
 */
public enum TextPatterns {
    _UPDATE_DATE, _CREATOR_NAME, _COMMENT, _LOCATION, _DATE, _SUPERVISOR, _EVENTGROUP_NAME, _EVENT_NAME
}
