package com.petitcl.collections;

import com.petitcl.collections.models.Person;
import com.petitcl.collections.utils.AbstractMapTestSuite;
import org.junit.Test;

import java.util.Map;

public class PcLinearProbingHashMapTest {

	@Test
	public void testLoad() {
		new PcLinearProbingHashMapTestSuite()
				.runTests(0, 15);
	}

	public static class PcLinearProbingHashMapTestSuite extends AbstractMapTestSuite {

		@Override
		protected Map<String, Person> createEmptyMap() {
			return new PcLinearProbingHashMap<>();
		}

		@Override
		protected void printMapLayout(Map<String, Person> map) {
			System.err.println(((PcLinearProbingHashMap<String, Person>)map).getLayout());
		}
	}
}
