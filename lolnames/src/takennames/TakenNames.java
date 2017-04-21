package takennames;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.Scanner;

public class TakenNames {
	private LinkedHashSet<Account> names;
	private int threadsRunning;
	private int threadsLimit;
	private Stat stats;
	private static String defaultFileName = "takennames.txt";

	public TakenNames() {
		setup(1);
	}

	public TakenNames(int threadsLimit) {
		setup(threadsLimit);
	}

	private void setup(int threadsLimit) {
		if (threadsLimit < 1)
			throw new IllegalArgumentException("TakenNames constructor must be more than 0.");
		names = new LinkedHashSet<Account>();
		threadsRunning = 1;
		this.threadsLimit = threadsLimit;
		stats = new Stat();
	}

	private class Stat {
		/**
		 * How long spent waiting on threads (connection speed) in millis.
		 */
		public long timeRunning;
		/**
		 * Number of id's checked so far.
		 */
		public int numChecked;
		public Stat() {
			timeRunning = 0;
			numChecked = 0;
		}
		public Stat(long timeRunning, int numChecked) {
			this.timeRunning = timeRunning;
			this.numChecked = numChecked;
		}
		public double checkRate() {
			return (timeRunning / 1000.0) / numChecked;
		}
		public String toString() {
			return "Time spent waiting: " + timeRunning + "\nNumber of id's checked: " + numChecked + "\nAverage rate: " + checkRate()
					+ " checks/second";
		}
	}

	public class Account {
		public String name;
		public int id;
		public Account(String name, int id) {
			this.name = name;
			this.id = id;
		}

		public boolean equals(Account a) {
			if (a == null)
				return false;
			if (a.id != id)
				return false;
			if (a.name == null)
				return name == null;
			if (!a.name.equals(name))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return name + "," + id;
		}
	}

	/**
	 * Checks all names between 1 and 70,000,000 (all possible names). TODO
	 * check max-range when run.
	 */
	public void run() {
		run(1, 70000000);
	}

	/**
	 * Checks id's between specified values (inclusive, exclusive)
	 * 
	 * @param start
	 *            starting number to be checked
	 * @param end
	 *            ending number to stop checking (not checked)
	 */
	public void run(int start, int end) {
		if (start < 1)
			start = 1;
		if (start > end)
			throw new IllegalArgumentException("Start number is greater than ending index: " + start + "," + end);
		System.out.print("[");
		for (int i = start; i < end / threadsLimit; i++)
			System.out.print(" ");
		System.out.print("]\r ");
		for (int i = start; i < end; i++) {
			boolean check = true;
			for (Account a : names)
				if (a.id == i)
					check = false;
			if (check)
				check(i);
			while (threadsRunning > threadsLimit)
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			if (i % threadsLimit == 0)
				System.out.print("-");
		}
		System.out.println();
		while (threadsRunning > 1)
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	}

	/**
	 * Returns a set of names and their ids.
	 * 
	 * @return list of names and their ids (not repeated)
	 */
	public LinkedHashSet<Account> getNames() {
		return names;
	}

	private void check(int i) {
		while (threadsRunning > threadsLimit) {
			System.out.println(threadsRunning + "," + threadsLimit);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		threadsRunning++;
		(new Thread() {
			public void run() {
				long startTime = System.currentTimeMillis();
				String name;
				if ((name = checkHelper(i)) != null)
					if (name.length() > 16)
						names.add(new Account("-", i));
					else
						names.add(new Account(name, i));
				threadsRunning--;

				stats.timeRunning += System.currentTimeMillis() - startTime;
				stats.numChecked++;
			}
		}).start();
	}

	private String checkHelper(int i) {
		try {
			String url = "http://www.lolking.net/summoner/na/" + i;
			URL obj = new URL(url);
			while (true) {
				try {
					HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
					conn.setReadTimeout(5000);
					conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
					conn.addRequestProperty("User-Agent", "Mozilla");
					conn.addRequestProperty("Referer", "google.com");
					boolean redirect = false;
					// normally, 3xx is redirect
					int status = conn.getResponseCode();
					if (status != HttpURLConnection.HTTP_OK) {
						if (status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM
								|| status == HttpURLConnection.HTTP_SEE_OTHER)
							redirect = true;
					}
					if (redirect) {
						// get redirect url from "location" header field
						String newUrl = conn.getHeaderField("Location");
						// get the cookie if need, for login
						String cookies = conn.getHeaderField("Set-Cookie");
						// open the new connnection again
						conn = (HttpURLConnection) new URL(newUrl).openConnection();
						conn.setRequestProperty("Cookie", cookies);
						conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
						conn.addRequestProperty("User-Agent", "Mozilla");
						conn.addRequestProperty("Referer", "google.com");
					}

					BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
					String inputLine;
					while ((inputLine = in.readLine()) != null) {
						if (inputLine.contains("<title>"))
							break;
					}
					in.close();
					inputLine = inputLine.substring(inputLine.indexOf("<title>") + 7);
					return inputLine.substring(0, inputLine.indexOf(' '));
				} catch (Exception e) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
			}
		} catch (MalformedURLException e) {
			System.out.println("[---CRITICAL---] Failed: " + i);
			throw new IllegalStateException();
		}
	}

	/**
	 * Load file from default directory (file name is "takennames.txt").
	 */
	public void load() {
		load("");
	}

	public String getStats() {
		return stats.toString();
	}

	/**
	 * Load file to specified folder (file name is "takennames.txt").
	 * 
	 * @param path
	 *            path to directory, should end in "/"
	 */
	public void load(String path) {
		File file = new File(path + defaultFileName);
		if (!file.exists())
			try {
				PrintWriter pw = new PrintWriter(file);
				pw.print("");
				pw.close();
			} catch (FileNotFoundException e) {
				throw new IllegalStateException("Could not create " + defaultFileName + " at path: " + file.getAbsolutePath());
			}
		try {
			Scanner scan = new Scanner(file);
			while (scan.hasNextLine()) {
				Account a = stringToAccount(scan.nextLine());
				if (a != null)
					names.add(a);
			}
			scan.close();
		} catch (FileNotFoundException e) {
			throw new IllegalStateException("For some reason the previous if statement messed up... not sure what happened but goodluck.");
		}
	}

	private Account stringToAccount(String string) {
		Account a = new Account(string.substring(0, string.indexOf(',')).trim(), Integer.parseInt(string.substring(string.indexOf(',') + 1)));
		return a;
	}

	/**
	 * Save the current data to default file (name "takennames.txt").
	 */
	public void save() {
		File file = new File(defaultFileName);
		try {
			PrintWriter pw = new PrintWriter(file);
			for (Account a : getNames())
				pw.println(a.toString());
			pw.close();
		} catch (FileNotFoundException e) {
			throw new IllegalStateException("Could not create the " + defaultFileName + " file at: " + file.getAbsolutePath());
		}
	}

	public void clear() {
		File file = new File(defaultFileName);
		if (!file.exists())
			try {
				PrintWriter pw = new PrintWriter(file);
				pw.print("");
				pw.close();
			} catch (FileNotFoundException e) {
				throw new IllegalStateException("Could not create " + defaultFileName + " at path: " + file.getAbsolutePath());
			}
		try {
			PrintWriter pw = new PrintWriter(file);
			pw.print("");
			pw.close();
		} catch (FileNotFoundException e) {
			throw new IllegalStateException("Wasn't able to write to " + defaultFileName + " at path: " + file.getAbsolutePath());
		}

	}

	public static void main(String[] args) {
		TakenNames tn = new TakenNames(20);
		tn.load();
		tn.run(1, 200);
		tn.save();
		System.out.println(tn.getStats());
	}
}
