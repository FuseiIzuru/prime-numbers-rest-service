package edu.example.primes;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Console client: asks the server first, calculates only on a cache miss, then PUTs the result. */
public final class PrimeNumbersClient {
    private static final Pattern PRIME_FIELD = Pattern.compile("\\\"prime\\\"\\s*:\\s*(true|false)");
    private final HttpClient http = HttpClient.newHttpClient();
    private final String baseUrl;

    private PrimeNumbersClient(String baseUrl) { this.baseUrl = baseUrl.replaceAll("/$", ""); }

    public static void main(String[] args) throws IOException, InterruptedException {
        String baseUrl = args.length > 0 ? args[0] : "http://localhost:8080";
        PrimeNumbersClient client = new PrimeNumbersClient(baseUrl);
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Enter signed integers to check (or 'quit' to exit).");
            while (true) {
                System.out.print("number> ");
                if (!scanner.hasNextLine()) break;
                String input = scanner.nextLine().trim();
                if (input.equalsIgnoreCase("quit") || input.equalsIgnoreCase("exit")) break;
                try {
                    long number = Long.parseLong(input);
                    client.check(number);
                } catch (NumberFormatException e) {
                    System.out.println("Please enter an integer between -9223372036854775808 and 9223372036854775807.");
                }
            }
        }
    }

    private void check(long number) throws IOException, InterruptedException {
        URI uri = URI.create(baseUrl + "/api/numbers/" + number);
        HttpResponse<String> get = http.send(HttpRequest.newBuilder(uri).GET().build(), HttpResponse.BodyHandlers.ofString());
        if (get.statusCode() == 200) {
            boolean prime = readPrime(get.body());
            System.out.printf("Server record: %d is %s.%n", number, prime ? "prime" : "not prime");
            return;
        }
        if (get.statusCode() != 404) {
            System.out.printf("Server GET failed (%d): %s%n", get.statusCode(), get.body());
            return;
        }

        System.out.println("No server record; calculating locally...");
        boolean prime = isPrime(number);
        String body = "{\"prime\":" + prime + "}";
        HttpRequest put = HttpRequest.newBuilder(uri)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(body)).build();
        HttpResponse<String> saved = http.send(put, HttpResponse.BodyHandlers.ofString());
        if (saved.statusCode() >= 200 && saved.statusCode() < 300) {
            System.out.printf("Calculated and stored: %d is %s.%n", number, prime ? "prime" : "not prime");
        } else {
            System.out.printf("Calculated result, but server PUT failed (%d): %s%n", saved.statusCode(), saved.body());
        }
    }

    static boolean isPrime(long number) {
        if (number < 2) return false;
        if (number == 2) return true;
        if (number % 2 == 0) return false;
        for (long divisor = 3; divisor <= number / divisor; divisor += 2) {
            if (number % divisor == 0) return false;
        }
        return true;
    }

    private static boolean readPrime(String json) throws IOException {
        Matcher matcher = PRIME_FIELD.matcher(json);
        if (!matcher.find()) throw new IOException("Server returned JSON without a prime field: " + json);
        return Boolean.parseBoolean(matcher.group(1));
    }
}
