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
package unicopa.copa.server.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * A collection of IO utility methods.
 * 
 * @author Philip Wendland
 */
public class IOutils {

    /**
     * Copies a file.
     * 
     * @param sourceFile
     *            source file
     * @param destFile
     *            destination file
     * @throws IOException
     */
    public static void copyFile(File sourceFile, File destFile)
	    throws IOException {
	if (!destFile.exists()) {
	    destFile.createNewFile();
	}

	FileChannel source = null;
	FileChannel destination = null;
	try {
	    source = new FileInputStream(sourceFile).getChannel();
	    destination = new FileOutputStream(destFile).getChannel();
	    destination.transferFrom(source, 0, source.size());
	} finally {
	    if (source != null) {
		source.close();
	    }
	    if (destination != null) {
		destination.close();
	    }
	}
    }

    /**
     * \brief Returns an array of Files ending with .txt from the given path
     * 
     * @param dirName
     *            the directory to scan
     * @return an array of Files from that directory ending with ".txt"
     */
    public static File[] FileFinder(File dir) {
	return dir.listFiles(new FilenameFilter() {
	    @Override
	    public boolean accept(File dir, String filename) {
		return filename.endsWith(".txt");
	    }
	});
    }

}
