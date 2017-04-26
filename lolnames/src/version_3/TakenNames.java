package version_3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TakenNames {

	public static void main(String[] args) {
		TakenNames tn = new TakenNames(100);
		tn.check();
		System.exit(0);
	}

	private Name[] usersArray;
	private ArrayList<ThreadTracker> threadTracker;
	private int threadsLimit;
	private boolean saving;
	private FileManager fm;

	public TakenNames() {
		setup(1);
	}

	public TakenNames(List<Name> names) {
		setup(1);
		set(names);
	}

	public TakenNames(int threadsLimit) {
		setup(threadsLimit);
	}

	public TakenNames(int threadsLimit, List<Name> names) {
		setup(threadsLimit);
		set(names);
	}

	private void setup(int threadsLimit) {
		usersArray = new Name[70000000];
		threadTracker = new ArrayList<ThreadTracker>();
		threadTracker.add(new ThreadTracker("main", false));
		threadTracker.add(new ThreadTracker("save", false));
		saving = true;
		saveThread(true);
		this.threadsLimit = threadsLimit;
		fm = new FileManager();
		set(fm.load());
	}

	public void set(Name name) {
		if (name == null)
			throw new IllegalArgumentException("Name was null.");
		if (name.getId() == -1)
			throw new IndexOutOfBoundsException("Name id was negative: " + name.getId());
		// TODO get id when only have username
		usersArray[name.getId()] = name;
	}

	public void set(List<Name> names) {
		for (Name n : names)
			set(n);
	}

	public List<Name> get() {
		// TODO convert this to the get(int, int)
		ArrayList<Name> names = new ArrayList<Name>();
		for (Name name : usersArray) {
			if (name == null)
				break;
			names.add(name);
		}
		return names;
	}

	public List<Name> get(int start, int end) {
		ArrayList<Name> names = new ArrayList<Name>();
		for (int i = start; i < end; i++)
			names.add(usersArray[i]);
		return names;
	}

	public Name[] getArray() {
		return usersArray;
	}

	private class ThreadTracker {
		public String name;
		public boolean completable;

		public ThreadTracker(String name, boolean completable) {
			this.name = name;
			this.completable = completable;
		}

		@Override
		public boolean equals(Object o) {
			if (o == null || o.getClass() != this.getClass())
				return false;
			ThreadTracker t = (ThreadTracker) o;
			return t.name.equals(name) && t.completable == completable;
		}
	}

	public int getThreadsRunning() {
		int count = 0;
		ArrayList<ThreadTracker> tempArray = new ArrayList<ThreadTracker>(threadTracker);
		if (tempArray.size() != 0)
			for (ThreadTracker t : tempArray)
				if (t != null)
					if (t.completable)
						count++;
		return count;
	}

	/**
	 * Checks all names in order starting with all unchecked.
	 */
	public void check() {
		int start = 0;
		while (usersArray[start] != null && usersArray[start].isSaved())
			start++;
		check(start, usersArray.length);
	}

	/**
	 * Check all unchecked id's through id number.
	 * 
	 * @param end
	 *            id to check through, can not be less than 1 (exclusive)
	 */
	public void check(int end) {
		check(0, end);
	}

	/**
	 * Checks all unchecked id's beginning at start and finishing through end.
	 * 
	 * @param start
	 *            id to start with (inclusive)
	 * @param end
	 *            id to end with (exclusive)
	 */
	public void check(int start, int end) {
		if (start < 0)
			throw new IllegalArgumentException("Start must be 1 or more but was: " + start);
		if (end > usersArray.length)
			throw new IllegalArgumentException("End must be less than the length of users (users length: "
					+ usersArray.length + "), given end: " + end);
		if (end < start)
			throw new IllegalArgumentException("End must be greater than start. Was: " + end + ", start was: " + start);
		for (int i = start; i < end; i++) {
			if (usersArray[i] == null || !usersArray[i].isSaved() || usersArray[i].getUname() == null) {
				while (getThreadsRunning() >= threadsLimit)
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				if (i % 100 == 0)
					System.out.println("Checking: " + (i / 1000) + "." + (i % 1000 / 100));
				if (!threadTracker.add(new ThreadTracker("Checking: " + i, true)))
					throw new IllegalStateException("Couldn't add threadTracker...");
				usersArray[i] = null;
				checkId(i);
			}
		}

		while (getThreadsRunning() != 0) {
			System.out.print(getThreadsRunning() + " left... ");
			if (getThreadsRunning() < 10)
				for (ThreadTracker t : threadTracker)
					if (t.completable)
						System.out.print(t.name + ", ");
			System.out.println();
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Saving...");
		fm.save(get(start, end));
		saving = false;
	}

	private void checkId(int i) {
		(new Thread() {
			public void run() {
				Name name = new Name(Checker.check(i));
				usersArray[name.getId()] = name;
				if (threadTracker.indexOf(new ThreadTracker("Checking: " + i, true)) != -1)
					threadTracker.remove(threadTracker.indexOf(new ThreadTracker("Checking: " + i, true)));
			}
		}).start();
	}

	public void saveThread(boolean on) {
		saving = on;
		saveThread();
	}

	private void saveThread() {
		(new Thread() {
			public void run() {
				int toSave = 0;
				while (saving) {
					boolean allDone = (usersArray.length < (toSave + 1) * 1000) ? false : true;
					if (allDone) {
						boolean allSaved = true;
						for (int i = toSave * 1000; i < (toSave + 1) * 1000; i++)
							if (usersArray[i] == null) {
								allDone = false;
								break;
							} else if (!usersArray[i].isSaved())
								allSaved = false;
						if (allDone && allSaved) {
							allDone = false;
							toSave++;
						}
					}
					if (allDone) {
						System.out.println("Save point: " + toSave);
						ArrayList<Name> names = new ArrayList<Name>();
						for (int i = toSave * 1000; i < (toSave + 1) * 1000; i++)
							names.add(usersArray[i]);
						fm.save(names);

						ArrayList<ThreadTracker> toRemove = new ArrayList<ThreadTracker>();
						for (ThreadTracker t : threadTracker) {
							int id = Integer.parseInt(t.name.substring(10).trim());
							if (id / 1000 == toSave)
								toRemove.add(t);
						}
						for (ThreadTracker t : threadTracker) {
							threadTracker.remove(t);
						}
						toSave++;
					}

					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
		threadTracker.remove(new ThreadTracker("save", false));
	}

	private class Stat {
		public int count;
		public long timeTaken;

		public Stat() {
			count = 0;
			timeTaken = 0;
		}
	}
}
