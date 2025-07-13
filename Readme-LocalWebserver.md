# LocalWebServer – Internal HTTP Server Utility

This module provides a lightweight HTTP server for handling internal callbacks and serving emulated endpoints during development and testing.  
It is designed to run strictly on `localhost` (127.0.0.1) and is **not intended for production or external use**.

---

## Features

- **Minimal dependencies:** Uses Java’s built-in `com.sun.net.httpserver.HttpServer`
- **Secure by default:** Binds only to `localhost` (127.0.0.1)
- **Customizable:** Easily extend by implementing your own request handlers
- **Test-friendly:** Useful for intercepting OAuth callbacks, emulating remote services, and integration tests

---

## Typical Use Cases

- **OAuth Redirect URI Server:**  
  Intercept authorization codes from third-party services (e.g., Strava API) during local development.

- **Test Site Emulator:**  
  Mimic remote APIs for reliable, offline integration tests.

---

## Usage

### Starting the Server

```java
LocalWebServer server = new MyCustomWebServer(8080); // or use 0 for random port
server.start();

// ... interact with the server ...

server.stop();
```

### Example: Implementing a Request Handler

```java
public class MyCustomWebServer extends LocalWebServer {
    public MyCustomWebServer(int port) {
        super(port);
    }

    @Override
    protected void handleRequest(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if ("/callback".equals(path)) {
            // Parse query, respond, etc.
            String response = "Received!";
            exchange.sendResponseHeaders(200, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }
}
```

---

## Security & Limitations

- The server **only listens on 127.0.0.1** by design. It cannot be accessed from outside the local machine.
- Not suitable for production web serving; lacks features and hardening found in mature HTTP frameworks.
- Intended for development tooling, test infrastructure, and internal automation only.

---

## Customization

- Extend `LocalWebServer` and override `handleRequest(HttpExchange exchange)` for custom behavior.
- Use utility methods in `HttpUtils` for query parsing and multipart form data.

---

## License

MIT License. See [LICENSE](LICENSE) for details.

---

## Contributing

Bug reports and pull requests are welcome!
