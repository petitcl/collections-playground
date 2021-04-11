package com.petitcl.collections;

import com.google.common.collect.testing.MapTestSuiteBuilder;
import com.google.common.collect.testing.features.CollectionFeature;
import com.google.common.collect.testing.features.CollectionSize;
import com.google.common.collect.testing.features.MapFeature;
import com.petitcl.collections.models.Person;
import com.petitcl.collections.utils.AbstractPersonMapTestGenerator;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.AllTests;

import java.util.HashMap;
import java.util.Map;

@RunWith(AllTests.class)
public class JdkHashMapGuavaTestSuite extends TestCase {

	@Test
	public static TestSuite suite() {
		return MapTestSuiteBuilder
				.using(new PersonJdkHashMapTestGenerator())
				.named("Guava testlib - Test JDK HashMap")
				.withFeatures(
						CollectionSize.ANY,
						MapFeature.SUPPORTS_PUT,
						MapFeature.SUPPORTS_REMOVE,
						MapFeature.ALLOWS_NULL_VALUES,
						MapFeature.ALLOWS_NULL_KEYS,
						CollectionFeature.SUPPORTS_ITERATOR_REMOVE
				)
				.createTestSuite();
	}

	public static class PersonJdkHashMapTestGenerator extends AbstractPersonMapTestGenerator {
		@SuppressWarnings("unchecked")
		@Override
		public Map<String, Person> create(Object... elements) {
			final HashMap<String, Person> map = new HashMap<>();
			if (elements.length == 0) {
				return map;
			}
			for (Object e : elements) {
				final Map.Entry<String, Person> entry = (Map.Entry<String, Person>)e;
				map.put(entry.getKey(), entry.getValue());
			}
			return map;
		}
	}
}
