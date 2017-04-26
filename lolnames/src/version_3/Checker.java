package version_3;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Checker {
	private static long maxWaitTimeMs = 300000; // 60s wait time before throwing
	// an exception.
	
	public static String check(int i) {
		if (i == 0)
			return "0,-";
		try {
			String url = "http://www.lolking.net/summoner/na/" + i;
			URL obj = new URL(url);
			long curTime = System.currentTimeMillis();
			while (curTime + maxWaitTimeMs > System.currentTimeMillis()) {
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
					return i + "," + inputLine.substring(0, inputLine.indexOf('-')).trim();
				} catch (Exception e) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
			}
			throw new IllegalStateException("Site took too long to respond with index: " + i + ", waited: " + (maxWaitTimeMs / 1000.0) + " seconds");
		} catch (MalformedURLException e) {
			System.out.println("[---CRITICAL---] Failed: " + i);
			throw new IllegalStateException();
		}
	}

	public static String check(String name) {
		// TODO
		return null;
	}

}
