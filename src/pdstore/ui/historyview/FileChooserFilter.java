package pdstore.ui.historyview;

import java.io.File;
import javax.swing.filechooser.*;

/**
 * Filters the open file types. Used in Open file dialog.
 */
public class FileChooserFilter extends FileFilter 
{
	final String pds = "pds";
	
    //Accept all directories and pds files only
    public boolean accept(File f)
    {
        if (f.isDirectory())
        {
            return true;
        }
        String extension = getExtension(f);
        if (extension != null) 
        {
            if (extension.equals(pds))
            {
                return true;
            }
            else 
            {
            	return false;
            }
        }
        return false;
    }
    
    /**
     * The description shown in the open dialog
     */
    public String getDescription()
    {
        return "PD Store Files (*.pds)";
    }
    
    /**
     * Get a file's extension
     * @param f The file needs to get file extension
     * @return The file extension
     */
    private static String getExtension(File f) 
    {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }
}
