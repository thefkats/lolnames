package version_1;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class FileManager {
	public String path;
	public Data data;

	public FileManager(String path, Data data) {
		this.path = path;
		this.data = data;
	}

	public void importFile(File f) {
		new Thread() {
			public void run() {
				Data.WordList wl = data.addWordList(f.getPath());
				try {
					Scanner scan = new Scanner(f);
					while (scan.hasNextLine())
						wl.add(scan.nextLine());
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}.start();

	}

}
