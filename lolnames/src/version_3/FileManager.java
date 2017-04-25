package version_3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileManager {

	public static void main(String[] args) {
		ArrayList<Name> names = new ArrayList<Name>();
		for (int i = 0; i < 1005; i++) {
			Name name = new Name(i, "hello");
			names.add(name);
		}
		FileManager fm = new FileManager();
		fm.save(names);
		// TakenNames tn = new TakenNames();
	}

	private String dir = "takennames/";

	public FileManager() {
		File directory = new File(dir);
		if (!directory.exists() || !directory.isDirectory())
			directory.mkdirs();
	}

	public List<Name> load() {
		ArrayList<Name> names = new ArrayList<Name>();
		File root = new File(dir);
		for (File f : root.listFiles())
			names.addAll(load(f));
		return names;
	}

	public List<Name> load(int i) {
		return (load(new File(dir + i)));
	}

	private List<Name> load(File file) {
		ArrayList<Name> names = new ArrayList<Name>();
		if (!file.exists()) {
			PrintWriter pw;
			try {
				pw = new PrintWriter(file);
				pw.print("");
				pw.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		try {
			Scanner scan = new Scanner(file);
			String info;
			while (scan.hasNextLine() && (info = scan.nextLine()).length() != 0)
				names.add(new Name(info));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return names;
	}

	public void save(List<Name> names) {
		if (names == null || names.isEmpty())
			throw new IllegalArgumentException("List names was null or empty.");
		ArrayList<Name> toAdd = new ArrayList<Name>();
		int cur = names.get(0).getId() / 1000;
		for (Name name : names) {
			if (name != null)
				if (name.getId() / 1000 == cur)
					toAdd.add(name);
				else {
					save(new File(dir + cur), toAdd);
					toAdd = new ArrayList<Name>();
					toAdd.add(name);
					cur++;
				}
			else
				System.out.println("was null");
		}
		save(new File(dir + cur), toAdd);
	}

	private void save(File file, List<Name> names) {
		try {
			// Could be more efficient if names and oldnames were arrays, but
			// would have to implement own methods?
			names = mergeNames(names, load(file));

			PrintWriter pw = new PrintWriter(file);
			for (Name name : names) {
				pw.println(name.toString());
				name.setSaved(true);
			}
			pw.close();
		} catch (FileNotFoundException e) {
			throw new IllegalStateException("Could not save to file: " + file.getAbsolutePath());
		}
	}

	public static List<Name> mergeNames(List<Name> arr1, List<Name> arr2) {
		int index = 0;
		ArrayList<Name> names = new ArrayList<Name>();
		for (int i = 0; i < 1000; i++) {
			int loc1 = getIndexOfId(i, arr1);
			int loc2 = getIndexOfId(i, arr2);
			if (loc1 == -1)
				if (loc2 == -1)
					names.add(new Name((arr1.get(0).getId() / 1000) * 1000 + i, "-"));
				else
					names.add(arr2.get(loc2));
			else
				names.add(arr1.get(loc1));
		}
		return names;
	}

	public static int getIndexOfId(int id, List<Name> names) {
		int start = id % 1000 < names.size() ? id % 1000 : 0;
		for (int i = start; i < names.size(); i++) {
			if (names.get(i).getId() == id)
				return i;
		}
		if (start != 0)
			for (int i = 0; i < start; i++) {
				if (names.get(i).getId() == id)
					return i;
			}
		return -1;
	}
}
