package irp01;

import java.io.File;
import java.io.FileFilter;

public class FileTypeGuard implements FileFilter {
	// INPUT : String
	// OUTPUT : Boolean
	// DESCRIPTION : takes file path, return true if file is text or html
	public boolean accept(File pathname) {
		return pathname.getName().toLowerCase().endsWith(".txt") || pathname.getName().toLowerCase().endsWith(".html");
	}
}
