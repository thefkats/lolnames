package version_1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
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
				Data.WordList wl = data.addWordList(FileManager.getName(f.getPath()));
				try {
					Scanner scan = new Scanner(f);
					while (scan.hasNextLine()) {
						String word = scan.nextLine();
						if (checkWord(word))
							wl.add(word);
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				System.out.println("-Finished importing!");
			}
		}.start();
	}

	public void save() {
		System.out.print("Saving...");
		Data.WordList wl = data.getFiles();
		if (!new File("sources").exists())
			new File("sources").mkdir();
		while (wl != null) {
			try {
				PrintWriter pw = new PrintWriter(new File(wl.path));
				Data.WordList.Word word = wl.get(0);
				while (word != null) {
					pw.println(word.word + " " + word.info.toString());
					word = word.next;
				}
				pw.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			wl = wl.next;
		}
		System.out.println("Done Saving!");
	}

	protected static boolean checkWord(String word) {
		if (word.contains(" ") || word.contains("&"))
			return false;
		return true;
	}

	protected static String getName(String path) {
		String name = path.substring(path.lastIndexOf("/") + 1, (path.lastIndexOf(".") == -1) ? path.length() : path.lastIndexOf("."));
		return "sources/" + name + ".txt";
	}

}
