# MarketPulse

MarketPulse is a high-load, reactive microservices system for real-time financial data aggregation and storage. It leverages the Financial Modeling Prep API to fetch and aggregate stock market data, processes it using Spring WebFlux, and coordinates service communication via Kafka. Aggregated data is stored in MySQL and exposed via a REST API.

## Architecture Overview

- **market-aggregator-service**: Fetches and aggregates financial/stock data from the Financial Modeling Prep API, then publishes results to Kafka.
- **market-storage-service**: Consumes aggregated data from Kafka, stores it in MySQL, and exposes an API for querying stored aggregates.
- **infrastructure**: Contains Docker, Kafka, MySQL, and other supporting infrastructure configurations.

## Technologies
- Cursor
- Java 21
- Spring Boot
- Kafka
- MySQL
- Gradle
- Docker & Kubernetes
- Keycloak
- Prometheus & Grafana
- OpenAPI & Swagger UI
- Mockito, JUnit, Testcontainers
- SonarQube
- GitHub Actions

## Project Structure

```
marketpulse/
├── market-aggregator-service/   # Service for data aggregation and Kafka publishing
├── market-storage-service/      # Service for consuming, storing, and exposing data
├── infrastructure/              # Docker, Kafka, MySQL, Keycloak, etc.
├── README.md
├── .gitignore
├── .editorconfig
└── gradle/                      # Gradle wrapper and config
```

## Getting Started

1. Clone the repository
2. Build with Gradle
3. Use Docker Compose or Kubernetes manifests in `infrastructure/` to start dependencies

## Code Quality Analysis with SonarQube

To run SonarQube locally for code quality analysis:

1. Start SonarQube using Docker Compose:

   ```sh
   docker-compose up sonarqube
   ```

   SonarQube will be available at [http://localhost:9000](http://localhost:9000) (default login: admin / admin).

2. Generate a user token:
   - Log in to SonarQube at http://localhost:9000
   - Go to your user profile (top right) → "My Account" → "Security"
   - Generate a new token (e.g., name it `marketpulse-token`)
   - Copy the token and set it in `gradle.properties` as `sonar.token=YOUR_GENERATED_TOKEN`
   - (Optional) The `sonar.organization` property is only required for SonarCloud, not for local SonarQube.

3. Run code analysis with Gradle:

   ```sh
   ./gradlew sonar --% -Dsonar.token=YOUR_GENERATED_TOKEN
   ```

   Make sure the SonarQube server is running and the token is set before executing the analysis command.

### Running SonarQube Analysis Automatically with Build

To run SonarQube analysis automatically after every build, use the following command (especially for PowerShell users):

```sh
./gradlew build -DrunSonar --% -Dsonar.token=YOUR_GENERATED_TOKEN
```

This will build the project and trigger SonarQube analysis at the end. The `--%` is required in PowerShell to pass the `-D` property correctly.

---

Further details and implementation will be added step by step as the project evolves. 
