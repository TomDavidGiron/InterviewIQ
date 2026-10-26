---
topic: java
tags: java, oop
---
# Java Fundamentals

## JDK, JRE and JVM
The JVM (Java Virtual Machine) executes Java bytecode. It loads classes, verifies them, interprets bytecode, JIT-compiles hot code to native machine code, and manages memory with garbage collection. Bytecode running on a JVM is what makes Java "write once, run anywhere". The JRE (Java Runtime Environment) is the JVM plus the standard class libraries needed to run programs. The JDK (Java Development Kit) is the JRE plus development tools such as the javac compiler, jar, javadoc and debugging tools. Since Java 11 a separate JRE is no longer distributed; you install a JDK or build a minimal runtime with jlink.
A strong answer explains the containment (JDK ⊃ JRE ⊃ JVM) and that javac compiles source into platform-independent bytecode which the JVM runs.

## Garbage collection
Java manages heap memory automatically. The garbage collector frees objects that are no longer reachable from GC roots (local variables on thread stacks, static fields, active threads). Most collectors are generational: new objects are allocated in the young generation, which is collected frequently and cheaply because most objects die young; survivors are promoted to the old generation, which is collected less often. Modern collectors include G1 (the default, balancing throughput and pause time), ZGC and Shenandoah (very low pause times).
Key points: GC prevents dangling pointers and double frees, but memory leaks are still possible when unneeded objects stay reachable (for example in static collections or caches). GC pauses can affect latency, and System.gc() is only a hint.

## equals and hashCode
== compares references for objects (are they the same instance?) and values for primitives. equals() compares logical equality as defined by the class. String overrides it to compare characters, while the default Object.equals is reference equality.
The contract: if a.equals(b) is true, then a.hashCode() must equal b.hashCode(). Hash-based collections such as HashMap and HashSet first use hashCode to find a bucket, then equals to find the matching key. If you override equals but not hashCode, two equal objects can land in different buckets, so lookups fail and sets hold duplicates. Fields used in equals and hashCode should be immutable while the object is stored in a hash collection.

## How HashMap works
A HashMap stores entries in an array of buckets. To put a key, it computes key.hashCode(), spreads the bits, and picks a bucket index from the array length. If several keys land in the same bucket (a collision), they are stored in a linked list in that bucket. Since Java 8, a bucket with many entries (8 or more) turns into a balanced red-black tree, improving the worst case from O(n) to O(log n). Lookup finds the bucket, then compares keys with equals.
When the number of entries exceeds capacity × load factor (default 0.75), the map resizes (doubles) and rehashes. Average get/put is O(1). HashMap allows one null key and is not thread-safe; use ConcurrentHashMap for concurrent access. Iteration order is not guaranteed; LinkedHashMap keeps insertion order.

## ArrayList vs LinkedList
ArrayList is backed by a resizable array: O(1) random access by index, amortized O(1) append, and O(n) insert or remove in the middle because elements shift. Its contiguous memory is cache-friendly. LinkedList is a doubly linked list: O(1) insert or remove once you hold a node or are at either end, but O(n) access by index and higher memory overhead per element.
In practice ArrayList is the right default for almost every case, and ArrayDeque is usually better than LinkedList as a queue or stack. A strong answer explains the complexity trade-offs instead of saying "LinkedList is faster for inserts" without qualification.

## Checked vs unchecked exceptions
Checked exceptions (subclasses of Exception but not RuntimeException, such as IOException) must be declared with throws or caught; the compiler enforces it. They represent recoverable conditions outside the program's control. Unchecked exceptions (RuntimeException and its subclasses, such as NullPointerException and IllegalArgumentException) usually indicate programming errors or invalid input and don't need to be declared. Errors (OutOfMemoryError, StackOverflowError) are serious JVM problems you normally don't catch.
Good practice: don't swallow exceptions silently, don't use exceptions for normal control flow, keep the original cause when wrapping, and catch at a level that can actually handle or report the problem.

## try-with-resources
try-with-resources (Java 7+) automatically closes any resource that implements AutoCloseable, such as streams, JDBC connections and statements, when the block exits, whether it ends normally or with an exception. Resources close in reverse order of declaration. If both the body and close() throw, the body's exception is kept and the close exception is attached as a suppressed exception.
It replaces error-prone finally blocks that often leaked resources or hid the original exception, which makes it the standard way to handle files, sockets and database connections.

## Immutability and final
An immutable object cannot change state after construction (String, Integer, LocalDate, records with immutable fields). To make a class immutable: make fields private and final, provide no setters, make the class final (or the constructor private) so it can't be subclassed, and defensively copy mutable inputs and outputs. Benefits: inherently thread-safe without synchronization, safe to share and cache, safe as HashMap keys, and easier to reason about.
The final keyword: a final variable can be assigned only once (for a reference, the reference is fixed, but the object it points to may still be mutable); a final method cannot be overridden; a final class cannot be extended.

## String, StringBuilder and StringBuffer
String is immutable: every modification creates a new object, and string literals are interned in a pool. Concatenating in a loop with + creates many temporary objects, so use StringBuilder, a mutable, non-synchronized buffer, for building strings. StringBuffer is the older synchronized (thread-safe) version and is slower; it is rarely needed, because builders are almost always used inside a single thread.
Key points: String immutability enables safe sharing, caching of hash codes and security (for example in class names and file paths). Compare strings with equals, not ==.

## Generics and type erasure
Generics let classes and methods work with types as parameters (List<String>, Map<K, V>), giving compile-time type safety and removing explicit casts. Bounded types restrict parameters (<T extends Number>). Wildcards make APIs flexible: ? extends T for reading from a producer, ? super T for writing to a consumer (PECS: Producer Extends, Consumer Super).
Java implements generics with type erasure: type parameters are checked at compile time and then removed, so at runtime List<String> and List<Integer> are the same class. As a result you can't do new T(), instanceof List<String>, or create generic arrays directly, and primitives must be boxed (List<Integer>, not List<int>).

## Autoboxing and unboxing
Autoboxing is the automatic conversion between primitives and their wrapper classes (int to Integer and back). It makes collections of numbers convenient but has pitfalls: unboxing a null Integer throws NullPointerException; comparing wrappers with == compares references, which only happens to work for cached values between -128 and 127; and boxing in tight loops creates garbage and slows code.
A strong answer gives the == pitfall or the null unboxing NPE as a concrete example.

## Interfaces vs abstract classes
An interface defines a contract. A class can implement many interfaces. Interfaces have no instance state, but since Java 8 they can have default and static methods, and since Java 9 private methods. An abstract class can have fields, constructors and implemented methods. It models an "is-a" relationship with shared state or behaviour, and a class can extend only one.
Use interfaces to define capabilities and keep code decoupled (programming to an interface, enabling mocks and multiple implementations). Use an abstract class when related classes share state or a template algorithm. Modern Java favours interfaces plus composition.

## Streams, lambdas and Optional
A lambda is a concise implementation of a functional interface (an interface with one abstract method, such as Runnable, Comparator or Function). Method references (String::length) are shorthand for lambdas. The Streams API processes collections declaratively with a pipeline: a source, lazy intermediate operations (filter, map, sorted) and a terminal operation (collect, reduce, forEach) that triggers execution. Streams can be parallel, but that only helps for large CPU-bound work.
Optional represents a value that may be absent and makes "no result" explicit in return types, avoiding null checks (map, orElse, orElseThrow). Avoid overusing it: don't use it for fields or method parameters, don't call get() without checking, and don't wrap collections (return an empty collection instead).

## Stack vs heap memory
Each thread has its own stack holding method call frames, local primitive variables and object references. It is fast, automatically freed when methods return, and limited in size; deep recursion causes StackOverflowError. The heap is shared by all threads and holds every object and array; it is managed by the garbage collector, and running out of it causes OutOfMemoryError.
Example: in "User u = new User()", the reference u lives on the stack and the User object lives on the heap. Because the heap is shared, objects accessed by several threads need synchronization, while local variables on the stack are thread-confined.

## OOP principles and SOLID
The four OOP pillars: encapsulation (hide internal state behind methods), abstraction (expose what an object does, not how), inheritance (reuse and specialize behaviour from a parent class), and polymorphism (one interface, many implementations, with the method chosen at runtime).
SOLID: Single Responsibility (one reason to change), Open/Closed (open for extension, closed for modification, for example new strategy classes instead of editing if/else chains), Liskov Substitution (subtypes must be usable wherever the parent is expected without breaking behaviour; Square extending Rectangle is the classic violation), Interface Segregation (small focused interfaces instead of one fat interface), Dependency Inversion (depend on abstractions, not concrete implementations, which is what dependency injection enables). Aim for high cohesion and low coupling.

## Composition vs inheritance
Inheritance ("is-a") reuses code by extending a class, but it couples the subclass tightly to the parent's implementation: parent changes can break subclasses (the fragile base class problem), and Java allows only one superclass. Composition ("has-a") builds behaviour by holding references to other objects and delegating to them. It is more flexible, allows swapping implementations at runtime, and keeps classes loosely coupled.
"Favour composition over inheritance" is the usual guideline. Use inheritance for a true is-a relationship where the subclass honours the parent's contract (Liskov), and composition with interfaces for sharing behaviour.
