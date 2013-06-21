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

import java.io.File;
import java.util.logging.Handler;
import unicopa.copa.base.ServerInfo;
import unicopa.copa.server.database.DatabaseService;
import unicopa.copa.server.notification.Notifier;

/**
 * 
 * @author Felix Wiemuth
 */
public class CopaSystemContext {

    private final DatabaseService dbservice;
    private final Notifier notifier;
    private final File settingsDirectory;
    private final File logDirectory;
    private final Handler debugLogHandler;
    private final ServerInfo serverInfo;

    public CopaSystemContext(DatabaseService dbservice, Notifier notifier,
	    File settingsDirectory, File logDirectory, Handler debugLogHandler,
	    ServerInfo serverInfo) {
	this.dbservice = dbservice;
	this.notifier = notifier;
	this.settingsDirectory = settingsDirectory;
	this.logDirectory = logDirectory;
	this.debugLogHandler = debugLogHandler;
	this.serverInfo = serverInfo;
    }

    public DatabaseService getDbservice() {
	return dbservice;
    }

    public Notifier getNotifier() {
	return notifier;
    }

    public File getSettingsDirectory() {
	return settingsDirectory;
    }

    public File getLogDirectory() {
	return logDirectory;
    }

    public Handler getDebugLogHandler() {
	return debugLogHandler;
    }
}
