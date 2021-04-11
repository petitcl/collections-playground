package com.petitcl.collections;

import com.petitcl.PcChainingHashMap;
import com.petitcl.collections.models.Person;
import com.petitcl.collections.utils.Fibonnaci;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;

public class PcChainingHashMapTest {

	@Test
	public void testLoad() {
		for (int i = 0; i < 15; ++i) {
			final int size = Fibonnaci.fibonacci(i);
			runAllTests(size);
		}
	}

	private void runAllTests(int size) {
		System.err.println("Testing with " + size + " elements");
		final PcChainingHashMap<String, Person> map = new PcChainingHashMap<>();
		final HashMap<String, Person> referenceMap = new HashMap<>();
		assertMapEqualsReferenceMap(referenceMap, map);

		for (int i = 0; i < size; i++) {
			final String key = "test" + i;
			final Person person = new Person(String.valueOf(i), key, i * 11);
			referenceMap.put(key, person);
			final Person oldValue = map.put(key, person);
			Assert.assertNull(
					String.format("Putting %s - %s should return null - ", key, person),
					oldValue
			);
			final int expectedSize = i + 1;
			Assert.assertEquals("Size should be " + expectedSize, expectedSize, map.size());
		}
		assertMapEqualsReferenceMap(referenceMap, map);

		for (int i = 0; i < size; i++) {
			final String key = "test" + i;
			final Person expectedPerson = new Person(String.valueOf(i), key, i * 11);
			final Person actualPerson = map.get(key);
			Assert.assertEquals(
					String.format("Getting %s should return %s  - ", key, expectedPerson),
					expectedPerson,
					actualPerson
			);
		}

		for (int i = 0; i < size; i++) {
			final String key = "test" + i;
			final Person expectedPerson = referenceMap.remove(key);
			final Person actualPerson = map.remove(key);
			Assert.assertEquals(
					String.format("Removing %s should return %s  - ", key, expectedPerson),
					expectedPerson,
					actualPerson
			);
			final int expectedSize = size - (i + 1);
			Assert.assertEquals("Size should be " + expectedSize, expectedSize, map.size());
		}
		assertMapEqualsReferenceMap(referenceMap, map);
	}

	private void assertMapEqualsReferenceMap(Map<String, Person> referenceMap, PcChainingHashMap<String, Person> map) {
		System.err.println("referenceMap=" + referenceMap);
		System.err.println("map=" + map);
		System.err.println(map.getLayout());
		assertThat(map.entrySet(), Matchers.containsInAnyOrder(referenceMap.entrySet().toArray()));
		Assert.assertEquals(referenceMap.size(), map.size());
	}

}
