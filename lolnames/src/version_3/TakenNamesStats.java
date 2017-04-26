package version_3;

import java.util.ArrayList;
import java.util.List;

public class TakenNamesStats {

	public static void main(String[] args) {
		TakenNamesStats tns = new TakenNamesStats();
		tns.check();
	}

	private List<Name> usersArray;
	private List<Name> usersArrayChecked;
	private FileManager fm;
	private int threadsRunning;
	private int threadsLimit;

	public TakenNamesStats(List<Name> names) {
		setup(names);
	}

	public TakenNamesStats() {
		fm = new FileManager();
		setup(fm.load());
	}

	private void setup(List<Name> names) {
		usersArray = names;
		usersArrayChecked = new ArrayList<Name>();
		threadsRunning = 0;
		threadsLimit = 1;
	}

	public void check() {
		for (Name name : usersArray) {
			if (threadsRunning >= threadsLimit)
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			if (name != null) {
				checkHelper(name);
			}
			if (name.getId() % 1 == 0)
				System.out.println("Checking: " + (name.getId() / 1));
		}
		while (threadsRunning != 0)
			try {
				System.out.println(threadsRunning + " left...");
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		usersArray = usersArrayChecked;
		fm.save(usersArrayChecked);
	}

	private void checkHelper(Name name) {
		(new Thread() {
			public void run() {
				threadsRunning++;
				if (name == null) {
					threadsRunning--;
					return;
				}
				Name newName = Checker.check(name.getUname());
				if (newName == null) {
					threadsRunning--;
					return;
				}
				usersArrayChecked.add(new Name(newName, name));
				threadsRunning--;
			}
		}).start();
	}

}
