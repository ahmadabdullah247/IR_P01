package irp01;

import java.io.File;
import java.io.FileFilter;

public class FileTypeGuard implements FileFilter {
	public boolean accept(File pathname) {
		// Return true id txt or html file
		return pathname.getName().toLowerCase().endsWith(".txt") || pathname.getName().toLowerCase().endsWith(".htm")
				|| pathname.getName().toLowerCase().endsWith(".html");
//		if (pathname.getName().toLowerCase().endsWith(".txt") || pathname.getName().toLowerCase().endsWith(".htm")
//				|| pathname.getName().toLowerCase().endsWith(".html")) {
//			return true;
//		} else {
//			return false;
//		}
	}
}
