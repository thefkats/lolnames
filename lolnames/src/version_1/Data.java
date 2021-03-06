package version_1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

public class Data {

	public static void main(String[] args) {
		Data d = new Data(3);
		WordList wl = d.addWordList("hello");
		wl.add("hello");
		wl.add("help");
		wl.add("hella");
		wl.add("abc");
		wl.add("Albania2314");
		// d.print();
		System.out.println(d.toString());
		wl.check();
	}

	private int threadsRunning;
	private int threadsLimit;
	private boolean backgroundCheckRunning;
	private WordList wordListHead;
	private MasterTree masterTree;

	public Data(int numThreads) {
		masterTree = new MasterTree();
		wordListHead = new WordList(null);
		backgroundCheckRunning = false;
		threadsLimit = numThreads;
	}

	public void run() {
		wordListHead.next.check();
	}

	public WordList getFiles() {
		return wordListHead.next;
	}

	public WordList addWordList(String path) {
		WordList wl = new WordList(path);
		if (wordListHead.next != null)
			wl.next = wordListHead.next;
		wordListHead.next = wl;
		return wl;
	}

	public boolean toggleBackgroundCheck() {
		backgroundCheckRunning = !backgroundCheckRunning;
		return backgroundCheckRunning;
	}

	protected class WordList implements Iterable {
		public WordList next;
		public String path;
		private int length;
		private Word head;
		private Word tail;
		public WordList(String path) {
			this.path = path;
			next = null;
			head = new Word(null);
			tail = new Word(null);
			head.next = tail;
			tail.prev = head;
			length = 0;
		}
		public int length() {
			return length;
		}
		public Word get(int index) {
			if (index < 0 || index >= length)
				throw new IllegalArgumentException("Tried getting index: " + index + ", but valid indexes are 0 through " + (length - 1));
			Word cur;
			if (index < length / 2) {
				cur = head;
				for (int i = 0; i <= index; i++)
					cur = cur.next;
			} else {
				cur = tail;
				for (int i = length - 1; i >= index; i--)
					cur = cur.prev;
			}
			return cur;
		}
		public void add(String word) { // TODO prevent duplicates
			if (word == null)
				throw new IllegalArgumentException("Null string");
			length++;
			Word w = new Word(word);
			w.next = tail;
			w.prev = tail.prev;
			tail.prev = w;
			w.prev.next = w;
			masterTree.add(w.word);
		}
		public void unlink(Word word) {
			if (word == head || word == tail)
				throw new IllegalArgumentException("Tried to unlink the head or tail.");
			length--;
			word.prev.next = word.next;
			word.next.prev = word.prev;
		}
		public void check() {
			Word cur = head.next;
			while (cur != tail) {
				while (threadsRunning >= threadsLimit)
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				WordCheck wc = new WordCheck(cur);
				wc.start();
				cur = cur.next;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		public void sortByExpired() {
			// TODO
		}
		public void sortByName() {
			// TODO
		}

		private class WordCheck extends Thread {
			private Word w;
			public WordCheck(Word word) {
				super();
				this.w = word;
			}

			@Override
			public void run() {
				threadsRunning++;
				System.out.println("- (" + threadsRunning + ") Checking: " + w.word);
				if (masterTree.search(w.word) == null)
					add(w.word);
				else
					w.info = masterTree.search(w.word);
				if (!w.info.isChecked)
					w.info = Data.this.check(w.word);
				System.out.println(" - " + w.word + ": " + w.info.toString());
				threadsRunning--;
			}
		}

		protected class Word {
			public Word next;
			public Word prev;
			public String word;
			public Info info;
			public Word(String word) {
				this.word = word;
				next = null;
				prev = null;
				Info results = masterTree.search(word);
				info = results == null ? new Info() : results;
			}
			public boolean isChecked() {
				return masterTree.search(word).isChecked;
			}
		}

		@Override
		public WordListIterator iterator() {
			return new WordListIterator();
		}

		private class WordListIterator implements Iterator {
			private int index;
			private Word cur;

			public WordListIterator() {
				index = 0;
				cur = head.next;
			}

			@Override
			public boolean hasNext() {
				return cur.next != null;
			}

			@Override
			public Word next() {
				if (!hasNext())
					throw new IllegalStateException("Does not have next element.");
				index++;
				cur = cur.next;
				return cur;
			}

			public int getIndex() {
				return index;
			}
		}
	}

	protected class MasterTree implements Iterable {
		private Node root;
		private int length;

		public MasterTree() {
			root = new Node(null);
			length = 0;
		}

		public MasterTree(WordList wl) {
			root = null;
			length = 0;
			add(wl);
		}

		public void add(String word) {
			Node cur = getLast(word);
			int level = cur.toString().length();
			for (int i = level; i < word.length(); i++) {
				Node n = new Node(new Character(word.charAt(i)));
				n.parent = cur;
				if (cur.child == null)
					cur.child = n;
				else {
					Node curChild = cur.child;
					while (curChild.next != null)
						curChild = curChild.next;
					curChild.next = n;
					n.prev = curChild;
				}
				cur = n;
			}
			length++;
			cur.info = new Info();
		}

		public void add(WordList wl) {
			WordList.WordListIterator iter = wl.iterator();
			while (iter.hasNext())
				add(iter.next().word);
		}

		public Info search(String word) {
			if (word == null)
				return null;
			Node last = getLast(word);
			if (word.equals(last.toString()))
				return last.info;
			return null;
		}

		private Node getLast(String word) {
			if (word == null)
				return root;
			Node cur = root;
			for (int i = 0; i < word.length(); i++) {
				String letter = word.charAt(i) + "";
				Node child = cur.child;
				while (child != null && !child.c.toString().equals(letter)) {
					child = child.next;
				}
				if (child == null)
					return cur;
				cur = child;
			}
			return cur;
		}

		public void backgroundCheck() {
			if (threadsRunning >= threadsLimit - 1)
				return;
			new Thread() {
				public void run() {
					threadsRunning++;
					while (backgroundCheckRunning) {
						MasterTreeIterator iter = masterTree.iterator();
						while (iter.hasNext()) {
							Node n = iter.next();
							if (!n.isChecked())
								n.info = check(n.toString());
						}
					}
					threadsRunning--;
				}
			}.start();
		}

		private class Node {
			public Node parent;
			public Node child;
			public Node next;
			public Node prev;
			public Character c;
			private Info info;
			public Node(Character c) {
				this.c = c;
				parent = null;
				child = null;
				next = null;
				prev = null;
				info = null;
			}

			public boolean isChecked() {
				return info.isChecked;
			}

			public boolean isTaken() {
				return info.isTaken;
			}

			/**
			 * Returns the word represented by this node.
			 */
			public String toString() {
				String s = "";
				Node cur = this;
				while (cur != root) {
					s = cur.c.toString() + s;
					cur = cur.parent;
				}
				return s;
			}
		}

		@Override
		public MasterTreeIterator iterator() {
			return new MasterTreeIterator();
		}

		private class MasterTreeIterator implements Iterator {
			private int index;
			private Node cur;

			public MasterTreeIterator() {
				cur = root;
				while (cur != null && cur.info == null)
					cur = cur.child;
				index = 0;
			}

			@Override
			public boolean hasNext() {
				return index < length;
			}

			@Override
			public Node next() {
				if (!hasNext() || cur == null)
					throw new IllegalStateException("Does not have next element.");
				if (index == 0) {
					index++;
					return cur;
				}
				index++;
				if (cur.child != null)
					cur = cur.child;
				else if (cur.next != null)
					cur = cur.next;
				else {
					while (cur.next == null) {
						cur = cur.parent;
					}
					cur = cur.next;
				}
				while (cur.info == null)
					cur = cur.child;

				return cur;
			}

			public int getIndex() {
				return index;
			}
		}
	}

	protected class Info {
		public boolean isChecked;
		public boolean isTaken;
		public String expires;
		public Info() {
			isChecked = false;
			isTaken = false;
			expires = null;
		}
		public String toString() {
			return isChecked ? isTaken ? "Name taken, expires: " + epochToDate(expires) : "Name available" : "Not checked.";
		}
	}

	public void print() {
		MasterTree.Node cur = masterTree.root;
		printHelper(cur.child, "");
	}

	private void printHelper(MasterTree.Node n, String level) {
		if (n == null) {
			System.out.println(level + "nul");
			return;
		}
		System.out.println(level + n.c.toString());
		if (n.child != null)
			printHelper(n.child, level + "\t");
		if (n.next != null)
			printHelper(n.next, level);
	}

	public String toString() {
		Iterator iter = masterTree.iterator();
		String output = "";
		while (iter.hasNext()) {
			MasterTree.Node n = (MasterTree.Node) iter.next();
			String next = (n == null) ? null : n.toString();
			String toAdd = (next == null) ? "(error)\n" : next + "\n";
			output += toAdd;
		}
		return output;
	}

	
	
	private Info check(String word) { // TODO find limit for this site
		try {
			URL url = new URL("http://lolinactive.com/LoLInactive.php");
			String returnValue = "";
			while (returnValue.length() == 0) {
				try {
					URLConnection con = url.openConnection();
					HttpURLConnection http = (HttpURLConnection) con;
					http.setRequestMethod("POST");
					http.setDoOutput(true);
					Map<String, String> arguments = new HashMap<>();
					arguments.put("region", "na");
					arguments.put("summonerName", word);
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
						returnValue += scan.nextLine();
					scan.close();
					http.disconnect();
					
				} catch (Exception e) {
					e.printStackTrace();
					Thread.sleep(60000);
				}
			}
			Info info = new Info();
			info.isChecked = true;
			info.isTaken = !returnValue.contains(",\"ERROR\":");
			if (info.isTaken) {
				int loc = returnValue.indexOf("\"revisionDate\":") + 15;
				long i = Long.parseLong(returnValue.substring(loc, loc + 10));
				info.expires = "" + (i + 47335384);
			}
			// TODO account for different cleanup times for different summoner
			// levels
			return info;
		} catch (Exception e) {
			System.out.println("There was an error at word: " + word);
			e.printStackTrace();
		}
		return null;
	}

	public static String epochToDate(String s) {
		long epoch = Long.parseLong(s);
		return new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new java.util.Date(epoch * 1000));

	}

	// TODO try op.gg and lolking and other sites
	
	// private Info check(String word) {
	// URL site;
	// try {
	// site = new URL("http://lolnamecheck.jj.ai/main/check?username=" + word +
	// "&region_name=na&_=1491495037161");
	// BufferedReader in;
	// int count = 0;
	//
	// while (true) {
	// count++;
	// try {
	// in = new BufferedReader(new InputStreamReader(site.openStream()));
	//
	// String inputLine = "";
	// String toAdd;
	// while ((toAdd = in.readLine()) != null) {
	// inputLine += toAdd;
	// }
	// new Data(0);
	// Info info = new Info();
	// if (!inputLine.contains("is (probably) available!")) {
	// info.isChecked = true;
	// info.isTaken = true;
	// int loc = inputLine.indexOf("Cleanup date (if inactive): ") + "Cleanup
	// date
	// (if inactive): ".length();
	// info.expires = inputLine.substring(loc, loc + 10);
	// in.close();
	// return info;
	// } else {
	// info.isChecked = true;
	// info.isTaken = false;
	// info.expires = null;
	// in.close();
	// return info;
	// }
	//
	// } catch (IOException e) {
	// try {
	// TimeUnit.SECONDS.sleep(1);
	// } catch (InterruptedException e1) {
	// System.out.println("ERROR: sleep was interrupted, didn't check " + word);
	// }
	// System.out.println(word + " is sleeping " + count + " seconds...");
	// }
	// }
	// } catch (MalformedURLException e1) {
	// System.out.println("ERROR: programmer messed up or lolnamecheck.jj.ai
	// changed
	// url or internet is down");
	// }
	// throw new IllegalStateException("IDK how the program got here but it
	// did...");
	// }
}
