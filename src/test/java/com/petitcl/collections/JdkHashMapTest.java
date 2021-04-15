package com.petitcl.collections;

import com.petitcl.collections.models.Person;
import com.petitcl.collections.utils.AbstractPersonMapTestSuite;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class JdkHashMapTest {

	@Test
	public void testLoad() {
		new JdkHashPersonMapTestSuite()
				.withStartDataSetSize(1)
				.withEndDataSetSize(15)
				.runAllTests();
	}

	public static class JdkHashPersonMapTestSuite extends AbstractPersonMapTestSuite {

		@Override
		protected Map<String, Person> createEmptyMap() {
			return new HashMap<>();
		}

		@Override
		protected Map<String, Person> createEmptyMap(float loadFactor) {
			return new HashMap<>(16, loadFactor);
		}

		@Override
		protected void printMapLayout(Map<String, Person> map) {
			System.err.println(((PcChainingHashMap<String, Person>)map).getLayout());
		}
	}
}
