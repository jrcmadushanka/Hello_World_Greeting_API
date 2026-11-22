# Hello World API

A simple HTTP API developed using Java and Maven.

The application accepts a name through a GET request and returns a greeting if the name starts with a letter between A and M (case-insensitive). Otherwise, it returns an error response.

## Technologies Used

- Java 21
- Maven
- JUnit 5
- Java HttpServer

## How to Run

Make sure Java 21 and Maven are installed.

Build the application:

```bash
mvn clean package
```

Run the generated JAR:

```bash
java -jar target/hello-world-api-1.0-SNAPSHOT.jar
```

The application will start on `http://localhost:8080`. 
If the port 8080 unavailable, Please checnge the port on Constants file PORT constant.

## API Endpoint

**GET** `/hello-world?name=alice`

Successful response (HTTP 200):

```json
{
  "message": "Hello Alice"
}
```

Invalid input (HTTP 400):

```json
{
  "error": "Invalid Input"
}
```

## Running Tests

To run the unit and integration tests:

```bash
mvn clean test
```

The tests cover valid and invalid names, alphabet boundaries, missing or empty inputs, and HTTP responses.

## Assumptions

- Only the first character of the name is checked against the A–M range.
- Leading and trailing spaces are removed before validation.
- Only the first character is capitalized in the greeting.
- Names starting with numbers, symbols, or letters outside the allowed range are rejected.
- Duplicate `name` parameters are treated as invalid.
- Additional query parameters are ignored.
- Unsupported HTTP methods return HTTP 405.

## Implementation

The application uses a simple layered structure. The service handles name validation and greeting generation, while the HTTP handler manages requests and responses.

JUnit tests are included to verify both the business logic and the API behavior.