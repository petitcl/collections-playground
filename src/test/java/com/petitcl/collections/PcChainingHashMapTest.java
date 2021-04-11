package com.petitcl.collections;

import com.petitcl.collections.models.Person;
import com.petitcl.collections.utils.AbstractMapTestSuite;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;

public class PcChainingHashMapTest {

	@Test
	public void testLoad() {
		new PcChainingHashMapTestSuite()
				.runTests();
	}

	public static class PcChainingHashMapTestSuite extends AbstractMapTestSuite {

		@Override
		protected Map<String, Person> createEmptyMap() {
			return new PcChainingHashMap<>();
		}

		@Override
		protected void printMapLayout(Map<String, Person> map) {
			System.err.println(((PcChainingHashMap<String, Person>)map).getLayout());
		}
	}
}
