import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Main {
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
        server.createContext("/", Main::handleRequest);
        server.createContext("/apps/", Main::handleRequest);
        server.createContext("/apps/profile", Main::handleRequest);
    }

    private static void handleRootRequest(HttpExchange exchange) {
        startResponse(exchange);
        try (PrintWriter writer = getWriterFrom(exchange)) {
            writer.println("=== ГЛАВНАЯ СТРАНИЦА ===");
            writeBaseInfo(writer, exchange);
        }
    }

    private static void writeBaseInfo(PrintWriter writer, HttpExchange exchange) {
    }

    private static void startResponse(HttpExchange exchange) {
    }

    private static void handleRequest(HttpExchange exchange) {
        try {
            exchange.getResponseHeaders().add("Content-Type", "text/plain; charset = utf-8");
            int responseCode = 200;
            int length = 0;
            exchange.sendResponseHeaders(responseCode, length);

        } catch (IOException e) {
            e.printStackTrace();
        }

        try (PrintWriter writer = getWriterFrom(exchange)) {
            String method = exchange.getRequestMethod();
            URI uri = exchange.getRequestURI();
            String path = exchange.getHttpContext().getPath();
            write(writer, "HTTP method", method);
            write(writer, "Запрос", uri.toString());
            write(writer, "Обработка через ", path);
            writeHeaders(writer, "Заголовки запроса", exchange.getResponseHeaders());
        }

    }

    private static void writeHeaders(PrintWriter writer, String type, Headers headers) {
        write(writer, type, "");
        headers.forEach((k, v) -> write(writer, "\t" + k, v.toString()));
    }

    private static void write(PrintWriter writer, String msg, String method) {
        String data = String.format("%s:%s%n%n", msg, method);
    }

    private static PrintWriter getWriterFrom(HttpExchange exchange) {
        OutputStream out = exchange.getResponseBody();
        Charset charset = StandardCharsets.UTF_8;
        return new PrintWriter(out , false, charset);
    }

    private static HttpServer makeServer() throws IOException {
        String host = "localhost";
        InetSocketAddress address = new InetSocketAddress(host, 8990);

        String msg = "запускаем сервер по адресу: " + "http://%s:%s/%n";
        System.out.printf(msg, address.getHostName(), address.getPort());

        HttpServer server = HttpServer.create(address, 50);
        System.out.println(" удачно");
        return server;
    }
}