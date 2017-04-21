package takennames;

import java.lang.reflect.Array;
import java.util.List;

public class UserInfo {
	private User[] users;
	private int size;
	private static final int STARTING_SIZE = 128;

	public UserInfo() {
		users = new User[STARTING_SIZE];
		size = 0;
	}
	public UserInfo(List<String> list) {
		users = new User[list.size() * 2];
		size = 0;
		for (String s : list) {
			add(s);
		}
	}

	/**
	 * Adds specified string to the end of the array.
	 * 
	 * @param s
	 *            info for the user in format "[name], [id], [last active]",
	 *            null is "-", last active DNE is -1, can't be null
	 * @return true if successfully added
	 */
	public boolean add(String s) {
		if (s == null)
			return false;
		if (size >= users.length) {
			if (users.length == Integer.MAX_VALUE)
				throw new IllegalStateException("Dang, you filled an entire list... or there's a problem in UserInfo. Length: " + Integer.MAX_VALUE);
			User[] newArr = new User[Math.min(Math.abs(users.length * 2), Integer.MAX_VALUE)];
			for (int i = 0; i < users.length; i++) {
				newArr[i] = users[i];
			}
			users = newArr;
		}
		size++;
		users[size] = new User(s);
		return true;
	}

	public boolean set(int i, String s) {
		if (i < 0 || i > size)
			return false;
		if (s == null)
			return false;
		users[i] = new User(s);
		return true;
	}
	
	public User get(int i) {
		return users[i];
	}
	
	public User[] get() {
		return users;
	}
	
	@Override
	public String toString() {
		String output = "";
		for (User u : users) {
			output += u.toString() + "\n";
		}
		return output;
	}

	public void clear() {
		users = new User[STARTING_SIZE];
	}

	public int size() {
		return size;
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
			// TODO parse info
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
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
}
