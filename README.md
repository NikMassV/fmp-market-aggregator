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

---

Further details and implementation will be added step by step as the project evolves. 
