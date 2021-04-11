package com.petitcl.collections;

import java.util.*;

/**
 * Implementation of {@link Map} that is backed by a hash table
 * and that uses chaining (via a linked list) to handle collisions.
 *
 * @param <K> type of the key
 * @param <V> type of the value
 */
public class PcChainingHashMap<K, V> extends AbstractMap<K, V> {

	public static final int DEFAULT_INITIAL_CAPACITY = 16;
	public static final float DEFAULT_LOAD_FACTOR = 0.75f;

	public static class Node<K, V> implements Map.Entry<K, V> {
		private final K key;
		private V value;
		private Node<K, V> next;

		public Node(K key, V value) {
			this.key = key;
			this.value = value;
			this.next = null;
		}

		public Node(K key, V value, Node<K, V> next) {
			this.key = key;
			this.value = value;
			this.next = next;
		}

		@Override
		public K getKey() {
			return key;
		}

		@Override
		public V getValue() {
			return value;
		}

		@Override
		public V setValue(V value) {
			assertNotNull(value, "Value must not be null");

			final V oldValue = this.value;
			this.value = value;
			return oldValue;
		}

		@Override
		public final int hashCode() {
			return Objects.hashCode(key) ^ Objects.hashCode(value);
		}

		@Override
		public final boolean equals(Object other) {
			if (other == this) {
				return true;
			}
			if (other == null) {
				return false;
			}
			if ((other instanceof Map.Entry)) {
				final Map.Entry that = (Map.Entry)other;
				return Objects.equals(key, that.getKey()) &&
						Objects.equals(value, that.getValue());
			}
			return false;
		}

		public final String toString() { return key + "=" + value; }
	}

	private Node<K, V>[] table;
	private int size;
	private final float loadFactor;

	@SuppressWarnings("unchecked")
	public PcChainingHashMap() {
		this.table = (Node<K, V>[]) new Node[DEFAULT_INITIAL_CAPACITY];
		this.loadFactor = DEFAULT_LOAD_FACTOR;
		this.size = 0;
	}

	@SuppressWarnings("unchecked")
	public PcChainingHashMap(int initialCapacity, float loadFactor) {
		this.table = (Node<K, V>[]) new Node[initialCapacity];
		this.loadFactor = loadFactor;
		this.size = 0;
	}

	@SuppressWarnings("unchecked")
	public PcChainingHashMap(int initialCapacity) {
		this.table = (Node<K, V>[]) new Node[initialCapacity];
		this.loadFactor = DEFAULT_LOAD_FACTOR;
		this.size = 0;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public boolean containsKey(Object key) {
		assertNotNull(key, "Key must not be null");

		final Node<K, V> foundNode = getNodeForKey(this.table, key);
		return foundNode != null;
	}

	@Override
	public boolean containsValue(Object value) {
		assertNotNull(value, "Value must not be null");
		final ValuesIterator iterator = new ValuesIterator();
		while (iterator.hasNext()) {
			final V nextValue = iterator.next();
			if (nextValue.equals(value)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public V get(Object key) {
		assertNotNull(key, "Key must not be null");

		final Node<K, V> foundNode = getNodeForKey(key);
		return foundNode != null ? foundNode.value : null;
	}

	@Override
	public V put(K key, V value) {
		assertNotNull(key, "Key must not be null");
		assertNotNull(value, "Value must not be null");

		return putNodeForKey(key, value);
	}

	@Override
	public V remove(Object key) {
		assertNotNull(key, "Key must not be null");

		final Node<K, V> removedNode = removeNodeForKey(key);
		return removedNode != null ? removedNode.value : null;
	}

	@Override
	public void clear() {
		doClear();
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return new EntrySet();
	}

	public class EntrySet extends AbstractSet<Entry<K, V>> {

		@Override
		public Iterator<Entry<K, V>> iterator() {
			return new EntrySetIterator();
		}

		@Override
		public int size() {
			return size;
		}

		@Override
		public boolean isEmpty() {
			return size() == 0;
		}

		@Override
		public void clear() {
			doClear();
		}
	}

	public class EntrySetIterator extends BaseIterator implements Iterator<Entry<K, V>> {
		@Override
		public Entry<K, V> next() {
			return nextNode();
		}
	}

	@Override
	public Set<K> keySet() {
		return new KeySet();
	}

	public class KeySet extends AbstractSet<K> {

		@Override
		public Iterator<K> iterator() {
			return new KeysIterator();
		}

		@Override
		public int size() {
			return size;
		}

		@Override
		public boolean isEmpty() {
			return size() == 0;
		}

		@Override
		public void clear() {
			doClear();
		}
	}

	public class KeysIterator extends BaseIterator implements Iterator<K> {
		@Override
		public K next() {
			return nextNode().getKey();
		}
	}

	@Override
	public Collection<V> values() {
		return new Values();
	}

	public class Values extends AbstractCollection<V> {

		@Override
		public Iterator<V> iterator() {
			return new ValuesIterator();
		}

		@Override
		public int size() {
			return size;
		}

		@Override
		public boolean isEmpty() {
			return size() == 0;
		}

		@Override
		public void clear() {
			doClear();
		}
	}

	public class ValuesIterator extends BaseIterator implements Iterator<V> {
		@Override
		public V next() {
			return nextNode().getValue();
		}
	}

	public abstract class BaseIterator {

		private int indexInTable;
		private Node<K, V> nextNode;
		private Node<K, V> currentNode;

		public BaseIterator() {
			this.indexInTable = 0;
			this.nextNode = null;
			this.currentNode = null;
			// initially advance to first node
			while (nextNode == null && indexInTable < table.length) {
				nextNode = table[indexInTable];
				if (nextNode == null) {
					indexInTable++;
				}
			}
		}

		public boolean hasNext() {
			return this.nextNode != null;
		}

		protected Node<K, V> nextNode() {
			if (nextNode == null) {
				throw new NoSuchElementException();
			}
			this.currentNode = nextNode;
			computeNext();
			return this.currentNode;
		}

		public void remove() {
			if (currentNode == null) {
				throw new IllegalStateException("next() has not been called yet, cannot use remove()");
			}
			final Node<K, V> toRemove = currentNode;
			currentNode = null;
			removeNodeForKey(toRemove.key);
		}

		private void computeNext() {
			// advance currentNode in current list if not null
			if (nextNode != null && nextNode.next != null) {
				// if currentNode has a next, advance to it
				nextNode = nextNode.next;
				return;
			} else {
				// advance to next head node
				nextNode = null;
				indexInTable++;
			}

			// advance currentNode to the next head node in the table
			// or to the end of the table
			while (nextNode == null && indexInTable < table.length) {
				nextNode = table[indexInTable];
				if (nextNode == null) {
					indexInTable++;
				}
			}
		}

	}

	public String getLayout() {
		if (size == 0) {
			return "EMPTY (size=0, capacity=" + this.table.length + ", loadFactor=0)\n";
		}
		StringBuilder result = new StringBuilder();
		result.append("NOT EMPTY (size=")
				.append(size)
				.append("), capacity=")
				.append(this.table.length)
				.append("), loadFactor=")
				.append(size / (float)this.table.length)
				.append("\n");
		for (int i = 0; i < table.length; ++i) {
			Node<K, V> node = table[i];
			if (node == null) {
				result.append("index ").append(i).append(" : NULL").append("\n");
			} else {
				int count = 0;
				final StringBuilder allNodes = new StringBuilder();
				while (node != null) {
					allNodes.append("(key=")
							.append(node.key)
							.append(",value=")
							.append(node.value)
							.append(")");
					allNodes.append("->");
					count++;
					node = node.next;
				}
				allNodes.append("NULL");
				result.append("index " )
						.append(i)
						.append(" (")
						.append(count)
						.append(" nodes) : ")
						.append(allNodes)
						.append("\n");
			}
		}
		return result.toString();
	}

	private int hash(Object key) {
		return key.hashCode();
	}

	private V putNodeForKey(K key, V value) {
		this.resizeIfNeeded(this.size + 1);
		final V oldValue = putNodeForKey(this.table, key, value);
		if (oldValue == null) {
			// if old value was null, it means we inserted a new element
			this.size++;
		}
		return oldValue;
	}

	private V putNodeForKey(Node<K, V>[] target, K key, V value) {
		// compute hash and index
		final int hash = hash(key);
		final int size = target.length;
		final int index = (size - 1) & hash;

		// get node at index
		final Node<K, V> nodeAtIndex = target[index];
		if (nodeAtIndex != null) {
			// if node exists, replace or append to node
			final Node<K, V > upsertedNode = updateOrAppendNode(nodeAtIndex, key);
			return upsertedNode.setValue(value);
		} else {
			// if node does not exist, simply put a new node at that index
			target[index] = new Node<>(key, value);
			return null;
		}
	}

	private Node<K, V> getNodeForKey(Object key) {
		return getNodeForKey(this.table, key);
	}

	private Node<K, V> getNodeForKey(Node<K, V>[] target, Object key) {
		// compute hash and index
		final int hash = hash(key);
		final int size = target.length;
		final int index = (size - 1) & hash;

		// get node at index
		final Node<K, V> nodeAtIndex = target[index];
		if (nodeAtIndex == null) {
			// if no node at index, return null
			return null;
		} else {
			// if there is a node at index, traverse it until we find it
			return findNode(nodeAtIndex, key);
		}
	}

	private Node<K, V> removeNodeForKey(Object key) {
		final Node<K, V> removedNode = removeNodeForKey(this.table, key);
		if (removedNode != null) {
			// if old value was null, it means we removed an element
			this.size--;
		}
		return removedNode;
	}

	private Node<K, V> removeNodeForKey(Node<K, V>[] target, Object key) {
		// compute hash and index
		final int hash = hash(key);
		final int size = target.length;
		final int index = (size - 1) & hash;

		// get node at index
		final Node<K, V> nodeAtIndex = target[index];
		if (nodeAtIndex != null) {
			// if node exists, try to remove from node
			final Node<K, V > removedNode = removeNodeForKey(nodeAtIndex, key);
			if (removedNode != null) {
				// if removed value was not null, it means we deleted an element
				if (removedNode == nodeAtIndex) {
					// if the removed node was the head, update the table with the rest of the list
					target[index] = removedNode.next;
				}
				return removedNode;
			} else {
				return null;
			}
		} else {
			// if node does not exist, the key didn't exist
			return null;
		}
	}

	private Node<K, V> removeNodeForKey(Node<K, V> head, Object key) {
		if (head == null) {
			return null;
		}
		Node<K, V> current = head;
		Node<K, V> prev = null;
		while (current != null) {
			if (current.key.equals(key)) {
				if (prev != null) {
					prev.next = current.next;
				}
				return current;
			}
			prev = current;
			current = current.next;
		}
		return null;
	}

	private Node<K, V> updateOrAppendNode(Node<K, V> head, K key) {
		if (head == null) {
			return new Node<>(key, null);
		}
		Node<K, V> current = head;
		Node<K, V> tail = head;
		while (current != null) {
			if (current.key.equals(key)) {
				return current;
			}
			if (current.next == null) {
				tail = current;
			}
			current = current.next;
		}
		final Node<K, V> insertedNode = new Node<>(key, null);
		tail.next = insertedNode;
		return insertedNode;
	}

	private Node<K, V> findNode(Node<K, V> head, Object key) {
		Node<K, V> current = head;
		while (current != null) {
			if (current.key.equals(key)) {
				return current;
			}
			current = current.next;
		}
		return null;
	}

	private void doClear() {
		this.size = 0;
		if (this.table == null) {
			return;
		}
		Arrays.fill(this.table, null);
	}

	@SuppressWarnings("unchecked")
	private void resizeIfNeeded(int newSize) {
		final float currentLoadFactor = this.size / (float)this.table.length;
		if (currentLoadFactor < loadFactor) {
			return;
		}
		final int nextPowerOfTwo = (32 - Integer.numberOfLeadingZeros(newSize - 1));
		final int newCapacity = 1 << (nextPowerOfTwo + 1);
		final Node<K, V>[] newTable = (Node<K, V>[]) new Node[newCapacity];
		for (Node<K, V> kvNode : this.table) {
			Node<K, V> current = kvNode;
			while (current != null) {
				putNodeForKey(newTable, current.key, current.value);
				current = current.next;
			}
		}
		this.table = newTable;
	}

	private static void assertNotNull(Object param, String message) {
		if (param == null) {
			throw new NullPointerException(message);
		}
	}
}
