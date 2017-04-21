package misc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.StringJoiner;

public class Test {
	private static String region;
	private static String urlString;
	public static void main(String[] args) throws IOException {
		region = "na";
		urlString = "http://lolinactive.com/LoLInactive.php";
		doStuff();
	}

	private static void doStuff() throws IOException {
		test3(100000);
		// Data d = new Data(3);
		// d.addWordList("/Users/JamesBeetham/Desktop");
		// d.run();
		// d.getFiles();
		// sendReq("asdf");
	}

	// http://www.lolking.net/search?name=asdf&region=NA

	public static String test3(int i) {
		try {
			String url = "http://www.lolking.net/summoner/na/" + i;
			URL obj = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
			conn.setReadTimeout(5000);
			conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
			conn.addRequestProperty("User-Agent", "Mozilla");
			conn.addRequestProperty("Referer", "google.com");
			System.out.println("Request URL ... " + url);
			boolean redirect = false;
			// normally, 3xx is redirect
			int status = conn.getResponseCode();
			if (status != HttpURLConnection.HTTP_OK) {
				if (status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM
						|| status == HttpURLConnection.HTTP_SEE_OTHER)
					redirect = true;
			}
			System.out.println("Response Code ... " + status);
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
				System.out.println("Redirect to URL : " + newUrl);
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
			e.printStackTrace();
		}
		return null;
	}
	
	
	public static void test() throws IOException {
		URL obj = new URL("http://mkyong.com");
		URLConnection conn = obj.openConnection();

		// get all headers
		Map<String, List<String>> map = conn.getHeaderFields();
		for (Map.Entry<String, List<String>> entry : map.entrySet()) {
			System.out.println(entry.getKey() + " : " + entry.getValue());
		}

		// get header by 'key'
		String server = conn.getHeaderField("Server");
	}

	public static void sendReq(String name) throws IOException {
		URL url = new URL("http://www.lolking.net/search?name=" + name + "&region=NA");
		URLConnection con = url.openConnection();
		HttpURLConnection http = (HttpURLConnection) con;
		http.setRequestMethod("POST"); // PUT is another valid option
		http.setDoOutput(true);
		Map<String, String> arguments = new HashMap<>();
		arguments.put("region", "na");
		arguments.put("summonerName", name); // This is a fake password
												// obviously
		StringJoiner sj = new StringJoiner("&");
		for (Map.Entry<String, String> entry : arguments.entrySet())
			sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "=" + URLEncoder.encode(entry.getValue(), "UTF-8"));
		byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
		int length = out.length;
		http.setFixedLengthStreamingMode(length);
		http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		http.connect();
		try (OutputStream os = http.getOutputStream()) {
			os.write(out);
		}
		Scanner scan = new Scanner(http.getInputStream());
		while (scan.hasNextLine())
			System.out.println(scan.nextLine());
	}

	// public static void sendReq(String name) throws IOException {
	// URL url = new URL(urlString);
	// URLConnection con = url.openConnection();
	// HttpURLConnection http = (HttpURLConnection)con;
	// http.setRequestMethod("POST"); // PUT is another valid option
	// http.setDoOutput(true);
	// Map<String,String> arguments = new HashMap<>();
	// arguments.put("region", "na");
	// arguments.put("summonerName", name); // This is a fake password obviously
	// StringJoiner sj = new StringJoiner("&");
	// for(Map.Entry<String,String> entry : arguments.entrySet())
	// sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "="
	// + URLEncoder.encode(entry.getValue(), "UTF-8"));
	// byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
	// int length = out.length;
	// http.setFixedLengthStreamingMode(length);
	// http.setRequestProperty("Content-Type",
	// "application/x-www-form-urlencoded; charset=UTF-8");
	// http.connect();
	// try(OutputStream os = http.getOutputStream()) {
	// os.write(out);
	// }
	// Scanner scan = new Scanner(http.getInputStream());
	// while(scan.hasNextLine())
	// System.out.println(scan.nextLine());
	// }

}
