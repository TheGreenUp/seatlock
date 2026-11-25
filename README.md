# SeatLock

SeatLock is a learning project for an event seat reservation platform. The current backend exposes a ping endpoint for checking that the application is running.

## Requirements

- JDK 21. Check the active JDK with `java -version`.
- Spring Boot 4.2.0, configured in the backend POM.
- Maven 3.10.1, downloaded by the included Maven Wrapper on first use. A separate Maven installation is not required.

The ping endpoint requires no database or other external services.

## Run locally

Run these commands from the repository root.

**Windows (PowerShell)**

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

**macOS / Linux**

```sh
cd backend
./mvnw spring-boot:run
```

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

## Run tests

From the `backend` directory, run the Maven verification build:

```powershell
.\mvnw.cmd clean verify
```

On macOS / Linux, run `./mvnw clean verify`.

`PingControllerTest` uses MockMvc to check the HTTP status and the JSON `message` field. The test runs without a separately started server.
