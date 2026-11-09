# binance-job-service

A Spring Boot service that polls the Binance API every 30 seconds for the average BNB/USDT price, saves it to PostgreSQL, and serves the saved prices over a REST API.

## How to run

You need Docker with Docker Compose. A .env file is not required: without it the defaults are used (database user and password root, Binance polled every 30 seconds). See [Environment variables](#environment-variables) to change them.

docker compose up -d --build

This builds the app image, starts PostgreSQL and the app. The first price is saved right after startup, so you can check it:

curl http://localhost:8080/api/v1/prices/latest

Useful commands:

docker compose logs -f app

docker compose down

Data is kept in a Docker volume and survives docker compose down. Add -v to delete it too.

To change settings (database password, poll interval), copy .env.example to .env and edit it, then run docker compose up -d --build again. If you change POSTGRES_USER, POSTGRES_PASSWORD or POSTGRES_DB after the database was created, run docker compose down -v first.

### Running the app without Docker

Start only the database, then run the app (needs JDK 21+):

docker compose up -d postgres

./mvnw spring-boot:run

The defaults in application.properties match the database from Compose, so no extra settings are needed.

## How to call the endpoints

Base URL: http://localhost:8080/api/v1. All endpoints are GET.

Dates (from, to) are ISO-8601 with Z, for example 2023-11-14T22:13:20Z.

| Endpoint | Parameters | Returns |
|---|---|---|
| /prices/latest | none | the newest saved price |
| /prices/average | from, to | average price in the period |
| /prices | from, to, page, size | saved prices, paginated (page starts at 0) |
| /prices/trend | from, to | UPWARD, DOWNWARD or FLAT for the period |

Examples:

curl http://localhost:8080/api/v1/prices/latest

curl "http://localhost:8080/api/v1/prices/average?from=2023-11-14T00:00:00Z&to=2023-11-15T00:00:00Z"

curl "http://localhost:8080/api/v1/prices?from=2023-11-14T00:00:00Z&to=2023-11-15T00:00:00Z&page=0&size=10"

curl "http://localhost:8080/api/v1/prices/trend?from=2023-11-14T00:00:00Z&to=2023-11-15T00:00:00Z"

Example response of /prices/latest:

{ "symbol": "BNBUSDT", "price": 612.34567890, "fetchedAt": "2023-11-14T22:13:20Z" }

## Environment variables

### Docker Compose (optional .env file)

| Variable | Default | Meaning |
|---|---|---|
| POSTGRES_USER | root | Database user |
| POSTGRES_PASSWORD | root | Database password (used by the database and the app) |
| POSTGRES_DB | postgres | Database name |
| BINANCE_POLL_INTERVAL | PT30S | Time between Binance requests (ISO-8601 duration, e.g. PT1M) |

### Supported by the application itself

Use these when you run the jar or the Docker image without Compose.

| Variable | Default | Meaning |
|---|---|---|
| SPRING_DATASOURCE_URL | jdbc:postgresql://localhost:5432/postgres | Database JDBC URL |
| SPRING_DATASOURCE_USERNAME | root | Database user |
| SPRING_DATASOURCE_PASSWORD | root | Database password |
| BINANCE_POLL_INTERVAL | PT30S | Time between Binance requests |
| API_BINANCE_BASE_URL | https://api.binance.com/api/ | Binance API address (must end with /) |
| SPRING_JPA_SHOW_SQL | true | Print SQL queries to the log |

## Tests

./mvnw test -Dtest='!BinanceJobServiceApplicationTests'
