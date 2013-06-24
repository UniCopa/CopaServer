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
package unicopa.copa.server.database.data.persistence;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.annotations.Param;

public interface UserSettingMapper {

    public HashSet<String> getUserGCMKey(@Param("userID") int userID);

    public List<Map<String, Integer>> getEventColors(@Param("userID") int userID);

    public HashSet<Integer> getSubscriptions(int userID);

    public Boolean getEmailNotification(int userID);

    public String getLanguage(int userID);

    public void deleteAllGCMKeys(@Param("userID") int userID);

    public void insertGCMKeys(@Param("gCMKeys") Set<String> gCMKey,
	    @Param("userID") int userID);

    public void updatePerson(@Param("language") String language,
	    @Param("eMailNotification") boolean eMailNotification,
	    @Param("userID") int userID);

    public void deleteAllSubscriptions(@Param("userID") int userID);

    public void insertSubscription(@Param("eventID") int eventID,
	    @Param("color") String color, @Param("userID") int userID);

    public void deleteSubscriptionLists();

}
