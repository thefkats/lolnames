package version_3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileManager {

	public static void main(String[] args) {
		System.out.println(true == false);
	}
	
	private String dir = "takennames/";
	private TakenNames takennames;
	
	public FileManager() {

	}

	public void load() {

	}

	public void load(int i) {
		takennames.add(load(new File(dir + i)));
	}

	private List<Name> load(File file) {
		ArrayList<Name> names = new ArrayList<Name>();
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

	public void save(File file, List<Name> names) {
		try {
			// Could be more efficient if names and oldnames were arrays, but
			// would have to implement own methods?
			List<Name> oldnames = load(file);
			int index;
			for (Name n : names)
				if ((index = oldnames.indexOf(n)) == -1)
					oldnames.set(index, n);
				else
					oldnames.add(n);
			PrintWriter pw = new PrintWriter(file);
			for (Name name : names)
				pw.println(name.toString());
			pw.close();
		} catch (FileNotFoundException e) {
			throw new IllegalStateException("Could not save to file: " + file.getAbsolutePath());
		}
	}
}
