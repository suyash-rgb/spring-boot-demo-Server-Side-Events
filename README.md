# SSE Spring Boot Demo

A minimal Spring Boot project demonstrating two Server-Sent Events (SSE) approaches:
- Servlet-based SSE using SseEmitter at /events  
- Reactive SSE using WebFlux and Flux<ServerSentEvent> at /reactive-events

This repo is ideal for a short demo showing differences in coding style, threading, and runtime behavior for LLM-style incremental streaming.

---

## Prerequisites

- **Java 17+** or the JDK version your Spring Boot starter requires  
- **Maven** (or use the bundled wrapper `./mvnw` / `mvnw.cmd`)  
- A code editor or IDE (IntelliJ IDEA recommended)  
- Basic Git and terminal/command-prompt skills

Optional:
- If you preview files from your IDE (port like 63342), you may need to enable CORS in the backend or serve the static page from Spring Boot to avoid cross-origin issues.

---

## Project layout (important files)

- src/main/java/.../SseController.java — servlet-based SSE using SseEmitter (GET /events)  
- src/main/java/.../ReactiveSseController.java — reactive SSE with Flux<ServerSentEvent> (GET /reactive-events)  
- src/main/java/.../config/WebConfig.java — optional CORS configuration bean (if present)  
- src/main/resources/static/index.html — simple demo page for the servlet SSE endpoint  
- src/main/resources/static/reactive.html — simple demo page for the reactive SSE endpoint  
- .gitignore — project ignores and settings

---

## Setup and run (step-by-step)

Follow these steps exactly to clone, build and run the demo locally.

1. Clone the repo
```bash
git clone https://github.com/your-username/your-repo-name.git
cd your-repo-name
```
Replace the URL with your repository path.

2. Inspect and edit configuration (optional)
- If you added a `WebConfig` CORS bean, confirm allowed origins match how you will open the frontend (e.g., `http://localhost:63342` if using IDE preview, or no CORS required if serving from Spring Boot).
- If you want to set an alternate server port, open `src/main/resources/application.properties` and set:
```properties
server.port=8080
```

3. Build the project
- On Unix/macOS:
```bash
./mvnw clean package
```
- On Windows (Command Prompt / PowerShell):
```cmd
mvnw.cmd clean package
```
This downloads dependencies and compiles the project.

4. Run the application
- Using Maven wrapper:
```bash
./mvnw spring-boot:run
```
or on Windows:
```cmd
mvnw.cmd spring-boot:run
```
- Or run the packaged jar:
```bash
java -jar target/*.jar
```

5. Open the demo pages
- Recommended (no CORS): serve static pages via Spring Boot.
  - Servlet SSE demo:
    - Open in browser: http://localhost:8080/index.html
    - This page connects to `/events` on the same origin.
  - Reactive SSE demo:
    - Open in browser: http://localhost:8080/reactive.html
    - This page connects to `/reactive-events`.

- Alternative (IDE preview or separate frontend):
  - If you open the HTML file through an IDE preview (a different origin like http://localhost:63342), either:
    - Update the EventSource URL to `http://localhost:8080/events` or `http://localhost:8080/reactive-events` in the page, and
    - Add CORS configuration allowing the preview origin (see CORS steps below).

---

## Endpoints and usage

- **GET /events**  
  - Servlet-based SSE. Each client connection uses a server-side emitter and a background thread that sends periodic events (SseEmitter).

- **GET /reactive-events**  
  - Reactive SSE. Returns `Flux<ServerSentEvent<String>>` with `Content-Type: text/event-stream`. Non-blocking and scalable.

Client example (browser console or static page):
```js
const s = new EventSource('/events'); // or '/reactive-events'
s.onmessage = e => console.log('received', e.data);
s.onerror = e => { console.error('EventSource failed', e); s.close(); };
```

---

## CORS & common troubleshooting

1. CORS error: "No 'Access-Control-Allow-Origin' header is present"
- Cause: Your web page is being served from a different origin (different port or protocol).
- Quick fixes:
  - Serve the HTML from Spring Boot (place files under `src/main/resources/static`) and open `http://localhost:8080/index.html` — same origin, no CORS needed.
  - Or enable CORS for the endpoint. Example config class:
    ```java
    @Configuration
    public class WebConfig {
        @Bean
        public WebMvcConfigurer corsConfigurer() {
            return new WebMvcConfigurer() {
                @Override
                public void addCorsMappings(CorsRegistry registry) {
                    registry.addMapping("/events")
                            .allowedOrigins("http://localhost:63342") // add your preview origin
                            .allowedMethods("GET")
                            .allowCredentials(true);
                    registry.addMapping("/reactive-events")
                            .allowedOrigins("http://localhost:63342")
                            .allowedMethods("GET")
                            .allowCredentials(true);
                }
            };
        }
    }
    ```
  - Restart the Spring Boot app after changing CORS settings.

2. 404 Not Found for /events or /reactive-events
- Ensure controllers are in a package scanned by Spring Boot (same or child package of the main application class).
- Confirm the app is running and listening on the expected port.
- Check logs for startup errors.

3. DOM errors like `Cannot read properties of null (reading 'appendChild')`
- Ensure your HTML has the element the script references:
  ```html
  <ul id="events"></ul>
  ```
- Ensure your script runs after the DOM is loaded (place `<script>` at bottom of body or use `DOMContentLoaded`).

4. EventSource immediately errors or closes
- Check backend logs for exceptions thrown while sending events.
- For SseEmitter: ensure emitter.complete()/completeWithError() is called appropriately.
- For reactive endpoint: ensure reactor dependency (`spring-boot-starter-webflux`) is on the classpath.

---

