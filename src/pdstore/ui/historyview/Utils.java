package pdstore.ui.historyview;

/**
 * The Utils class contains helper methods
 */
public class Utils {

	/**
	 * Removes file extensions given a full filename
	 * @param filename The full filename that its extension needs to be removed
	 * @return A filename without extension
	 */
public static String RemoveFileExtension(String filename)
{
        if (filename == null)
            return null;
        int pos = filename.lastIndexOf(".");
        if (pos == -1)
            return filename;
        return filename.substring(0,pos);
  
}
}
