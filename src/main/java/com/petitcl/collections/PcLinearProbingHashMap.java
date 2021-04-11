package com.petitcl.collections;

import java.util.*;

/**
 * Implementation of {@link Map} that is backed by a hash table
 * and that uses linear probing to handle collisions.
 *
 * @param <K> type of the key
 * @param <V> type of the value
 */
public class PcLinearProbingHashMap<K, V> extends AbstractMap<K, V> {

//	public static final int DEFAULT_INITIAL_CAPACITY = 16;
	public static final int DEFAULT_INITIAL_CAPACITY = 1000;
	public static final float DEFAULT_LOAD_FACTOR = 0.75f;

	public static class Entry<K, V> implements Map.Entry<K, V> {
		private final K key;
		private V value;

		public Entry(K key, V value) {
			this.key = key;
			this.value = value;
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
				final Map.Entry that = (Map.Entry) other;
				return Objects.equals(key, that.getKey()) &&
						Objects.equals(value, that.getValue());
			}
			return false;
		}

		public final String toString() {
			return key + "=" + value;
		}
	}

	private Entry<K, V>[] table;
	private int size;
	private final float loadFactor;

	@SuppressWarnings("unchecked")
	public PcLinearProbingHashMap() {
		this.table = (Entry<K, V>[]) new Entry[DEFAULT_INITIAL_CAPACITY];
		this.loadFactor = DEFAULT_LOAD_FACTOR;
		this.size = 0;
	}

	@SuppressWarnings("unchecked")
	public PcLinearProbingHashMap(int initialCapacity, float loadFactor) {
		this.table = (Entry<K, V>[]) new Entry[initialCapacity];
		this.loadFactor = loadFactor;
		this.size = 0;
	}

	@SuppressWarnings("unchecked")
	public PcLinearProbingHashMap(int initialCapacity) {
		this.table = (Entry<K, V>[]) new Entry[initialCapacity];
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
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsValue(Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public V get(Object key) {
		assertNotNull(key, "Key must not be null");

		final Entry<K, V> foundEntry = getEntryForKey(key);
		return foundEntry != null ? foundEntry.value : null;
	}

	@Override
	public V put(K key, V value) {
		assertNotNull(key, "Key must not be null");
		assertNotNull(value, "Value must not be null");

		return putEntryForKey(key, value);
	}

	@Override
	public V remove(Object key) {
		assertNotNull(key, "Key must not be null");

		final Entry<K, V> removedEntry = removeEntryForKey(key);
		return removedEntry != null ? removedEntry.value : null;
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<Map.Entry<K, V>> entrySet() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<K> keySet() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<V> values() {
		throw new UnsupportedOperationException();
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
				.append(size / (float) this.table.length)
				.append("\n");
		for (int i = 0; i < table.length; ++i) {
			Entry<K, V> entry = table[i];
			if (entry == null) {
				result.append("index ").append(i).append(" : NULL").append("\n");
			} else {
				result.append("index ").append(i).append(" : ")
						.append("(key=")
						.append(entry.key)
						.append(",value=")
						.append(entry.value)
						.append(")")
						.append("\n");
			}
		}
		return result.toString();
	}

	private int hash(Object key) {
		return key.hashCode();
	}

	private V putEntryForKey(K key, V value) {
		this.resizeIfNeeded(false);

		for (int i = 0; i < 5; i++) {
			try {
				final V oldValue = putEntryForKey(this.table, key, value);
				if (oldValue == null) {
					// if old value was null, it means we inserted a new element
					this.size++;
				}
				return oldValue;
			} catch (UnsupportedOperationException e) {
				System.err.println("caught overflow error, retrying");
				this.resizeIfNeeded(true);
			}
		}
		throw new UnsupportedOperationException();
	}

	private V putEntryForKey(Entry<K, V>[] target, K key, V value) {
		System.err.println("put " + key);
		// compute hash and index
		final int hash = hash(key);
		final int size = target.length;
		final int index = (size - 1) & hash;

		// get entry at index
		Entry<K, V> entryAtIndex = null;
		int i = index;
		while (i < target.length && target[i] != null) {
			// if there in an entry at index, traverse adjacent entries
			// until find an entry with the same key or no entry
			entryAtIndex = target[i];
			if (Objects.equals(entryAtIndex.getKey(), key)) {
				// if entry exists and is the same key, replace it
				target[i] = new Entry<>(key, value);
				System.err.println("put " + key + " at index + " + i + " instead of " + index);
				return entryAtIndex.value;
			}
			i++;
		}
		if (i < target.length && target[i] == null) {
			// if no entry at index, use that spot to store new key
			target[i] = new Entry<>(key, value);
			System.err.println("put " + key + " at index " + i + " instead of " + index + " with hash " + hash);
			return null;
		} else {
			System.err.println("need resize for put for " + key + " at index " + i);
			throw new UnsupportedOperationException();
//			this.resizeIfNeeded(true);
//			return putEntryForKey(target, targetSize + 1, key, value);
		}
	}

	private Entry<K, V> getEntryForKey(Object key) {
		return getEntryForKey(this.table, key);
	}

	private Entry<K, V> getEntryForKey(Entry<K, V>[] target, Object key) {
		// compute hash and index
		final int hash = hash(key);
		final int size = target.length;
		final int index = (size - 1) & hash;

		// get entry at index
		Entry<K, V> entryAtIndex = null;
		int i = index;
		while (i < target.length && target[i] != null) {
			// if there is an entry at index,
			// traverse adjacent entries until finding the desired key or no entry
			entryAtIndex = target[i];
			if (Objects.equals(key, entryAtIndex.key)) {
				return entryAtIndex;
			}
			i++;
		}
		// if no entry was found, the key is not present
		System.err.println("get " + key + " not found");
		return null;
	}

	private Entry<K, V> removeEntryForKey(Object key) {
		final Entry<K, V> removedNode = removeEntryForKey(this.table, key);
		if (removedNode != null) {
			// if old value was null, it means we removed an element
			this.size--;
		}
		return removedNode;
	}

	private Entry<K, V> removeEntryForKey(Entry<K, V>[] target, Object key) {
		// compute hash and index
		final int hash = hash(key);
		final int size = target.length;
		final int index = (size - 1) & hash;

		// get node at index
		Entry<K, V> entryAtIndex = null;
		Entry<K, V> deletedEntry = null;
		int i = index;
		while (i < target.length && target[i] != null) {
			// if there is an entry at index,
			// traverse adjacent entries until finding the desired key or no entry
			entryAtIndex = target[i];
			if (Objects.equals(key, entryAtIndex.key)) {
				// delete from here
				System.err.println("delete " + key + " at index " + i);
				deletedEntry = entryAtIndex;
				target[i] = null;
				break;
			}
			i++;
		}
		if (deletedEntry == null) {
			// if no entry was found, the key was not there
			System.err.println("could not delete " + key + " : not found at index " + i);
			return null;
		} else {
			int emptiedIndex = i;
			do {
				emptiedIndex = fillEntryIfPossible(target, emptiedIndex);
			} while (emptiedIndex != -1);
			return deletedEntry;
		}
	}

	private int fillEntryIfPossible(Entry<K, V>[] target, int emptyIndex) {
		int i = emptyIndex + 1;
		Entry<K, V> entryAtIndex;
		while (i < target.length && target[i] != null) {
			entryAtIndex = target[i];
			final int currentHash = hash(entryAtIndex.key);
			final int desiredPosition = (target.length - 1) & currentHash;
			System.err.println("checking if can move " + entryAtIndex.key + " from index " + i + " to index " + emptyIndex + " desiredPosition=" + desiredPosition);
			if (desiredPosition <= emptyIndex) {
				System.err.println("moving " + entryAtIndex.key + " from index " + i + " to index " + emptyIndex + " desiredPosition=" + desiredPosition);
				target[emptyIndex] = target[i];
				target[i] = null;
				return i;
			}
			i++;
		}
		System.err.println("could move anything until index " + i + " " + (i < target.length ? target[i] : "END"));
		return -1;
	}

	@SuppressWarnings("unchecked")
	private void resizeIfNeeded(boolean force) {
		final float currentLoadFactor = this.size / (float)this.table.length;
		System.err.println("currentLoadFactor=" + currentLoadFactor);
		if (currentLoadFactor < loadFactor && !force) {
			return;
		}
		final int nextPowerOfTwo = (32 - Integer.numberOfLeadingZeros(this.table.length - 1));
		final int newCapacity = 1 << (nextPowerOfTwo + 1);
		System.err.println("newCapacity=" + newCapacity);
		final Entry<K, V>[] newTable = (Entry<K, V>[]) new Entry[newCapacity];
		for (Entry<K, V> entry : this.table) {
			Entry<K, V> current = entry;
			if (entry != null) {
				System.err.println("nested put for key " + current.key);
				putEntryForKey(newTable, current.key, current.value);
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
