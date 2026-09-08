# Backend — I ain't ur fan

Spring Boot REST API that compares Instagram `followers_1.json` and `following.json` exports in memory (no database, no Instagram login).

**Production:** [https://i-aint-ur-fan.onrender.com](https://i-aint-ur-fan.onrender.com)

Full project docs: [../README.md](../README.md)

## How to start

From the repository root, open a terminal in `backend` and run the Maven wrapper.

**Windows:**

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

**macOS / Linux:**

```bash
cd backend
./mvnw spring-boot:run
```

- API: [http://localhost:8080](http://localhost:8080)
- Health: [http://localhost:8080/api/health](http://localhost:8080/api/health) → `{ "status": "ok" }`

No extra install step: the wrapper downloads Maven dependencies on first run.

### Tests

```powershell
cd backend
.\mvnw.cmd test
```

```bash
cd backend
./mvnw test
```

## Folder structure

```text
backend/
├── Dockerfile
├── .dockerignore
├── pom.xml
├── mvnw
├── mvnw.cmd
├── README.md
├── .mvn/
│   └── wrapper/
│       └── maven-wrapper.properties
└── src/
    ├── main/
    │   ├── java/com/iainturfan/
    │   │   ├── IAintUrFanApplication.java   # entry point
    │   │   ├── AnalyzerController.java      # /api/health, /api/analyzer/analyze
    │   │   ├── AnalyzerService.java         # compare followers vs following
    │   │   ├── JsonManager.java             # parse Instagram JSON
    │   │   ├── AnalysisResult.java          # response DTO
    │   │   ├── AnalysisException.java
    │   │   ├── ApiExceptionHandler.java
    │   │   ├── CorsConfig.java
    │   │   └── RootRedirectController.java  # GET / → /api/health
    │   └── resources/
    │       └── application.properties
    └── test/
        ├── java/com/iainturfan/
        │   ├── AnalyzerControllerTest.java
        │   ├── AnalyzerServiceTest.java
        │   └── JsonManagerTest.java
        └── resources/fixtures/              # sample JSON for tests
```

## Stack

- Java 21
- Spring Boot 3.4
- Maven (wrapper included)

## Endpoints

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/` | Redirects to `/api/health` |
| `GET` | `/api/health` | Health check → `{ "status": "ok" }` |
| `POST` | `/api/analyzer/analyze` | Multipart upload: `followers` + `following` files |

### Analyze example

```bash
curl -X POST http://localhost:8080/api/analyzer/analyze \
  -F "followers=@followers_1.json" \
  -F "following=@following.json"
```

Response includes lists and counts for followers, following, friends, fans, and idols.

## Configuration

`src/main/resources/application.properties`:

| Property | Default / notes |
|----------|-----------------|
| `server.port` | `${PORT:8080}` (Render sets `PORT`) |
| Multipart max file | `10MB` |
| Multipart max request | `21MB` |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:4200,https://i-aint-ur-fan.netlify.app` |

Override CORS when needed:

```text
CORS_ALLOWED_ORIGINS=http://localhost:4200,https://i-aint-ur-fan.netlify.app
```

## Deploy (Render + Docker)

1. Web Service from this repo
2. **Root Directory:** `backend`
3. **Dockerfile Path:** `./Dockerfile`
4. Optional: set `CORS_ALLOWED_ORIGINS`

On Render’s free tier the service may sleep; cold starts can take ~30–60s.
