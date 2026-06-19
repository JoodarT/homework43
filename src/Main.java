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

    private static final String PUBLIC_DIR = "templates";


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

    private static void handleFileRequest(HttpExchange httpExchange) {
    }

    private static void handleRootRequest(HttpExchange exchange) {
        startResponse(exchange);
        try (PrintWriter writer = getWriterFrom(exchange)) {
            writer.println("=== ГЛАВНАЯ СТРАНИЦА ===");
            writeBaseInfo(writer, exchange);
        }
    }

    private static void handleAppsRequest(HttpExchange exchange) {
        startResponse(exchange);
        try (PrintWriter writer = getWriterFrom(exchange)) {
            writer.println("=== СПИСОК ПРИЛОЖЕНИЙ ===");
            writer.println("Доступные приложения: Чат, Калькулятор, Заметки.");
            writeBaseInfo(writer, exchange);
        }
    }

    private static void handleProfileRequest(HttpExchange exchange) {
        startResponse(exchange);
        try (PrintWriter writer = getWriterFrom(exchange)) {
            writer.println("=== ПРОФИЛЬ ПОЛЬЗОВАТЕЛЯ ===");
            writer.println("Имя: Иван Иванов");
            writer.println("Статус: Студент Tractor School");
            writeBaseInfo(writer, exchange);
        }
    }

    private static void startResponse(HttpExchange exchange) {
        try {
            exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");
            exchange.sendResponseHeaders(200, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeBaseInfo(PrintWriter writer, HttpExchange exchange) {
        String method = exchange.getRequestMethod();
        URI uri = exchange.getRequestURI();
        String path = exchange.getHttpContext().getPath();

        write(writer, "HTTP method", method);
        write(writer, "Запрос", uri.toString());
        write(writer, "Обработка через", path);
        writeHeaders(writer, "Заголовки запроса", exchange.getRequestHeaders()); // Изменено на getRequestHeaders
    }

    private static void writeHeaders(PrintWriter writer, String type, Headers headers) {
        write(writer, type, "");
        headers.forEach((k, v) -> write(writer, "\t" + k, v.toString()));
    }

    private static void write(PrintWriter writer, String msg, String value) {
        writer.printf("%s: %s%n%n", msg, value);
    }

    private static PrintWriter getWriterFrom(HttpExchange exchange) {
        OutputStream out = exchange.getResponseBody();
        Charset charset = StandardCharsets.UTF_8;
        return new PrintWriter(out , false, charset);
    }

    private static HttpServer makeServer() throws IOException {
        String host = "localhost";
        InetSocketAddress address = new InetSocketAddress(host, 8990);

        String msg = "Запускаем сервер по адресу: " + "http://%s:%s/%n";
        System.out.printf(msg, address.getHostName(), address.getPort());

        HttpServer server = HttpServer.create(address, 50);
        System.out.println("Удачно");
        return server;
    }
}