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
package unicopa.copa.server.com.requestHandler.executor;

import java.util.Date;
import unicopa.copa.base.event.SingleEvent;
import unicopa.copa.base.event.SingleEventUpdate;
import unicopa.copa.server.CopaSystemContext;
import unicopa.copa.server.database.IncorrectObjectException;
import unicopa.copa.server.database.ObjectNotFoundException;

/**
 * Handles the introduction of SingleEventUpdates to the system.
 * It should be used by RequestHandlers.
 * @author Felix Wiemuth
 */
public class AddSingleEventUpdateExecutor extends AbstractExecutor {

    public AddSingleEventUpdateExecutor(CopaSystemContext context) {
        super(context);
    }
    
    public void addSingleEvent(SingleEvent singleEvent, String comment, int userID) throws ObjectNotFoundException, IncorrectObjectException {
            SingleEventUpdate update = new SingleEventUpdate(singleEvent, 0, new Date(), getContext().getDbservice().getUserName(userID), comment);
            insertUpdateAndNotify(update);
    }
    
    public void updateSingleEvent(int oldSingleEventID, SingleEvent newSingleEvent, String comment, int userID) throws ObjectNotFoundException, IncorrectObjectException {
        SingleEventUpdate update = new SingleEventUpdate(newSingleEvent, oldSingleEventID, new Date(), getContext().getDbservice().getUserName(userID), comment);
        insertUpdateAndNotify(update);
    }
    
    public void cancelSingleEvent(int oldSingleEventID, String comment, int userID) throws ObjectNotFoundException, IncorrectObjectException {
        SingleEventUpdate update = new SingleEventUpdate(null, oldSingleEventID, new Date(), getContext().getDbservice().getUserName(userID), comment);
        insertUpdateAndNotify(update);
    }
    
    private void insertUpdateAndNotify(SingleEventUpdate update) throws ObjectNotFoundException, IncorrectObjectException {
        getContext().getDbservice().insertSingleEventUpdate(update);
        getContext().getNotifier().notifyClients(update);
    }
    
}
