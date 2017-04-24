package version_3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

	public void check() {

	}

	public void check(int end) {

	}

	public void check(int start, int end) {

	}

}
