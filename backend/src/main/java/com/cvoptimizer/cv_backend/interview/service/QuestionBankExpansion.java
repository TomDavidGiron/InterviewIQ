package com.cvoptimizer.cv_backend.interview.service;

import com.cvoptimizer.cv_backend.interview.model.InterviewQuestion;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Questions across frontend, CS fundamentals, behavioral, cloud/DevOps,
 * web security, testing, and (Q801+) Python/data engineering/data analytics.
 * IDs Q501-Q910. No overlap with QuestionBankService.
 */
@Component
public class QuestionBankExpansion {

    public List<InterviewQuestion> getAll() {
        List<InterviewQuestion> q = new ArrayList<>();
        addHtmlCss(q);
        addJavaScript(q);
        addReact(q);
        addTypeScript(q);
        addDataStructures(q);
        addAlgorithms(q);
        addNetworking(q);
        addOsConcurrency(q);
        addBehavioral(q);
        addCloud(q);
        addDockerKubernetes(q);
        addWebSecurity(q);
        addTesting(q);
        addPython(q);
        addEtlDataPipelines(q);
        addAirflow(q);
        addDbt(q);
        addDataWarehousingAnalytics(q);
        return q;
    }

    // =====================================================================
    // HTML / CSS  (Q501-Q520)
    // =====================================================================
    private void addHtmlCss(List<InterviewQuestion> q) {
        q.add(new InterviewQuestion("Q501",
                "What is the CSS box model? Describe its four components.",
                Set.of("css", "frontend"), false,
                Set.of("margin", "border", "padding", "content"), "EASY"));

        q.add(new InterviewQuestion("Q502",
                "What is the difference between `display: flex` and `display: grid`? When would you choose one over the other?",
                Set.of("css", "frontend"), false,
                Set.of("flex", "grid", "layout"), "EASY"));

        q.add(new InterviewQuestion("Q503",
                "What is CSS specificity and how is it calculated? Give an example where two rules conflict.",
                Set.of("css", "frontend"), false,
                Set.of("specificity", "selector", "weight"), "EASY"));

        q.add(new InterviewQuestion("Q504",
                "What is the difference between `position: relative`, `absolute`, `fixed`, and `sticky`?",
                Set.of("css", "frontend"), false,
                Set.of("position", "relative", "absolute"), "EASY"));

        q.add(new InterviewQuestion("Q505",
                "What is a semantic HTML element? Give three examples and explain why semantics matter.",
                Set.of("html", "frontend"), false,
                Set.of("semantic", "header", "article", "section"), "EASY"));

        q.add(new InterviewQuestion("Q506",
                "What is the difference between `em`, `rem`, `px`, and `%` units in CSS?",
                Set.of("css", "frontend"), false,
                Set.of("em", "rem", "responsive"), "EASY"));

        q.add(new InterviewQuestion("Q507",
                "What is a CSS pseudo-class and a pseudo-element? Give two examples of each.",
                Set.of("css", "frontend"), false,
                Set.of("pseudo", "hover", "before", "after"), "EASY"));

        q.add(new InterviewQuestion("Q508",
                "What is responsive design? How do CSS media queries help implement it?",
                Set.of("css", "frontend"), false,
                Set.of("responsive", "media-query", "breakpoint"), "EASY"));

        q.add(new InterviewQuestion("Q509",
                "What is the difference between `visibility: hidden` and `display: none`?",
                Set.of("css", "frontend"), false,
                Set.of("visibility", "display", "hidden"), "EASY"));

        q.add(new InterviewQuestion("Q510",
                "What is a CSS variable (custom property)? How do you define and use one?",
                Set.of("css", "frontend"), false,
                Set.of("variable", "custom property", "--"), "EASY"));

        q.add(new InterviewQuestion("Q511",
                "What is `z-index` and what is a stacking context?",
                Set.of("css", "frontend"), false,
                Set.of("z-index", "stacking"), "MEDIUM"));

        q.add(new InterviewQuestion("Q512",
                "Explain the difference between `inline`, `block`, and `inline-block` display values.",
                Set.of("css", "frontend"), false,
                Set.of("inline", "block"), "EASY"));

        q.add(new InterviewQuestion("Q513",
                "What is the difference between `localStorage` and `sessionStorage`?",
                Set.of("html", "frontend", "browser"), false,
                Set.of("localstorage", "sessionstorage", "persist"), "EASY"));

        q.add(new InterviewQuestion("Q514",
                "What does `async` and `defer` do on a `<script>` tag? When should you use each?",
                Set.of("html", "frontend"), false,
                Set.of("async", "defer", "parsing"), "MEDIUM"));

        q.add(new InterviewQuestion("Q515",
                "What is BEM (Block Element Modifier) naming convention in CSS?",
                Set.of("css", "frontend"), false,
                Set.of("bem", "naming", "class"), "EASY"));

        q.add(new InterviewQuestion("Q516",
                "What is a CSS preprocessor (e.g. SCSS/Sass)? What features do they add over plain CSS?",
                Set.of("css", "frontend"), false,
                Set.of("sass", "scss", "nesting", "mixin"), "EASY"));

        q.add(new InterviewQuestion("Q517",
                "How does CSS inheritance work? Which properties are inherited by default?",
                Set.of("css", "frontend"), false,
                Set.of("inherit", "color", "font"), "EASY"));

        q.add(new InterviewQuestion("Q518",
                "What is a CSS transition vs a CSS animation?",
                Set.of("css", "frontend"), false,
                Set.of("transition", "animation", "keyframe"), "EASY"));

        q.add(new InterviewQuestion("Q519",
                "What is the `data-*` attribute in HTML? When would you use it?",
                Set.of("html", "frontend"), false,
                Set.of("data-attribute", "dataset"), "EASY"));

        q.add(new InterviewQuestion("Q520",
                "What is the critical rendering path in a browser?",
                Set.of("html", "css", "frontend", "performance"), false,
                Set.of("dom", "cssom", "render"), "HARD"));
    }

    // =====================================================================
    // JavaScript  (Q521-Q560)
    // =====================================================================
    private void addJavaScript(List<InterviewQuestion> q) {
        q.add(new InterviewQuestion("Q521",
                "What is the difference between `var`, `let`, and `const`? What is block scope?",
                Set.of("javascript", "frontend"), false,
                Set.of("var", "let", "const", "scope"), "EASY"));

        q.add(new InterviewQuestion("Q522",
                "What is hoisting in JavaScript? Does it apply to `let` and `const`?",
                Set.of("javascript", "frontend"), false,
                Set.of("hoisting", "temporal dead zone"), "MEDIUM"));

        q.add(new InterviewQuestion("Q523",
                "What is a closure in JavaScript? Give a practical example.",
                Set.of("javascript", "frontend"), false,
                Set.of("closure", "lexical scope"), "MEDIUM"));

        q.add(new InterviewQuestion("Q524",
                "What is the difference between `==` and `===` in JavaScript?",
                Set.of("javascript", "frontend"), false,
                Set.of("equality", "type coercion"), "EASY"));

        q.add(new InterviewQuestion("Q525",
                "Explain event bubbling and event capturing. What does `event.stopPropagation()` do?",
                Set.of("javascript", "frontend"), false,
                Set.of("bubbling", "capturing", "propagation"), "MEDIUM"));

        q.add(new InterviewQuestion("Q526",
                "What is the difference between `null` and `undefined` in JavaScript?",
                Set.of("javascript", "frontend"), false,
                Set.of("null", "undefined"), "EASY"));

        q.add(new InterviewQuestion("Q527",
                "What is a Promise in JavaScript? How does it differ from a callback?",
                Set.of("javascript", "frontend", "async"), false,
                Set.of("promise", "resolve", "reject", "callback"), "MEDIUM"));

        q.add(new InterviewQuestion("Q528",
                "Explain `async/await` in JavaScript. How does it relate to Promises?",
                Set.of("javascript", "frontend", "async"), false,
                Set.of("async", "await", "promise"), "MEDIUM"));

        q.add(new InterviewQuestion("Q529",
                "What is the JavaScript event loop? Explain the call stack, task queue, and microtask queue.",
                Set.of("javascript", "frontend"), false,
                Set.of("event loop", "call stack", "microtask"), "HARD"));

        q.add(new InterviewQuestion("Q530",
                "What is prototypal inheritance in JavaScript?",
                Set.of("javascript", "frontend"), false,
                Set.of("prototype", "inheritance", "__proto__"), "MEDIUM"));

        q.add(new InterviewQuestion("Q531",
                "What is the difference between `call()`, `apply()`, and `bind()`?",
                Set.of("javascript", "frontend"), false,
                Set.of("call", "apply", "bind", "this"), "MEDIUM"));

        q.add(new InterviewQuestion("Q532",
                "What is destructuring in JavaScript? Give examples for arrays and objects.",
                Set.of("javascript", "frontend"), false,
                Set.of("destructuring", "spread", "rest"), "EASY"));

        q.add(new InterviewQuestion("Q533",
                "How do arrow functions differ from regular functions in JavaScript?",
                Set.of("javascript", "frontend"), false,
                Set.of("arrow function", "this", "lexical"), "MEDIUM"));

        q.add(new InterviewQuestion("Q534",
                "What is the spread operator (`...`) and the rest parameter? Give examples.",
                Set.of("javascript", "frontend"), false,
                Set.of("spread", "rest", "array"), "EASY"));

        q.add(new InterviewQuestion("Q535",
                "What are `map()`, `filter()`, and `reduce()` in JavaScript? When would you use each?",
                Set.of("javascript", "frontend"), false,
                Set.of("map", "filter", "reduce"), "EASY"));

        q.add(new InterviewQuestion("Q536",
                "What is `this` in JavaScript? How does it behave differently in arrow vs regular functions?",
                Set.of("javascript", "frontend"), false,
                Set.of("this", "context", "arrow"), "MEDIUM"));

        q.add(new InterviewQuestion("Q537",
                "What is debouncing vs throttling? Give a real-world use case for each.",
                Set.of("javascript", "frontend", "performance"), false,
                Set.of("debounce", "throttle", "delay"), "MEDIUM"));

        q.add(new InterviewQuestion("Q538",
                "What is the module system in JavaScript? What is the difference between ES modules and CommonJS?",
                Set.of("javascript", "frontend"), false,
                Set.of("import", "export", "require", "module"), "MEDIUM"));

        q.add(new InterviewQuestion("Q539",
                "What is event delegation in JavaScript and why is it useful?",
                Set.of("javascript", "frontend"), false,
                Set.of("event delegation", "bubbling", "performance"), "MEDIUM"));

        q.add(new InterviewQuestion("Q540",
                "What is a WeakMap and a WeakSet? When would you use them over Map and Set?",
                Set.of("javascript", "frontend"), false,
                Set.of("weakmap", "weakset", "garbage collection"), "HARD"));

        q.add(new InterviewQuestion("Q541",
                "What is currying in JavaScript? Write a curried function example.",
                Set.of("javascript", "frontend"), false,
                Set.of("currying", "partial application", "function"), "HARD"));

        q.add(new InterviewQuestion("Q542",
                "What is a generator function in JavaScript? What is the `yield` keyword?",
                Set.of("javascript", "frontend"), false,
                Set.of("generator", "yield", "iterator"), "HARD"));

        q.add(new InterviewQuestion("Q543",
                "What is optional chaining (`?.`) and nullish coalescing (`??`)? When are they useful?",
                Set.of("javascript", "frontend"), false,
                Set.of("optional chaining", "nullish coalescing"), "EASY"));

        q.add(new InterviewQuestion("Q544",
                "What is a Proxy in JavaScript? Give a use case.",
                Set.of("javascript", "frontend"), false,
                Set.of("proxy", "trap", "handler"), "HARD"));

        q.add(new InterviewQuestion("Q545",
                "What is the difference between shallow copy and deep copy in JavaScript?",
                Set.of("javascript", "frontend"), false,
                Set.of("shallow copy", "deep copy", "reference"), "MEDIUM"));

        q.add(new InterviewQuestion("Q546",
                "What is `Object.freeze()` and `Object.seal()`? How do they differ?",
                Set.of("javascript", "frontend"), false,
                Set.of("freeze", "immutable", "seal"), "MEDIUM"));

        q.add(new InterviewQuestion("Q547",
                "What is a Symbol in JavaScript? When would you use one?",
                Set.of("javascript", "frontend"), false,
                Set.of("symbol", "unique", "key"), "HARD"));

        q.add(new InterviewQuestion("Q548",
                "What is the temporal dead zone (TDZ) in JavaScript?",
                Set.of("javascript", "frontend"), false,
                Set.of("temporal dead zone", "let", "const"), "MEDIUM"));

        q.add(new InterviewQuestion("Q549",
                "What is memoization in JavaScript? Write a simple memoize function.",
                Set.of("javascript", "frontend", "performance"), false,
                Set.of("memoization", "cache", "performance"), "MEDIUM"));

        q.add(new InterviewQuestion("Q550",
                "What is a microtask vs a macrotask in the JavaScript event loop? Give examples of each.",
                Set.of("javascript", "frontend"), false,
                Set.of("microtask", "macrotask", "promise", "settimeout"), "HARD"));

        q.add(new InterviewQuestion("Q551",
                "What is the difference between `Array.from()` and the spread operator for converting iterables?",
                Set.of("javascript", "frontend"), false,
                Set.of("array.from", "spread", "iterable"), "EASY"));

        q.add(new InterviewQuestion("Q552",
                "What is `Promise.all()` vs `Promise.allSettled()` vs `Promise.race()`?",
                Set.of("javascript", "frontend", "async"), false,
                Set.of("promise.all", "promise.race", "concurrent"), "MEDIUM"));

        q.add(new InterviewQuestion("Q553",
                "How does `try/catch` work with `async/await`? What happens if you forget it?",
                Set.of("javascript", "frontend", "async"), false,
                Set.of("try", "catch", "async", "error handling"), "MEDIUM"));

        q.add(new InterviewQuestion("Q554",
                "What is a service worker? What problems does it solve?",
                Set.of("javascript", "frontend", "pwa"), false,
                Set.of("service worker", "cache", "offline"), "HARD"));

        q.add(new InterviewQuestion("Q555",
                "What is `typeof` vs `instanceof` in JavaScript?",
                Set.of("javascript", "frontend"), false,
                Set.of("typeof", "instanceof", "type check"), "EASY"));
    }

    // =====================================================================
    // React  (Q556-Q575)
    // =====================================================================
    private void addReact(List<InterviewQuestion> q) {
        q.add(new InterviewQuestion("Q556",
                "What problem does React solve? Why use a virtual DOM?",
                Set.of("react", "frontend"), false,
                Set.of("virtual dom", "reconciliation", "ui"), "EASY"));

        q.add(new InterviewQuestion("Q557",
                "What is JSX? How does it get compiled?",
                Set.of("react", "frontend"), false,
                Set.of("jsx", "babel", "createElement"), "EASY"));

        q.add(new InterviewQuestion("Q558",
                "What are React hooks? Why were they introduced?",
                Set.of("react", "frontend"), false,
                Set.of("hooks", "useState", "useEffect"), "EASY"));

        q.add(new InterviewQuestion("Q559",
                "Explain `useState`. What happens when you call the setter with the same value?",
                Set.of("react", "frontend"), false,
                Set.of("useState", "state", "re-render"), "EASY"));

        q.add(new InterviewQuestion("Q560",
                "Explain `useEffect`. What are the three ways to use the dependency array?",
                Set.of("react", "frontend"), false,
                Set.of("useEffect", "cleanup", "dependency"), "MEDIUM"));

        q.add(new InterviewQuestion("Q561",
                "What is reconciliation in React? How does React decide what to re-render?",
                Set.of("react", "frontend"), false,
                Set.of("reconciliation", "diffing", "fiber"), "HARD"));

        q.add(new InterviewQuestion("Q562",
                "What is lifting state up in React?",
                Set.of("react", "frontend"), false,
                Set.of("lifting state", "parent", "props"), "EASY"));

        q.add(new InterviewQuestion("Q563",
                "What is the Context API? When would you use it instead of prop drilling?",
                Set.of("react", "frontend"), false,
                Set.of("context", "provider", "consumer"), "MEDIUM"));

        q.add(new InterviewQuestion("Q564",
                "What is `useCallback`? When should you use it and what are the risks of overusing it?",
                Set.of("react", "frontend", "performance"), false,
                Set.of("useCallback", "memoize", "dependency"), "MEDIUM"));

        q.add(new InterviewQuestion("Q565",
                "What is `useMemo`? How does it differ from `useCallback`?",
                Set.of("react", "frontend", "performance"), false,
                Set.of("useMemo", "memoize", "expensive"), "MEDIUM"));

        q.add(new InterviewQuestion("Q566",
                "What is `useRef` used for? Give two different use cases.",
                Set.of("react", "frontend"), false,
                Set.of("useRef", "dom", "persist"), "MEDIUM"));

        q.add(new InterviewQuestion("Q567",
                "What is a controlled vs uncontrolled component in React?",
                Set.of("react", "frontend"), false,
                Set.of("controlled", "uncontrolled", "input", "ref"), "MEDIUM"));

        q.add(new InterviewQuestion("Q568",
                "Why are keys important in React lists? What are the risks of using array index as a key?",
                Set.of("react", "frontend"), false,
                Set.of("key", "list", "reconciliation"), "MEDIUM"));

        q.add(new InterviewQuestion("Q569",
                "What is `React.memo`? When should you use it?",
                Set.of("react", "frontend", "performance"), false,
                Set.of("memo", "re-render", "props"), "MEDIUM"));

        q.add(new InterviewQuestion("Q570",
                "What is a custom hook? Write an example of a `useFetch` custom hook.",
                Set.of("react", "frontend"), false,
                Set.of("custom hook", "reuse", "fetch"), "MEDIUM"));

        q.add(new InterviewQuestion("Q571",
                "What is `useReducer`? When would you prefer it over `useState`?",
                Set.of("react", "frontend"), false,
                Set.of("useReducer", "dispatch", "action"), "MEDIUM"));

        q.add(new InterviewQuestion("Q572",
                "What is React Suspense and lazy loading? How do they improve performance?",
                Set.of("react", "frontend", "performance"), false,
                Set.of("suspense", "lazy", "code splitting"), "HARD"));

        q.add(new InterviewQuestion("Q573",
                "What is a Higher-Order Component (HOC) in React?",
                Set.of("react", "frontend"), false,
                Set.of("hoc", "wrapper", "composition"), "HARD"));

        q.add(new InterviewQuestion("Q574",
                "What is the difference between state and props in React?",
                Set.of("react", "frontend"), false,
                Set.of("state", "props", "immutable"), "EASY"));

        q.add(new InterviewQuestion("Q575",
                "How would you optimize a React application that re-renders too often?",
                Set.of("react", "frontend", "performance"), false,
                Set.of("memo", "useCallback", "profiler", "re-render"), "HARD"));
    }

    // =====================================================================
    // TypeScript  (Q576-Q590)
    // =====================================================================
    private void addTypeScript(List<InterviewQuestion> q) {
        q.add(new InterviewQuestion("Q576",
                "What is TypeScript and what are its main benefits over plain JavaScript?",
                Set.of("typescript", "frontend"), false,
                Set.of("static types", "compile", "safety"), "EASY"));

        q.add(new InterviewQuestion("Q577",
                "What is the difference between `type` and `interface` in TypeScript?",
                Set.of("typescript", "frontend"), false,
                Set.of("type", "interface", "extends"), "MEDIUM"));

        q.add(new InterviewQuestion("Q578",
                "What is a generic type in TypeScript? Write a generic `identity` function.",
                Set.of("typescript", "frontend"), false,
                Set.of("generic", "T", "type parameter"), "MEDIUM"));

        q.add(new InterviewQuestion("Q579",
                "What is the difference between `any`, `unknown`, and `never` in TypeScript?",
                Set.of("typescript", "frontend"), false,
                Set.of("any", "unknown", "never", "type safety"), "HARD"));

        q.add(new InterviewQuestion("Q580",
                "What is a union type and an intersection type in TypeScript?",
                Set.of("typescript", "frontend"), false,
                Set.of("union", "intersection", "|", "&"), "MEDIUM"));

        q.add(new InterviewQuestion("Q581",
                "What is `keyof` in TypeScript? Give a practical example.",
                Set.of("typescript", "frontend"), false,
                Set.of("keyof", "mapped type", "key"), "HARD"));

        q.add(new InterviewQuestion("Q582",
                "What are `Partial<T>`, `Required<T>`, `Pick<T,K>`, and `Omit<T,K>` utility types?",
                Set.of("typescript", "frontend"), false,
                Set.of("partial", "pick", "omit", "utility types"), "HARD"));

        q.add(new InterviewQuestion("Q583",
                "What is a type assertion in TypeScript? When is it appropriate to use `as`?",
                Set.of("typescript", "frontend"), false,
                Set.of("as", "type assertion", "cast"), "MEDIUM"));

        q.add(new InterviewQuestion("Q584",
                "What is the `readonly` modifier in TypeScript?",
                Set.of("typescript", "frontend"), false,
                Set.of("readonly", "immutable"), "EASY"));

        q.add(new InterviewQuestion("Q585",
                "What is a discriminated union (tagged union) in TypeScript?",
                Set.of("typescript", "frontend"), false,
                Set.of("discriminated union", "literal type", "narrowing"), "HARD"));
    }

    // =====================================================================
    // Data Structures  (Q586-Q610)
    // =====================================================================
    private void addDataStructures(List<InterviewQuestion> q) {
        q.add(new InterviewQuestion("Q586",
                "What is a linked list? What are the trade-offs vs an array?",
                Set.of("data-structures", "algorithms"), false,
                Set.of("linked list", "node", "pointer"), "EASY"));

        q.add(new InterviewQuestion("Q587",
                "What is the difference between a stack and a queue? Give a real-world use case for each.",
                Set.of("data-structures", "algorithms"), false,
                Set.of("stack", "queue", "lifo", "fifo"), "EASY"));

        q.add(new InterviewQuestion("Q588",
                "What is a hash table? How does it handle collisions?",
                Set.of("data-structures", "algorithms"), false,
                Set.of("hash table", "collision", "chaining", "probing"), "MEDIUM"));

        q.add(new InterviewQuestion("Q589",
                "What is a binary search tree (BST)? What are its average and worst-case complexities for search, insert, and delete?",
                Set.of("data-structures", "algorithms"), false,
                Set.of("bst", "balanced", "O(log n)"), "MEDIUM"));

        q.add(new InterviewQuestion("Q590",
                "What is a heap data structure? What is a min-heap vs a max-heap?",
                Set.of("data-structures", "algorithms"), false,
                Set.of("heap", "min-heap", "max-heap", "priority queue"), "MEDIUM"));

        q.add(new InterviewQuestion("Q591",
                "What is the difference between BFS and DFS? When would you use each?",
                Set.of("data-structures", "algorithms"), false,
                Set.of("bfs", "dfs", "graph", "tree"), "MEDIUM"));

        q.add(new InterviewQuestion("Q592",
                "What is a graph? What is the difference between an adjacency matrix and an adjacency list?",
                Set.of("data-structures", "algorithms"), false,
                Set.of("graph", "adjacency", "edge", "vertex"), "MEDIUM"));

        q.add(new InterviewQuestion("Q593",
                "What is Big O notation? Why is it used instead of measuring exact execution time?",
                Set.of("data-structures", "algorithms", "complexity"), false,
                Set.of("big o", "time complexity", "space complexity"), "EASY"));

        q.add(new InterviewQuestion("Q594",
                "What is a balanced binary tree? What is the difference between AVL trees and Red-Black trees?",
                Set.of("data-structures", "algorithms"), false,
                Set.of("avl", "red-black", "balanced", "rotation"), "HARD"));

        q.add(new InterviewQuestion("Q595",
                "What is a trie? What problems is it particularly suited for?",
                Set.of("data-structures", "algorithms"), false,
                Set.of("trie", "prefix", "string"), "HARD"));

        q.add(new InterviewQuestion("Q596",
                "What is an LRU cache? How would you implement one efficiently?",
                Set.of("data-structures", "algorithms"), false,
                Set.of("lru", "hashmap", "doubly linked list", "eviction"), "HARD"));

        q.add(new InterviewQuestion("Q597",
                "What is the sliding window technique? Give an example problem it solves.",
                Set.of("data-structures", "algorithms"), false,
                Set.of("sliding window", "subarray", "two pointer"), "MEDIUM"));

        q.add(new InterviewQuestion("Q598",
                "What is the two-pointer technique? Give an example.",
                Set.of("data-structures", "algorithms"), false,
                Set.of("two pointer", "array", "sorted"), "MEDIUM"));

        q.add(new InterviewQuestion("Q599",
                "What is dynamic programming? How does it differ from divide-and-conquer?",
                Set.of("data-structures", "algorithms"), false,
                Set.of("dynamic programming", "memoization", "subproblem"), "HARD"));

        q.add(new InterviewQuestion("Q600",
                "What is a priority queue? How is it typically implemented?",
                Set.of("data-structures", "algorithms"), false,
                Set.of("priority queue", "heap", "ordering"), "MEDIUM"));

        q.add(new InterviewQuestion("Q601",
                "What is the time complexity of common array operations: access, search, insert at end, insert at middle?",
                Set.of("data-structures", "algorithms", "complexity"), false,
                Set.of("O(1)", "O(n)", "array"), "EASY"));

        q.add(new InterviewQuestion("Q602",
                "What is backtracking? Give an example problem that uses it.",
                Set.of("algorithms"), false,
                Set.of("backtracking", "recursion", "constraint"), "HARD"));

        q.add(new InterviewQuestion("Q603",
                "What is a deque (double-ended queue)? When would you use it over a regular queue?",
                Set.of("data-structures", "algorithms"), false,
                Set.of("deque", "double-ended", "both ends"), "MEDIUM"));

        q.add(new InterviewQuestion("Q604",
                "What is the difference between a tree and a graph?",
                Set.of("data-structures", "algorithms"), false,
                Set.of("tree", "graph", "cycle", "root"), "EASY"));

        q.add(new InterviewQuestion("Q605",
                "How would you detect a cycle in a linked list?",
                Set.of("data-structures", "algorithms"), false,
                Set.of("cycle", "slow fast pointer", "floyd"), "MEDIUM"));
    }

    // =====================================================================
    // Algorithms  (Q606-Q625)
    // =====================================================================
    private void addAlgorithms(List<InterviewQuestion> q) {
        q.add(new InterviewQuestion("Q606",
                "Explain quicksort. What is its average and worst-case time complexity?",
                Set.of("algorithms", "sorting"), false,
                Set.of("quicksort", "pivot", "O(n log n)", "O(n^2)"), "MEDIUM"));

        q.add(new InterviewQuestion("Q607",
                "Explain merge sort. Why is it preferred over quicksort in some situations?",
                Set.of("algorithms", "sorting"), false,
                Set.of("merge sort", "stable", "O(n log n)"), "MEDIUM"));

        q.add(new InterviewQuestion("Q608",
                "What is binary search? What are its preconditions and time complexity?",
                Set.of("algorithms"), false,
                Set.of("binary search", "sorted", "O(log n)"), "EASY"));

        q.add(new InterviewQuestion("Q609",
                "How would you reverse a linked list in-place?",
                Set.of("algorithms", "data-structures"), false,
                Set.of("reverse", "pointer", "in-place"), "MEDIUM"));

        q.add(new InterviewQuestion("Q610",
                "How would you find the middle element of a linked list in one pass?",
                Set.of("algorithms", "data-structures"), false,
                Set.of("slow fast pointer", "middle"), "MEDIUM"));

        q.add(new InterviewQuestion("Q611",
                "What is topological sorting? What kind of graph requires it?",
                Set.of("algorithms"), false,
                Set.of("topological sort", "dag", "dependency"), "HARD"));

        q.add(new InterviewQuestion("Q612",
                "What is Dijkstra's algorithm? What type of problems does it solve?",
                Set.of("algorithms"), false,
                Set.of("dijkstra", "shortest path", "weighted graph"), "HARD"));

        q.add(new InterviewQuestion("Q613",
                "What is the difference between greedy algorithms and dynamic programming?",
                Set.of("algorithms"), false,
                Set.of("greedy", "dynamic programming", "optimal"), "HARD"));

        q.add(new InterviewQuestion("Q614",
                "How would you check if a string is a palindrome? What is the time complexity?",
                Set.of("algorithms"), false,
                Set.of("palindrome", "two pointer", "O(n)"), "EASY"));

        q.add(new InterviewQuestion("Q615",
                "What is Kadane's algorithm? What problem does it solve?",
                Set.of("algorithms"), false,
                Set.of("kadane", "maximum subarray", "O(n)"), "MEDIUM"));

        q.add(new InterviewQuestion("Q616",
                "What is the difference between stable and unstable sorting algorithms? Why does it matter?",
                Set.of("algorithms", "sorting"), false,
                Set.of("stable", "unstable", "equal elements"), "MEDIUM"));

        q.add(new InterviewQuestion("Q617",
                "How would you find all permutations of a string? What is the time complexity?",
                Set.of("algorithms"), false,
                Set.of("permutation", "backtracking", "O(n!)"), "HARD"));

        q.add(new InterviewQuestion("Q618",
                "What is a divide-and-conquer algorithm? Give two examples.",
                Set.of("algorithms"), false,
                Set.of("divide and conquer", "merge sort", "binary search"), "MEDIUM"));

        q.add(new InterviewQuestion("Q619",
                "How would you implement BFS? What data structure does it rely on?",
                Set.of("algorithms"), false,
                Set.of("bfs", "queue", "visited"), "MEDIUM"));

        q.add(new InterviewQuestion("Q620",
                "How would you implement DFS iteratively (without recursion)?",
                Set.of("algorithms"), false,
                Set.of("dfs", "stack", "iterative"), "HARD"));
    }

    // =====================================================================
    // Networking  (Q621-Q640)
    // =====================================================================
    private void addNetworking(List<InterviewQuestion> q) {
        q.add(new InterviewQuestion("Q621",
                "What is the difference between HTTP and HTTPS? How does TLS work at a high level?",
                Set.of("networking", "security"), false,
                Set.of("http", "https", "tls", "encryption"), "EASY"));

        q.add(new InterviewQuestion("Q622",
                "What is DNS and how does a DNS lookup work?",
                Set.of("networking"), false,
                Set.of("dns", "resolver", "record"), "EASY"));

        q.add(new InterviewQuestion("Q623",
                "What is the difference between TCP and UDP? When would you use UDP?",
                Set.of("networking"), false,
                Set.of("tcp", "udp", "reliable", "stream"), "MEDIUM"));

        q.add(new InterviewQuestion("Q624",
                "What happens step-by-step when you type a URL and press Enter in a browser?",
                Set.of("networking", "frontend"), false,
                Set.of("dns", "tcp", "http", "render"), "MEDIUM"));

        q.add(new InterviewQuestion("Q625",
                "What are the main HTTP status code categories (1xx, 2xx, 3xx, 4xx, 5xx)? Give one example from each.",
                Set.of("networking", "http", "rest"), false,
                Set.of("200", "404", "500", "redirect"), "EASY"));

        q.add(new InterviewQuestion("Q626",
                "What is a WebSocket? How does it differ from a regular HTTP request?",
                Set.of("networking", "frontend"), false,
                Set.of("websocket", "bidirectional", "persistent"), "MEDIUM"));

        q.add(new InterviewQuestion("Q627",
                "What is GraphQL? How does it differ from a REST API?",
                Set.of("networking", "api"), false,
                Set.of("graphql", "query", "schema", "overfetching"), "MEDIUM"));

        q.add(new InterviewQuestion("Q628",
                "What is a CDN? How does it improve performance?",
                Set.of("networking", "performance"), false,
                Set.of("cdn", "edge", "latency", "cache"), "EASY"));

        q.add(new InterviewQuestion("Q629",
                "What is load balancing? What are round-robin, least-connections, and IP-hash strategies?",
                Set.of("networking", "devops"), false,
                Set.of("load balancer", "round-robin", "distribute"), "MEDIUM"));

        q.add(new InterviewQuestion("Q630",
                "What is OAuth2? Describe the authorization code flow.",
                Set.of("networking", "security"), false,
                Set.of("oauth2", "authorization code", "token"), "HARD"));

        q.add(new InterviewQuestion("Q631",
                "What is the OSI model? Name all 7 layers and what they are responsible for.",
                Set.of("networking"), false,
                Set.of("osi", "physical", "transport", "application"), "MEDIUM"));

        q.add(new InterviewQuestion("Q632",
                "What is a reverse proxy? Give two examples of what it is used for.",
                Set.of("networking", "devops"), false,
                Set.of("reverse proxy", "nginx", "ssl termination"), "MEDIUM"));

        q.add(new InterviewQuestion("Q633",
                "What is caching? Describe cache-aside vs write-through vs write-back strategies.",
                Set.of("networking", "performance"), false,
                Set.of("cache", "cache-aside", "write-through"), "HARD"));

        q.add(new InterviewQuestion("Q634",
                "What is gRPC and how does it compare to REST?",
                Set.of("networking", "api"), false,
                Set.of("grpc", "protobuf", "streaming"), "HARD"));

        q.add(new InterviewQuestion("Q635",
                "What is HTTP/2 and what improvements does it bring over HTTP/1.1?",
                Set.of("networking", "performance"), false,
                Set.of("http2", "multiplexing", "header compression"), "MEDIUM"));

        q.add(new InterviewQuestion("Q636",
                "What is the difference between authentication and authorization?",
                Set.of("networking", "security"), false,
                Set.of("authentication", "authorization", "identity"), "EASY"));

        q.add(new InterviewQuestion("Q637",
                "What is a cookie? What is the difference between `HttpOnly` and `Secure` flags?",
                Set.of("networking", "security"), false,
                Set.of("cookie", "httponly", "secure", "session"), "MEDIUM"));

        q.add(new InterviewQuestion("Q638",
                "What is a message queue? Why use one instead of direct API calls between services?",
                Set.of("networking", "devops", "architecture"), false,
                Set.of("message queue", "async", "decoupled", "rabbitmq"), "MEDIUM"));

        q.add(new InterviewQuestion("Q639",
                "What is polling vs long polling vs server-sent events (SSE)?",
                Set.of("networking", "frontend"), false,
                Set.of("polling", "sse", "real-time"), "MEDIUM"));

        q.add(new InterviewQuestion("Q640",
                "What is the difference between a public API and a private API? How do you version an API?",
                Set.of("networking", "api"), false,
                Set.of("versioning", "v1", "backward compatible"), "EASY"));
    }

    // =====================================================================
    // OS / Concurrency  (Q641-Q655)
    // =====================================================================
    private void addOsConcurrency(List<InterviewQuestion> q) {
        q.add(new InterviewQuestion("Q641",
                "What is the difference between a process and a thread?",
                Set.of("os", "concurrency"), false,
                Set.of("process", "thread", "memory", "isolation"), "MEDIUM"));

        q.add(new InterviewQuestion("Q642",
                "What is a deadlock? What are the four necessary conditions for it?",
                Set.of("os", "concurrency"), false,
                Set.of("deadlock", "mutual exclusion", "circular wait"), "MEDIUM"));

        q.add(new InterviewQuestion("Q643",
                "What is a race condition? How do you prevent one?",
                Set.of("os", "concurrency"), false,
                Set.of("race condition", "lock", "synchronize"), "MEDIUM"));

        q.add(new InterviewQuestion("Q644",
                "What is a mutex vs a semaphore?",
                Set.of("os", "concurrency"), false,
                Set.of("mutex", "semaphore", "lock"), "MEDIUM"));

        q.add(new InterviewQuestion("Q645",
                "What is the difference between concurrency and parallelism?",
                Set.of("os", "concurrency"), false,
                Set.of("concurrency", "parallelism", "multi-core"), "MEDIUM"));

        q.add(new InterviewQuestion("Q646",
                "What is a thread pool and why is it useful?",
                Set.of("os", "concurrency"), false,
                Set.of("thread pool", "reuse", "overhead"), "MEDIUM"));

        q.add(new InterviewQuestion("Q647",
                "What is virtual memory? How does it differ from physical memory?",
                Set.of("os"), false,
                Set.of("virtual memory", "paging", "swap"), "HARD"));

        q.add(new InterviewQuestion("Q648",
                "What is garbage collection? What is the difference between stop-the-world and concurrent GC?",
                Set.of("os", "jvm"), false,
                Set.of("garbage collection", "stop the world", "heap"), "HARD"));

        q.add(new InterviewQuestion("Q649",
                "What is an atomic operation? Why is it important for thread safety?",
                Set.of("os", "concurrency"), false,
                Set.of("atomic", "thread safety", "compare and swap"), "HARD"));

        q.add(new InterviewQuestion("Q650",
                "What is the producer-consumer problem? How is it typically solved?",
                Set.of("os", "concurrency"), false,
                Set.of("producer consumer", "blocking queue", "semaphore"), "HARD"));

        q.add(new InterviewQuestion("Q651",
                "What is context switching and what is its cost?",
                Set.of("os", "concurrency"), false,
                Set.of("context switch", "overhead", "cpu"), "MEDIUM"));

        q.add(new InterviewQuestion("Q652",
                "What is a CPU cache? What is cache coherence in multicore systems?",
                Set.of("os"), false,
                Set.of("l1", "l2", "cache miss", "coherence"), "HARD"));

        q.add(new InterviewQuestion("Q653",
                "What is an I/O-bound vs a CPU-bound task? How does this affect your concurrency strategy?",
                Set.of("os", "concurrency", "performance"), false,
                Set.of("io-bound", "cpu-bound", "thread", "async"), "MEDIUM"));

        q.add(new InterviewQuestion("Q654",
                "What is memory leak? How do you detect and prevent one?",
                Set.of("os", "performance"), false,
                Set.of("memory leak", "heap", "profiler"), "MEDIUM"));

        q.add(new InterviewQuestion("Q655",
                "What is the difference between optimistic and pessimistic locking?",
                Set.of("os", "concurrency", "database"), false,
                Set.of("optimistic", "pessimistic", "version", "lock"), "HARD"));
    }

    // =====================================================================
    // Behavioral  (Q656-Q695)
    // =====================================================================
    private void addBehavioral(List<InterviewQuestion> q) {
        q.add(new InterviewQuestion("Q656",
                "Tell me about a time you had to learn a new technology quickly. How did you approach it?",
                Set.of("behavioral"), false, Set.of(), "EASY"));

        q.add(new InterviewQuestion("Q657",
                "Describe the most challenging bug you have debugged. How did you find it?",
                Set.of("behavioral"), false, Set.of(), "EASY"));

        q.add(new InterviewQuestion("Q658",
                "How do you handle a disagreement with a teammate about a technical decision?",
                Set.of("behavioral"), false, Set.of(), "EASY"));

        q.add(new InterviewQuestion("Q659",
                "Tell me about a project you are most proud of. What was your contribution?",
                Set.of("behavioral"), false, Set.of(), "EASY"));

        q.add(new InterviewQuestion("Q660",
                "How do you prioritize tasks when everything feels urgent?",
                Set.of("behavioral"), false, Set.of(), "EASY"));

        q.add(new InterviewQuestion("Q661",
                "Tell me about a time you failed on a project. What did you learn?",
                Set.of("behavioral"), false, Set.of(), "EASY"));

        q.add(new InterviewQuestion("Q662",
                "How do you stay current with new technologies and best practices?",
                Set.of("behavioral"), false, Set.of(), "EASY"));

        q.add(new InterviewQuestion("Q663",
                "Describe a time you worked under a very tight deadline. What did you do?",
                Set.of("behavioral"), false, Set.of(), "EASY"));

        q.add(new InterviewQuestion("Q664",
                "How do you handle critical feedback during a code review?",
                Set.of("behavioral"), false, Set.of(), "EASY"));

        q.add(new InterviewQuestion("Q665",
                "Tell me about a time you improved a process or workflow.",
                Set.of("behavioral"), false, Set.of(), "EASY"));

        q.add(new InterviewQuestion("Q666",
                "How do you approach estimating tasks? What happens when you underestimate?",
                Set.of("behavioral"), false, Set.of(), "EASY"));

        q.add(new InterviewQuestion("Q667",
                "Describe a situation where you had to collaborate with a difficult team member.",
                Set.of("behavioral"), false, Set.of(), "EASY"));

        q.add(new InterviewQuestion("Q668",
                "Tell me about a time you went above and beyond what was expected.",
                Set.of("behavioral"), false, Set.of(), "EASY"));

        q.add(new InterviewQuestion("Q669",
                "How do you handle ambiguous or unclear requirements from a stakeholder?",
                Set.of("behavioral"), false, Set.of(), "EASY"));

        q.add(new InterviewQuestion("Q670",
                "Describe a time you had to make an important decision with incomplete information.",
                Set.of("behavioral"), false, Set.of(), "EASY"));

        q.add(new InterviewQuestion("Q671",
                "Tell me about a time you had to refactor existing code. What was the outcome?",
                Set.of("behavioral"), false, Set.of(), "EASY"));

        q.add(new InterviewQuestion("Q672",
                "How do you balance paying down technical debt with delivering new features?",
                Set.of("behavioral"), false, Set.of(), "EASY"));

        q.add(new InterviewQuestion("Q673",
                "Describe a time you disagreed with a technical or architectural decision your team made.",
                Set.of("behavioral"), false, Set.of(), "EASY"));

        q.add(new InterviewQuestion("Q674",
                "Tell me about a time you had to deal with a production incident. What did you do?",
                Set.of("behavioral"), false, Set.of(), "EASY"));

        q.add(new InterviewQuestion("Q675",
                "How do you ensure the quality of your code before submitting a pull request?",
                Set.of("behavioral"), false, Set.of(), "EASY"));

        q.add(new InterviewQuestion("Q676",
                "Tell me about a time you improved the performance of a system.",
                Set.of("behavioral"), false, Set.of(), "EASY"));

        q.add(new InterviewQuestion("Q677",
                "Describe a time you had to work with legacy code. What challenges did you face?",
                Set.of("behavioral"), false, Set.of(), "EASY"));

        q.add(new InterviewQuestion("Q678",
                "Tell me about a time you identified a problem before it became critical.",
                Set.of("behavioral"), false, Set.of(), "EASY"));

        q.add(new InterviewQuestion("Q679",
                "How do you approach onboarding onto an unfamiliar codebase?",
                Set.of("behavioral"), false, Set.of(), "EASY"));

        q.add(new InterviewQuestion("Q680",
                "What does good code look like to you? What principles guide your writing?",
                Set.of("behavioral"), false, Set.of(), "EASY"));

        q.add(new InterviewQuestion("Q681",
                "Tell me about a time you had to deliver bad news to a manager or team.",
                Set.of("behavioral"), false, Set.of(), "EASY"));

        q.add(new InterviewQuestion("Q682",
                "How do you handle scope creep during a project?",
                Set.of("behavioral"), false, Set.of(), "EASY"));

        q.add(new InterviewQuestion("Q683",
                "What motivates you as a software engineer?",
                Set.of("behavioral"), false, Set.of(), "EASY"));

        q.add(new InterviewQuestion("Q684",
                "How do you give constructive feedback to a colleague?",
                Set.of("behavioral"), false, Set.of(), "EASY"));

        q.add(new InterviewQuestion("Q685",
                "Tell me about your biggest technical achievement so far.",
                Set.of("behavioral"), false, Set.of(), "EASY"));

        q.add(new InterviewQuestion("Q686",
                "How do you approach cross-team collaboration when teams have different priorities?",
                Set.of("behavioral"), false, Set.of(), "EASY"));

        q.add(new InterviewQuestion("Q687",
                "Tell me about a time you had to pivot mid-project due to changing requirements.",
                Set.of("behavioral"), false, Set.of(), "EASY"));

        q.add(new InterviewQuestion("Q688",
                "How do you stay productive and focused when working remotely?",
                Set.of("behavioral"), false, Set.of(), "EASY"));

        q.add(new InterviewQuestion("Q689",
                "Describe a time you built something from scratch. How did you plan and execute it?",
                Set.of("behavioral"), false, Set.of(), "EASY"));

        q.add(new InterviewQuestion("Q690",
                "How do you keep yourself accountable on a project with no micromanagement?",
                Set.of("behavioral"), false, Set.of(), "EASY"));

        q.add(new InterviewQuestion("Q691",
                "Tell me about a time you had to compromise on a technical decision.",
                Set.of("behavioral"), false, Set.of(), "EASY"));

        q.add(new InterviewQuestion("Q692",
                "How do you approach API design? What makes a good API?",
                Set.of("behavioral"), false, Set.of(), "EASY"));

        q.add(new InterviewQuestion("Q693",
                "Describe your process when starting a new feature end-to-end.",
                Set.of("behavioral"), false, Set.of(), "EASY"));

        q.add(new InterviewQuestion("Q694",
                "Tell me about a time you mentored or helped a junior developer grow.",
                Set.of("behavioral"), false, Set.of(), "EASY"));

        q.add(new InterviewQuestion("Q695",
                "Where do you see yourself in 3-5 years as a software engineer?",
                Set.of("behavioral"), false, Set.of(), "EASY"));
    }

    // =====================================================================
    // Cloud / AWS  (Q696-Q715)
    // =====================================================================
    private void addCloud(List<InterviewQuestion> q) {
        q.add(new InterviewQuestion("Q696",
                "What is cloud computing? What are the main benefits of moving to the cloud?",
                Set.of("cloud", "devops"), false,
                Set.of("scalability", "managed", "on-demand"), "EASY"));

        q.add(new InterviewQuestion("Q697",
                "What is the difference between IaaS, PaaS, and SaaS? Give one example of each.",
                Set.of("cloud", "devops"), false,
                Set.of("iaas", "paas", "saas"), "EASY"));

        q.add(new InterviewQuestion("Q698",
                "What is AWS EC2? What is an AMI?",
                Set.of("cloud", "aws"), false,
                Set.of("ec2", "ami", "instance", "virtual machine"), "EASY"));

        q.add(new InterviewQuestion("Q699",
                "What is AWS S3? What are common use cases?",
                Set.of("cloud", "aws"), false,
                Set.of("s3", "bucket", "object storage"), "EASY"));

        q.add(new InterviewQuestion("Q700",
                "What is serverless computing? What is AWS Lambda?",
                Set.of("cloud", "aws"), false,
                Set.of("lambda", "serverless", "function as a service"), "EASY"));

        q.add(new InterviewQuestion("Q701",
                "What is horizontal scaling vs vertical scaling? When is each appropriate?",
                Set.of("cloud", "devops"), false,
                Set.of("horizontal", "vertical", "scale out", "scale up"), "EASY"));

        q.add(new InterviewQuestion("Q702",
                "What is an auto-scaling group in AWS? How does it work?",
                Set.of("cloud", "aws"), false,
                Set.of("auto scaling", "min", "max", "trigger"), "MEDIUM"));

        q.add(new InterviewQuestion("Q703",
                "What is AWS RDS? When would you use RDS vs a self-managed database?",
                Set.of("cloud", "aws", "database"), false,
                Set.of("rds", "managed", "backup", "replica"), "MEDIUM"));

        q.add(new InterviewQuestion("Q704",
                "What is a VPC (Virtual Private Cloud)? What is a subnet?",
                Set.of("cloud", "aws", "networking"), false,
                Set.of("vpc", "subnet", "private", "public"), "MEDIUM"));

        q.add(new InterviewQuestion("Q705",
                "What is AWS IAM? What is the principle of least privilege in the context of IAM?",
                Set.of("cloud", "aws", "security"), false,
                Set.of("iam", "role", "policy", "least privilege"), "MEDIUM"));

        q.add(new InterviewQuestion("Q706",
                "What is infrastructure as code (IaC)? Name two tools used for it.",
                Set.of("cloud", "devops"), false,
                Set.of("iac", "terraform", "cloudformation"), "MEDIUM"));

        q.add(new InterviewQuestion("Q707",
                "What is blue-green deployment? What is a canary release?",
                Set.of("cloud", "devops"), false,
                Set.of("blue-green", "canary", "zero downtime"), "MEDIUM"));

        q.add(new InterviewQuestion("Q708",
                "What is AWS SQS vs SNS? What is the difference between a queue and a pub/sub system?",
                Set.of("cloud", "aws"), false,
                Set.of("sqs", "sns", "queue", "pub/sub"), "MEDIUM"));

        q.add(new InterviewQuestion("Q709",
                "What is disaster recovery (DR)? What is RTO vs RPO?",
                Set.of("cloud", "devops"), false,
                Set.of("rto", "rpo", "backup", "failover"), "HARD"));

        q.add(new InterviewQuestion("Q710",
                "What is AWS CloudWatch? What is the difference between a metric, a log, and an alarm?",
                Set.of("cloud", "aws", "monitoring"), false,
                Set.of("cloudwatch", "metric", "log", "alarm"), "MEDIUM"));

        q.add(new InterviewQuestion("Q711",
                "What is Terraform? How does it compare to Ansible?",
                Set.of("cloud", "devops"), false,
                Set.of("terraform", "iac", "idempotent"), "MEDIUM"));

        q.add(new InterviewQuestion("Q712",
                "What is a deployment pipeline? What stages does a typical CI/CD pipeline have?",
                Set.of("cloud", "devops"), false,
                Set.of("ci/cd", "build", "test", "deploy"), "EASY"));

        q.add(new InterviewQuestion("Q713",
                "What is AWS ECS vs EKS? When would you use each?",
                Set.of("cloud", "aws", "containers"), false,
                Set.of("ecs", "eks", "fargate", "kubernetes"), "HARD"));

        q.add(new InterviewQuestion("Q714",
                "What is a distributed tracing system? Name two tools used for it.",
                Set.of("cloud", "devops", "monitoring"), false,
                Set.of("tracing", "jaeger", "zipkin", "opentelemetry"), "HARD"));

        q.add(new InterviewQuestion("Q715",
                "What is observability? What are the three pillars?",
                Set.of("cloud", "devops", "monitoring"), false,
                Set.of("logs", "metrics", "traces"), "MEDIUM"));
    }

    // =====================================================================
    // Docker / Kubernetes  (Q716-Q740)
    // =====================================================================
    private void addDockerKubernetes(List<InterviewQuestion> q) {
        q.add(new InterviewQuestion("Q716",
                "What is Docker and what problem does it solve?",
                Set.of("docker", "devops"), false,
                Set.of("container", "isolation", "portability"), "EASY"));

        q.add(new InterviewQuestion("Q717",
                "What is the difference between a Docker image and a container?",
                Set.of("docker", "devops"), false,
                Set.of("image", "container", "read-only", "layer"), "EASY"));

        q.add(new InterviewQuestion("Q718",
                "What is a Dockerfile? Describe its most commonly used instructions.",
                Set.of("docker", "devops"), false,
                Set.of("from", "run", "copy", "cmd", "entrypoint"), "EASY"));

        q.add(new InterviewQuestion("Q719",
                "What is Docker Compose? What problem does it solve?",
                Set.of("docker", "devops"), false,
                Set.of("compose", "multi-container", "service"), "EASY"));

        q.add(new InterviewQuestion("Q720",
                "What is a multi-stage Docker build? Why is it useful?",
                Set.of("docker", "devops"), false,
                Set.of("multi-stage", "build", "smaller image"), "MEDIUM"));

        q.add(new InterviewQuestion("Q721",
                "What is Docker networking? What is the difference between bridge, host, and none networks?",
                Set.of("docker", "devops", "networking"), false,
                Set.of("bridge", "host", "network"), "MEDIUM"));

        q.add(new InterviewQuestion("Q722",
                "What is Kubernetes and what problem does it solve?",
                Set.of("kubernetes", "devops"), false,
                Set.of("orchestration", "container", "scheduling"), "EASY"));

        q.add(new InterviewQuestion("Q723",
                "What is a Pod in Kubernetes? Can a Pod contain multiple containers?",
                Set.of("kubernetes", "devops"), false,
                Set.of("pod", "container", "sidecar"), "EASY"));

        q.add(new InterviewQuestion("Q724",
                "What is a Deployment in Kubernetes? How is it different from a ReplicaSet?",
                Set.of("kubernetes", "devops"), false,
                Set.of("deployment", "replicaset", "rolling update"), "MEDIUM"));

        q.add(new InterviewQuestion("Q725",
                "What is a Kubernetes Service? What are the types (ClusterIP, NodePort, LoadBalancer)?",
                Set.of("kubernetes", "devops"), false,
                Set.of("service", "clusterip", "loadbalancer"), "MEDIUM"));

        q.add(new InterviewQuestion("Q726",
                "What is a namespace in Kubernetes? Why would you use multiple namespaces?",
                Set.of("kubernetes", "devops"), false,
                Set.of("namespace", "isolation", "environment"), "MEDIUM"));

        q.add(new InterviewQuestion("Q727",
                "What is a ConfigMap and a Secret in Kubernetes? How do they differ?",
                Set.of("kubernetes", "devops"), false,
                Set.of("configmap", "secret", "environment variable"), "MEDIUM"));

        q.add(new InterviewQuestion("Q728",
                "What is a liveness probe vs a readiness probe in Kubernetes?",
                Set.of("kubernetes", "devops"), false,
                Set.of("liveness", "readiness", "health check"), "MEDIUM"));

        q.add(new InterviewQuestion("Q729",
                "What is Kubernetes Ingress? How does it differ from a Service?",
                Set.of("kubernetes", "devops", "networking"), false,
                Set.of("ingress", "routing", "host"), "MEDIUM"));

        q.add(new InterviewQuestion("Q730",
                "What is a Helm chart? What problem does Helm solve?",
                Set.of("kubernetes", "devops"), false,
                Set.of("helm", "chart", "template", "package manager"), "MEDIUM"));

        q.add(new InterviewQuestion("Q731",
                "What is Kubernetes autoscaling? What is HPA vs VPA?",
                Set.of("kubernetes", "devops"), false,
                Set.of("hpa", "vpa", "autoscale", "replicas"), "HARD"));

        q.add(new InterviewQuestion("Q732",
                "What is a DaemonSet in Kubernetes? Give a use case.",
                Set.of("kubernetes", "devops"), false,
                Set.of("daemonset", "every node", "log collector"), "HARD"));

        q.add(new InterviewQuestion("Q733",
                "What is a StatefulSet in Kubernetes? How does it differ from a Deployment?",
                Set.of("kubernetes", "devops"), false,
                Set.of("statefulset", "stable identity", "persistent storage"), "HARD"));

        q.add(new InterviewQuestion("Q734",
                "What is container resource limits and requests in Kubernetes?",
                Set.of("kubernetes", "devops"), false,
                Set.of("limits", "requests", "cpu", "memory"), "MEDIUM"));

        q.add(new InterviewQuestion("Q735",
                "What is the difference between Docker Swarm and Kubernetes?",
                Set.of("docker", "kubernetes", "devops"), false,
                Set.of("swarm", "kubernetes", "orchestration"), "MEDIUM"));
    }

    // =====================================================================
    // Web Security  (Q736-Q760)
    // =====================================================================
    private void addWebSecurity(List<InterviewQuestion> q) {
        q.add(new InterviewQuestion("Q736",
                "What is XSS (Cross-Site Scripting)? What are the three types and how do you prevent it?",
                Set.of("security", "web-security", "owasp"), false,
                Set.of("xss", "sanitize", "csp", "escape"), "MEDIUM"));

        q.add(new InterviewQuestion("Q737",
                "What is CSRF (Cross-Site Request Forgery)? How do you prevent it?",
                Set.of("security", "web-security", "owasp"), false,
                Set.of("csrf", "token", "samesite"), "MEDIUM"));

        q.add(new InterviewQuestion("Q738",
                "What is SQL injection? How do prepared statements prevent it?",
                Set.of("security", "web-security", "owasp", "database"), false,
                Set.of("sql injection", "prepared statement", "parameterized"), "EASY"));

        q.add(new InterviewQuestion("Q739",
                "What is the OWASP Top 10? Name at least five items from it.",
                Set.of("security", "web-security", "owasp"), false,
                Set.of("owasp", "injection", "broken auth", "xss"), "MEDIUM"));

        q.add(new InterviewQuestion("Q740",
                "What is a Content Security Policy (CSP)? How does it help prevent XSS?",
                Set.of("security", "web-security"), false,
                Set.of("csp", "header", "whitelist"), "MEDIUM"));

        q.add(new InterviewQuestion("Q741",
                "What is the principle of least privilege? Give a practical example.",
                Set.of("security"), false,
                Set.of("least privilege", "minimum access", "role"), "EASY"));

        q.add(new InterviewQuestion("Q742",
                "What is multi-factor authentication (MFA)? How does TOTP work?",
                Set.of("security"), false,
                Set.of("mfa", "totp", "second factor"), "MEDIUM"));

        q.add(new InterviewQuestion("Q743",
                "What is a man-in-the-middle (MITM) attack? How does HTTPS prevent it?",
                Set.of("security", "networking"), false,
                Set.of("mitm", "certificate", "tls"), "MEDIUM"));

        q.add(new InterviewQuestion("Q744",
                "What is broken access control? Give a real-world example.",
                Set.of("security", "owasp"), false,
                Set.of("access control", "authorization", "privilege escalation"), "MEDIUM"));

        q.add(new InterviewQuestion("Q745",
                "What is clickjacking? How does the `X-Frame-Options` header prevent it?",
                Set.of("security", "web-security"), false,
                Set.of("clickjacking", "iframe", "x-frame-options"), "MEDIUM"));

        q.add(new InterviewQuestion("Q746",
                "How should passwords be stored? What is the difference between hashing, salting, and encryption?",
                Set.of("security"), false,
                Set.of("hash", "salt", "bcrypt", "encryption"), "MEDIUM"));

        q.add(new InterviewQuestion("Q747",
                "What is a brute force attack? How do you protect an API endpoint against it?",
                Set.of("security"), false,
                Set.of("brute force", "rate limiting", "lockout"), "EASY"));

        q.add(new InterviewQuestion("Q748",
                "What is the Same-Origin Policy (SOP)? How does CORS relate to it?",
                Set.of("security", "web-security", "networking"), false,
                Set.of("same-origin", "cors", "browser"), "MEDIUM"));

        q.add(new InterviewQuestion("Q749",
                "What is zero-trust security? How does it differ from a perimeter-based model?",
                Set.of("security"), false,
                Set.of("zero trust", "verify", "never trust"), "HARD"));

        q.add(new InterviewQuestion("Q750",
                "What are important security HTTP response headers? Explain at least three.",
                Set.of("security", "web-security"), false,
                Set.of("hsts", "csp", "x-content-type-options"), "MEDIUM"));

        q.add(new InterviewQuestion("Q751",
                "What is session fixation? How do you prevent it?",
                Set.of("security", "web-security"), false,
                Set.of("session fixation", "regenerate", "cookie"), "HARD"));

        q.add(new InterviewQuestion("Q752",
                "What is an API key vs a JWT for authentication? What are the trade-offs?",
                Set.of("security", "api"), false,
                Set.of("api key", "jwt", "stateless"), "MEDIUM"));

        q.add(new InterviewQuestion("Q753",
                "What is OWASP broken authentication? Give two examples of broken authentication.",
                Set.of("security", "owasp"), false,
                Set.of("broken auth", "weak password", "session"), "MEDIUM"));

        q.add(new InterviewQuestion("Q754",
                "What is certificate transparency (CT)? Why does it matter?",
                Set.of("security", "networking"), false,
                Set.of("certificate transparency", "ct log", "ssl"), "HARD"));

        q.add(new InterviewQuestion("Q755",
                "What is a dependency vulnerability? How do you manage supply chain security?",
                Set.of("security", "devops"), false,
                Set.of("dependency", "cve", "audit", "sbom"), "MEDIUM"));
    }

    // =====================================================================
    // Testing  (Q756-Q800)
    // =====================================================================
    private void addTesting(List<InterviewQuestion> q) {
        q.add(new InterviewQuestion("Q756",
                "What is the difference between unit, integration, and end-to-end tests?",
                Set.of("testing"), false,
                Set.of("unit", "integration", "e2e"), "EASY"));

        q.add(new InterviewQuestion("Q757",
                "What is TDD (Test-Driven Development)? Describe the red-green-refactor cycle.",
                Set.of("testing", "tdd"), false,
                Set.of("tdd", "red", "green", "refactor"), "MEDIUM"));

        q.add(new InterviewQuestion("Q758",
                "What is BDD (Behavior-Driven Development)? How does it differ from TDD?",
                Set.of("testing", "tdd"), false,
                Set.of("bdd", "given when then", "cucumber"), "MEDIUM"));

        q.add(new InterviewQuestion("Q759",
                "What is the difference between a mock, a stub, and a spy in testing?",
                Set.of("testing"), false,
                Set.of("mock", "stub", "spy", "test double"), "MEDIUM"));

        q.add(new InterviewQuestion("Q760",
                "What is test coverage? Is 100% coverage always a good goal? Why or why not?",
                Set.of("testing"), false,
                Set.of("coverage", "line coverage", "branch coverage"), "MEDIUM"));

        q.add(new InterviewQuestion("Q761",
                "What is a test fixture? How do `@BeforeEach` and `@AfterEach` work in JUnit?",
                Set.of("testing"), false,
                Set.of("fixture", "beforeeach", "aftereach", "setup"), "EASY"));

        q.add(new InterviewQuestion("Q762",
                "What is regression testing? How do automated tests help with it?",
                Set.of("testing"), false,
                Set.of("regression", "automated", "ci"), "EASY"));

        q.add(new InterviewQuestion("Q763",
                "What is load testing? What is the difference between load, stress, and soak testing?",
                Set.of("testing", "performance"), false,
                Set.of("load", "stress", "soak", "throughput"), "MEDIUM"));

        q.add(new InterviewQuestion("Q764",
                "What is a flaky test? How do you identify and fix one?",
                Set.of("testing"), false,
                Set.of("flaky", "non-deterministic", "retry"), "MEDIUM"));

        q.add(new InterviewQuestion("Q765",
                "What is the test pyramid? Why does it suggest having fewer E2E tests than unit tests?",
                Set.of("testing"), false,
                Set.of("test pyramid", "fast", "cost"), "EASY"));

        q.add(new InterviewQuestion("Q766",
                "What is snapshot testing? What are its advantages and disadvantages?",
                Set.of("testing", "frontend"), false,
                Set.of("snapshot", "jest", "regression"), "MEDIUM"));

        q.add(new InterviewQuestion("Q767",
                "What is property-based testing? How does it differ from example-based testing?",
                Set.of("testing"), false,
                Set.of("property-based", "generative", "quickcheck"), "HARD"));

        q.add(new InterviewQuestion("Q768",
                "What is mutation testing? What does it verify that code coverage cannot?",
                Set.of("testing"), false,
                Set.of("mutation", "pitest", "survived"), "HARD"));

        q.add(new InterviewQuestion("Q769",
                "What is the AAA pattern in testing (Arrange, Act, Assert)?",
                Set.of("testing"), false,
                Set.of("arrange", "act", "assert", "aaa"), "EASY"));

        q.add(new InterviewQuestion("Q770",
                "How would you test a REST API endpoint? What would you verify?",
                Set.of("testing", "api"), false,
                Set.of("status code", "response body", "headers"), "MEDIUM"));

        q.add(new InterviewQuestion("Q771",
                "What is contract testing? What is Pact and what problem does it solve?",
                Set.of("testing"), false,
                Set.of("contract", "pact", "consumer", "provider"), "HARD"));

        q.add(new InterviewQuestion("Q772",
                "What is the difference between black-box and white-box testing?",
                Set.of("testing"), false,
                Set.of("black-box", "white-box", "internal"), "EASY"));

        q.add(new InterviewQuestion("Q773",
                "How do you test asynchronous code in JavaScript (e.g. Promises or async/await)?",
                Set.of("testing", "javascript", "frontend"), false,
                Set.of("async", "done", "resolves", "rejects"), "MEDIUM"));

        q.add(new InterviewQuestion("Q774",
                "What is the difference between `@Mock` and `@InjectMocks` in Mockito?",
                Set.of("testing"), false,
                Set.of("mock", "injectmocks", "mockito"), "MEDIUM"));

        q.add(new InterviewQuestion("Q775",
                "What is smoke testing? When in the release cycle is it used?",
                Set.of("testing"), false,
                Set.of("smoke test", "sanity", "deployment"), "EASY"));

        q.add(new InterviewQuestion("Q776",
                "How would you test a React component? What library would you use?",
                Set.of("testing", "react", "frontend"), false,
                Set.of("testing library", "render", "screen", "user event"), "MEDIUM"));

        q.add(new InterviewQuestion("Q777",
                "What is a CI/CD pipeline and how do tests fit into it?",
                Set.of("testing", "devops"), false,
                Set.of("ci", "cd", "pipeline", "automated"), "EASY"));

        q.add(new InterviewQuestion("Q778",
                "What is test isolation? Why is it important that tests do not share state?",
                Set.of("testing"), false,
                Set.of("isolation", "independent", "side effects"), "MEDIUM"));

        q.add(new InterviewQuestion("Q779",
                "What is an integration test in Spring Boot? How does `@SpringBootTest` work?",
                Set.of("testing"), false,
                Set.of("springboottest", "application context", "integration"), "MEDIUM"));

        q.add(new InterviewQuestion("Q780",
                "How do you test code that depends on the current time or random values?",
                Set.of("testing"), false,
                Set.of("clock", "mock", "deterministic"), "HARD"));

        q.add(new InterviewQuestion("Q781",
                "What is code review? What do you look for when reviewing a pull request?",
                Set.of("behavioral", "testing"), false,
                Set.of("code review", "readability", "correctness"), "EASY"));

        q.add(new InterviewQuestion("Q782",
                "What is static code analysis? Name two tools used for Java or JavaScript.",
                Set.of("testing"), false,
                Set.of("static analysis", "sonarqube", "eslint", "checkstyle"), "EASY"));

        q.add(new InterviewQuestion("Q783",
                "What is a test environment vs a staging environment vs production?",
                Set.of("testing", "devops"), false,
                Set.of("test", "staging", "production", "environment"), "EASY"));

        q.add(new InterviewQuestion("Q784",
                "How do you prevent test interdependencies when tests share a database?",
                Set.of("testing"), false,
                Set.of("transaction rollback", "test containers", "clean state"), "HARD"));

        q.add(new InterviewQuestion("Q785",
                "What is Testcontainers? When would you use it?",
                Set.of("testing"), false,
                Set.of("testcontainers", "docker", "integration"), "MEDIUM"));

        q.add(new InterviewQuestion("Q786",
                "What is performance testing vs functional testing?",
                Set.of("testing", "performance"), false,
                Set.of("performance", "functional", "throughput"), "EASY"));

        q.add(new InterviewQuestion("Q787",
                "What is fuzz testing? What types of bugs does it find?",
                Set.of("testing", "security"), false,
                Set.of("fuzz", "random input", "crash"), "HARD"));

        q.add(new InterviewQuestion("Q788",
                "How do you measure and improve the quality of a test suite?",
                Set.of("testing"), false,
                Set.of("coverage", "mutation", "quality"), "MEDIUM"));

        q.add(new InterviewQuestion("Q789",
                "What is a test plan? What does it include?",
                Set.of("testing"), false,
                Set.of("test plan", "scope", "strategy"), "EASY"));

        q.add(new InterviewQuestion("Q790",
                "What is the difference between verification and validation in software testing?",
                Set.of("testing"), false,
                Set.of("verification", "validation", "requirements"), "MEDIUM"));

        q.add(new InterviewQuestion("Q791",
                "How would you approach testing a microservices architecture?",
                Set.of("testing", "architecture"), false,
                Set.of("contract", "integration", "service"), "HARD"));

        q.add(new InterviewQuestion("Q792",
                "What is acceptance testing? Who typically writes acceptance tests?",
                Set.of("testing"), false,
                Set.of("acceptance", "stakeholder", "user story"), "EASY"));

        q.add(new InterviewQuestion("Q793",
                "What is an end-to-end test framework? Compare Playwright, Cypress, and Selenium.",
                Set.of("testing", "frontend"), false,
                Set.of("playwright", "cypress", "selenium", "e2e"), "MEDIUM"));

        q.add(new InterviewQuestion("Q794",
                "How do you decide what NOT to test? What is the cost of too many tests?",
                Set.of("testing"), false,
                Set.of("maintenance", "brittle", "over testing"), "MEDIUM"));

        q.add(new InterviewQuestion("Q795",
                "What is a test report? What metrics does it typically include?",
                Set.of("testing"), false,
                Set.of("pass rate", "coverage", "duration"), "EASY"));

        q.add(new InterviewQuestion("Q796",
                "What is chaos engineering? How does it relate to testing?",
                Set.of("testing", "devops"), false,
                Set.of("chaos", "resilience", "fault injection"), "HARD"));

        q.add(new InterviewQuestion("Q797",
                "What is a test-driven refactoring? How do tests enable safe refactoring?",
                Set.of("testing", "tdd"), false,
                Set.of("refactor", "safety net", "regression"), "MEDIUM"));

        q.add(new InterviewQuestion("Q798",
                "How would you introduce automated testing into a project that has none?",
                Set.of("testing"), false,
                Set.of("strategy", "critical path", "buy-in"), "MEDIUM"));

        q.add(new InterviewQuestion("Q799",
                "What is a testing environment variable? How do you manage test configuration safely?",
                Set.of("testing"), false,
                Set.of("env variable", "configuration", "secret"), "EASY"));

        q.add(new InterviewQuestion("Q800",
                "What is the difference between `assertEquals` and `assertSame` in JUnit?",
                Set.of("testing"), false,
                Set.of("assertEquals", "assertSame", "reference", "value"), "EASY"));
    }

    // =====================================================================
    // Python  (Q801-Q825)
    // =====================================================================
    private void addPython(List<InterviewQuestion> q) {
        q.add(new InterviewQuestion("Q801",
                "What is the GIL (Global Interpreter Lock) in CPython, and how does it affect multithreaded Python programs?",
                Set.of("python"), false,
                Set.of("GIL", "threads", "CPU-bound", "I/O-bound"), "MEDIUM"));

        q.add(new InterviewQuestion("Q802",
                "What is a Python decorator? Give an example of when you'd use one.",
                Set.of("python"), false,
                Set.of("decorator", "wrap", "function"), "EASY"));

        q.add(new InterviewQuestion("Q803",
                "What is the difference between a Python list comprehension and a generator expression?",
                Set.of("python"), false,
                Set.of("list comprehension", "generator", "lazy", "memory"), "EASY"));

        q.add(new InterviewQuestion("Q804",
                "What does the `yield` keyword do? How does a generator function differ from a regular function?",
                Set.of("python"), false,
                Set.of("yield", "generator", "lazy evaluation", "state"), "MEDIUM"));

        q.add(new InterviewQuestion("Q805",
                "Explain Python's `*args` and `**kwargs`. When would you use them?",
                Set.of("python"), false,
                Set.of("args", "kwargs", "variadic", "unpacking"), "EASY"));

        q.add(new InterviewQuestion("Q806",
                "What is the mutable default argument bug in Python? How do you avoid it?",
                Set.of("python"), false,
                Set.of("mutable default", "list", "None"), "MEDIUM"));

        q.add(new InterviewQuestion("Q807",
                "What are Python type hints? Are they enforced at runtime?",
                Set.of("python"), false,
                Set.of("type hints", "mypy", "static typing", "runtime"), "EASY"));

        q.add(new InterviewQuestion("Q808",
                "What is the difference between `==` and `is` in Python?",
                Set.of("python"), false,
                Set.of("equality", "identity"), "EASY"));

        q.add(new InterviewQuestion("Q809",
                "What is a context manager in Python? How does the `with` statement use it?",
                Set.of("python"), false,
                Set.of("context manager", "with", "__enter__", "__exit__"), "MEDIUM"));

        q.add(new InterviewQuestion("Q810",
                "What is the difference between a Python module and a package?",
                Set.of("python"), false,
                Set.of("module", "package", "__init__"), "EASY"));

        q.add(new InterviewQuestion("Q811",
                "What are Python virtual environments, and why are they used?",
                Set.of("python"), false,
                Set.of("venv", "virtualenv", "dependency isolation"), "EASY"));

        q.add(new InterviewQuestion("Q812",
                "What is the difference between `pip` with a requirements.txt and a tool like Poetry for dependency management?",
                Set.of("python"), false,
                Set.of("pip", "poetry", "requirements", "lock file"), "MEDIUM"));

        q.add(new InterviewQuestion("Q813",
                "What is a Python dataclass, and what boilerplate does it save you from writing?",
                Set.of("python"), false,
                Set.of("dataclass", "__init__", "__repr__", "boilerplate"), "EASY"));

        q.add(new InterviewQuestion("Q814",
                "Explain Python's exception handling with `try`/`except`/`else`/`finally`.",
                Set.of("python"), false,
                Set.of("try", "except", "else", "finally"), "EASY"));

        q.add(new InterviewQuestion("Q815",
                "What is duck typing in Python?",
                Set.of("python"), false,
                Set.of("duck typing", "dynamic typing"), "EASY"));

        q.add(new InterviewQuestion("Q816",
                "What is the difference between `async def` and a regular function in Python? How does `await` work?",
                Set.of("python", "async"), false,
                Set.of("async", "await", "coroutine", "event loop"), "MEDIUM"));

        q.add(new InterviewQuestion("Q817",
                "What is the difference between multiprocessing and multithreading in Python, and why is multiprocessing preferable for CPU-bound work given the GIL?",
                Set.of("python", "concurrency"), false,
                Set.of("multiprocessing", "multithreading", "CPU-bound", "GIL"), "MEDIUM"));

        q.add(new InterviewQuestion("Q818",
                "What are Python's `collections` module tools like `defaultdict` and `Counter` useful for?",
                Set.of("python"), false,
                Set.of("defaultdict", "Counter", "collections"), "EASY"));

        q.add(new InterviewQuestion("Q819",
                "What does `itertools` provide, and when would you use `itertools.chain` or `itertools.groupby`?",
                Set.of("python"), false,
                Set.of("itertools", "chain", "groupby", "lazy"), "MEDIUM"));

        q.add(new InterviewQuestion("Q820",
                "How does Python's garbage collection work, combining reference counting with a cycle detector?",
                Set.of("python"), false,
                Set.of("garbage collection", "reference counting", "cycles"), "MEDIUM"));

        q.add(new InterviewQuestion("Q821",
                "What is a Python generator's `.send()` method used for?",
                Set.of("python"), false,
                Set.of("generator", "send", "coroutine"), "HARD"));

        q.add(new InterviewQuestion("Q822",
                "What is the purpose of the `if __name__ == \"__main__\":` guard in a Python script?",
                Set.of("python"), false,
                Set.of("__name__", "__main__", "entry point"), "EASY"));

        q.add(new InterviewQuestion("Q823",
                "How do you structure unit tests with `pytest`? What are fixtures used for?",
                Set.of("python", "testing"), false,
                Set.of("pytest", "fixture", "assert", "test discovery"), "MEDIUM"));

        q.add(new InterviewQuestion("Q824",
                "What is the difference between a shallow copy and a deep copy in Python?",
                Set.of("python"), false,
                Set.of("shallow copy", "deep copy", "copy module"), "EASY"));

        q.add(new InterviewQuestion("Q825",
                "How would you profile a slow Python function to find the bottleneck?",
                Set.of("python", "performance"), false,
                Set.of("profiling", "cProfile", "bottleneck"), "MEDIUM"));
    }

    // =====================================================================
    // ETL / Data pipelines  (Q826-Q850)
    // =====================================================================
    private void addEtlDataPipelines(List<InterviewQuestion> q) {
        q.add(new InterviewQuestion("Q826",
                "What is the difference between ETL and ELT? When would you prefer one over the other?",
                Set.of("etl"), false,
                Set.of("ETL", "ELT", "transform", "load"), "EASY"));

        q.add(new InterviewQuestion("Q827",
                "What is an idempotent data pipeline, and why does idempotency matter for ETL jobs?",
                Set.of("etl"), false,
                Set.of("idempotent", "retry", "duplicate"), "MEDIUM"));

        q.add(new InterviewQuestion("Q828",
                "What is the difference between a full load and an incremental load in a data pipeline?",
                Set.of("etl"), false,
                Set.of("full load", "incremental load", "watermark", "delta"), "EASY"));

        q.add(new InterviewQuestion("Q829",
                "What is a watermark (or high-water mark) in incremental data extraction?",
                Set.of("etl"), false,
                Set.of("watermark", "high-water mark", "timestamp"), "MEDIUM"));

        q.add(new InterviewQuestion("Q830",
                "What data quality checks would you add to a pipeline (nulls, duplicates, schema drift)?",
                Set.of("etl"), false,
                Set.of("data quality", "validation", "nulls", "schema drift"), "MEDIUM"));

        q.add(new InterviewQuestion("Q831",
                "What is schema evolution, and how would you handle a source system adding or removing a column?",
                Set.of("etl"), false,
                Set.of("schema evolution", "backward compatible", "column"), "MEDIUM"));

        q.add(new InterviewQuestion("Q832",
                "What is the difference between batch processing and stream processing?",
                Set.of("etl"), false,
                Set.of("batch", "streaming", "latency", "throughput"), "EASY"));

        q.add(new InterviewQuestion("Q833",
                "What is a dead-letter queue, and why would a data pipeline use one?",
                Set.of("etl"), false,
                Set.of("dead-letter queue", "failed records", "reprocessing"), "MEDIUM"));

        q.add(new InterviewQuestion("Q834",
                "How would you design a pipeline to safely backfill historical data without duplicating already-loaded records?",
                Set.of("etl"), false,
                Set.of("backfill", "deduplication", "idempotent"), "HARD"));

        q.add(new InterviewQuestion("Q835",
                "What is a slowly changing dimension (SCD), and what's the difference between Type 1 and Type 2?",
                Set.of("etl", "data warehousing"), false,
                Set.of("slowly changing dimension", "SCD", "Type 1", "Type 2"), "MEDIUM"));

        q.add(new InterviewQuestion("Q836",
                "What is data lineage, and why is it important in a data pipeline?",
                Set.of("etl"), false,
                Set.of("data lineage", "traceability", "audit"), "EASY"));

        q.add(new InterviewQuestion("Q837",
                "What is the difference between a data lake and a data warehouse?",
                Set.of("etl", "data warehousing"), false,
                Set.of("data lake", "data warehouse", "schema-on-read", "schema-on-write"), "EASY"));

        q.add(new InterviewQuestion("Q838",
                "How do you handle late-arriving data in a pipeline that partitions by event date?",
                Set.of("etl"), false,
                Set.of("late-arriving data", "partition", "event time"), "HARD"));

        q.add(new InterviewQuestion("Q839",
                "What is exactly-once processing, and why is it hard to guarantee in distributed pipelines?",
                Set.of("etl"), false,
                Set.of("exactly-once", "at-least-once", "distributed"), "HARD"));

        q.add(new InterviewQuestion("Q840",
                "What is a checkpoint in a streaming pipeline, and what does it protect against?",
                Set.of("etl"), false,
                Set.of("checkpoint", "streaming", "failure recovery"), "MEDIUM"));

        q.add(new InterviewQuestion("Q841",
                "What is partitioning in the storage layer of a data pipeline, and how does it improve query performance?",
                Set.of("etl", "data warehousing"), false,
                Set.of("partitioning", "pruning", "performance"), "MEDIUM"));

        q.add(new InterviewQuestion("Q842",
                "What is data deduplication, and what strategies would you use to detect duplicate records?",
                Set.of("etl"), false,
                Set.of("deduplication", "hash", "primary key"), "MEDIUM"));

        q.add(new InterviewQuestion("Q843",
                "What monitoring and alerting would you put on a production ETL pipeline?",
                Set.of("etl"), false,
                Set.of("monitoring", "alerting", "SLA", "freshness"), "MEDIUM"));

        q.add(new InterviewQuestion("Q844",
                "What is a data contract between a source system and a downstream consumer, and why does it matter?",
                Set.of("etl"), false,
                Set.of("data contract", "schema", "breaking change"), "MEDIUM"));

        q.add(new InterviewQuestion("Q845",
                "How would you test a data transformation step before deploying it to production?",
                Set.of("etl", "testing"), false,
                Set.of("testing", "unit test", "sample data", "validation"), "MEDIUM"));

        q.add(new InterviewQuestion("Q846",
                "What is CDC (Change Data Capture), and how is it used to sync data between systems?",
                Set.of("etl"), false,
                Set.of("CDC", "change data capture", "replication"), "MEDIUM"));

        q.add(new InterviewQuestion("Q847",
                "What is the difference between a fact table and a dimension table in a data warehouse model?",
                Set.of("etl", "data warehousing"), false,
                Set.of("fact table", "dimension table", "star schema"), "EASY"));

        q.add(new InterviewQuestion("Q848",
                "How would you handle a pipeline step that depends on an upstream API with rate limits?",
                Set.of("etl"), false,
                Set.of("rate limit", "retry", "backoff", "throttling"), "MEDIUM"));

        q.add(new InterviewQuestion("Q849",
                "What is data partition skew, and how can it slow down a distributed processing job?",
                Set.of("etl"), false,
                Set.of("skew", "distributed processing", "bottleneck"), "HARD"));

        q.add(new InterviewQuestion("Q850",
                "What questions would you ask before designing a new data pipeline (source, volume, freshness, consumers)?",
                Set.of("etl"), false,
                Set.of("requirements", "freshness", "volume", "consumers"), "EASY"));
    }

    // =====================================================================
    // Airflow  (Q851-Q870)
    // =====================================================================
    private void addAirflow(List<InterviewQuestion> q) {
        q.add(new InterviewQuestion("Q851",
                "What is a DAG in Apache Airflow?",
                Set.of("airflow"), false,
                Set.of("DAG", "directed acyclic graph", "tasks"), "EASY"));

        q.add(new InterviewQuestion("Q852",
                "What is the difference between an Airflow Operator and a Sensor?",
                Set.of("airflow"), false,
                Set.of("operator", "sensor", "poke"), "EASY"));

        q.add(new InterviewQuestion("Q853",
                "What is an Airflow XCom, and what is it typically used for?",
                Set.of("airflow"), false,
                Set.of("XCom", "cross-communication", "task"), "MEDIUM"));

        q.add(new InterviewQuestion("Q854",
                "What is the difference between Airflow's `schedule_interval` and a DAG run's `execution_date`?",
                Set.of("airflow"), false,
                Set.of("schedule_interval", "start_date", "execution_date"), "MEDIUM"));

        q.add(new InterviewQuestion("Q855",
                "What does Airflow's \"backfill\" mean, and when would you use it?",
                Set.of("airflow"), false,
                Set.of("backfill", "catchup", "historical runs"), "MEDIUM"));

        q.add(new InterviewQuestion("Q856",
                "What is an Airflow Executor, and what's the difference between the LocalExecutor and CeleryExecutor?",
                Set.of("airflow"), false,
                Set.of("executor", "LocalExecutor", "CeleryExecutor", "parallelism"), "MEDIUM"));

        q.add(new InterviewQuestion("Q857",
                "How does Airflow handle task retries, and what parameters control retry behavior?",
                Set.of("airflow"), false,
                Set.of("retries", "retry_delay", "backoff"), "EASY"));

        q.add(new InterviewQuestion("Q858",
                "How do you set a task dependency in Airflow using `>>` or `set_downstream`?",
                Set.of("airflow"), false,
                Set.of("dependency", "upstream", "downstream"), "EASY"));

        q.add(new InterviewQuestion("Q859",
                "What is the Airflow scheduler, and how does it decide when to trigger a DAG run?",
                Set.of("airflow"), false,
                Set.of("scheduler", "DAG run", "trigger"), "MEDIUM"));

        q.add(new InterviewQuestion("Q860",
                "What is an Airflow Hook, and how does it differ from an Operator?",
                Set.of("airflow"), false,
                Set.of("hook", "connection", "operator"), "MEDIUM"));

        q.add(new InterviewQuestion("Q861",
                "Why should you avoid putting large amounts of data into an Airflow XCom, and what would you do instead?",
                Set.of("airflow"), false,
                Set.of("XCom", "large data", "external storage"), "MEDIUM"));

        q.add(new InterviewQuestion("Q862",
                "What is a TaskGroup (or SubDAG) in Airflow, and why might you use one?",
                Set.of("airflow"), false,
                Set.of("SubDAG", "TaskGroup", "modularity"), "MEDIUM"));

        q.add(new InterviewQuestion("Q863",
                "What is the purpose of Airflow's `catchup` parameter?",
                Set.of("airflow"), false,
                Set.of("catchup", "scheduler", "missed runs"), "MEDIUM"));

        q.add(new InterviewQuestion("Q864",
                "How does Airflow's task concurrency and pool mechanism prevent overloading a downstream system?",
                Set.of("airflow"), false,
                Set.of("pool", "concurrency", "throttling"), "HARD"));

        q.add(new InterviewQuestion("Q865",
                "Why does idempotency matter for an Airflow task that might be retried or re-run?",
                Set.of("airflow", "etl"), false,
                Set.of("idempotent", "retry", "side effect"), "MEDIUM"));

        q.add(new InterviewQuestion("Q866",
                "What is the difference between Airflow's classic operators and the TaskFlow API (`@task` decorator) in Airflow 2.x?",
                Set.of("airflow", "python"), false,
                Set.of("TaskFlow API", "@task", "Airflow 2"), "MEDIUM"));

        q.add(new InterviewQuestion("Q867",
                "How would you configure an Airflow task to alert on failure, e.g. via Slack or email?",
                Set.of("airflow"), false,
                Set.of("on_failure_callback", "alerting", "notification"), "MEDIUM"));

        q.add(new InterviewQuestion("Q868",
                "What is the difference between a Sensor's `poke` mode and `reschedule` mode in Airflow?",
                Set.of("airflow"), false,
                Set.of("poke", "reschedule", "worker slot"), "HARD"));

        q.add(new InterviewQuestion("Q869",
                "How would you structure an Airflow DAG to process multiple similar datasets without duplicating code?",
                Set.of("airflow"), false,
                Set.of("dynamic DAG", "loop", "parameterization"), "HARD"));

        q.add(new InterviewQuestion("Q870",
                "What is Airflow's metadata database used for?",
                Set.of("airflow"), false,
                Set.of("metadata database", "DAG state", "task instance"), "EASY"));
    }

    // =====================================================================
    // dbt  (Q871-Q890)
    // =====================================================================
    private void addDbt(List<InterviewQuestion> q) {
        q.add(new InterviewQuestion("Q871",
                "What is a dbt model?",
                Set.of("dbt"), false,
                Set.of("model", "SQL", "SELECT", "compiled"), "EASY"));

        q.add(new InterviewQuestion("Q872",
                "What is the difference between dbt's `view`, `table`, and `incremental` materializations?",
                Set.of("dbt"), false,
                Set.of("materialization", "view", "table", "incremental"), "MEDIUM"));

        q.add(new InterviewQuestion("Q873",
                "What is a dbt test, and what do the built-in generic tests like `unique` and `not_null` check?",
                Set.of("dbt"), false,
                Set.of("test", "unique", "not_null"), "EASY"));

        q.add(new InterviewQuestion("Q874",
                "What is a dbt macro, and when would you write one?",
                Set.of("dbt"), false,
                Set.of("macro", "Jinja", "reusable"), "MEDIUM"));

        q.add(new InterviewQuestion("Q875",
                "What is the difference between a dbt `source` and a `ref`?",
                Set.of("dbt"), false,
                Set.of("source", "ref", "lineage"), "MEDIUM"));

        q.add(new InterviewQuestion("Q876",
                "What is the difference between `dbt run`, `dbt test`, and `dbt build`?",
                Set.of("dbt"), false,
                Set.of("dbt run", "dbt test", "dbt build"), "EASY"));

        q.add(new InterviewQuestion("Q877",
                "What is a dbt seed, and when would you use one?",
                Set.of("dbt"), false,
                Set.of("seed", "CSV", "static data"), "EASY"));

        q.add(new InterviewQuestion("Q878",
                "How does dbt's incremental materialization decide which rows to insert or update?",
                Set.of("dbt"), false,
                Set.of("incremental", "is_incremental", "unique_key"), "HARD"));

        q.add(new InterviewQuestion("Q879",
                "What is dbt's model dependency graph (DAG), and how is it built from `ref()` calls?",
                Set.of("dbt"), false,
                Set.of("DAG", "ref", "lineage", "dependency"), "MEDIUM"));

        q.add(new InterviewQuestion("Q880",
                "What is a dbt snapshot, and what problem does it solve?",
                Set.of("dbt"), false,
                Set.of("snapshot", "SCD Type 2", "history"), "MEDIUM"));

        q.add(new InterviewQuestion("Q881",
                "What are dbt packages, and why would you install one like `dbt_utils`?",
                Set.of("dbt"), false,
                Set.of("package", "dbt_utils", "reusable"), "EASY"));

        q.add(new InterviewQuestion("Q882",
                "What is the purpose of `dbt docs generate`?",
                Set.of("dbt"), false,
                Set.of("docs", "documentation", "lineage graph"), "EASY"));

        q.add(new InterviewQuestion("Q883",
                "What is a dbt exposure, and why is it useful for downstream consumers of a model?",
                Set.of("dbt"), false,
                Set.of("exposure", "downstream", "dashboard"), "MEDIUM"));

        q.add(new InterviewQuestion("Q884",
                "How would you organize dbt models into staging, intermediate, and marts layers?",
                Set.of("dbt"), false,
                Set.of("staging", "intermediate", "marts", "layering"), "MEDIUM"));

        q.add(new InterviewQuestion("Q885",
                "What is Jinja templating's role in a dbt project?",
                Set.of("dbt"), false,
                Set.of("Jinja", "templating"), "EASY"));

        q.add(new InterviewQuestion("Q886",
                "What is a dbt `config` block used for at the top of a model?",
                Set.of("dbt"), false,
                Set.of("config", "materialized", "tags"), "EASY"));

        q.add(new InterviewQuestion("Q887",
                "How would you debug a dbt model that's producing unexpected duplicate rows?",
                Set.of("dbt"), false,
                Set.of("duplicate", "unique test", "debugging"), "MEDIUM"));

        q.add(new InterviewQuestion("Q888",
                "What is the difference between dbt Core and dbt Cloud?",
                Set.of("dbt"), false,
                Set.of("dbt Core", "dbt Cloud", "scheduler"), "EASY"));

        q.add(new InterviewQuestion("Q889",
                "What is a custom (singular) dbt test, and how does it differ from a generic test?",
                Set.of("dbt"), false,
                Set.of("singular test", "custom SQL", "generic test"), "MEDIUM"));

        q.add(new InterviewQuestion("Q890",
                "How would you handle a slowly changing source schema in dbt without breaking downstream models?",
                Set.of("dbt"), false,
                Set.of("schema change", "source", "downstream"), "HARD"));
    }

    // =====================================================================
    // Data Warehousing / Data Analytics  (Q891-Q910)
    // =====================================================================
    private void addDataWarehousingAnalytics(List<InterviewQuestion> q) {
        q.add(new InterviewQuestion("Q891",
                "What is the difference between a star schema and a snowflake schema in data warehouse design?",
                Set.of("data warehousing"), false,
                Set.of("star schema", "snowflake schema", "normalization"), "MEDIUM"));

        q.add(new InterviewQuestion("Q892",
                "What is table partitioning in a cloud data warehouse like BigQuery or Snowflake, and how does it reduce query cost and time?",
                Set.of("data warehousing"), false,
                Set.of("partitioning", "pruning", "cost"), "MEDIUM"));

        q.add(new InterviewQuestion("Q893",
                "What is clustering (or micro-partitioning) in Snowflake, and how does it differ from partitioning?",
                Set.of("data warehousing"), false,
                Set.of("clustering", "micro-partition"), "HARD"));

        q.add(new InterviewQuestion("Q894",
                "What is the difference between a materialized view and a regular view in a data warehouse?",
                Set.of("data warehousing"), false,
                Set.of("materialized view", "refresh", "precomputed"), "MEDIUM"));

        q.add(new InterviewQuestion("Q895",
                "What is BigQuery's separation of storage and compute, and why does it matter for cost and performance?",
                Set.of("data warehousing"), false,
                Set.of("storage", "compute", "separation"), "MEDIUM"));

        q.add(new InterviewQuestion("Q896",
                "What is a surrogate key, and why is it preferred over a natural key in a dimension table?",
                Set.of("data warehousing"), false,
                Set.of("surrogate key", "natural key", "dimension"), "MEDIUM"));

        q.add(new InterviewQuestion("Q897",
                "What is Redshift's distribution style (KEY, ALL, EVEN), and how does it affect query performance?",
                Set.of("data warehousing"), false,
                Set.of("distribution style", "KEY", "EVEN"), "HARD"));

        q.add(new InterviewQuestion("Q898",
                "What is a data mart, and how does it differ from an enterprise data warehouse?",
                Set.of("data warehousing"), false,
                Set.of("data mart", "subset", "department"), "EASY"));

        q.add(new InterviewQuestion("Q899",
                "What query cost optimizations would you apply in a pay-per-query warehouse, like avoiding `SELECT *` or scanning less data?",
                Set.of("data warehousing"), false,
                Set.of("cost optimization", "SELECT", "bytes scanned"), "MEDIUM"));

        q.add(new InterviewQuestion("Q900",
                "What is the difference between row-based and columnar storage, and why do data warehouses use columnar formats?",
                Set.of("data warehousing"), false,
                Set.of("columnar", "row-based", "compression"), "MEDIUM"));

        q.add(new InterviewQuestion("Q901",
                "What is A/B testing, and what statistical concept determines whether a result is significant?",
                Set.of("data analytics"), false,
                Set.of("A/B testing", "significance", "p-value"), "MEDIUM"));

        q.add(new InterviewQuestion("Q902",
                "What is the difference between correlation and causation, and why does it matter when analyzing data?",
                Set.of("data analytics"), false,
                Set.of("correlation", "causation", "confounding"), "EASY"));

        q.add(new InterviewQuestion("Q903",
                "What is a p-value, and what does a p-value of 0.05 actually mean?",
                Set.of("data analytics"), false,
                Set.of("p-value", "hypothesis testing", "significance level"), "MEDIUM"));

        q.add(new InterviewQuestion("Q904",
                "What is the difference between mean, median, and mode, and when would you prefer one over another for summarizing data?",
                Set.of("data analytics"), false,
                Set.of("mean", "median", "mode", "outliers"), "EASY"));

        q.add(new InterviewQuestion("Q905",
                "What is a confidence interval, and how would you explain one to a non-technical stakeholder?",
                Set.of("data analytics"), false,
                Set.of("confidence interval", "uncertainty", "estimate"), "MEDIUM"));

        q.add(new InterviewQuestion("Q906",
                "What is an outlier, and how would you detect one in a dataset before analysis?",
                Set.of("data analytics"), false,
                Set.of("outlier", "skew", "distribution"), "EASY"));

        q.add(new InterviewQuestion("Q907",
                "What is the difference between a KPI and a metric, and how would you choose a good north-star metric?",
                Set.of("data analytics"), false,
                Set.of("KPI", "metric", "north-star"), "MEDIUM"));

        q.add(new InterviewQuestion("Q908",
                "How would you use a pivot table, or `pandas.pivot_table`, to summarize a large dataset?",
                Set.of("data analytics", "python"), false,
                Set.of("pivot table", "groupby", "aggregation"), "MEDIUM"));

        q.add(new InterviewQuestion("Q909",
                "What is Simpson's Paradox, and why is it a risk when aggregating data across groups?",
                Set.of("data analytics"), false,
                Set.of("Simpson's paradox", "aggregation", "subgroup"), "HARD"));

        q.add(new InterviewQuestion("Q910",
                "What is the difference between a dashboard (e.g. Tableau or Power BI) and an ad-hoc analysis, and when would you build each?",
                Set.of("data analytics"), false,
                Set.of("dashboard", "ad-hoc", "reporting"), "EASY"));
    }
}
