package takennames;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class UserInfo implements ISave, Iterable<Object> {
	private User[] users;
	private static final int STARTING_SIZE = 0;
	public static final String FILENAME = "userinfo.txt";

	public UserInfo() {
		users = new User[STARTING_SIZE];
	}
	public UserInfo(List<String> list) {
		users = new User[list.size()];
		for (String s : list) {
			add(s);
		}
	}

	/**
	 * Adds specified string to the index of it's specified id. Eg: user with id
	 * of 12 get's put at index 12.
	 * 
	 * @param s
	 *            info for the user in format "[name], [id], [last active]",
	 *            null is "-", last active DNE is -1, can't be null
	 * @return true if successfully added
	 */
	public boolean add(String s) {
		if (s == null)
			return false;
		User u = new User(s);
		return add(u);
	}

	public boolean add(User u) {
		if (u.getId() >= users.length) {
			if (users.length == Integer.MAX_VALUE)
				throw new IllegalStateException("Dang, you filled an entire list... or there's a problem in UserInfo. Length: " + Integer.MAX_VALUE);
			User[] newArr = new User[Math.min(Math.abs(u.getId()) + 1, Integer.MAX_VALUE)];
			for (int i = 0; i < users.length; i++) {
				newArr[i] = users[i];
			}
			users = newArr;
		}
		users[u.getId()] = u;
		return true;
	}

	public User get(int i) {
		return users[i];
	}

	public User[] get() {
		return users;
	}

	public boolean contains(int i) {
		if (i >= users.length)
			return false;
		return users[i] != null;
	}

	public boolean contains(String name) {
		// TODO
		return false;
	}

	public boolean contains(User u) {
		// TODO
		return false;
	}

	public String toString(int i) {
		return users[i].toString();
	}

	@Override
	public String toString() {
		String output = "";
		Iterator iter = iterator();
		while (iter.hasNext())
			output += iter.next().toString() + "\n";
		return output;
	}

	public void clear() {
		users = new User[STARTING_SIZE];
	}

	public int size() {
		return users.length;
	}

	public class User {
		/**
		 * Null when id is taken with no name.
		 */
		private String name;
		private int id;
		/**
		 * -1 when not applicable.
		 */
		private long lastActive;
		public User(String name, int id, long lastActive) {
			this.name = name;
			this.id = id;
			this.lastActive = lastActive;
		}
		public User(String info) {
			if (info == null)
				throw new IllegalArgumentException("Info can not be null.");
			String[] parts = info.split(",");
			if (parts.length < 2)
				throw new IllegalArgumentException("Info must specify both a username and a number but was: " + info);
			setId(Integer.parseInt(parts[0].trim()));
			setName(parts[1]);
			setLastActive(((parts.length < 3) ? 0 : Long.parseLong(parts[2].trim())));
		}
		@Override
		public String toString() {
			return id + ", " + (name == null ? "-" : name) + ", " + lastActive;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			if (name == null || name.equals("-")) {
				this.name = null;
				return;
			}
			name = name.trim();
			if (name.length() > 16)
				throw new IllegalArgumentException(
						"Name must be 16 characters or shorter but had: " + name.length() + " characters. name was: " + name);
			this.name = name.trim();
		}
		public int getId() {
			return id;
		}
		public void setId(int id) {
			if (id < 0)
				throw new IllegalArgumentException("ID can't be negative (was: " + id + ").");
			this.id = id;
		}
		public long getLastActive() {
			if (name == null)
				return -1;
			return lastActive;
		}
		public void setLastActive(long lastActive) {
			if (lastActive < 0)
				lastActive = -1;
			this.lastActive = lastActive;
		}
	}

	@Override
	public boolean save(String path) {
		String lines = toString();
		File f = getFile(path);
		try {
			PrintWriter pw = new PrintWriter(f);
			pw.print(lines);
			pw.close();
		} catch (FileNotFoundException e) {
			throw new IllegalStateException("Could not write to file: " + f.getAbsolutePath());
		}
		return true;
	}
	
	@Override
	public void load(String path) {
		File f = getFile(path);
		if (!f.exists())
			try {
				PrintWriter pw = new PrintWriter(f);
				pw.print("");
				pw.close();
			} catch (FileNotFoundException e) {
				throw new IllegalStateException("Could not create file: " + f.getAbsolutePath());
			}
		else
			try {
				Scanner scan = new Scanner(f);
				while (scan.hasNextLine())
					add(scan.nextLine());
				scan.close();
			} catch (FileNotFoundException e) {
				throw new IllegalStateException("IDK how this was thrown... the file should've been created or thrown a different error...");
			}
	}
	@Override
	public void clear(String path) {
		File f = getFile(path);
		try {
			PrintWriter pw = new PrintWriter(f);
			pw.print("");
			pw.close();
		} catch (FileNotFoundException e) {
			throw new IllegalStateException("Could not create file: " + f.getAbsolutePath());
		}
	}
	private File getFile(String path) {
		return new File((path == null) ? FILENAME : (!path.endsWith(FILENAME)) ? path + FILENAME : path);
	}

	@Override
	public Iterator<Object> iterator() {
		return new UserInfoIterator();
	}
	private class UserInfoIterator implements Iterator<Object> {
		private int index;
		public UserInfoIterator() {
			index = 0;
		}
		@Override
		public boolean hasNext() {
			return index < users.length - 1;
		}
		@Override
		public User next() {
			index++;
			while (users[index] == null)
				index++;
			User u = get(index);
			return u;
		}
	}

	public static void main(String[] args) {
		UserInfo ui = new UserInfo();
		ui.load(null);
	}
}
