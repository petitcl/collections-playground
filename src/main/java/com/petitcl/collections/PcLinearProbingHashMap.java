package com.petitcl.collections;

import java.util.*;

/**
 * Implementation of {@link Map} that is backed by a hash table
 * and that uses linear probing to handle collisions.
 * This implementation does not use any tombstones: if an element is deleted,
 * its spot will be filled with an out of place entry if such an entry exists.
 *
 * @param <K> type of the key
 * @param <V> type of the value
 */
public class PcLinearProbingHashMap<K, V> extends AbstractMap<K, V> {

	/**
	 * Maximum number of successive resizes that can occur
	 * during an insertion operation.
	 * If more resizes that this constant should occur,
	 * a
	 */
	public static final int MAX_SUCCESSIVE_RESIZES = 10;
	public static final int DEFAULT_INITIAL_CAPACITY = 16;
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
			Objects.requireNonNull(value);

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
		Objects.requireNonNull(key);

		final Entry<K, V> foundEntry = getEntryForKey(key);
		return foundEntry != null;
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

		final Entry<K, V> foundEntry = getEntryForKey(key);
		return foundEntry != null ? foundEntry.value : null;
	}

	@Override
	public V put(K key, V value) {
		Objects.requireNonNull(key);
		Objects.requireNonNull(value);

		return putEntryForKey(key, value);
	}

	@Override
	public V remove(Object key) {
		Objects.requireNonNull(key);

		final Entry<K, V> removedEntry = removeEntryForKey(key);
		return removedEntry != null ? removedEntry.value : null;
	}

	@Override
	public void clear() {
		this.size = 0;
		Arrays.fill(this.table, null);
	}

	@Override
	public Set<Map.Entry<K, V>> entrySet() {
		return new EntrySet();
	}

	public class EntrySet extends AbstractSet<Map.Entry<K, V>> {

		@Override
		public Iterator<Map.Entry<K, V>> iterator() {
			return new EntrySetIterator();
		}

		@Override
		public int size() {
			return PcLinearProbingHashMap.this.size;
		}

		@Override
		public boolean isEmpty() {
			return size() == 0;
		}

		@Override
		public void clear() {
			PcLinearProbingHashMap.this.clear();
		}

		@Override
		public boolean remove(Object o) {
			if (o instanceof Map.Entry) {
				final Map.Entry entry = (Map.Entry)o;
				final V removed = PcLinearProbingHashMap.this.remove(entry.getKey());
				return removed != null;
			}
			return false;
		}

	}

	public class EntrySetIterator extends BaseIterator implements Iterator<Map.Entry<K, V>> {

		@Override
		public Map.Entry<K, V> next() {
			return nextEntry();
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
			PcLinearProbingHashMap.this.clear();
		}

		@Override
		public boolean remove(Object o) {
			final V removed = PcLinearProbingHashMap.this.remove(o);
			return removed != null;
		}

	}

	public class KeysIterator extends BaseIterator implements Iterator<K> {

		@Override
		public K next() {
			return nextEntry().getKey();
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
			return PcLinearProbingHashMap.this.size;
		}

		@Override
		public boolean isEmpty() {
			return size() == 0;
		}

		@Override
		public void clear() {
			PcLinearProbingHashMap.this.clear();
		}

	}

	public class ValuesIterator extends BaseIterator implements Iterator<V> {

		@Override
		public V next() {
			return nextEntry().getValue();
		}

	}

	public abstract class BaseIterator {

		private int indexInTable;
		private Entry<K, V> nextEntry;
		private Entry<K, V> currentEntry;

		public BaseIterator() {
			final Entry<K, V>[] table = PcLinearProbingHashMap.this.table;
			this.indexInTable = 0;
			this.nextEntry = null;
			this.currentEntry = null;
			// initially advance to first entry
			while (this.nextEntry == null && this.indexInTable < table.length) {
				this.nextEntry = table[this.indexInTable];
				if (this.nextEntry == null) {
					this.indexInTable++;
				}
			}
		}

		public boolean hasNext() {
			return this.nextEntry != null;
		}

		protected Entry<K, V> nextEntry() {
			if (this.nextEntry == null) {
				throw new NoSuchElementException();
			}
			this.currentEntry = this.nextEntry;
			computeNext();
			return this.currentEntry;
		}

		public void remove() {
			if (this.currentEntry == null) {
				throw new IllegalStateException("next() has not been called yet, cannot use remove()");
			}
			PcLinearProbingHashMap.this.remove(this.currentEntry.key);
			this.currentEntry = null;
		}

		private void computeNext() {
			// advance to the next spot
			if (this.nextEntry != null) {
				this.nextEntry = null;
				this.indexInTable++;
			}

			final Entry<K, V>[] table = PcLinearProbingHashMap.this.table;
			// advance nextEntry to the next entry in the table
			// or to the beginning of the table
			while (this.nextEntry == null && this.indexInTable < table.length) {
				this.nextEntry = table[indexInTable];
				if (this.nextEntry == null) {
					this.indexInTable++;
				}
			}
		}

	}

	/**
	 * Return a string representing the layout of the underlying table.
	 * Useful for debugging.
	 */
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
		final int hashCode = key.hashCode();
		return hashCode ^ (hashCode >>> 16);
	}

	private V putEntryForKey(K key, V value) throws TooManyHashMapResizeException {
		this.resizeIfNeeded(false);

		for (int i = 0; i < MAX_SUCCESSIVE_RESIZES; i++) {
			try {
				final V oldValue = putEntryForKey(this.table, key, value);
				if (oldValue == null) {
					// if old value was null, it means we inserted a new element
					this.size++;
				}
				return oldValue;
			} catch (HashMapNeedResizeException e) {
				this.resizeIfNeeded(true);
			}
		}
		throw new TooManyHashMapResizeException("Too many successive resizes");
	}

	private V putEntryForKey(Entry<K, V>[] target, K key, V value) throws HashMapNeedResizeException {
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
				return entryAtIndex.value;
			}
			i++;
		}
		if (i < target.length && target[i] == null) {
			// if no entry at index, use that spot to store new key
			target[i] = new Entry<>(key, value);
			return null;
		} else {
			throw new HashMapNeedResizeException("Hash Map need resize");
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
				deletedEntry = entryAtIndex;
				target[i] = null;
				break;
			}
			i++;
		}
		if (deletedEntry == null) {
			// if no entry was found, the key was not there
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
			if (desiredPosition <= emptyIndex) {
				target[emptyIndex] = target[i];
				target[i] = null;
				return i;
			}
			i++;
		}
		return -1;
	}

	/**
	 * Check if the threshold has been met to trigger a resize, and perform the resize if it was met.
	 * The threshold is when the current load factor is more than the configured maximum load factor.
	 * A new table will be created, with a size equal to the next power of two, and all the elements
	 * of the current table will be copied to the new table.
	 *
	 * @param force if true, force resize even if the threshold is not reached
	 */
	@SuppressWarnings("unchecked")
	private void resizeIfNeeded(boolean force) {
		final float currentLoadFactor = this.size / (float)this.table.length;
		if (currentLoadFactor < this.loadFactor && !force) {
			return;
		}
		final int nextPowerOfTwo = (32 - Integer.numberOfLeadingZeros(this.table.length - 1));
		final int newCapacity = 1 << (nextPowerOfTwo + 1);
		final Entry<K, V>[] newTable = (Entry<K, V>[]) new Entry[newCapacity];
		for (Entry<K, V> entry : this.table) {
			Entry<K, V> current = entry;
			if (entry != null) {
				putEntryForKey(newTable, current.key, current.value);
			}
		}
		this.table = newTable;
	}

	/**
	 * Exception that will be thrown when a Hash Map is resized more than
	 * a pre-defined count during an insertion.
	 * This is needed in order to prevent the operation to cause a stack overflow
	 * or out of memory error.
	 * This should not happen in theory, as one resize is enough to handle most cases.
	 */
	public static class TooManyHashMapResizeException extends RuntimeException {
		public TooManyHashMapResizeException(String message) {
			super(message);
		}
	}

	/**
	 * Exception that is thrown when a resize is needed.
	 */
	private static class HashMapNeedResizeException extends RuntimeException {
		public HashMapNeedResizeException(String message) {
			super(message);
		}
	}
}
