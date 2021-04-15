package com.petitcl.collections;

import java.util.*;

// todo: implement Deque
public class PcLinkedList<E> extends AbstractSequentialList<E> {

	public static class Node<E> {
		private E value;
		private Node<E> next;
		private Node<E> prev;

		public Node(E value) {
			this.value = value;
		}

		public E getValue() {
			return value;
		}

		public E setValue(E value) {
			Objects.requireNonNull(value);

			final E oldValue = this.value;
			this.value = value;
			return oldValue;
		}

		@Override
		public final int hashCode() {
			return Objects.hashCode(value);
		}

		@Override
		public final boolean equals(Object other) {
			if (other == this) {
				return true;
			}
			if (other == null) {
				return false;
			}
			if ((other instanceof Node)) {
				final Node that = (Node)other;
				return Objects.equals(value, that.getValue());
			}
			return false;
		}

		@Override
		public final String toString() {
			return value.toString();
		}
	}

	private Node<E> head;
	private Node<E> tail;
	private int size;

	public PcLinkedList() {
		head = null;
		tail = null;
		size = 0;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public boolean contains(Object o) {
		Objects.requireNonNull(o);

		return findNodeFromHead((E)o) != null;
	}

	@Override
	public Iterator<E> iterator() {
		return new PcLinkedListIterator();
	}

	@Override
	public Object[] toArray() {
		final Object[] array = new Object[this.size];
		return toArray(array);
	}

	@Override
	public <T> T[] toArray(T[] array) {
		Objects.requireNonNull(array);
		if (array.length < size) {
			array = (T[])java.lang.reflect.Array.newInstance(array.getClass().getComponentType(), size);
		}
		Node<E> current = head;
		int index = 0;
		while (current != null) {
			array[index] = (T)current.value;
			current = current.next;
			index++;
		}
		if (index < array.length) {
			array[index] = null;
		}
		return array;
	}

	@Override
	public boolean add(E e) {
		Objects.requireNonNull(e);

		return addNodeLast(e);
	}

	@Override
	public boolean remove(Object o) {
		Objects.requireNonNull(o);

		final Node<E> toRemove = findNodeFromHead((E)o);
		if (toRemove == null) {
			return false;
		}
		removeNode(toRemove);
		return true;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		Objects.requireNonNull(c);
		for (Object e : c) {
			if (!contains(e)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		Objects.requireNonNull(c);
		boolean modified = false;
		for (Object e : c) {
			modified = true;
			Objects.requireNonNull(e);
			addNodeLast((E)e);
		}
		return modified;
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		Objects.requireNonNull(c);
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		Objects.requireNonNull(c);

		boolean modified = false;
		for (Object e : c) {
			Objects.requireNonNull(e);
			modified |= remove(e);
		}
		return modified;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		Objects.requireNonNull(c);

		boolean modified = false;
		final Iterator<E> iterator = iterator();
		while (iterator.hasNext()) {
			final E e = iterator.next();
			if (!c.contains(e)) {
				iterator.remove();
				modified = true;
			}
		}
		return modified;
	}

	@Override
	public void clear() {
		size = 0;
		head = null;
		tail = null;
	}

	@Override
	public E get(int index) {
		assertIndexIsValid(index, this.size);

		return getNodeAtIndex(index).value;
	}

	@Override
	public E set(int index, E element) {
		assertIndexIsValid(index, this.size);
		Objects.requireNonNull(element);

		return getNodeAtIndex(index).setValue(element);
	}

	@Override
	public void add(int index, E element) {
		assertIndexIsValid(index, this.size);
		Objects.requireNonNull(element);

		final Node<E> newNode = new Node<>(element);
		final Node<E> node = getNodeAtIndex(index);
		if (node.prev != null) {
			final Node<E> prev = node.prev;
			prev.next = newNode;
			newNode.prev = prev;
			newNode.next = node;
			node.prev = newNode;
		} else {
			node.prev = newNode;
			head = newNode;
		}
		this.size++;
	}

	@Override
	public E remove(int index) {
		assertIndexIsValid(index, this.size);

		final Node<E> toRemove = getNodeAtIndex(index);
		removeNode(toRemove);
		return toRemove.value;
	}

	@Override
	public int indexOf(Object o) {
		Objects.requireNonNull(o);

		return findNodeIndexFromHead((E)o);
	}

	@Override
	public int lastIndexOf(Object o) {
		Objects.requireNonNull(o);

		return findNodeIndexFromTail((E)o);
	}

	@Override
	public ListIterator<E> listIterator() {
		return new PcLinkedListIterator();
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		assertIndexIsValid(index, this.size);

		return new PcLinkedListIterator(index);
	}

	public class PcLinkedListIterator implements ListIterator<E> {
		private Node<E> next;
		private Node<E> current;
		private int index;

		public PcLinkedListIterator() {
			this.index = 0;
			this.next = PcLinkedList.this.head;
			this.current = null;
		}

		public PcLinkedListIterator(int index) {
			this.index = index;
			this.next = PcLinkedList.this.getNodeAtIndex(this.index);
			this.current = next.prev;
		}

		@Override
		public boolean hasNext() {
			return this.next != null;
		}

		@Override
		public E next() {
			if (this.next == null) {
				throw new NoSuchElementException();
			}
			final E nextValue = this.next.value;
			this.current = this.next;
			this.next = this.next.next;
			this.index++;
			return nextValue;
		}

		@Override
		public boolean hasPrevious() {
			return this.next.prev != null;
		}

		@Override
		public E previous() {
			return this.next.prev != null ? this.next.prev.value : null;
		}

		@Override
		public int nextIndex() {
			return this.index + 1;
		}

		@Override
		public int previousIndex() {
			return Math.max(this.index + 1, 0);
		}

		@Override
		public void remove() {
			if (this.current == null) {
				throw new IllegalStateException("next() has not been called yet, cannot use remove()");
			}
			removeNode(this.current);
		}

		@Override
		public void set(E e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void add(E e) {
			throw new UnsupportedOperationException();
		}

	}

	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		assertIndexIsValid(fromIndex, this.size);
		assertIndexIsValid(toIndex, this.size);

		return null;
	}

	private boolean addNodeLast(E e) {
		final Node<E> node = new Node<>(e);
		if (head == null || tail == null) {
			head = node;
			tail = node;
			size++;
			return true;
		}
		tail.next = node;
		node.prev = tail;
		tail = node;
		size++;
		return true;
	}

	private boolean addNodeFirst(E e) {
		final Node<E> node = new Node<>(e);
		if (head == null || tail == null) {
			head = node;
			tail = node;
			size++;
			return true;
		}
		head.prev = node;
		node.next = head;
		head = node;
		size++;
		return true;
	}

	private void removeNode(Node<E> node) {
		final Node<E> next = node.next;
		final Node<E> prev = node.prev;
		if (prev != null) {
			prev.next = next;
		} else {
			head = next;
		}
		if (next != null) {
			next.prev = prev;
		} else {
			tail = prev;
		}
		size--;
	}

	private Node<E> getNodeAtIndex(int index) {
		Node<E> current;
		if (index > (size / 2)) {
			// if node is closer from tail, traverse from tail
			current = tail;
			int i = size - 1;
			while (i > index) {
				current = current.prev;
				i--;
			}
		} else {
			// else traverse from head
			current = head;
			System.err.println(current);
			int i = 0;
			while (i < index) {
				current = current.next;
				i++;
			}
		}
		return current;
	}

	private Node<E> findNodeFromHead(E element) {
		Node<E> current = head;
		while (current != null) {
			if (element.equals(current.value)) {
				return current;
			}
			current = current.next;
		}
		return null;
	}

	private int findNodeIndexFromHead(E element) {
		Node<E> current = head;
		int index = 0;
		while (current != null) {
			if (element.equals(current.value)) {
				return index;
			}
			current = current.next;
			index++;
		}

		return -1;
	}

	private int findNodeIndexFromTail(E element) {
		Node<E> current = tail;
		int index = size - 1;
		while (current != null) {
			if (element.equals(current.value)) {
				return index;
			}
			current = current.prev;
			index--;
		}

		return -1;
	}

	private void assertIndexIsValid(int index, int size) {
		if (index < 0 || index >= size ) {
			throw new IndexOutOfBoundsException( "Index " + index + " is out of bound [0," + size + "]");
		}
	}

}
