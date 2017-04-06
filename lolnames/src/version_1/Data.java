package version_1;

public class Data {

	private WordList wordListHead;
	private MasterTree masterTree;
	
	public Data() {
		wordListHead = null;
		masterTree = new MasterTree();

	}

	private class WordList {
		public WordList next;
		public WordList prev;
		public String path;
		private Word head;
		private Word tail;
		public WordList(String path) {
			this.path = path;
			next = null;
			prev = null;
			head = new Word(null);
			tail = new Word(null);
			head.next = tail;
			tail.prev = head;
		}
		public boolean loadWords() {
			// TODO make a thread
			return false;
		}
		public Word get(int index) {
			// TODO
			return null;
		}
		public void add(Word word) {
			// TODO
		}
		public void remove(Word word) {
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
	}

	private class MasterTree {
		private Node root;

		public MasterTree() {
			root = new Node(null);
		}

		public MasterTree(WordList wl) {
			root = null;
			add(wl);
		}

		public void add(String word) {
			// TODO
		}

		public void add(WordList wl) {
			// TODO
		}

		public Info search(String word) {
			// TODO
			return null;
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
		}
		
		private class Info {
			public boolean isChecked;
			public boolean isTaken;
			public Info() {
				isChecked = false;
				isTaken = false;
			}
		}
	}
}
