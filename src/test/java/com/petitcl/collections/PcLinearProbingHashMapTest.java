package com.petitcl.collections;

import com.petitcl.collections.models.Person;
import com.petitcl.collections.utils.AbstractPersonMapTestSuite;
import org.junit.Test;

import java.util.Map;

public class PcLinearProbingHashMapTest {

	@Test
	public void testLoad() {
		new PcLinearProbingHashPersonMapTestSuite()
				.withStartDataSetSize(0)
				.withEndDataSetSize(15)
				.runAllTests();
	}

	public static class PcLinearProbingHashPersonMapTestSuite extends AbstractPersonMapTestSuite {

		@Override
		protected Map<String, Person> createEmptyMap() {
			return new PcLinearProbingHashMap<>();
		}

		@Override
		protected Map<String, Person> createEmptyMap(float loadFactor) {
			return new PcLinearProbingHashMap<>(PcLinearProbingHashMap.DEFAULT_INITIAL_CAPACITY, loadFactor);
		}

		@Override
		protected void printMapLayout(Map<String, Person> map) {
			System.err.println(((PcLinearProbingHashMap<String, Person>)map).getLayout());
		}
	}

}
