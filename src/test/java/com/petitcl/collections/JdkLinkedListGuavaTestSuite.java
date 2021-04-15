package com.petitcl.collections;

import com.google.common.collect.testing.ListTestSuiteBuilder;
import com.google.common.collect.testing.MapTestSuiteBuilder;
import com.google.common.collect.testing.features.CollectionFeature;
import com.google.common.collect.testing.features.CollectionSize;
import com.google.common.collect.testing.features.ListFeature;
import com.google.common.collect.testing.features.MapFeature;
import com.petitcl.collections.models.Person;
import com.petitcl.collections.utils.AbstractPersonListTestGenerator;
import com.petitcl.collections.utils.AbstractPersonMapTestGenerator;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.AllTests;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Reference test suite to demonstrate the use of Guava test lib.
 * This test suite verifies that java.util.HashMap correctly implements java.util.Map.
 */
@Ignore
@RunWith(AllTests.class)
public class JdkLinkedListGuavaTestSuite extends TestCase {

	@Test
	public static TestSuite suite() {
		return ListTestSuiteBuilder
				.using(new PersonJdkLinkedListTestGenerator())
				.named("Guava testlib - Test JDK Linked List")
				.withFeatures(
						CollectionSize.ANY,
						ListFeature.REMOVE_OPERATIONS,
						ListFeature.SUPPORTS_ADD_WITH_INDEX,
						ListFeature.SUPPORTS_REMOVE_WITH_INDEX,
						ListFeature.SUPPORTS_SET,
						CollectionFeature.ALLOWS_NULL_VALUES,
						CollectionFeature.SUPPORTS_ITERATOR_REMOVE
				)
				.createTestSuite();
	}

	public static class PersonJdkLinkedListTestGenerator extends AbstractPersonListTestGenerator {

		@SuppressWarnings("unchecked")
		@Override
		public List<Person> create(Object... elements) {
			final List<Person> list = new LinkedList<>();
			for (Object e : elements) {
				list.add((Person)e);
			}
			return list;
		}

	}

}
