import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {


    private static final String PUBLIC_DIR = "public";

    public static void main(String[] args) {
        try {
            HttpServer server = makeServer();


            initRoutes(server);
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void initRoutes(HttpServer server) {
        server.createContext("/", Main::handleFileRequest);
    }

    private static void handleFileRequest(HttpExchange exchange) {
        try {
            URI uri = exchange.getRequestURI();
            String pathStr = uri.getPath();

            if (pathStr.equals("/")) {
                pathStr = "/index.html";
            }

            Path filePath = Paths.get(PUBLIC_DIR, pathStr);

            if (!Files.exists(filePath) || Files.isDirectory(filePath)) {
                send404(exchange, pathStr);
                return;
            }

            String contentType = getContentType(filePath);
            byte[] fileBytes = Files.readAllBytes(filePath);

            exchange.getResponseHeaders().add("Content-Type", contentType);
            exchange.sendResponseHeaders(200, fileBytes.length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(fileBytes);
            }

            System.out.printf("Success: %s (%s)%n", filePath, contentType);

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static String getContentType(Path filePath) {
        String fileName = filePath.getFileName().toString().toLowerCase();

        if (fileName.endsWith(".html") || fileName.endsWith(".htm")) {
            return "text/html; charset=utf-8";
        } else if (fileName.endsWith(".css")) {
            return "text/css; charset=utf-8";
        } else if (fileName.endsWith(".js")) {
            return "application/javascript; charset=utf-8";
        } else if (fileName.endsWith(".png")) {
            return "image/png";
        } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (fileName.endsWith(".json")) {
            return "application/json; charset=utf-8";
        }

        return "application/octet-stream";
    }

    private static void send404(HttpExchange exchange, String missingPath) throws IOException {
        String responseText = "Что такого документа нет: " + missingPath;
        byte[] responseBytes = responseText.getBytes(java.nio.charset.StandardCharsets.UTF_8);

        exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");
        exchange.sendResponseHeaders(404, responseBytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
        System.out.println("404 Not Found: " + missingPath);
    }

    private static HttpServer makeServer() throws IOException {
        InetSocketAddress address = new InetSocketAddress("localhost", 9889);
        System.out.printf("Server starting at: http://localhost:%s/%n", address.getPort());
        return HttpServer.create(address, 50);
    }
}