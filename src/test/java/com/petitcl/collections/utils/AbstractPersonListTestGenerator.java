package com.petitcl.collections.utils;

import com.google.common.collect.testing.SampleElements;
import com.google.common.collect.testing.TestListGenerator;
import com.google.common.collect.testing.TestMapGenerator;
import com.petitcl.collections.models.Person;

import java.util.List;
import java.util.Map;

public abstract class AbstractPersonListTestGenerator implements TestListGenerator<Person> {

	@Override
	public SampleElements<Person> samples() {
		return new SampleElements<>(
				new Person("1", "Alice", 23),
				new Person("2", "Bob", 42),
				new Person("3", "Charles", 65),
				new Person("4", "Denise", 35),
				new Person("5", "Eve", 37)
		);
	}

	@Override
	public Person[] createArray(int length) {
		return new Person[length];
	}

	@Override
	public Iterable<Person> order(List<Person> insertionOrder) {
		return insertionOrder;
	}

}