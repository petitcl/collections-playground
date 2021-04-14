# Collections Playground
A playground for personal re-implementation of well known data structures with different environments.

## Introduction
This repository contains custom implementations of various well known data structures,
such as `Map` or `List`.
Some data structures have multiple implementations in order to demonstrate
the different strategies available.

The custom implementations are unit testes with [Guava test lib](https://github.com/google/guava).

## Collections

- [PcChainingHashMap](src/main/java/com/petitcl/collections/PcChainingHashMap.java) :
  an implementation of `Map` that uses chaining (ie: a linked list) to handle collisions.
  This implementation is similar to the implementation of the JDK (`java.util.HashMap`).
- [PcDeterministicHashMap](src/main/java/com/petitcl/collections/PcDeterministicHashMap.java) :
  an implementation of `Map` that uses chaining (ie: a linked list) to handle collisions. 
  This map also maintains a separate table of entries in order to maintain insertion order
  while iterating on the map.
  This implementation is sometimes referred as Close Tables, after its inventor, Tyler Close.
  See [Deterministic hash table](https://wiki.mozilla.org/User:Jorend/Deterministic_hash_tables)
  for more information.  
- [PcLinearProbingHashMap](src/main/java/com/petitcl/collections/PcLinearProbingHashMap.java) :
  an implementation of `Map` that uses [Linear Probing](https://en.wikipedia.org/wiki/Linear_probing)
  to handle collisions.

## Todo
- Linear probing hash table with tombstones
- [Quadratic probing hash table](https://en.wikipedia.org/wiki/Quadratic_probing)
- [Robin Hood hash table](https://en.wikipedia.org/wiki/Hash_table#Robin_Hood_hashing)
- Array list
- Double Linked list (Queue / )
- Queue
- Stack
- Deque
- Tree map
- Skip list