package com.petitcl.collections.utils;

import com.petitcl.collections.models.Person;
import org.junit.Assert;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractMapTestSuite {

	public void runTests() {
		runTests(1, 15);
	}

	/**
	 * Test adding then removing elements in the map
	 * Follow a Fibonnaci sequence to determine the number of elements to use
	 */
	public void runTests(int start, int end) {
		for (int i = start; i < end; i++) {
			final int size = Fibonnaci.fibonacci(i);
			runAllTests(size);
		}
	}

	private void runAllTests(int size) {
		System.err.println("Testing with " + size + " elements");
		final Map<String, Person> map = createEmptyMap();
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
		assertMapEqualsReferenceMap(referenceMap, map);

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

	private void assertMapEqualsReferenceMap(Map<String, Person> referenceMap, Map<String, Person> map) {
		System.err.println("referenceMap=" + referenceMap);
//		System.err.println("map=" + map);
		printMapLayout(map);
//		assertThat(map.entrySet(), Matchers.containsInAnyOrder(referenceMap.entrySet().toArray()));
		Assert.assertEquals(referenceMap.size(), map.size());
	}

	protected abstract Map<String, Person> createEmptyMap();

	protected abstract void printMapLayout(Map<String, Person> map);

}
