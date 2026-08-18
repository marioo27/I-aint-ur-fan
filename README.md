# I ain't ur fan

Aplicación web pública para analizar dos archivos JSON exportados desde Instagram: el de seguidores y el de cuentas que sigues.

## Qué problema resuelve

Instagram no muestra de forma clara quién no te sigue de vuelta. Esta aplicación compara tus exportaciones JSON y te indica:

- Qué cuentas sigues y no te siguen de vuelta.
- Qué cuentas te siguen y tú no sigues.
- El número total de seguidores.
- El número total de cuentas que sigues.

## Cómo funciona

1. Exportas tus datos de Instagram en JSON.
2. Subes `followers_1.json` y `following.json` en la web.
3. El frontend Angular envía los archivos al backend Spring Boot.
4. El backend lee los JSON en memoria, compara las listas y devuelve el resultado.
5. Los archivos no se guardan de forma permanente.

No hay base de datos, ni usuarios, ni login de Instagram.

## Cómo ejecutar el backend en local

Requisitos: Java 17 o superior. Maven no hace falta porque el proyecto incluye Maven Wrapper.

```bash
cd backend
./mvnw spring-boot:run
```

En Windows:

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

El backend queda en [http://localhost:8080](http://localhost:8080).

## Cómo ejecutar el frontend en local

Requisitos: Node.js 20 o superior.

```bash
cd frontend
npm install
npm start
```

La interfaz queda en [http://localhost:4200](http://localhost:4200) y llama a `http://localhost:8080`.

## Cómo probar GET /api/health

Con el backend en marcha:

```bash
curl http://localhost:8080/api/health
```

Respuesta esperada:

```json
{ "status": "ok" }
```

## Cómo probar el análisis con dos archivos JSON

1. Arranca backend y frontend.
2. Abre [http://localhost:4200](http://localhost:4200).
3. Selecciona el archivo de seguidores y el de cuentas seguidas.
4. Pulsa **Analizar**.

También puedes probar el endpoint a mano:

```bash
curl -X POST http://localhost:8080/api/analyzer/analyze ^
  -F "followers=@followers_1.json" ^
  -F "following=@following.json"
```

## Formato esperado de los JSON

Los archivos salen de la exportación de Instagram:

**Instagram → Tu perfil → Menú → Your Instagram Data → Request Download → JSON.**

Cuando llegue el ZIP, entra en `connections/followers_and_following` y usa:

- `followers_1.json`
- `following.json`

`followers_1.json` es un array. El usuario está en `string_list_data[0].value`:

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

`following.json` es un objeto. El usuario está en `title`:

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

Cada archivo debe ser JSON, no estar vacío, contener usuarios y no superar 10 MB.

## Cómo subir el repositorio a GitHub

El remoto ya puede existir. Desde la carpeta del repositorio Git:

```bash
git add .
git status
git commit -m "Add first working version of I ain't ur fan"
git push origin main
```

No subas `node_modules`, `target`, `dist` ni archivos `.env`.

## Cómo desplegar el backend en Render

1. Crea un Web Service en Render a partir del repositorio.
2. Configura:

- **Root Directory:** `backend`
- **Build Command:** `./mvnw clean package -DskipTests`
- **Start Command:** `java -jar target/iaint-ur-fan-backend.jar`

3. Render asigna `PORT`. El backend ya usa `server.port=${PORT:8080}`.
4. Añade, si hace falta, la variable `CORS_ALLOWED_ORIGINS`.

También hay un `backend/Dockerfile` por si prefieres desplegar con Docker. El JAR generado se llama `iaint-ur-fan-backend.jar`.

## Cómo desplegar el frontend en Netlify

1. Conecta el repositorio a Netlify.
2. El archivo `netlify.toml` ya define:

- **Base:** `frontend`
- **Build command:** `npm run build`
- **Publish:** `dist/iaint-ur-fan/browser`

Angular 20 genera la carpeta `browser`. `frontend/public/_redirects` redirige las rutas al `index.html`.

## Dónde cambiar la URL de Render

Edita:

`frontend/src/environments/environment.prod.ts`

Sustituye:

```ts
apiUrl: 'https://REEMPLAZAR-CON-URL-DE-RENDER'
```

por la URL real de Render, por ejemplo `https://iaint-ur-fan.onrender.com`, sin barra final. Después vuelve a desplegar el frontend.

En local se usa `frontend/src/environments/environment.ts` con `http://localhost:8080`.

## Cómo configurar CORS

Por defecto el backend permite:

- `http://localhost:4200`
- `https://iaint-ur-fan.netlify.app`

En Render puedes cambiar los orígenes con:

```text
CORS_ALLOWED_ORIGINS=http://localhost:4200,https://iaint-ur-fan.netlify.app,https://tu-dominio.netlify.app
```

No uses `*` en producción. Si el sitio de Netlify tiene otro dominio, añádelo a esa variable.

## Qué datos se procesan

Solo los nombres de usuario que aparecen en los JSON de seguidores y seguidos. No se pide contraseña ni acceso a tu cuenta de Instagram.

## Los archivos no se guardan de forma permanente

El backend lee los archivos en memoria, calcula el resultado y responde. No hay base de datos ni almacenamiento persistente de las subidas.

## Limitaciones actuales

- No hay cuentas de usuario ni historial de análisis.
- No se conecta con la API de Instagram; hace falta exportar los JSON a mano.
- El formato esperado es el de la exportación oficial. Si Instagram lo cambia, el parser puede dejar de reconocer usuarios.
- La URL de producción del backend hay que ponerla a mano en `environment.prod.ts`.
- El frontend de producción no debe apuntar a `localhost`.
- Listas muy grandes se muestran con scroll, pero el análisis ocurre en una sola petición.
- Esta primera versión no calcula seguidores mutuos.

## Estructura

```text
I-aint-ur-fan/
├── backend/          Spring Boot + Maven
├── frontend/         Angular
├── netlify.toml
├── README.md
└── .gitignore
```
