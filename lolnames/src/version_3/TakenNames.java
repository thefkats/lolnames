package version_3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import takennames_v2.Name;

public class TakenNames {

	public static void main(String[] args) {
		TakenNames tn = new TakenNames();
		FileManager fm = new FileManager(tn);

	}

	private Name[] usersArray;
	private ArrayList<ThreadTracker> threadTracker;
	private int threadsLimit;

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
		this.threadsLimit = threadsLimit;
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
		return Arrays.asList(usersArray);
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
	}

	public int getThreadsRunning() {
		int count = 0;
		for (ThreadTracker t : threadTracker)
			if (t.completable)
				count++;
		return count;
	}

	/**
	 * Checks all names in order starting with all unchecked.
	 */
	public void check() {
		check(1, usersArray.length);
	}

	/**
	 * Check all unchecked id's through id number.
	 * 
	 * @param end
	 *            id to check through, can not be less than 1 (exclusive)
	 */
	public void check(int end) {
		check(1, end);
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
			while (getThreadsRunning() >= threadsLimit)
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			threadTracker.add(new ThreadTracker("Checking: " + i, true));
			(new Thread() {
				public void run() {

				}
			}).start();

		}
	}

	private void checkId(int i) {
		Name name = new Name(Checker.check(i));
		usersArray[name.getId()] = name;
	}
}
