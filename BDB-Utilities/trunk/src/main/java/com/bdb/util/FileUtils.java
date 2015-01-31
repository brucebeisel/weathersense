/* 
 * Copyright (C) 2015 Bruce Beisel
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
package com.bdb.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils
{
    /**
     * Copy a file to another
     * 
     * @param srcFile The file to copy
     * @param destFile The file where srcFile is copied
     * 
     * @exception IOException Thrown if either the source cannot be read or the destination cannot be written
     */
    public static void copyFile(File srcFile, File destFile) throws IOException
    {
        FileOutputStream os = null;
        try (FileInputStream is = new FileInputStream(srcFile)) {
            os = new FileOutputStream(destFile);
            byte[] buffer = new byte[1024];
            int bcount;
            while ((bcount = is.read(buffer)) > 0)
                os.write(buffer, 0, bcount);
        }
        finally {
            if (os != null)
	        os.close();
        }
    }

    /**
     * Remove a directory along with all of its contents. Note that the delete will continue even if a single file could not be deleted.
     * 
     * @param dir The directory to remove
     * @return True if the directory and its children were deleted
     */
    public static boolean removeDir(File dir) {
        boolean success = true;
	//
	// If this is not a directory then there is nothing to do
	//
	if (!dir.isDirectory())
	    return false;

	//
	// Get the list of files that are in this directory
	//
	File[] files = dir.listFiles();

        //
        // Delete all of the files. Directories cause recursive calls to this
        // method
        //
        for (File file : files) {
            if (file.isDirectory())
                success = success && removeDir(file);

            success = success && file.delete();
        }

        return success;
    }
}
