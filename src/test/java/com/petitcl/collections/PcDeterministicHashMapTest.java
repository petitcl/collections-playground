package com.petitcl.collections;

import com.petitcl.collections.models.Person;
import com.petitcl.collections.utils.AbstractPersonMapTestSuite;
import org.junit.Test;

import java.util.Map;

public class PcDeterministicHashMapTest {

	@Test
	public void testLoad() {
		new PcDeterministicHashPersonMapTestSuite()
				.withStartDataSetSize(1)
				.withEndDataSetSize(8)
				.withTestDelete(false)
				.runAllTests();
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
