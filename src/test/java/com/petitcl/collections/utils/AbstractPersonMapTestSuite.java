package com.petitcl.collections.utils;

import com.petitcl.collections.models.Person;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;

public abstract class AbstractPersonMapTestSuite {

	private int startDataSetSize = 0;
	private int endDataSetSize = 15;
	private int startLoadFactor = 50;
	private int endLoadFactor = 99;
	private int loadFactorStep = 1;
	private boolean debugLayout = false;
	private boolean verifyContent = true;
	private boolean runScenarioOne = true;
	private boolean runScenarioTwo = true;
	private boolean runScenarioThree = true;
//	private long seed = 424242424242L;
	private long seed = System.nanoTime();

	private Random random;

	public AbstractPersonMapTestSuite withStartDataSetSize(int startDataSetSize) {
		this.startDataSetSize = startDataSetSize;
		return this;
	}

	public AbstractPersonMapTestSuite withEndDataSetSize(int endDataSetSize) {
		this.endDataSetSize = endDataSetSize;
		return this;
	}

	public AbstractPersonMapTestSuite withStartLoadFactor(int startLoadFactor) {
		this.startLoadFactor = startLoadFactor;
		return this;
	}

	public AbstractPersonMapTestSuite withEndLoadFactor(int endLoadFactor) {
		this.endLoadFactor = endLoadFactor;
		return this;
	}

	public AbstractPersonMapTestSuite withLoadFactorStep(int loadFactorStep) {
		this.loadFactorStep = loadFactorStep;
		return this;
	}

	public AbstractPersonMapTestSuite withDebugLayout(boolean debugLayout) {
		this.debugLayout = debugLayout;
		return this;
	}

	public AbstractPersonMapTestSuite withVerifyContent(boolean verifyContent) {
		this.verifyContent = verifyContent;
		return this;
	}

	public AbstractPersonMapTestSuite withSeed(long seed) {
		this.seed = seed;
		return this;
	}

	public AbstractPersonMapTestSuite withRandomSeed() {
		this.seed = System.nanoTime();
		return this;
	}

	public AbstractPersonMapTestSuite withRunScenarioOne(boolean runScenarioOne) {
		this.runScenarioOne = runScenarioOne;
		return this;
	}

	public AbstractPersonMapTestSuite withRunScenarioTwo(boolean runScenarioTwo) {
		this.runScenarioTwo = runScenarioTwo;
		return this;
	}

	public AbstractPersonMapTestSuite withRunScenarioThree(boolean runScenarioThree) {
		this.runScenarioThree = runScenarioThree;
		return this;
	}

	public void runAllTests() {
		random = new Random(seed);
		System.out.println("Using seed " + seed);
		if (runScenarioOne) {
			for (int i = startLoadFactor; i <= endLoadFactor; i += loadFactorStep) {
				final float loadFactor = i / (float)100;
				for (int j = startDataSetSize; j < endDataSetSize; j++) {
					final int dataSetSize = Fibonnaci.fibonacci(j);
					final Map<String, Person> map = createEmptyMap(1 / loadFactor);
					System.out.println("Testing scenario one with loadFactor " + loadFactor + " and with " + dataSetSize + " elements");
					runScenarioOne(map, dataSetSize);
				}
			}
		}
		if (runScenarioTwo) {
			for (int i = startLoadFactor; i <= endLoadFactor; i += loadFactorStep) {
				final float loadFactor = i / (float)100;
				for (int j = startDataSetSize; j < endDataSetSize; j++) {
					final int dataSetSize = Fibonnaci.fibonacci(j);
					final Map<String, Person> map = createEmptyMap(1 / loadFactor);
					System.out.println("Testing scenario two with loadFactor " + loadFactor + " and with " + dataSetSize + " elements");
					runScenarioTwo(map, dataSetSize);
				}
			}
		}
		if (runScenarioThree) {
			for (int i = startLoadFactor; i <= endLoadFactor; i += loadFactorStep) {
				final float loadFactor = i / (float)100;
				for (int j = startDataSetSize; j < endDataSetSize; j++) {
					final int dataSetSize = Fibonnaci.fibonacci(j);
					final Map<String, Person> map = createEmptyMap(1 / loadFactor);
					System.out.println("Testing scenario two with loadFactor " + loadFactor + " and with " + dataSetSize + " elements");
					runScenarioThree(map, dataSetSize);
				}
			}
		}
	}

	/**
	 * Scenario that will:
	 * - fill the map item by item until the desired size is reached
	 * - then, get each entry in the map item by item
	 */
	private void runScenarioOne(Map<String, Person> map, int dataSetSize) {
		final HashMap<String, Person> referenceMap = new HashMap<>();
		assertMapEqualsReferenceMap(referenceMap, map);

		for (int i = 0; i < dataSetSize; i++) {
			final String key = RandomStringUtils.random(12, 0, 0, false, true, null, random);
			final Person person = new Person(key, key, random.nextInt(100));
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
	}

	/**
	 * Scenario that will:
	 * - fill the map item by item until the desired size is reached
	 * - then, get each entry in the map item by item
	 * - then, delete each entry in the map item by item
	 */
	private void runScenarioTwo(Map<String, Person> map, int dataSetSize) {
		final HashMap<String, Person> referenceMap = new HashMap<>();
		assertMapEqualsReferenceMap(referenceMap, map);

		for (int i = 0; i < dataSetSize; i++) {
			final String key = RandomStringUtils.random(12, 0, 0, false, true, null, random);
			final Person person = new Person(key, key, random.nextInt(100));
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

	/**
	 * Scenario that will randomly select actions to perform on the map.
	 * The scenario will perform roughly 70% puts and 30% removes until it reaches the
	 * -
	 * - 30% removes
	 */
	private void runScenarioThree(Map<String, Person> map, int dataSetSize) {
		final HashMap<String, Person> referenceMap = new HashMap<>();
		assertMapEqualsReferenceMap(referenceMap, map);

		while (referenceMap.size() < dataSetSize) {
			final int randomValue = random.nextInt(100);
			if (randomValue > 30 || referenceMap.size() == 0) {
				// insert
				final String key = RandomStringUtils.random(12, 0, 0, false, true, null, random);
				final Person person = new Person(key, key, random.nextInt(100));
				referenceMap.put(key, person);
				final Person oldValue = map.put(key, person);
				Assert.assertNull(
						String.format("Putting %s - %s should return null - ", key, person),
						oldValue
				);
				final int expectedSize = referenceMap.size();
				Assert.assertEquals("Size should be " + expectedSize, expectedSize, map.size());
			} else {
				// delete
				final int randomIndex = random.nextInt(referenceMap.size());

				final Iterator<Map.Entry<String, Person>> it = referenceMap.entrySet().iterator();
				for (int j = 0; j < randomIndex; ++j, it.next());

				Map.Entry<String, Person> entry = it.next();
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
		}

		for (int i = 0; i < dataSetSize; i++) {
			final String key = RandomStringUtils.random(12, 0, 0, false, true, null, random);
			final Person person = new Person(String.valueOf(i), key, random.nextInt(100));
			referenceMap.put(key, person);
			final Person oldValue = map.put(key, person);
			Assert.assertNull(
					String.format("Putting %s - %s should return null - ", key, person),
					oldValue
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
		if (debugLayout) {
			System.out.println("referenceMap=" + referenceMap);
			System.out.println("map=" + map);
			printMapLayout(map);
		}
		if (verifyContent) {
			assertThat(map.entrySet(), Matchers.containsInAnyOrder(referenceMap.entrySet().toArray()));
		}
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
