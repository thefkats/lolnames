package version_1;

public class Data {

	private WordList wordListHead;

	public Data() {
		wordListHead = null;

	}

	private class WordList {
		public WordList next;
		public WordList prev;
		public String name;
		private Word head;
		private Word tail;
		public WordList(String name) {
			this.name = name;
			next = null;
			prev = null;
			head = new Word(null);
			tail = new Word(null);
			head.next = tail;
			tail.prev = head;
		}
		public boolean loadWords() {
			// TODO
			return false;
		}
		public Word get(int index) {
			// TODO
			return null;
		}
		public boolean add(Word word) {
			// TODO
			return false;
		}
		public boolean remove(Word word) {
			// TODO
			return false;
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
		}
	}

	private class Node {
		public Node parent;
		public Node child;
		public Node next;
		public Node prev;
		public char c;
		private Info info;
		public Node(char c) {
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
