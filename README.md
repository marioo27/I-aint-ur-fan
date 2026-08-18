# I ain't ur fan

<p align="left">
  <img src="https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white"/>
  <img src="https://img.shields.io/badge/Maven-3.7%2B-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white"/>
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"/>
  <img src="https://img.shields.io/badge/Angular-20-DD0031?style=for-the-badge&logo=angular&logoColor=white"/>
  <img src="https://img.shields.io/badge/TypeScript-5.9-3178C6?style=for-the-badge&logo=typescript&logoColor=white"/>
  <img src="https://img.shields.io/badge/Node.js-20%2B-339933?style=for-the-badge&logo=nodedotjs&logoColor=white"/>
  <img src="https://img.shields.io/badge/License-MIT-2ea44f?style=for-the-badge"/>
</p>

[English](#english) · [Español](#español)

---

## English

**Public web app** | **Instagram JSON analysis** | **No login · No database**

I ain't ur fan compares two Instagram JSON exports and shows who is a friend, a fan, or an idol.

> Tired of being a fan of people you know? Only be a fan of who you want to be a fan of.

A **fan** is someone you follow who does not follow you back.

The UI can be switched between **Spanish** and **English**.

### What it shows

- 👥 **Followers** and **accounts you follow** (full lists)
- 🤝 **Friends** — mutual follow
- ⭐ **Fans** — they follow you, you do not follow them
- 👑 **Idols** — you follow them, they do not follow you back
- Totals for followers, following, and each list

Files are processed in memory and are **not stored permanently**. There is no Instagram login, no passwords, and no database.

This first version compares two files: `following.json` and `followers_1.json`. Extra follower files are not supported yet.

### Tech stack

- **Java 21** — backend language
- **Maven 3.7** — build tool
- **Spring Boot 3.4** — REST API, multipart uploads, CORS, embedded Tomcat
- **Angular 20** — frontend
- **TypeScript 5.9** — frontend language
- **Node.js 20+** — run and build the Angular app
- **RxJS** — HTTP calls from the UI

### How to get your Instagram JSON files

1. Go to **Instagram** → **Your profile** → **Menu (≡)** → **Your Instagram Data**
2. Select **Request Download** → **JSON**
3. You will receive an **email** with a download link (0–48h)
4. Download the ZIP, extract it, and open **connections** → **followers_and_following**
5. Upload these two files in the web app:
   - `followers_1.json`
   - `following.json`

### Expected file structure

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

### Run locally

Use **two terminals**. Start the backend first.

#### Backend

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

#### Frontend

```bash
cd frontend
npm install
npm start
```

UI: [http://localhost:4200](http://localhost:4200) — it calls `http://localhost:8080`.

Then choose `followers_1.json` and `following.json` and click **Analyze**.

You can also call the API directly:

```bash
curl -X POST http://localhost:8080/api/analyzer/analyze ^
  -F "followers=@followers_1.json" ^
  -F "following=@following.json"
```

#### Tests

```powershell
cd backend
.\mvnw.cmd test
```

### Project structure

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
│         ├── analyzer.service.ts
│         ├── language.service.ts
│         └── translations.ts
├── README.md
└── .gitignore
```

### Privacy

Only usernames from the two JSON files are processed. The backend does not save uploads, does not ask for an Instagram password, and does not call Instagram’s API.

### Limitations

- You must export the JSON files yourself; there is no Instagram login.
- If Instagram changes the export format, parsing may fail.
- Large lists scroll in the UI; analysis is still a single request.
- Only `followers_1.json` is supported if Instagram splits followers into several files.

### License

MIT License. See [LICENSE](LICENSE).

---

## Español

**Aplicación web pública** | **Análisis de JSON de Instagram** | **Sin login · Sin base de datos**

I ain't ur fan compara dos exportaciones JSON de Instagram y te muestra quién es amigo, fan o ídolo.

> ¿Cansado de ser fan de gente que conoces? Sé solo fan de quien quieras serlo.

Un **fan** es alguien a quien sigues y que no te sigue de vuelta.

La interfaz se puede cambiar entre **español** e **inglés**.

### Qué muestra

- 👥 **Seguidores** y **cuentas que sigues** (listas completas)
- 🤝 **Amigos** — os seguís mutuamente
- ⭐ **Fans** — te siguen, pero tú no les sigues
- 👑 **Ídolos** — les sigues, pero no te siguen de vuelta
- Totales de seguidores, seguidos y de cada lista

Los archivos se procesan en memoria y **no se guardan de forma permanente**. No hay login de Instagram, ni contraseñas, ni base de datos.

Esta primera versión compara dos archivos: `following.json` y `followers_1.json`. Si tienes más archivos de seguidores, de momento no se pueden usar.

### Tecnologías

- **Java 21** — lenguaje del backend
- **Maven 3.7** — herramienta de build
- **Spring Boot 3.4** — API REST, subida multipart, CORS, Tomcat embebido
- **Angular 20** — frontend
- **TypeScript 5.9** — lenguaje del frontend
- **Node.js 20+** — ejecutar y construir la app Angular
- **RxJS** — llamadas HTTP desde la interfaz

### Cómo obtener tus archivos JSON de Instagram

1. Ve a **Instagram** → **Tu perfil** → **Menú (≡)** → **Tus datos de Instagram**
2. Selecciona **Solicitar descarga** → **JSON**
3. Recibirás un **correo** con un enlace de descarga (0–48 h)
4. Descarga el ZIP, extráelo y entra en **connections** → **followers_and_following**
5. Sube estos dos archivos en la web:
   - `followers_1.json`
   - `following.json`

### Estructura esperada de los archivos

Los dos archivos **no** tienen la misma forma.

- `followers_1.json`: un array de personas que te siguen. El usuario está en `string_list_data[0].value`.
- `following.json`: un objeto con `relationships_following`. El usuario está en `title`.

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

Cada archivo debe ser JSON válido, no estar vacío, contener usuarios y no superar **10 MB**.

### Ejecutar en local

Usa **dos terminales**. Arranca primero el backend.

#### Backend

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

En macOS / Linux:

```bash
cd backend
./mvnw spring-boot:run
```

API: [http://localhost:8080](http://localhost:8080)

Comprobar salud:

```bash
curl http://localhost:8080/api/health
```

Respuesta esperada:

```json
{ "status": "ok" }
```

#### Frontend

```bash
cd frontend
npm install
npm start
```

Interfaz: [http://localhost:4200](http://localhost:4200) — llama a `http://localhost:8080`.

Luego elige `followers_1.json` y `following.json` y pulsa **Analizar**.

También puedes llamar a la API a mano:

```bash
curl -X POST http://localhost:8080/api/analyzer/analyze ^
  -F "followers=@followers_1.json" ^
  -F "following=@following.json"
```

#### Tests

```powershell
cd backend
.\mvnw.cmd test
```

### Estructura del proyecto

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
│         ├── analyzer.service.ts
│         ├── language.service.ts
│         └── translations.ts
├── README.md
└── .gitignore
```

### Privacidad

Solo se procesan los nombres de usuario de los dos JSON. El backend no guarda las subidas, no pide contraseña de Instagram y no llama a la API de Instagram.

### Limitaciones

- Tienes que exportar los JSON tú mismo; no hay login de Instagram.
- Si Instagram cambia el formato de la exportación, el análisis puede fallar.
- Las listas grandes se ven con scroll; el análisis sigue siendo una sola petición.
- Solo se admite `followers_1.json` si Instagram divide los seguidores en varios archivos.

### Licencia

Licencia MIT. Consulta [LICENSE](LICENSE).
