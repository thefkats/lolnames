package version_1;

import java.util.Iterator;

public class Data {

	private boolean backgroundCheckRunning;
	private WordList wordListHead;
	private MasterTree masterTree;

	public Data() {
		wordListHead = null;
		masterTree = new MasterTree();
		backgroundCheckRunning = true;
	}

	public WordList addWordList(String path) {
		WordList wl = new WordList(path);
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
		public void add(String word) {
			if (word == null)
				throw new IllegalArgumentException("Null string");
			length++;
			Word w = new Word(word);
			w.next = tail;
			w.prev = tail.prev;
			tail.prev = w;
			w.prev.next = w;
		}
		public void unlink(Word word) {
			if (word == head || word == tail)
				throw new IllegalArgumentException("Tried to unlink the head or tail.");
			length--;
			word.prev.next = word.next;
			word.next.prev = word.prev;
		}
		public void check() {
			// TODO
		}

		private class Word {
			public Word next;
			public Word prev;
			public String word;
			public Word(String word) {
				this.word = word;
				next = null;
				prev = null;
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

	private class MasterTree implements Iterable {
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
				cur = n;
			}
			
			
			length++;
		}

		public void add(WordList wl) {
			WordList.WordListIterator iter = wl.iterator();
			while (iter.hasNext())
				add(iter.next().word);
		}

		public Info search(String word) {
			Node last = getLast(word);
			if (word.equals(last.toString()))
				return last.info;
			return null;
		}
		
		private Node getLast(String word) {
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
			new Thread() {
				public void run() {
					while (backgroundCheckRunning) {
						MasterTreeIterator iter = masterTree.iterator();
						while (iter.hasNext()) {
							Node n = iter.next();
							if (!n.isChecked())
								check(n.toString());
						}
					}
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
				// if (!info.isChecked) TODO
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

		private class Info {
			public boolean isChecked;
			public boolean isTaken;
			public String expires;
			public Info() {
				isChecked = false;
				isTaken = false;
				expires = null;
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
				while (cur.info == null)
					cur = cur.child;
				index = 0;
			}

			@Override
			public boolean hasNext() {
				return index < length - 1;
			}

			@Override
			public Node next() {
				if (!hasNext())
					throw new IllegalStateException("Does not have next element.");
				index++;
				if (cur.child != null)
					cur = cur.child;
				else if (cur.next != null)
					cur = cur.next;
				else {
					while (cur.next == null)
						cur = cur.parent;
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
	private static String getName(String path) {
		// TODO
		return null;
	}

	private static boolean check(String word) {
		// TODO
		return false;
	}
}
