package takennames_v2;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;

public class TakenNames implements ISave {
	private IDLinkedList users;
	private int threadsRunning;
	private int threadsLimit;
	private Stat stats;
	private FileManager fm;

	public static void main(String[] args) {
		TakenNames tn = new TakenNames(100);
		tn.load();
		tn.run();
		System.out.println(tn.getStats());
	}

	public TakenNames() {
		setup(1);
	}

	public TakenNames(int threadsLimit) {
		setup(threadsLimit);
	}

	private void setup(int threadsLimit) {
		if (threadsLimit < 1)
			throw new IllegalArgumentException("TakenNames constructor must be more than 0.");
		users = new IDLinkedList();
		threadsRunning = 1;
		this.threadsLimit = threadsLimit;
		stats = new Stat();
		fm = new FileManager("");
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
		/**
		 * Time the last run call took.
		 */
		public long actualTime;

		public Stat() {
			timeRunning = 0;
			numChecked = 0;
		}

		public Stat(long timeRunning, int numChecked) {
			this.timeRunning = timeRunning;
			this.numChecked = numChecked;
			actualTime = 0;
		}

		public double getRate() {
			if (numChecked == 0)
				return 0;
			return (timeRunning / 1000.0) / numChecked;
		}

		public double getRealRate() {
			if (numChecked == 0)
				return 0;
			return numChecked / (actualTime / 1000.0);
		}

		public double getTimePer() {
			if (numChecked == 0)
				return 0;
			return numChecked / (timeRunning / 1000.0);
		}

		public String toBriefString() {
			DecimalFormat df = new DecimalFormat("#####0.0##");
			return "[Checked: " + numChecked + "]\t[Rate: " + df.format(getRealRate()) + " checks/second]\t[Time: "
					+ timeFormat(actualTime) + "]\t[Total: " + timeFormat(timeRunning) + "]";
		}

		public String toString() {
			return "Number of IDs checked: " + numChecked + "\nTime spent waiting: " + timeFormat(timeRunning)
					+ "\nAverage rate: " + getRate() + " checks/second\nTime per check: " + getTimePer()
					+ "s\nTime of the last run: " + timeFormat(actualTime);
		}
	}

	public static String timeFormat(long milliseconds) {
		double[] arr = new double[4];
		arr[0] = milliseconds / 1000.0;
		arr[1] = arr[0] / 60;
		arr[0] = arr[0] % 60;
		arr[2] = arr[1] / 60;
		arr[1] = arr[1] % 60;
		arr[3] = arr[2] / 24;
		arr[2] = arr[2] % 60;
		String[] arrString = { "s", "m", "h", "d" };

		int first = 0;
		for (int i = arr.length - 1; i > 0; i--)
			if (arr[i] >= 1)
				first = i;
		if (first != 0)
			arr[first] = (int) arr[first];
		DecimalFormat df = new DecimalFormat("#0.##");
		return df.format(arr[first]) + arrString[first]
				+ ((first == 0) ? "" : ", " + df.format(arr[first - 1]) + arrString[first - 1]);
	}

	/**
	 * Checks all names between 1 and 70,000,000 (all possible names). TODO
	 * check max-range when run.
	 */
	public void run() {
		run(users.size(), 70000000);
	}

	public void run(int numChecks) {
		run(users.size(), users.size() + numChecks);
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
		long startTime = System.currentTimeMillis();
		start = users.size();
		if (start > end)
			throw new IllegalArgumentException("Start number is greater than ending index: " + start + "," + end);
		System.out.print("[");
		for (int i = 0; i < (end - start) / threadsLimit; i++)
			System.out.print(" ");
		System.out.print("]\n[");

		Stat curStat = new Stat(stats.timeRunning, stats.numChecked);
		long curStatTime = System.currentTimeMillis();
		for (int i = start; i < end; i++) {
			if (i % 1000 == 0) {
				curStat.actualTime = System.currentTimeMillis() - curStatTime;
				curStatTime = System.currentTimeMillis();
				curStat.numChecked = stats.numChecked - curStat.numChecked;
				curStat.timeRunning = stats.timeRunning - curStat.timeRunning;
				System.out.println("[Number: " + i + "]\t" + curStat.toBriefString());
				curStat.numChecked = stats.numChecked;
				if (curStat.numChecked != 0) {
					fm.save(users.get(i - 1000, i - 1));
					System.out.println("[Save time: " + timeFormat((System.currentTimeMillis() - curStatTime)) + "]");
					curStatTime = System.currentTimeMillis();
				}
				System.out.print(" ");
			}
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
		stats.actualTime = System.currentTimeMillis() - startTime;
	}

	/**
	 * Returns a set of names and their ids.
	 * 
	 * @return list of names and their ids (not repeated)
	 */
	public IDLinkedList getUsers() {
		return users;
	}

	private void check(int i) {
		while (threadsRunning > threadsLimit) {
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
						users.add(new Name(i));
					else
						users.add(new Name(name, i));
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
					return inputLine.substring(0, inputLine.indexOf('-')).trim();
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

	public String getStats() {
		return stats.toString();
	}

	@Override
	public void save() {
		fm.save(users.getAll());
	}

	@Override
	public void load() {
		fm.loadAll();
	}

}
