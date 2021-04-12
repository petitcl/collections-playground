package com.petitcl.collections.utils;

import com.petitcl.collections.models.Person;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public abstract class AbstractPersonMapTestSuite {

	private int startDataSetSize = 0;
	private int endDataSetSize = 15;
	private int startLoadFactor = 50;
	private int endLoadFactor = 99;
	private int loadFactorStep = 1;
//	private long seed = System.nanoTime();
	private long seed = 424242424242L;
	private Random random;


	public AbstractPersonMapTestSuite withStartDataSetSize(int startDataSetSize) {
		this.startDataSetSize = startDataSetSize;
		return this;
	}

	public AbstractPersonMapTestSuite withEndDataSetSize(int endDataSetSize) {
		this.endDataSetSize = endDataSetSize;
		return this;
	}

	public void runAllTests() {
		random = new Random(seed);
		System.err.println("Using seed " + seed);
		for (int i = startDataSetSize; i < endDataSetSize; i++) {
			final int dataSetSize = Fibonnaci.fibonacci(i);
			final Map<String, Person> map = createEmptyMap();
			System.err.println("Testing with default loadFactor and with " + dataSetSize + " elements");
			runLoadTest(map, dataSetSize);
		}
		for (int i = startLoadFactor; i <= endLoadFactor; i += loadFactorStep) {
			final float loadFactor = i / (float)100;
			for (int j = startDataSetSize; j < endDataSetSize; j++) {
				final int dataSetSize = Fibonnaci.fibonacci(j);
				final Map<String, Person> map = createEmptyMap(1 / loadFactor);
				System.err.println("Testing with loadFactor " + loadFactor + " and with " + dataSetSize + " elements");
				runLoadTest(map, dataSetSize);
			}
		}
	}

	private void runLoadTest(Map<String, Person> map, int dataSetSize) {
		final HashMap<String, Person> referenceMap = new HashMap<>();
		assertMapEqualsReferenceMap(referenceMap, map);

		for (int i = 0; i < dataSetSize; i++) {
			final String key = RandomStringUtils.random(12, 0, 0, false, true, null, random);
			final Person person = new Person(String.valueOf(i), key, random.nextInt(100));
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

		for (String key : referenceMap.keySet()) {
			final Person expectedPerson = referenceMap.get(key);
			final Person actualPerson = map.get(key);
			Assert.assertEquals(
					String.format("Getting %s should return %s  - ", key, expectedPerson),
					expectedPerson,
					actualPerson
			);
			final int expectedSize = referenceMap.size();
			Assert.assertEquals("Size should be " + expectedSize, expectedSize, map.size());
		}
		assertMapEqualsReferenceMap(referenceMap, map);

		for (Iterator<Map.Entry<String, Person>> it = referenceMap.entrySet().iterator(); it.hasNext();) {
			final Map.Entry<String, Person> entry = it.next();
			final String key = entry.getKey();
			final Person expectedPerson = entry.getValue();
			it.remove();
			final Person actualPerson = map.remove(entry.getKey());
			Assert.assertEquals(
					String.format("Removing %s should return %s  - ", key, expectedPerson),
					expectedPerson,
					actualPerson
			);
			final int expectedSize = referenceMap.size();
			Assert.assertEquals("Size should be " + expectedSize, expectedSize, map.size());
		}
		assertMapEqualsReferenceMap(referenceMap, map);
	}

	private void assertMapEqualsReferenceMap(Map<String, Person> referenceMap, Map<String, Person> map) {
		System.err.println("referenceMap=" + referenceMap);
//		System.err.println("map=" + map);
//		printMapLayout(map);
//		assertThat(map.entrySet(), Matchers.containsInAnyOrder(referenceMap.entrySet().toArray()));
		Assert.assertEquals(referenceMap.size(), map.size());
	}

	/**
	 * Overrides of this method should return an empty map with the default load factor.
	 */
	protected abstract Map<String, Person> createEmptyMap();

	/**
	 * Overrides of this method should return an empty map with the given load factor.
	 */
	protected abstract Map<String, Person> createEmptyMap(float loadFactor);

	/**
	 * Print the layout of the map being tested.
	 * Useful when debugging.
	 */
	protected abstract void printMapLayout(Map<String, Person> map);

}
