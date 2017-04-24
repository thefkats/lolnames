package takennames_v2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class FileManager {
	public static String path = "";
	private ArrayList<FileSaving> savingFiles;
	private File takennames;

	public FileManager(String path) {
		takennames = new File(path + "takennames/");
		takennames.mkdirs();
		savingFiles = new ArrayList<FileSaving>();
	}

	public ArrayList<Name> loadAll() {
		ArrayList<Name> names = new ArrayList<Name>();
		for (File f : takennames.listFiles())
			names.addAll(load(f));
		return names;
	}

	public ArrayList<Name> load(File f) {
		ArrayList<Name> arr = new ArrayList<Name>();
		if (!f.exists())
			try {
				PrintWriter pw = new PrintWriter(f);
				pw.print("");
				pw.close();
				return arr;
			} catch (Exception e) {
				throw new IllegalStateException("Unable to create: " + f.getAbsolutePath());
			}
		try {
			Scanner scan = new Scanner(f);
			while (scan.hasNextLine())
				arr.add(Name.toName(scan.nextLine()));
		} catch (FileNotFoundException e) {
			throw new IllegalStateException(
					"I literally checked if the file exists 3 lines ago... whatever good luck fixing this.");
		}
		return arr;
	}

	public void save(Name[] names) {
		if (names == null)
			throw new IllegalArgumentException("Can not save a null array.");
		if (names.length == 0)
			throw new IllegalArgumentException("Can not save an empty array.");
		for (int i = 0; i < names.length; i++)
			if (names[i] == null)
				throw new IllegalArgumentException("Names in array can not be null. Was null at index: " + i);

		(new Thread() {
			public void run() {
				FileSaving fs = getSaving("" + (names[0].getId() / 1000));
				while (fs.isSaving)
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				fs.isSaving = true;
				PrintWriter pw;
				try {
					int index = 0;
					Name curName = names[index];
					while (index != names.length) {
						pw = new PrintWriter(fs.file);
						while (((curName.getId() / 1000) + "").equals(fs.file.getName())) {
							pw.println(curName.toString());
							index++;
							if (index == names.length)
								break;
							curName = names[index];
						}
						fs = getSaving("" + (curName.getId() / 1000));
						pw.close();
					}
				} catch (FileNotFoundException e) {
					throw new IllegalStateException("Could not create the file: " + fs.file.getAbsolutePath());
				}
				fs.isSaving = false;
			}
		}).start();
	}

	private FileSaving getSaving(String fileName) {
		for (FileSaving f : savingFiles)
			if (f.file.getName().equals(fileName))
				return f;
		FileSaving fs = new FileSaving(new File(path + "takennames/" + fileName));
		savingFiles.add(fs);
		return fs;
	}

	private class FileSaving {
		public boolean isSaving;
		public File file;

		public FileSaving(File file) {
			isSaving = false;
			this.file = file;
		}
	}

	public static void main(String[] args) {
		FileManager fm = new FileManager("");
		fm.loadAll();
		Name[] names = new Name[2000];
		for (int i = 0; i < names.length; i++) {
			names[i] = new Name("hi", i);
		}
		fm.save(names);
	}
}
