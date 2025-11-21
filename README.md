
# AI-powered DB Assistant

A minimal Spring Boot application that accepts a natural-language query, sends it to a local Ollama model to extract intent (JSON), then runs a safe DB query and returns the result.


## Features
- Natural language -> intent via Ollama
- Simple JSON-based intent format
- Safe DB access via JPA repository

## Prerequisites
- Java 17+
- Maven
- Running Ollama daemon (assumed at http://localhost:11434)
- PostgreSQL (or change configuration to MySQL)

## Run
1. Edit `src/main/resources/application.yml` with your DB settings.
2. Start PostgreSQL and apply your `transactions` table (example entity provided).
3. Start the Spring Boot app:

```bash
mvn spring-boot:run
