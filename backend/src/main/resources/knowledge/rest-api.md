---
topic: rest-api
tags: rest, api
---
# REST and HTTP APIs

## HTTP methods and REST basics
REST models a system as resources identified by URLs (for example /orders/42) and manipulated with standard HTTP methods. GET reads a resource, POST creates a new resource or triggers processing, PUT replaces a resource at a known URL, PATCH partially updates it, and DELETE removes it. REST APIs are stateless: each request carries everything the server needs (for example an auth token), so any server instance can handle it.
A strong answer names the methods with their meaning, mentions resources and statelessness, and that responses use status codes and a representation format such as JSON.
Common mistakes: using verbs in URLs (/getOrder), using GET for actions that change data, or describing REST as "just JSON over HTTP".

## Idempotency and safe methods
A method is idempotent if repeating the same request has the same effect on server state as sending it once. GET, PUT and DELETE are idempotent: putting the same body twice leaves the same final state, and deleting twice still leaves the resource deleted (the second call may return 404, but the state is unchanged). POST is not idempotent by default, because repeating it can create duplicates. Safe methods (GET, HEAD, OPTIONS) do not change state at all.
Idempotency matters because clients and proxies retry on timeouts. For non-idempotent operations such as payments, APIs accept an idempotency key so the server can detect and ignore repeats.
Common mistake: confusing idempotent with "returns the same response".

## PUT vs PATCH
PUT replaces the entire resource with the representation in the request body; fields left out are reset or removed. PATCH applies a partial update and changes only the fields provided. PUT is idempotent. PATCH can be idempotent (for example "set status to SHIPPED") but is not guaranteed to be (for example "increment counter").
Use PUT when the client sends the full object, such as saving a settings form. Use PATCH for small changes to large resources, such as updating only an email address.
Common mistake: using PUT with a partial body and accidentally wiping the fields that were omitted.

## HTTP status codes
2xx means success: 200 OK (with a body), 201 Created (new resource, usually with a Location header), 204 No Content (success with no body, common for DELETE). 4xx means the client made an error: 400 Bad Request (invalid input), 401 Unauthorized (not authenticated), 403 Forbidden (authenticated but not allowed), 404 Not Found, 409 Conflict (the request conflicts with the current state, such as a duplicate username or a version mismatch), 422 Unprocessable Entity (well-formed but semantically invalid), 429 Too Many Requests (rate limited). 5xx means the server failed: 500 Internal Server Error, 502/503/504 for gateway or availability problems.
A strong answer explains the 4xx-versus-5xx split: 4xx means the client should change the request, 5xx means retrying later may help.
Common mistake: returning 200 with an error message in the body.

## Authentication vs authorization, 401 vs 403
Authentication answers "who are you?" (verifying identity with a password, token or certificate). Authorization answers "what are you allowed to do?" (checking roles or permissions for an action). Authentication happens first.
401 Unauthorized despite its name means unauthenticated: credentials are missing, invalid or expired, and the client should log in. 403 Forbidden means the identity is known but lacks permission, and logging in again will not help.
Common mistake: returning 403 for a missing token, or 401 when a logged-in user lacks a role.

## CORS
Cross-Origin Resource Sharing is a browser security mechanism. By default the same-origin policy stops JavaScript on one origin (scheme + host + port) from reading responses from another origin. CORS lets a server opt in by returning headers such as Access-Control-Allow-Origin. For non-simple requests (for example JSON bodies or custom headers) the browser first sends a preflight OPTIONS request to check that the method and headers are allowed.
Key points: CORS is enforced by the browser, not the server, so curl and server-to-server calls ignore it. It protects users, not APIs, and it is not a replacement for authentication. In a backend, allow specific trusted origins rather than "*", especially when credentials are involved.
Common mistake: thinking CORS blocks the request from reaching the server. The server still receives it; the browser just hides the response from the page.

## Pagination
Large collections are returned in pages to limit response size and database load. Offset pagination (limit=20&offset=40) is simple and supports jumping to a page number, but it gets slower for deep pages, because the database still scans skipped rows, and it can skip or duplicate items when data changes between requests. Cursor (keyset) pagination returns an opaque cursor encoding the last item's sort key (for example WHERE created_at < :cursor ORDER BY created_at DESC LIMIT 20); it is fast at any depth and stable under inserts, but cannot jump to an arbitrary page.
A strong answer mentions a stable sort order (often with a unique tie-breaker such as id), a maximum page size, and returning metadata such as a next cursor.

## API versioning
Versioning lets an API evolve without breaking existing clients. Common strategies are a version in the URL path (/v1/orders), which is explicit and easy to route and cache; a version in a header or media type (Accept: application/vnd.app.v2+json), which keeps URLs clean but is less visible; and a query parameter (?version=2). Breaking changes include removing or renaming fields, changing types, or changing behaviour. Additive changes, such as new optional fields or new endpoints, usually don't need a new version.
A strong answer also mentions deprecation: support old versions for a period, communicate timelines, and monitor usage before removing them.

## DTOs and request validation
A DTO (Data Transfer Object) is a class shaped for the API contract, separate from the database entity. DTOs keep internal fields (passwords, internal IDs) from leaking, decouple the API from schema changes, prevent mass-assignment (clients setting fields like isAdmin), and avoid lazy-loading and serialization problems with JPA entities.
Validate input at the API boundary (required fields, formats, ranges, sizes) and return 400 with clear field errors. Enforce business rules in the service layer, and integrity rules with database constraints as the last line of defence. Client-side validation improves the user experience but never replaces server-side validation.

## Structured error responses
APIs should return errors in one consistent JSON shape so clients can handle them programmatically, for example an error code (a stable machine-readable string like ORDER_NOT_FOUND), a human-readable message, optional field-level details, and a request or trace ID for support. RFC 7807 / RFC 9457 "Problem Details" (application/problem+json) is a standard format for this.
Pair the body with the correct HTTP status. Never expose stack traces, SQL errors or internal class names. Log the full detail server-side and return a safe message instead.

## Rate limiting
Rate limiting caps how many requests a client can make in a time window (for example 100 requests per minute per API key or IP). It protects against abuse, brute-force attacks and runaway clients, keeps one user from starving others, and controls costs. Common algorithms are token bucket (allows short bursts and refills at a steady rate), leaky bucket, fixed window and sliding window. Exceeded limits return 429 Too Many Requests, often with Retry-After and remaining-quota headers.
In distributed systems the counters live in a shared store such as Redis, so all instances enforce the same limit. Behind a proxy, the client IP has to come from a trusted forwarding header, not from the proxy's address.

## Timeouts, retries and duplicate requests
Every outbound call needs a timeout. Without one, a slow dependency ties up threads until the whole service stalls. Retries handle transient failures but must use exponential backoff with jitter and a retry limit, otherwise they amplify outages (a retry storm). Only retry operations that are idempotent or protected by an idempotency key, because a timeout does not mean the request failed: the server may have processed it.
To prevent duplicate submissions, such as a double-clicked "Pay" button, combine client-side measures (disable the button) with server-side protection: idempotency keys stored with their result, unique constraints, or state checks.

## JWT and OAuth2
A JWT (JSON Web Token) is a compact token with a header, a payload of claims (user ID, roles, expiry) and a signature. The server verifies the signature without a database lookup, which makes JWTs useful for stateless authentication. The payload is only Base64-encoded, not encrypted, so it must never contain secrets or sensitive personal data. Risks include long-lived tokens that cannot easily be revoked (use short expiry plus refresh tokens), weak signing keys, and accepting the "none" algorithm.
OAuth2 is an authorization framework that lets a user grant an application limited access to their resources on another service without sharing their password, for example "Sign in with Google". It defines roles (resource owner, client, authorization server, resource server) and flows such as Authorization Code with PKCE. OpenID Connect adds identity (login) on top of OAuth2.

## CSRF and XSS
CSRF (Cross-Site Request Forgery) tricks a logged-in user's browser into sending an unwanted request to a site where it automatically attaches the user's cookies. It matters for cookie-based sessions. Defences include CSRF tokens, SameSite cookies and checking the Origin header. APIs authenticated with a bearer token in the Authorization header are not exposed to CSRF in the same way, because browsers do not attach that header automatically.
XSS (Cross-Site Scripting) injects malicious script into a page that other users view, letting it steal tokens or act as the user. It runs in the browser but is often caused by the backend storing or reflecting untrusted input. Defences: escape output by context, sanitize HTML, use a Content-Security-Policy, and use HttpOnly cookies so scripts cannot read session cookies. Both frontend and backend share responsibility.
