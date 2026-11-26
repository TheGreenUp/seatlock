# SeatLock

SeatLock is a learning project for an event seat reservation platform. The backend exposes a ping endpoint and a read-only event catalog backed by PostgreSQL.

## Requirements

- JDK 21. Check the active JDK with `java -version`.
- Spring Boot 4.2.0, configured in the backend POM.
- Maven 3.10.1, downloaded by the included Maven Wrapper on first use. A separate Maven installation is not required.

- PostgreSQL 17 for local development. The example below runs it with Docker.
- A running Docker engine for the test suite. Testcontainers starts its own PostgreSQL instance.

## Run locally

### Start PostgreSQL

If you already have PostgreSQL, create a local database and use its connection settings in the next step. Otherwise, create a development container once using the [official PostgreSQL image](https://hub.docker.com/_/postgres):

```sh
docker run --name seatlock-postgres -d -p 127.0.0.1:5433:5432 -e POSTGRES_DB=seatlock -e POSTGRES_USER=seatlock -e POSTGRES_PASSWORD=seatlock-dev-only -v seatlock-postgres-data:/var/lib/postgresql/data postgres:17
docker exec seatlock-postgres pg_isready -h 127.0.0.1 -U seatlock -d seatlock
```

Wait until PostgreSQL accepts connections. To start this container again after stopping it, run `docker start seatlock-postgres`. The named volume preserves the database between starts. The password above is for local development only. Keep real credentials out of Git.

### Start the backend

Run the commands for your shell from the repository root. If you use your own PostgreSQL instance, substitute its URL, username, and password.

**Windows (PowerShell)**

```powershell
$env:DB_URL = 'jdbc:postgresql://127.0.0.1:5433/seatlock'
$env:DB_USERNAME = 'seatlock'
$env:DB_PASSWORD = 'seatlock-dev-only'
cd backend
.\mvnw.cmd spring-boot:run
```

**macOS / Linux**

```sh
export DB_URL='jdbc:postgresql://127.0.0.1:5433/seatlock'
export DB_USERNAME='seatlock'
export DB_PASSWORD='seatlock-dev-only'
cd backend
./mvnw spring-boot:run
```

### Check the ping endpoint

Once the application has started, send a request from a second terminal:

```sh
curl -i http://localhost:8080/api/v1/ping
```

In Windows PowerShell, use `curl.exe` in place of `curl`.

Expected response: HTTP `200 OK`, `Content-Type: application/json`, and this JSON body:

```json
{"message":"pong"}
```

### List events

`GET /api/v1/events` returns HTTP `200 OK` with a JSON array:

```sh
curl -i http://localhost:8080/api/v1/events
```

In Windows PowerShell, use `curl.exe`. An empty catalog returns `[]`. When events exist, each item contains a numeric `id`, a `name`, and a `startsAt` timestamp in ISO 8601 UTC:

```json
[
  {"id": 30, "name": "Jazz Evening", "startsAt": "2026-12-12T18:00:00Z"},
  {"id": 40, "name": "Board Game Night", "startsAt": "2026-12-12T18:00:00Z"},
  {"id": 10, "name": "Film Screening", "startsAt": "2026-12-13T18:00:00Z"}
]
```

Events are sorted by `startsAt` ascending, then by `id` ascending when two events start at the same instant. Spring Data JPA applies this order when reading PostgreSQL. The controller returns an `EventDto` for each row so the HTTP contract stays separate from the persistence model.

The Flyway migration creates the schema. The response example above illustrates a populated database. Press `Ctrl+C` in the application terminal to stop the backend.

### Check the Flyway migration

On the first start with an empty database, Flyway applies V1 and creates the `events` table. Stop and restart the backend with the same connection settings, keeping the database intact. Flyway should report that the schema is up to date. For the Docker example, inspect the migration history with:

```sh
docker exec seatlock-postgres psql -U seatlock -d seatlock -c "SELECT version, description, success FROM flyway_schema_history ORDER BY installed_rank;"
```

After both starts, expect one successful entry for version `1`.

## Run tests

With Docker running, run the Maven verification build from the `backend` directory:

```powershell
.\mvnw.cmd clean verify
```

On macOS / Linux, run `./mvnw clean verify`.

Testcontainers starts disposable PostgreSQL 17 instances for the database-backed tests. These tests use their own connection settings.

- `BackendApplicationTests` checks that the Spring application context starts with PostgreSQL and its Flyway schema.
- `EventControllerIntegrationTest` sends requests through MockMvc against real PostgreSQL. It checks HTTP `200`, JSON content type, the empty array, the populated response, ordering by start time and ID, and conversion of offset timestamps to UTC.
- `PingControllerTest` checks HTTP `200` and the JSON `message` field through MockMvc.

GitHub Actions runs the same Maven verification on Linux with JDK 21 when a pull request targeting `main` is opened or updated.
