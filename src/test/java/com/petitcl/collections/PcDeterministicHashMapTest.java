package com.petitcl.collections;

import com.petitcl.collections.models.HashCollider;
import com.petitcl.collections.models.Person;
import com.petitcl.collections.utils.AbstractPersonMapTestSuite;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Map;

public class PcDeterministicHashMapTest {

	@Test
	public void testLoad() {
		new PcDeterministicHashPersonMapTestSuite()
				.withStartDataSetSize(1)
				.withEndDataSetSize(12)
				.withDebugLayout(true)
				.withVerifyContent(false)
				.runAllTests();
	}

	@Ignore
	@Test
	public void testReHash() {
		final PcDeterministicHashMap<HashCollider, String> map = new PcDeterministicHashMap<>(16);
		map.put(HashCollider.of("A", 0), "A");
		map.put(HashCollider.of("B", 1), "B");
		map.put(HashCollider.of("C", 2), "C");
		map.put(HashCollider.of("D", 3), "D");
		map.put(HashCollider.of("E", 4), "E");
		map.put(HashCollider.of("F", 5), "F");
		map.put(HashCollider.of("G", 6), "G");
		System.err.println(map.getLayout());
		map.remove(HashCollider.of("A", 0));
		System.err.println(map.getLayout());
		map.put(HashCollider.of("H", 7), "H");
		map.put(HashCollider.of("I", 8), "I");
		map.put(HashCollider.of("J", 9), "J");
		map.put(HashCollider.of("K", 10), "K");
		map.put(HashCollider.of("L", 11), "L");
		map.put(HashCollider.of("M", 12), "M");
		map.put(HashCollider.of("N", 13), "N");
		map.put(HashCollider.of("O", 14), "O");
		System.err.println(map.getLayout());
	}

	public static class PcDeterministicHashPersonMapTestSuite extends AbstractPersonMapTestSuite {

		@Override
		protected Map<String, Person> createEmptyMap() {
			return new PcDeterministicHashMap<>();
		}

		@Override
		protected Map<String, Person> createEmptyMap(float loadFactor) {
			return new PcDeterministicHashMap<>(PcDeterministicHashMap.DEFAULT_INITIAL_CAPACITY, loadFactor);
		}

		@Override
		protected void printMapLayout(Map<String, Person> map) {
			System.err.println(((PcDeterministicHashMap<String, Person>)map).getLayout());
		}
	}
}
