---
topic: spring
tags: spring, spring boot, hibernate
---
# Spring, Spring Boot and JPA

## Inversion of Control and dependency injection
Inversion of Control means objects don't create or look up their own dependencies; a container provides them. Dependency injection is the main way to achieve it: a class declares what it needs (usually as constructor parameters) and the Spring IoC container (the ApplicationContext) creates the objects, called beans, wires them together, and manages their lifecycle.
Benefits: loose coupling (depend on interfaces, swap implementations), easy unit testing (pass mocks into the constructor), central configuration, and less boilerplate. A strong answer separates the principle (dependency inversion and DI) from Spring's implementation of it.

## Constructor vs field injection
Constructor injection is preferred. Dependencies can be final, the object can never be half-initialized, required dependencies are explicit, and tests can create the class with plain new and mocks, without Spring or reflection. With a single constructor, Spring injects it without needing @Autowired. A long constructor is also a useful warning sign that a class has too many responsibilities.
Field injection (@Autowired on a private field) hides dependencies, prevents final fields, needs reflection or a Spring context to test, and can hide circular dependencies. Setter injection suits optional dependencies.

## Beans, stereotypes and scopes
A bean is an object created and managed by the Spring container. Classes become beans through component scanning of stereotype annotations: @Component (generic), @Service (business logic), @Repository (data access, which also translates persistence exceptions into Spring's DataAccessException) and @Controller/@RestController (web layer). The specialised stereotypes document each layer's role and let aspects target layers.
Scopes: singleton (the default: one instance per container, so it must be stateless or thread-safe), prototype (a new instance on every injection), and web scopes such as request and session. Injecting a shorter-lived scoped bean into a singleton requires a proxy or ObjectProvider.

## @Configuration and @Bean
A @Configuration class declares beans with @Bean factory methods. Use it for objects you can't annotate yourself, such as third-party library classes, or that need custom construction (for example a configured RestTemplate, ObjectMapper or DataSource). In @Configuration classes Spring proxies the class, so calling one @Bean method from another returns the same singleton instead of creating a new object.
@Qualifier (or @Primary) resolves ambiguity when several beans of the same type exist.

## Auto-configuration and starters
Spring Boot auto-configuration creates sensible default beans based on what is on the classpath, which beans already exist, and which properties are set. Conditions like @ConditionalOnClass and @ConditionalOnMissingBean drive it. For example, with a JDBC driver and spring-boot-starter-data-jpa present, Boot configures a DataSource, Hibernate and transaction management automatically. Defining your own bean of the same type makes the default back off.
Starters (spring-boot-starter-web and so on) are dependency bundles that pull in a working set of libraries. Running with --debug prints the conditions report showing what was and wasn't configured, and why.

## Configuration properties and profiles
Spring Boot reads configuration from application.properties or application.yml, profile-specific files (application-prod.properties), environment variables, and command-line arguments. Command-line and environment variables override file values, which is how the same build runs with different settings in each environment. Relaxed binding maps DB_URL or db.url to the same property. @ConfigurationProperties binds a group of properties to a typed class, and @Value injects a single value.
Profiles (spring.profiles.active=dev) activate environment-specific configuration and beans annotated with @Profile. Secrets belong in environment variables or a secret manager, never in committed property files.

## REST controllers, request mapping and validation
@RestController is @Controller plus @ResponseBody: return values are serialized (usually to JSON by Jackson) instead of being resolved as view templates. Map endpoints with @GetMapping, @PostMapping and so on. @PathVariable binds a URL path segment (/users/{id}), which identifies a resource. @RequestParam binds a query parameter (?page=2), which is typically used for filtering, sorting and pagination. @RequestBody deserializes the request JSON.
Bean Validation: annotate DTO fields (@NotBlank, @Email, @Size, @Min) and the parameter with @Valid. On failure Spring throws MethodArgumentNotValidException, which you map to a 400 response with field errors. Keep controllers thin: validate and map at the edge, and put business logic in services.

## Exception handling with @ControllerAdvice
@RestControllerAdvice (or @ControllerAdvice) defines global exception handling for all controllers. Methods annotated with @ExceptionHandler(SomeException.class) translate exceptions into HTTP responses with the right status and a consistent error body, for example mapping a not-found exception to 404 and validation errors to 400. This removes try/catch from controllers and keeps error responses consistent across the API.
Also mention: a fallback handler for unexpected exceptions (500 with a safe message and full server-side logging), never leaking stack traces, and ResponseStatusException for simple cases.

## @Transactional
@Transactional makes a method run in a database transaction: Spring's proxy begins a transaction before the method and commits after it returns, or rolls back if a RuntimeException or Error escapes. Checked exceptions do not trigger rollback unless configured with rollbackFor. Propagation (default REQUIRED joins an existing transaction; REQUIRES_NEW suspends it and starts a new one), isolation and readOnly are configurable.
What it does NOT do: it doesn't apply to self-invocation (calling a @Transactional method from another method of the same class bypasses the proxy), it only works on public methods of Spring beans, and it can't roll back non-database side effects such as sent emails or HTTP calls. For those, use events after commit or the outbox pattern.

## JPA, Hibernate and the persistence context
JPA is the Java specification for ORM (object-relational mapping); Hibernate is its most common implementation. ORM maps classes to tables and objects to rows, reducing boilerplate SQL, but you must still understand the SQL it generates. Spring Data JPA adds repository interfaces with derived queries (findByEmail), pagination through Pageable, and @Query for JPQL or native SQL. JPQL queries entities and their fields; native SQL queries tables directly.
The persistence context (the first-level cache, one per EntityManager or transaction) tracks loaded entities. Within a transaction, loading the same entity twice returns the same instance, and changes to managed entities are flushed automatically at commit (dirty checking) without calling save.

## Lazy vs eager loading and the N+1 problem
Lazy loading fetches associated data only when it is accessed. It is the default for collections (@OneToMany, @ManyToMany). Eager loading fetches it immediately with the parent, the default for @ManyToOne and @OneToOne. Accessing a lazy association after the transaction has closed throws LazyInitializationException.
The N+1 problem: one query loads N parent rows, then accessing each parent's lazy association triggers another query per row, giving N+1 queries in total. Detect it by enabling SQL logging or counting queries. Fix it with JOIN FETCH in JPQL, @EntityGraph, batch fetching (@BatchSize / hibernate.default_batch_fetch_size), or DTO projections. Making everything eager is not a fix: it just moves the problem.

## Entity relationships and cascading
In a bidirectional relationship, one side owns it: the side holding the foreign key, usually @ManyToOne. The other side uses mappedBy (@OneToMany(mappedBy = "order")). Only changes on the owning side are written to the database, so keep both sides in sync with helper methods.
CascadeType propagates operations (PERSIST, MERGE, REMOVE, ALL) from parent to children. It suits real parent-child aggregates such as Order → OrderLines. It is dangerous on @ManyToOne or shared entities: CascadeType.REMOVE on a reference to a shared Customer could delete data still used elsewhere. orphanRemoval deletes children removed from the collection.

## Optimistic and pessimistic locking in JPA
Optimistic locking assumes conflicts are rare. Add a @Version column. Each update runs "UPDATE ... WHERE id = ? AND version = ?" and increments the version. If another transaction changed the row first, no row matches and JPA throws OptimisticLockException. The application then retries or reports a conflict, for example with 409. No locks are held, so it scales well for read-heavy workloads.
Pessimistic locking locks the row when reading it (@Lock(LockModeType.PESSIMISTIC_WRITE), which issues SELECT ... FOR UPDATE), so other writers wait. Use it when conflicts are frequent or retrying is expensive, such as decrementing scarce stock or bank balances, and keep these transactions short to avoid contention and deadlocks.

## Filters vs interceptors
Servlet filters sit in the servlet container, before the request reaches Spring's DispatcherServlet. They see every request, including static resources, and suit cross-cutting HTTP concerns such as security (Spring Security is a filter chain), CORS, logging, request IDs and compression. Spring MVC HandlerInterceptors run inside the DispatcherServlet, around a resolved controller handler (preHandle, postHandle, afterCompletion), and know which handler will run. They suit MVC-level concerns such as rate limiting per endpoint, auth checks tied to handlers, or adding model data.
A strong answer states the order: filter → DispatcherServlet → interceptor → controller.

## Spring Security basics
Spring Security is a chain of servlet filters that handles authentication (who are you, through form login, HTTP Basic, JWT or OAuth2) and authorization (what may you access). Authenticated identity is stored in the SecurityContext. Configure it with a SecurityFilterChain bean: which endpoints are public, which need roles (hasRole("ADMIN")), session policy (stateless for JWT APIs), CORS and CSRF. Method-level security (@PreAuthorize("hasRole('ADMIN')")) enforces rules on service methods.
CSRF protection matters for cookie or session-based browser apps. It is commonly disabled for stateless APIs that authenticate with bearer tokens in the Authorization header, because browsers don't attach that header automatically. Passwords must be stored with an adaptive hash such as BCrypt.
