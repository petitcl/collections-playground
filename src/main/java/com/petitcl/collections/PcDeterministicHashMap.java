package com.petitcl.collections;

import java.util.*;

/**
 * Implementation of {@link Map} that is backed by a hash table
 * and that uses chaining (via a linked list) to handle collisions.
 * This version manages 2 tables:
 * - a hash table that maps key hashes to indexes in the data table
 * - a data table that contains inserted entries. The order in this table is the insertion order.
 * This allows to maintain insertion order while iterating over the map.
 *
 * This class also uses tombstones: when a node is delete from the data table,
 * a tombstone node is left in the spot of the deleted node. This keeps intact
 * the chain of non null nodes, allowing to iterate on the data table until finding a null node,
 * thus lowering the number of steps needed to traverse the map.
 *
 * This method is also referred as Close Tables, after their inventor, Tyler Close.
 */
public class PcDeterministicHashMap<K, V> extends AbstractMap<K, V> {

	public static final int DEFAULT_INITIAL_CAPACITY = 16;
	public static final float DEFAULT_LOAD_FACTOR = 0.75f;

	public static class Node<K, V> implements Entry<K, V> {
		private K key;
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
			Objects.requireNonNull(value);

			final V oldValue = this.value;
			this.value = value;
			return oldValue;
		}

		public void setTombstone() {
			this.key = null;
			this.value = null;
		}

		public boolean isTombstone() {
			return this.key == null && this.value == null;
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
				final Entry that = (Entry)other;
				return Objects.equals(key, that.getKey()) &&
						Objects.equals(value, that.getValue());
			}
			return false;
		}

		@Override
		public final String toString() { return key + "=" + value; }
	}

	/**
	 * Table of nodes.
	 */
	private Node<K, V>[] dataTable;

	/**
	 * Table of indexes of the nodes in the data table.
	 * The indexes of this table are hashes (clamped to the table size).
	 * The values of this table are indexes of nodes in the data table.
	 */
	private int[] hashTable;

	/**
	 * Next available slot in the data table.
	 */
	private int nextSlot;

	private int size;
	private final float loadFactor;

	@SuppressWarnings("unchecked")
	public PcDeterministicHashMap() {
		this.dataTable = (Node<K, V>[]) new Node[DEFAULT_INITIAL_CAPACITY];
		this.hashTable = new int[DEFAULT_INITIAL_CAPACITY];
		Arrays.fill(this.hashTable, -1);
		this.loadFactor = DEFAULT_LOAD_FACTOR;
		this.nextSlot = 0;
		this.size = 0;
	}

	@SuppressWarnings("unchecked")
	public PcDeterministicHashMap(int initialCapacity, float loadFactor) {
		this.dataTable = (Node<K, V>[]) new Node[initialCapacity];
		this.hashTable = new int[initialCapacity];
		Arrays.fill(this.hashTable, -1);
		this.loadFactor = loadFactor;
		this.nextSlot = 0;
		this.size = 0;
	}

	@SuppressWarnings("unchecked")
	public PcDeterministicHashMap(int initialCapacity) {
		this.dataTable = (Node<K, V>[]) new Node[initialCapacity];
		this.hashTable = new int[initialCapacity];
		Arrays.fill(this.hashTable, -1);
		this.loadFactor = DEFAULT_LOAD_FACTOR;
		this.nextSlot = 0;
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
		Objects.requireNonNull(key);

		final Node<K, V> foundNode = getNodeForKey(key);
		return foundNode != null;
	}

	@Override
	public boolean containsValue(Object value) {
		Objects.requireNonNull(value);

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
		Objects.requireNonNull(key);

		final Node<K, V> foundNode = getNodeForKey(key);
		return foundNode != null ? foundNode.value : null;
	}

	@Override
	public V put(K key, V value) {
		Objects.requireNonNull(key);
		Objects.requireNonNull(value);

		return putNodeForKey(key, value);
	}

	@Override
	public V remove(Object key) {
		Objects.requireNonNull(key);

		return removeNodeForKey(key);
	}

	@Override
	public void clear() {
		this.size = 0;
		Arrays.fill(this.dataTable, null);
		Arrays.fill(this.hashTable, -1);
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
			PcDeterministicHashMap.this.clear();
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
			PcDeterministicHashMap.this.clear();
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
			PcDeterministicHashMap.this.clear();
		}
	}

	public class ValuesIterator extends BaseIterator implements Iterator<V> {
		@Override
		public V next() {
			return nextNode().getValue();
		}
	}

	public abstract class BaseIterator {

		private int indexInDataTable;
		private Node<K, V> nextNode;
		private Node<K, V> currentNode;

		public BaseIterator() {
			this.indexInDataTable = 0;
			this.nextNode = null;
			this.currentNode = null;
			// initially advance to first node
			final Node<K, V>[] dataTable = PcDeterministicHashMap.this.dataTable;
			while (indexInDataTable < dataTable.length && dataTable[indexInDataTable] != null && nextNode == null) {
				final Node<K, V> current = dataTable[indexInDataTable];
				if (!current.isTombstone()) {
					nextNode = current;
				} else {
					indexInDataTable++;
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
			final Node<K, V>[] dataTable = PcDeterministicHashMap.this.dataTable;
			indexInDataTable++;
			nextNode = null;
			// advance index in data table
			// stop iterating if we reached a null value
			while (indexInDataTable < dataTable.length && dataTable[indexInDataTable] != null && nextNode == null) {
				final Node<K, V> current = dataTable[indexInDataTable];
				if (!current.isTombstone()) {
					nextNode = current;
				} else {
					indexInDataTable++;
				}
			}
		}
	}

	public String getLayout() {
		if (size == 0) {
			return "EMPTY (size=0, capacity=" + this.dataTable.length + ", loadFactor=0)\n";
		}
		StringBuilder result = new StringBuilder();
		result.append("NOT EMPTY (size=")
				.append(size)
				.append("), capacity=")
				.append(this.dataTable.length)
				.append("), loadFactor=")
				.append(size / (float)this.dataTable.length)
				.append("\n");
		result.append("hashTable:\n");
		for (int i = 0; i < hashTable.length; ++i) {
			result.append("index ").append(i).append(" : ").append(hashTable[i]).append("\n");
		}
		result.append("dataTable:\n");
		for (int i = 0; i < dataTable.length; ++i)  {
			Node<K, V> node = dataTable[i];
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
		final V oldValue = putNodeForKey(this.dataTable, this.hashTable, this.nextSlot, key, value);
		if (oldValue == null) {
			// if old value was null, it means we inserted a new element
			// so we increase size and next slot
			this.size++;
			this.nextSlot++;
		}
		return oldValue;
	}

	private V putNodeForKey(Node<K, V>[] targetDataTable, int[] targetHashTable, int nextSlot, K key, V value) {
		// compute hash and index
		final int hash = hash(key);
		final int size = targetHashTable.length;
		final int indexInHashTable = (size - 1) & hash;

		// get node at index
		final int indexInDataTable = targetHashTable[indexInHashTable];
		if (indexInDataTable == -1 || targetDataTable[indexInDataTable] == null) {
			// if the index in hash table is -1, the node does not exist yet
			// we can simply create it and insert it in the data table at the next slot
			// we also set the data table index in the hash table
			targetDataTable[nextSlot] = new Node<>(key, value);
			targetHashTable[indexInHashTable] = nextSlot;
			return null;
		} else {
			// if the index in hash table is not -1, there are nodes at that index
			// and we have to traverse those nodes until we find a node with the same key,
			// or the end of the chain
			final Node<K, V> nodeAtIndex = targetDataTable[indexInDataTable];
			Node<K, V> current = nodeAtIndex;
			Node<K, V> tail = nodeAtIndex;
			// todo: find out if it's better to append or prepend
			while (current != null) {
				if (key.equals(current.key)) {
					break;
				}
				if (current.next == null) {
					tail = current;
				}
				current = current.next;
			}
			if (current == null) {
				// if we reached the end, the key does not exist, so we append a new node to the tail
				// we insert it in the data table at next slot
				// we do no update the hash table, as it is already pointing to the head of the chain
				final Node<K, V> upsertedNode = new Node<>(key, value);
				if (tail != null) {
					tail.next = upsertedNode;
				}
				targetDataTable[nextSlot] = upsertedNode;
				return null;
			} else {
				// if we found a node with the same key,
				// we just update it without adding it to the hash table
				return current.setValue(value);
			}
		}
	}

	private Node<K, V> getNodeForKey(Object key) {
		return getNodeForKey(this.dataTable, this.hashTable, key);
	}

	private Node<K, V> getNodeForKey(Node<K, V>[] targetDataTable, int[] targetHashTable, Object key) {
		// compute hash and index
		final int hash = hash(key);
		final int size = targetHashTable.length;
		final int indexInHashTable = (size - 1) & hash;

		// get node at index
		final int indexInDataTable = targetHashTable[indexInHashTable];
		// if index in hash table is -1, it means no node was inserted for this index
		if (indexInDataTable == -1) {
			return null;
		}
		final Node<K, V> nodeAtIndex = targetDataTable[indexInDataTable];
		if (nodeAtIndex == null) {
			// if no node at index, return null
			return null;
		} else {
			// if there is a node at index, traverse it until we find it
			return findNode(nodeAtIndex, key);
		}
	}

	private V removeNodeForKey(Object key) {
		final V removedValue = removeNodeForKey(this.dataTable, this.hashTable, key);
		if (removedValue != null) {
			// if old value was null, it means we removed an element
			this.size--;
		}
		return removedValue;
	}

	private V removeNodeForKey(Node<K, V>[] targetDataTable, int[] targetHashTable, Object key) {
		// compute hash and index
		final int hash = hash(key);
		final int size = targetHashTable.length;
		final int indexInHashTable = (size - 1) & hash;

		// get node at index
		final int indexInDataTable = targetHashTable[indexInHashTable];
		if (indexInDataTable == -1 || targetDataTable[indexInDataTable] == null) {
			// if index is -1 or node does not exist, the key didn't exist
			return null;
		} else {
			// if node exists, try to remove key from node chain
			Node<K, V> head = targetDataTable[indexInDataTable];
			Node<K, V> current = head;
			while (current != null) {
				if (key.equals(current.key) && !current.isTombstone()) {
					final V oldValue = current.value;
					// delete node by setting its key and value to null
					// this is also called a tombstone
					current.setTombstone();
					return oldValue;
				}
				current = current.next;
			}
			return null;
		}
	}

	private Node<K, V> findNode(Node<K, V> head, Object key) {
		Node<K, V> current = head;
		while (current != null) {
			if (key.equals(current.key)) {
				return current;
			}
			current = current.next;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private void resizeIfNeeded(int newSize) {
		final float currentLoadFactor = this.size / (float)this.hashTable.length;
		if (currentLoadFactor < this.loadFactor && this.nextSlot < this.hashTable.length) {
			return;
		}
		final int nextPowerOfTwo = (32 - Integer.numberOfLeadingZeros(newSize - 1));
		final int newCapacity = 1 << (nextPowerOfTwo + 1);
		final Node<K, V>[] newDataTable = (Node<K, V>[]) new Node[newCapacity];
		final int[] newHashTable = new int[newCapacity];
		Arrays.fill(newHashTable, -1);
		int newNextSlot = 0;
		// we iterate on the table until we find a null node
		// this means all remaining entries are also null
		for (int i = 0; i < this.dataTable.length && this.dataTable[i] != null; i++) {
			final Node<K, V> node = this.dataTable[i];
			if (!node.isTombstone()) {
				putNodeForKey(newDataTable, newHashTable, newNextSlot, node.key, node.value);
				newNextSlot++;
			}
		}
		this.dataTable = newDataTable;
		this.hashTable = newHashTable;
		this.nextSlot = newNextSlot;
	}

}
