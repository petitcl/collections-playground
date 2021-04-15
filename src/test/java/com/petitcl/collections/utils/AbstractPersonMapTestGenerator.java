package com.petitcl.collections.utils;

import com.google.common.collect.testing.SampleElements;
import com.google.common.collect.testing.TestMapGenerator;
import com.petitcl.collections.models.Person;

import java.util.List;
import java.util.Map;

public abstract class AbstractPersonMapTestGenerator implements TestMapGenerator<String, Person> {

	@Override
	public String[] createKeyArray(int length) {
		return new String[length];
	}

	@Override
	public Person[] createValueArray(int length) {
		return new Person[length];
	}

	@Override
	public SampleElements<Map.Entry<String, Person>> samples() {
		return new SampleElements<>(
				Map.entry("test1", new Person("1", "Alice", 23)),
				Map.entry("test2", new Person("2", "Bob", 42)),
				Map.entry("test3", new Person("3", "Charles", 65)),
				Map.entry("test4", new Person("4", "Denise", 35)),
				Map.entry("test5", new Person("5", "Eve", 37))
		);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map.Entry<String, Person>[] createArray(int length) {
		return new Map.Entry[length];
	}

	@Override
	public Iterable<Map.Entry<String, Person>> order(List<Map.Entry<String, Person>> insertionOrder) {
		return insertionOrder;
	}

}