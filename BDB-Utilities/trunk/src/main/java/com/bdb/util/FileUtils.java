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
