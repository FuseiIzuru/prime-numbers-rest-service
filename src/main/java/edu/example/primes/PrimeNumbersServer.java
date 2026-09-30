package edu.example.primes;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** REST API that stores classifications in data/numbers.txt. */
public final class PrimeNumbersServer {
    private static final Pattern PRIME_FIELD = Pattern.compile("\\\"prime\\\"\\s*:\\s*(true|false)", Pattern.CASE_INSENSITIVE);
    private final Map<Long, Boolean> records = new HashMap<>();
    private final Path dataFile;

    private PrimeNumbersServer(Path dataFile) throws IOException {
        this.dataFile = dataFile;
        load();
    }

    public static void main(String[] args) throws IOException {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 8080;
        Path file = Path.of(args.length > 1 ? args[1] : "data/numbers.txt");
        PrimeNumbersServer app = new PrimeNumbersServer(file);
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/api/numbers", app::handleNumbers);
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
        System.out.printf("Prime numbers REST service listening at http://localhost:%d/api/numbers/{number}%n", port);
        System.out.println("Stored records: " + app.recordCount());
    }

    private void handleNumbers(HttpExchange exchange) throws IOException {
        try (exchange) {
            String[] parts = exchange.getRequestURI().getPath().split("/");
            if (parts.length != 4 || !parts[1].equals("api") || !parts[2].equals("numbers")) {
                send(exchange, 404, "{\"error\":\"Use /api/numbers/{integer}\"}");
                return;
            }
            final long number;
            try {
                number = Long.parseLong(parts[3]);
            } catch (NumberFormatException e) {
                send(exchange, 400, "{\"error\":\"Number must be a signed 64-bit integer\"}");
                return;
            }

            switch (exchange.getRequestMethod()) {
                case "GET" -> getNumber(exchange, number);
                case "PUT" -> putNumber(exchange, number);
                default -> {
                    exchange.getResponseHeaders().set("Allow", "GET, PUT");
                    send(exchange, 405, "{\"error\":\"Method not allowed; use GET or PUT\"}");
                }
            }
        }
    }

    private synchronized void getNumber(HttpExchange exchange, long number) throws IOException {
        Boolean prime = records.get(number);
        if (prime == null) {
            send(exchange, 404, "{\"number\":" + number + ",\"known\":false}");
        } else {
            send(exchange, 200, "{\"number\":" + number + ",\"prime\":" + prime + ",\"known\":true}");
        }
    }

    private synchronized void putNumber(HttpExchange exchange, long number) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Matcher matcher = PRIME_FIELD.matcher(body);
        if (!matcher.find()) {
            send(exchange, 400, "{\"error\":\"Request body must contain boolean field prime\"}");
            return;
        }
        boolean prime = Boolean.parseBoolean(matcher.group(1));
        records.put(number, prime);
        try {
            save();
        } catch (IOException e) {
            send(exchange, 500, "{\"error\":\"Could not persist the record\"}");
            return;
        }
        send(exchange, 200, "{\"number\":" + number + ",\"prime\":" + prime + ",\"known\":true}");
    }

    private void load() throws IOException {
        if (!Files.exists(dataFile)) return;
        for (String line : Files.readAllLines(dataFile, StandardCharsets.UTF_8)) {
            if (line.isBlank() || line.startsWith("#")) continue;
            String[] fields = line.split("\\t");
            if (fields.length == 2) {
                try { records.put(Long.parseLong(fields[0]), Boolean.parseBoolean(fields[1])); }
                catch (NumberFormatException ignored) { System.err.println("Skipping invalid data row: " + line); }
            }
        }
    }

    private void save() throws IOException {
        Path parent = dataFile.toAbsolutePath().getParent();
        if (parent != null) Files.createDirectories(parent);
        Path temp = dataFile.resolveSibling(dataFile.getFileName() + ".tmp");
        StringBuilder contents = new StringBuilder("# number\tprime\n");
        records.entrySet().stream().sorted(Map.Entry.comparingByKey())
                .forEach(entry -> contents.append(entry.getKey()).append('\t').append(entry.getValue()).append('\n'));
        Files.writeString(temp, contents, StandardCharsets.UTF_8);
        try { Files.move(temp, dataFile, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE); }
        catch (IOException e) { Files.move(temp, dataFile, StandardCopyOption.REPLACE_EXISTING); }
    }

    private synchronized int recordCount() { return records.size(); }

    private static void send(HttpExchange exchange, int status, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.getResponseHeaders().set("Cache-Control", "no-store");
        exchange.sendResponseHeaders(status, bytes.length);
        exchange.getResponseBody().write(bytes);
    }
}
