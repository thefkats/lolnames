package takennames_v2;

import java.util.Iterator;
import java.util.List;

public class IDLinkedList implements Iterable {
	private Node start;
	private Node end;
	private int length;

	public IDLinkedList() {
		setup();
	}

	public IDLinkedList(List<Name> arr) {
		addAll(arr);
	}

	private void setup() {
		start = new Node(null);
		end = new Node(null);
		start.next = end;
		end.prev = start;
		length = 0;
	}

	public int size() {
		return length;
	}

	public boolean add(Name name) {
		Node n = new Node(name);
		n.prev = end.prev;
		n.next = end;
		length++;
		return true;
	}

	public boolean addAll(List<Name> names) {
		for (Name name : names) {
			if (!add(name))
				return false;
		}
		return true;
	}

	public Name get(int index) {
		Node cur = start;
		for (int i = 0; i < index; i++) {
			cur = cur.next;
		}

		return cur.data;
	}

	public Name[] getAll() {
		return get(0, length);
	}

	public Name[] get(int start, int end) {
		if (start < 0)
			start = 0;
		if (end > length)
			end = length;
		if (start > end)
			throw new IllegalArgumentException("Start must be smaller than end, was: (" + start + "," + end + ").");
		Name[] arr = new Name[end - start];
		Iterator<Name> iter = iterator();
		for (int i = 0; i < start; i++)
			iter.next();
		for (int i = 0; i < end - start - 1; i++) {
			if (!iter.hasNext())
				throw new IllegalStateException(
						"No more elements in the array (should have been caught in program). At index: " + i);
			arr[i] = iter.next();
		}
		return arr;
	}

	public boolean set(int index, Name name) {
		if (index < 0)
			throw new IndexOutOfBoundsException("Must be 0 or more. Was: " + index);
		while (length < index)
			add(null);
		Name n = get(index);
		n.setLastActive(name.getLastActive());
		n.setLastChecked(n.getLastChecked());
		n.setUsername(name.getUsername());
		return true;
	}

	public void clear() {
		setup();
	}

	@Override
	public Iterator<Name> iterator() {
		return new LinkedListIterator();
	}

	private class LinkedListIterator implements Iterator<Name> {
		private Node cur;

		public LinkedListIterator() {
			cur = start;
		}

		@Override
		public boolean hasNext() {
			return cur.next != end;
		}

		@Override
		public Name next() {
			if (hasNext())
				return (cur = cur.next).data;
			return null;
		}

	}

	private class Node {
		public Node next;
		public Node prev;
		public Name data;

		public Node(Name data) {
			this.data = data;
			next = null;
			prev = null;
		}
	}
}
