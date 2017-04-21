package takennames;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * Unneeded? :(
 * 
 * @author JamesBeetham
 *
 */
public class BinaryIDTree implements Set {
	private int length;
	private Node root;
	public BinaryIDTree() {
		root = new Node(1, null, -1);
	}

	@Override
	public int size() {
		return length;
	}

	@Override
	public boolean isEmpty() {
		return length == 0;
	}

	@Override
	public boolean contains(Object o) {
		return false;
	}

	public boolean contains(int i) {

		return false;
	}

	@Override
	public Iterator iterator() {
		return new BinaryIDTreeIterator();
	}

	@Override
	public Object[] toArray() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] toArray(Object[] a) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean add(Object e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean remove(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsAll(Collection c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addAll(Collection c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean retainAll(Collection c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeAll(Collection c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void clear() {
		// TODO
	}

	public String get(int index) {
		// TODO
		return null;
	}

	private class Node {
		private Node parent;
		private Node right;
		private Node left;
		private int id;
		private String name;
		private long lastActive;
		public Node(int id, String name, long lastActive) {
			parent = null;
			right = null;
			left = null;
			this.id = id;
			this.name = name;
			this.lastActive = lastActive;
		}
		@Override
		public String toString() {
			return null; // TODO
		}
		public Node getParent() {
			return parent;
		}
		public void setParent(Node parent) {
			this.parent = parent;
		}
		public Node getRight() {
			return right;
		}
		public void setRight(Node right) {
			this.right = right;
		}
		public Node getLeft() {
			return left;
		}
		public void setLeft(Node left) {
			this.left = left;
		}
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			if (name.length() < 3 || name.length() > 16)
				throw new IllegalStateException(
						"Illegal name length, must be between 3 and 16 charaters, was " + name.length() + "characters (" + name + ").");
			this.name = name;
		}
		public long getLastActive() {
			return (name == null) ? -1 : lastActive;
		}
		public void setLastActive(long lastActive) {
			this.lastActive = Math.max(-1, lastActive);
		}
	}

	private class BinaryIDTreeIterator implements Iterator {
		private int index;
		public BinaryIDTreeIterator() {
			index = 0;
		}
		@Override
		public boolean hasNext() {
			return index < length;
		}
		@Override
		public String next() {
			return get(index);
		}
	}
}
