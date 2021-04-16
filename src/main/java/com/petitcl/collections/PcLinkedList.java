package com.petitcl.collections;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

// todo: reimplement List<E> subList(int fromIndex, int toIndex);
// todo: reimplement Spliterator<E> spliterator();
// todo: reimplement void sort(Comparator<? super E> c);
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
		System.err.println("");
		System.err.println("PcLinkedList");
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
		System.err.println("add " + e);
		Objects.requireNonNull(e);

		addNodeLast(e);
		return true;
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
		System.err.println(" addAll " + 0 + " " + c);
		assertIndexIsValid(index, this.size + 1);
		Objects.requireNonNull(c);

		if (c.isEmpty()) {
			return false;
		}
		Node<E> listHead = null;
		Node<E> listTail = null;
		Node<E> prev = null;
		int i = 0;
		for (Object e : c) {
			Objects.requireNonNull(e);
			final Node<E> newNode = new Node<>((E)e);
			if (prev != null) {
				prev.next = newNode;
				newNode.prev = prev;
			}
			if (i == 0) {
				listHead = newNode;
			}
			if ((i + 1) == c.size()) {
				listTail = newNode;
			}
			i++;
			prev = newNode;
		}
		insertListAtIndex(index, listHead, listTail, i);
		return true;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		Objects.requireNonNull(c);

		boolean modified = false;
		final Iterator<E> iterator = iterator();
		while (iterator.hasNext()) {
			final E e = iterator.next();
			if (c.contains(e)) {
				iterator.remove();
				modified = true;
			}
		}
		return modified;
	}

	@Override
	public boolean removeIf(Predicate<? super E> filter) {
		Objects.requireNonNull(filter);

		boolean modified = false;
		final Iterator<E> iterator = iterator();
		while (iterator.hasNext()) {
			final E e = iterator.next();
			if (filter.test(e)) {
				iterator.remove();
				modified = true;
			}
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
	public void replaceAll(UnaryOperator<E> operator) {
		Objects.requireNonNull(operator);

		final ListIterator<E> iterator = listIterator();
		while (iterator.hasNext()) {
			final E e = iterator.next();
			iterator.set(operator.apply(e));
		}
	}

	@Override
	public void forEach(Consumer<? super E> action) {
		Objects.requireNonNull(action);

		for (E e : this) {
			action.accept(e);
		}
	}

	@Override
	public void clear() {
		this.size = 0;
		this.head = null;
		this.tail = null;
	}

	@Override
	public E get(int index) {
		assertIndexIsValid(index, this.size);


		Node<E> nodeAtIndex = getNodeAtIndex(index);
		if (nodeAtIndex == null) {
			return null;
		}
		return nodeAtIndex.value;
	}

	@Override
	public E set(int index, E element) {
		System.err.println("set " + index + " " + element);
		assertIndexIsValid(index, this.size);
		Objects.requireNonNull(element);

		return getNodeAtIndex(index).setValue(element);
	}

	@Override
	public void add(int index, E element) {
		System.err.println("add " + index + " " + element);
		System.err.println(getLayout());
		assertIndexIsValid(index, this.size + 1);
		Objects.requireNonNull(element);

		insertNodeAtIndex(index, element);
		System.err.println(getLayout());
	}

	@Override
	public E remove(int index) {
		System.err.println("remove " + index);
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
		assertIndexIsValid(index, this.size + 1);

		return new PcLinkedListIterator(index);
	}

	public class PcLinkedListIterator implements ListIterator<E> {

		private int previousIndex;
		private int nextIndex;
		private Node<E> previous;
		private Node<E> next;
		private Node<E> lastVisited;

		public PcLinkedListIterator() {
			this.previousIndex = -1;
			this.nextIndex = 0;
			this.previous = null;
			this.next = PcLinkedList.this.head;
			this.lastVisited = null;
		}

		public PcLinkedListIterator(int index) {
			this();
			for (int i = 0; i < index; i++) {
				next();
			}
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
			final E value = this.next.value;
			this.lastVisited = this.next;
			this.previous = this.next;
			this.next = this.next.next;
			this.previousIndex++;
			this.nextIndex++;
			return value;
		}

		@Override
		public boolean hasPrevious() {
			return this.previous != null;
		}

		@Override
		public E previous() {
			if (this.previous == null) {
				throw new NoSuchElementException();
			}
			final E value = this.previous.value;
			this.lastVisited = this.previous;
			this.next = this.previous;
			this.previous = this.previous.prev;
			this.previousIndex--;
			this.nextIndex--;
			return value;
		}

		@Override
		public int nextIndex() {
			return this.nextIndex;
		}

		@Override
		public int previousIndex() {
			return this.previousIndex;
		}

		@Override
		public void remove() {
			if (this.lastVisited == null) {
				throw new IllegalStateException();
			}
			if (this.lastVisited == this.next) {
				this.next = this.next.next;
				removeNode(this.lastVisited);
			} else {
				this.previous = this.previous.prev;
				removeNode(this.lastVisited);
				this.nextIndex--;
				this.previousIndex--;
			}
			this.lastVisited = null;
		}

		@Override
		public void set(E e) {
			if (this.lastVisited == null) {
				throw new IllegalStateException();
			}
			this.lastVisited.value = e;
		}

		@Override
		public void add(E e) {
			final Node<E> newNode = new Node<>(e);
			newNode.next = this.next;
			newNode.prev = this.previous;
			if (this.previous != null) {
				this.previous.next = newNode;
			}
			if (this.next != null) {
				this.next.prev = newNode;
			}
			PcLinkedList.this.size++;
			if (newNode.prev == null) {
				PcLinkedList.this.head = newNode;
			}
			if (newNode.next == null) {
				PcLinkedList.this.tail = newNode;
			}

			this.previous = newNode;
			this.previousIndex = this.nextIndex;
			this.nextIndex++;
			this.lastVisited = null;
		}
	}

	public String getLayout() {
		final StringBuilder sb = new StringBuilder();
		sb.append("size=").append(this.size).append("\n")
			.append("HEAD=").append(this.head).append("\n")
			.append("TAIL=").append(this.tail).append("\n");
		Node<E> current = this.head;
		int i = 0;
		while (current != null) {
			sb.append(i + "=" + current.value).append("\n");
			current = current.next;
			i++;
		}
		return sb.toString();
	}

	private Node<E> addNodeLast(E e) {
		final Node<E> node = new Node<>(e);
		if (head == null || tail == null) {
			head = node;
			tail = node;
			size++;
			return node;
		}
		tail.next = node;
		node.prev = tail;
		tail = node;
		size++;
		return node;
	}

	private Node<E> addNodeFirst(E e) {
		final Node<E> node = new Node<>(e);
		if (head == null || tail == null) {
			head = node;
			tail = node;
			size++;
			return node;
		}
		head.prev = node;
		node.next = head;
		head = node;
		size++;
		return node;
	}

	private Node<E> insertNodeAtIndex(int index, E element) {
		final Node<E> newNode = new Node<>(element);
		return insertListAtIndex(index, newNode, newNode, 1);
	}

	private Node<E> insertListAtIndex(int index, Node<E> listHead, Node<E> listTail, int listSize) {
		if (index == size) {
			System.err.println("index == size");
			if (this.tail != null) {
				this.tail.next = listHead;
			}
			listHead.prev = this.tail;
			this.tail = listTail;
		}
		if (index == 0) {
			System.err.println("index == 0");
			if (this.head != null) {
				this.head.prev = listTail;
			}
			listTail.next = this.head;
			this.head = listHead;
			System.err.println("head=" + this.head);
		}
		if (index != 0 && index != size) {
			final Node<E> node = getNodeAtIndex(index);
			final Node<E> prev = node.prev;
			prev.next = listHead;
			listHead.prev = prev;
			listTail.next = node;
			node.prev = listTail;
		}
		this.size += listSize;
		return listHead;
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
			throw new IndexOutOfBoundsException("Index " + index + " is out of bound [0," + size + "]");
		}
	}

}
