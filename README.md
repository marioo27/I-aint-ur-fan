# I ain't ur fan

<p align="left">
  <img src="https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white"/>
  <img src="https://img.shields.io/badge/Maven-3.7%2B-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white"/>
  <img src="https://img.shields.io/badge/License-MIT-2ea44f?style=for-the-badge"/>
</p>

**Public web app** | **Instagram JSON analysis** | **No login · No database**

I ain't ur fan compares two Instagram JSON exports and shows who is a friend, a fan, or an admirer.

> ¿Cansado de ser fan de gente que conoces? Sé solo fan de quien quieras serlo.

A **fan** is someone you follow who does not follow you back.

## What it shows

- 👥 **Seguidores** and **cuentas que sigues** (full lists)
- 🤝 **Amigos** — mutual follow
- ⭐ **Fans** — you follow them, they do not follow you
- 👀 **Admiradores** — they follow you, you do not follow them
- Totals for followers, following, and each list

Files are processed in memory and are **not stored permanently**. There is no Instagram login, no passwords, and no database.

## Built with

- Java 17
- Spring Boot 3.4
- Maven (wrapper included)
- Angular 20
- Node.js 20+ for the frontend

## How to get your Instagram JSON files

1. Go to **Instagram** → **Your profile** → **Menu (≡)** → **Your Instagram Data**
2. Select **Request Download** → **JSON**
3. You will receive an **email** with a download link (0–48h)
4. Download the ZIP, extract it, and open **connections** → **followers_and_following**
5. Upload these two files in the web app:
   - `followers_1.json`
   - `following.json`

## Expected file structure

The two files do **not** have the same shape.

- `followers_1.json`: an array of people who follow you. The username is in `string_list_data[0].value`.
- `following.json`: an object with `relationships_following`. The username is in `title`.

`followers_1.json`:

```json
[
  {
    "title": "",
    "media_list_data": [],
    "string_list_data": [
      {
        "href": "https://www.instagram.com/usuario",
        "value": "usuario",
        "timestamp": 123456789
      }
    ]
  }
]
```

`following.json`:

```json
{
  "relationships_following": [
    {
      "title": "usuario",
      "string_list_data": [
        {
          "href": "https://www.instagram.com/_u/usuario",
          "timestamp": 123456789
        }
      ]
    }
  ]
}
```

Each file must be valid JSON, not empty, contain usernames, and stay under **10 MB**.

## Run locally

Use **two terminals**. Start the backend first.

### Backend

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

On macOS / Linux:

```bash
cd backend
./mvnw spring-boot:run
```

API: [http://localhost:8080](http://localhost:8080)

Health check:

```bash
curl http://localhost:8080/api/health
```

Expected:

```json
{ "status": "ok" }
```

### Frontend

```bash
cd frontend
npm install
npm start
```

UI: [http://localhost:4200](http://localhost:4200) — it calls `http://localhost:8080`.

Then choose `followers_1.json` and `following.json` and click **Analizar**.

You can also call the API directly:

```bash
curl -X POST http://localhost:8080/api/analyzer/analyze ^
  -F "followers=@followers_1.json" ^
  -F "following=@following.json"
```

### Tests

```powershell
cd backend
.\mvnw.cmd test
```

## Project structure

```text
I-aint-ur-fan/
├── backend/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/java/com/iainturfan/
│         ├── IAintUrFanApplication.java
│         ├── AnalyzerController.java
│         ├── AnalyzerService.java
│         ├── JsonManager.java
│         ├── AnalysisResult.java
│         └── CorsConfig.java
├── frontend/
│   └── src/app/
│         ├── app.component.ts
│         ├── app.component.html
│         ├── app.component.css
│         └── analyzer.service.ts
├── README.md
└── .gitignore
```

## Privacy

Only usernames from the two JSON files are processed. The backend does not save uploads, does not ask for an Instagram password, and does not call Instagram’s API.

## Limitations

- You must export the JSON files yourself; there is no Instagram login.
- If Instagram changes the export format, parsing may fail.
- Large lists scroll in the UI; analysis is still a single request.

## License

MIT License. See [LICENSE](LICENSE).
