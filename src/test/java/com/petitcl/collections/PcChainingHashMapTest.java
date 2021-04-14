package com.petitcl.collections;

import com.petitcl.collections.models.Person;
import com.petitcl.collections.utils.AbstractPersonMapTestSuite;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;

public class PcChainingHashMapTest {

	@Test
	public void testLoad() {
		new PcChainingHashPersonMapTestSuite()
				.withStartDataSetSize(1)
				.withEndDataSetSize(15)
				.withDebugLayout(true)
				.runAllTests();
	}

	public static class PcChainingHashPersonMapTestSuite extends AbstractPersonMapTestSuite {

		@Override
		protected Map<String, Person> createEmptyMap() {
			return new PcChainingHashMap<>();
		}

		@Override
		protected Map<String, Person> createEmptyMap(float loadFactor) {
			return new PcChainingHashMap<>(PcChainingHashMap.DEFAULT_INITIAL_CAPACITY, loadFactor);
		}

		@Override
		protected void printMapLayout(Map<String, Person> map) {
			System.err.println(((PcChainingHashMap<String, Person>)map).getLayout());
		}
	}
}
