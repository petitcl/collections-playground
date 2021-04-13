# Collections Playground
A playground for personal re-implementation of well known data structures with different environments.

## Introduction
This repository contains custom implementations of various well known data structures,
such as `Map` or `List`.
Some data structures have multiple implementations in order to demonstrate
the different strategies available.

The custom implementations are unit testes with [Guava test lib](https://github.com/google/guava)

## Collections

- [PcChainingHashMap](src/main/java/com/petitcl/collections/PcChainingHashMap.java) :
  an implementation of `Map` that uses chaining (ie: a tree) to handle collisions.
  This implementation is similar to the implementation of the JDK.
- [PcLinearProbingHashMap](src/main/java/com/petitcl/collections/PcLinearProbingHashMap.java) :
  an implementation of `Map` that uses [Linear Probing](https://en.wikipedia.org/wiki/Linear_probing)
  to handle collisions.

## Todo
- [Deterministic hash table](https://wiki.mozilla.org/User:Jorend/Deterministic_hash_tables)
- Linear probing hash table with tombstones
- [Quadratic probing hash table](https://en.wikipedia.org/wiki/Quadratic_probing)
- [Robin Hood hash table](https://en.wikipedia.org/wiki/Hash_table#Robin_Hood_hashing)
- Array list
- Linked list
- Tree map