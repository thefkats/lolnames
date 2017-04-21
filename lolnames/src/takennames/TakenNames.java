package takennames;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashSet;

public class TakenNames {
	private LinkedHashSet<Account> names;
	private int threadsRunning;
	private int threadsLimit;

	public TakenNames() {
		names = new LinkedHashSet<Account>();
		threadsRunning = 1;
		threadsLimit = 1;
	}

	public TakenNames(int threadsLimit) {
		if (threadsLimit < 1)
			throw new IllegalArgumentException("TakenNames constructor must be more than 0.");
		names = new LinkedHashSet<Account>();
		threadsRunning = 1;
		this.threadsLimit = threadsLimit;
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
	}

	public void run() {
		run(0, 100000);
	}

	public void run(int start, int end) {
		System.out.print("[");
		for (int i = 0; i < (end - start) / threadsLimit; i++)
			System.out.print(" ");
		System.out.print("]\n ");
		int count = 0;
		for (int i = start; i < end; i++) {
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
		while (threadsRunning > 1)
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	}

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
				//System.out.println("[" + threadsRunning + "] Checking: " + i);
				String name;
				if ((name = checkHelper(i)) != null)
					names.add(new Account(name, i));
				threadsRunning--;
				//System.out.println("\t Finished (" + ((System.currentTimeMillis() - startTime) / 1000.0) + "): " + i + ", " + name);
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

	public static void main(String[] args) {
		TakenNames tn = new TakenNames(20);
		tn.run(1, 100);
		for (Account a : tn.getNames())
			System.out.println(a.id + ": " + a.name);
	}
}
