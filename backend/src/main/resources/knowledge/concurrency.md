---
topic: concurrency
tags: concurrency, java
---
# Concurrency and Multithreading

## Concurrency vs parallelism
Concurrency means structuring a program so several tasks make progress during overlapping periods of time. They may interleave on a single CPU core through context switching. Parallelism means tasks actually run at the same instant on multiple cores. Concurrency is about dealing with many things at once (for example a server handling thousands of requests); parallelism is about doing many things at once to finish CPU-heavy work faster.
I/O-bound tasks spend most of their time waiting on network or disk, and benefit from concurrency even on one core. CPU-bound tasks only speed up with parallelism, and only up to roughly the number of cores.

## Processes and threads
A process is a running program with its own isolated memory space. A thread is a unit of execution inside a process. Threads in the same process share the heap (objects, static fields) but each has its own stack and program counter. Threads are cheaper to create and switch between than processes, and they communicate through shared memory, which is fast but requires synchronization. Processes are isolated, so a crash in one doesn't corrupt the others; they communicate through IPC such as pipes, sockets or files.
Context switching (saving one thread's state and restoring another's) costs CPU time and pollutes caches, so having far more busy threads than cores reduces throughput.

## Race conditions and critical sections
A race condition occurs when the result depends on the unpredictable timing of threads accessing shared mutable data, and at least one of them writes. Classic example: count++ is not atomic. It is read, add, write, so two threads can both read 5 and both write 6, losing an update. Check-then-act races are similar: "if (stock > 0) stock--" lets two buyers both pass the check for the last item.
A critical section is code that accesses shared state and must not be executed by more than one thread at a time. Fixes: mutual exclusion (synchronized, locks), atomic operations (AtomicInteger, compare-and-set), immutable objects, confinement (no sharing), or, for data in a database, a transaction with proper locking or an atomic conditional update.

## synchronized and intrinsic locks
synchronized gives mutual exclusion and visibility. Only one thread at a time can hold an object's intrinsic lock (monitor), so a synchronized method or block runs exclusively for that lock. Releasing the lock also publishes the thread's writes, so the next thread to acquire the same lock sees them. A synchronized instance method locks "this"; a static synchronized method locks the Class object. Locks are reentrant: a thread can re-enter a lock it already holds.
Trade-offs: blocked threads wait with no timeout or interruptibility, and a coarse lock reduces throughput. Keep critical sections small, and lock on a private final object rather than a publicly reachable one.

## volatile and the Java memory model
Without synchronization, threads may not see each other's writes: values can be cached in CPU registers or caches, and the compiler and CPU may reorder instructions. A volatile field guarantees visibility, meaning reads always see the latest write, and prevents reordering around it, but it does NOT make compound actions atomic. volatile count++ still races.
The Java Memory Model defines happens-before relationships that guarantee visibility: unlocking a monitor happens-before the next lock of it, a volatile write happens-before later reads of that variable, Thread.start() happens-before the started thread's actions, and a thread's actions happen-before another thread's successful join() on it. volatile suits flags such as a "running" boolean read by a worker thread; use synchronized or atomics for read-modify-write operations.

## Atomic variables and compare-and-set
Classes in java.util.concurrent.atomic (AtomicInteger, AtomicLong, AtomicReference, LongAdder) provide lock-free thread-safe operations such as incrementAndGet and compareAndSet. They rely on hardware compare-and-swap (CAS): set the new value only if the current value still equals the expected one, otherwise retry. An atomic operation is indivisible: no other thread can observe it half-done.
Use atomics for single-variable counters, flags and sequence numbers. They are faster than locks under low or moderate contention, and LongAdder scales better for heavily contended counters. When several variables must change together consistently, you still need a lock.

## Deadlocks
A deadlock happens when threads wait forever for locks held by each other: thread 1 holds lock A and waits for B, while thread 2 holds B and waits for A. There are four necessary conditions (Coffman): mutual exclusion, hold-and-wait, no preemption, and circular wait. Break any one of them and deadlock is impossible.
Prevention: always acquire locks in a consistent global order (removes circular wait), use tryLock with a timeout and back off, hold as few locks as possible for as short as possible, and avoid calling unknown or external code while holding a lock. Diagnose deadlocks with a thread dump (jstack), which reports deadlocked threads.

## ReentrantLock, mutexes and semaphores
A mutex (mutual exclusion lock) lets one thread at a time into a critical section, and only its owner can release it. A semaphore holds a number of permits, letting up to N threads proceed at once. It is used to limit concurrent access to a resource such as a connection pool or an external API; a semaphore with one permit behaves like a mutex, but without ownership.
ReentrantLock is an explicit lock with the same mutual exclusion as synchronized, plus extra features: tryLock (optionally with a timeout), interruptible locking, an optional fairness policy, and multiple Condition objects. The trade-off is that you must always call unlock() in a finally block. ReadWriteLock allows many concurrent readers or one writer.

## Thread pools and ExecutorService
Creating a new platform thread per task is expensive and unbounded thread creation can exhaust memory. A thread pool reuses a fixed set of worker threads that take tasks from a queue. ExecutorService is Java's abstraction for submitting tasks (execute, submit returning a Future, invokeAll) and managing their lifecycle (shutdown, awaitTermination). Executors provides factories such as newFixedThreadPool, while ThreadPoolExecutor gives full control over core and max size, the queue, and the rejection policy.
Benefits: bounded resource use, lower latency since threads already exist, backpressure through bounded queues, and a clean separation between task submission and execution. Size pools for the workload: about the number of cores for CPU-bound work, more for I/O-bound work. Always shut executors down.

## Future and CompletableFuture
A Future represents the result of an asynchronous computation, but the only way to use it is get(), which blocks until the result is ready. CompletableFuture (Java 8+) supports non-blocking composition: chain dependent steps with thenApply and thenCompose, combine independent ones with thenCombine or allOf, handle errors with exceptionally or handle, and run on a chosen executor with supplyAsync(task, executor).
Typical use: calling three independent external APIs in parallel instead of one after another, so total latency becomes roughly the slowest call rather than the sum. Always add timeouts (orTimeout, completeOnTimeout), and avoid running blocking I/O on the common ForkJoinPool.

## Concurrent collections
Standard collections such as HashMap and ArrayList are not thread-safe. Concurrent modification can corrupt them or throw ConcurrentModificationException. Collections.synchronizedMap wraps every method in a single lock, so throughput suffers under contention, and compound actions like "check then put" or iteration still need external locking. ConcurrentHashMap allows concurrent reads and fine-grained concurrent writes, never throws ConcurrentModificationException while iterating (its iterators are weakly consistent), and offers atomic compound operations such as putIfAbsent, computeIfAbsent and merge.
Other options: CopyOnWriteArrayList for read-mostly lists, BlockingQueue implementations (ArrayBlockingQueue, LinkedBlockingQueue) for producer-consumer handoff, and ConcurrentLinkedQueue for non-blocking queues.

## ThreadLocal
ThreadLocal gives each thread its own independent copy of a variable, so the value is never shared and needs no synchronization. Backend frameworks use it to carry per-request context through the call stack without passing parameters: Spring Security's SecurityContextHolder, Spring's transaction and request context, logging MDC (request or trace IDs), and per-thread non-thread-safe objects such as SimpleDateFormat.
Pitfall: in thread pools, threads are reused, so values must be cleared (remove()) at the end of each task, or they leak memory and bleed one request's data into the next. Plain ThreadLocals also don't propagate to async tasks or child threads automatically.

## Virtual threads
Virtual threads (Java 21, Project Loom) are lightweight threads managed by the JVM rather than the operating system. Millions can exist, because a virtual thread blocked on I/O is unmounted from its carrier platform thread, which then runs other virtual threads. This lets simple blocking, thread-per-request code scale like asynchronous code, without callbacks or reactive APIs.
They help I/O-bound workloads with many concurrent waits (web requests, database or HTTP calls). They don't make CPU-bound work faster. Don't pool them; create one per task. Be careful with synchronized blocks around blocking calls, which can pin the carrier thread in Java 21, and with large ThreadLocal usage.

## Optimistic vs pessimistic concurrency control
Pessimistic concurrency control assumes conflicts are likely and prevents them by locking data before using it (synchronized in memory, SELECT ... FOR UPDATE in a database), so other writers wait. It is safe under high contention but reduces throughput and risks deadlocks. Optimistic concurrency control assumes conflicts are rare: work without locks, then verify at write time that nothing changed, using a version number or compare-and-set, and retry or fail on a conflict. It is fast when conflicts are rare, but under heavy contention it wastes work on repeated retries.
Example: for a bank transfer or the last item in stock, pessimistic locking (or an atomic conditional update like UPDATE ... SET stock = stock - 1 WHERE id = ? AND stock > 0) guarantees correctness. For editing a profile, optimistic locking with a @Version column suits better.

## Producer-consumer
In the producer-consumer pattern, producer threads create work items and consumer threads process them, decoupled by a shared buffer. The buffer must be thread-safe, and coordination must stop producers when it is full and consumers when it is empty. In Java the standard solution is a BlockingQueue: put() blocks when the queue is full and take() blocks when it is empty, giving backpressure without manual wait/notify. ExecutorService is built on this pattern.
The same idea scales out to message queues such as Kafka and RabbitMQ between services, which buffer load spikes and let producers and consumers scale independently.
