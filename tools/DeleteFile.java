package tools;

import java.io.File;

public class DeleteFile {
	public static void Delete(File file) {
		if (file.isFile()) {
			file.delete();
		}else if (file.isDirectory()) {
			File[] files=file.listFiles();
			if (files.length>0) {
				for (int i = 0; i < files.length; i++) {
					Delete(files[i]);
				}
			}
			file.delete();
		}
	}
}
