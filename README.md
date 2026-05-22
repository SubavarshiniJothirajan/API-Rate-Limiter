# API Rate Limiter

A scalable API Rate Limiter built using Spring Boot and Redis that protects backend services from excessive requests. The project supports multiple rate-limiting algorithms and uses Redis for distributed request tracking.

## Features

- Multiple rate-limiting algorithms
  - Fixed Window
  - Sliding Window
  - Token Bucket
- Redis-backed distributed storage
- Endpoint-specific rate limits
- Custom rate limit headers
- Global exception handling
- Request filtering using Spring Boot filters
- Metrics collection and monitoring support
- Lua scripts for atomic Redis operations
- JWT-based client identification support
- Configurable rate limit rules

## Tech Stack

- Java 17+
- Spring Boot
- Redis
- Maven
- Lua Scripting
- REST APIs

## Project Structure

```text
src/main/java/com/example/ratelimiter
│
├── algorithm
│   ├── FixedWindowAlgorithm
│   ├── SlidingWindowAlgorithm
│   └── TokenBucketAlgorithm
│
├── config
│   ├── RedisConfig
│   └── EndpointRateLimitConfig
│
├── controller
│   ├── TestController
│   └── DebugController
│
├── exception
│   ├── GlobalExceptionHandler
│   └── RateLimitExceededException
│
├── filter
│   └── RateLimitFilter
│
├── identity
│   └── ClientIdentifier
│
├── metrics
│   └── RateLimiterMetrics
│
├── model
│   └── RateLimitDecision
│
├── service
│   ├── RateLimitService
│   ├── RedisRateLimitService
│   └── RedisDebugService
│
└── util
    ├── HeaderUtil
    └── JwtUtil
```

## Supported Algorithms

### Fixed Window

Limits requests within a fixed time window.

Example:
- Limit: 10 requests/minute
- Request 11 is blocked.

### Sliding Window

Provides smoother traffic control by considering the previous window.

Benefits:
- Reduces burst traffic at window boundaries.
- More accurate request tracking.

### Token Bucket

Tokens are replenished at a fixed rate.

Benefits:
- Allows controlled bursts.
- Commonly used in production-grade systems.

## Rate Limit Response Headers

The API returns useful headers:

```http
X-RateLimit-Limit: 100
X-RateLimit-Remaining: 75
X-RateLimit-Reset: 1710000000
```

## Example Response When Limit Exceeds

```json
{
  "status": 429,
  "error": "Too Many Requests",
  "message": "Rate limit exceeded"
}
```

## Redis Usage

The application uses Redis to:

- Store request counters
- Maintain sliding window data
- Track token bucket state
- Support distributed deployments

Lua scripts are used to ensure atomic operations and avoid race conditions.

## Running the Project

### Clone Repository

```bash
git clone https://github.com/your-username/API-Rate-Limiter.git
```

### Start Redis

```bash
docker run -p 6379:6379 redis
```

### Build Project

```bash
mvn clean install
```

### Run Application

```bash
mvn spring-boot:run
```

Application runs at:

```text
http://localhost:8080
```

## Sample API Testing

### Send Requests

```bash
curl http://localhost:8080/api/test
```

After exceeding the configured limit:

```http
HTTP/1.1 429 Too Many Requests
```

## Future Enhancements

- User-specific rate limits
- Dynamic configuration updates
- Dashboard for monitoring
- Prometheus and Grafana integration
- API key management
- Distributed tracing support

## Learning Outcomes

This project demonstrates:

- System Design fundamentals
- API Gateway concepts
- Distributed rate limiting
- Redis data structures
- Lua scripting
- Spring Boot backend development
- Scalable microservice patterns

## Author

Subavarshini Jothirajan

GitHub: https://github.com/SubavarshiniJothirajan
