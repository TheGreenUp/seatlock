# SeatLock

SeatLock is a learning project for an event seat reservation platform. The current backend exposes a ping endpoint for checking that the application is running.

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

### Check the backend

Once the application has started, send a request from a second terminal:

```sh
curl -i http://localhost:8080/api/v1/ping
```

In Windows PowerShell, use `curl.exe` in place of `curl`.

Expected response: HTTP `200 OK`, `Content-Type: application/json`, and this JSON body:

```json
{"message":"pong"}
```

Press `Ctrl+C` in the application terminal to stop it.

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

`BackendApplicationTests` starts the Spring context against a disposable PostgreSQL 17 container managed by Testcontainers. The development database and its `DB_*` variables are not required for this test. `PingControllerTest` checks the response status and JSON `message` field through MockMvc without a separately started server.
