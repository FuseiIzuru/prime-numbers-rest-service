# Prime Numbers REST Service

A small Java 17 + Maven project for the prime-number REST exercise. The server keeps a persistent text-file database; the client checks the server first and calculates an unknown number locally.

## Requirements

- JDK 17 or newer
- Maven 3.8 or newer

## Build and run

Open a terminal in the project directory and build:

```bash
mvn clean package
```

Start the server in terminal 1:

```bash
mvn exec:java -Dexec.mainClass=edu.example.primes.PrimeNumbersServer
```

Start the client in terminal 2:

```bash
mvn exec:java -Dexec.mainClass=edu.example.primes.PrimeNumbersClient
```

The server listens on `http://localhost:8080`. Optional arguments are the port and database file for the server, and the base URL for the client. For example:

```bash
mvn exec:java -Dexec.mainClass=edu.example.primes.PrimeNumbersServer -Dexec.args="8081 data/custom.txt"
mvn exec:java -Dexec.mainClass=edu.example.primes.PrimeNumbersClient -Dexec.args="http://localhost:8081"
```

## REST API

| Operation | Request | Result |
|---|---|---|
| Look up | `GET /api/numbers/17` | `200` and the saved classification, or `404` if unknown |
| Store | `PUT /api/numbers/17` with `Content-Type: application/json` and `{"prime":true}` | `200` and the stored record |

The data file is `data/numbers.txt`. It is created automatically and intentionally ignored by Git so runtime data is not committed.

## Suggested manual checks

1. Build and start the server; confirm its listening message.
2. Query `17` in the client. Expect a cache miss, a local calculation, and a successful save.
3. Query `17` again. Expect the client to report the server record without recalculating.
4. Query `18`; expect it to be calculated and stored as not prime.
5. Query `1`, `2`, and `-7`; expect not prime, prime, and not prime respectively.
6. Stop and restart the server, then query `17` again to confirm file persistence.
7. Optionally inspect the HTTP methods with curl:

```bash
curl -i http://localhost:8080/api/numbers/19
curl -i -X PUT http://localhost:8080/api/numbers/19 -H "Content-Type: application/json" -d '{"prime":true}'
curl -i http://localhost:8080/api/numbers/19
```

For submission, include source code and screenshots showing the server running, a first-time calculation and save, and a repeated lookup served from the server. See [REPORT_EN_ZH.md](REPORT_EN_ZH.md) for the short bilingual report.
