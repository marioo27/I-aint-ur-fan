# Frontend — I ain't ur fan

Angular 20 UI for uploading Instagram JSON exports and showing friends, fans, and idols. Language toggle: Spanish / English.

**Production:** [https://i-aint-ur-fan.netlify.app](https://i-aint-ur-fan.netlify.app)

Full project docs: [../README.md](../README.md)

## How to start

Requires the backend running at [http://localhost:8080](http://localhost:8080) (see [../backend/README.md](../backend/README.md)).

From the repository root:

```bash
cd frontend
npm install
npm start
```

- App: [http://localhost:4200](http://localhost:4200)
- Dev API URL: `http://localhost:8080` (from `src/environments/environment.ts`)

### Other scripts

```bash
cd frontend
npm run build    # production build → dist/iaint-ur-fan/browser
npm test         # unit tests
```

## Folder structure

```text
frontend/
├── angular.json
├── package.json
├── package-lock.json
├── tsconfig.json
├── tsconfig.app.json
├── tsconfig.spec.json
├── README.md
├── public/
│   └── _redirects                 # Netlify SPA redirects
└── src/
    ├── index.html
    ├── main.ts
    ├── styles.css
    ├── environments/
    │   ├── environment.ts         # apiUrl → localhost:8080
    │   └── environment.prod.ts    # apiUrl → Render
    └── app/
        ├── app.config.ts
        ├── app.component.ts
        ├── app.component.html
        ├── app.component.css
        ├── analyzer.service.ts    # POST /api/analyzer/analyze
        ├── language.service.ts    # ES / EN toggle
        └── translations.ts
```

## Stack

- Angular 20
- TypeScript 5.9
- RxJS
- Node.js 20+

## API URL

| Environment | File | `apiUrl` |
|-------------|------|----------|
| Development | `src/environments/environment.ts` | `http://localhost:8080` |
| Production | `src/environments/environment.prod.ts` | `https://i-aint-ur-fan.onrender.com` |

## Deploy (Netlify)

Configured in the repo-root [`netlify.toml`](../netlify.toml):

- **Base:** `frontend`
- **Build:** `npm run build`
- **Publish:** `dist/iaint-ur-fan/browser`
