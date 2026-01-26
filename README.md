# Data Visualization API

A comprehensive Spring Boot microservices architecture for data visualization and analytics, providing REST APIs for dataset management, security, and integration with Apache Superset.

## Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Modules](#modules)
- [Technology Stack](#technology-stack)
- [Getting Started](#getting-started)
- [API Documentation](#api-documentation)
- [Configuration](#configuration)
- [Development](#development)
- [Deployment](#deployment)

## Overview

The Data Visualization API is a multi-module Spring Boot application designed to provide a scalable, secure platform for data visualization and analytics. It follows a microservices architecture with service discovery, API gateway, security layer, and business logic components.

More Documentation: [Data VIZ Documentation](https://devgateway.github.io/data-viz-example)

### Key Features

- **Microservices Architecture**: Modular design with independent, scalable services
- **Service Discovery**: Netflix Eureka for dynamic service registration and discovery
- **API Gateway**: Spring Cloud Gateway for routing and load balancing
- **Security**: JWT-based authentication and authorization
- **Data Management**: Generic dataset handling with CSV import/export capabilities
- **Caching**: Redis and Caffeine-based caching for performance optimization
- **Superset Integration**: Proxy service for Apache Superset integration
- **Multi-dimensional Analysis**: Support for dimensions, measures, and filters
- **RESTful APIs**: Comprehensive REST endpoints for all operations

## Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                       Client Applications                    │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                      API Gateway (Port 8080)                 │
│              Spring Cloud Gateway + Caffeine Cache           │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                  Service Registry (Eureka)                   │
│                         Port 8761                            │
└─────────────────────────────────────────────────────────────┘
                              │
              ┌───────────────┼───────────────┐
              ▼               ▼               ▼
    ┌─────────────┐  ┌──────────────┐  ┌────────────────┐
    │ API Security│  │Superset Proxy│  │Business Services│
    │   Service   │  │   Service    │  │  (with Commons)│
    └─────────────┘  └──────────────┘  └────────────────┘
              │               │               │
              └───────────────┼───────────────┘
                              ▼
                    ┌──────────────────┐
                    │  PostgreSQL DB   │
                    └──────────────────┘
```

## Modules

### 1. Commons (`commons/`)

The core module containing shared business logic, domain models, services, and utilities.

**Key Components:**
- **Domain Models**: Dataset, Category, Job, DimensionDefinition, MeasureDefinition, FilterDefinition
- **Services**:
  - `DatasetService`: Dataset CRUD operations and management
  - `DatasetRecordService`: Generic record handling
  - `CategoryService`: Category management
  - `DimensionDefinitionService`: Dimension metadata management
  - `MeasureDefinitionService`: Measure metadata management
  - `FilterDefinitionService`: Filter metadata management
  - `JobService`: Asynchronous job processing
  - `GenericConfigService`: Configuration management
- **Controllers**:
  - `BaseStatsController`: Generic statistics endpoints
  - `AdminDatasetsController`: Dataset admin
  - `AdminCategoriesController`: Category admin
  - `AdminDimensionsController`: Dimension admini
  - `AdminMeasuresController`: Measure admin
  - `JobsController`: Job status monitoring
- **CSV Import/Export**: `BaseCSVImporter` for data import functionality
- **Observer Pattern**: Dataset change notification system

**Technologies:**
- Spring Boot 3.2.10
- Spring Data JPA with QueryDSL
- Liquibase for database migrations
- OpenCSV for CSV processing
- PostgreSQL

### 2. API Gateway (`api-gateway/`)

Spring Cloud Gateway providing routing, load balancing, and caching.

**Key Features:**
- Dynamic service routing via Eureka service discovery
- Caffeine-based response caching (60-minute TTL)
- Request/response filtering
- Gateway-level security headers

**Configuration:**
- Port: 8080
- Cache expiration: 60 minutes
- Eureka integration: Enabled with lowercase service IDs

**Dependencies:**
- Spring Cloud Gateway
- Spring Cloud Netflix Eureka Client
- Caffeine Cache

### 3. API Security (`api-security/`)

Authentication and authorization service providing JWT-based security.

**Key Components:**
- `LoginController`: Authentication endpoints
- `LoginService`: User authentication logic
- Spring Security configuration
- JWT token generation and validation
- PostgreSQL-backed user management

**API Endpoints:**
- `POST /gateway/signin`: User authentication

**Technologies:**
- Spring Security
- JWT (JSON Web Tokens)
- Spring Data JPA
- PostgreSQL

### 4. Registry (`registry/`)

Netflix Eureka service registry for microservices discovery.

**Key Features:**
- Service registration and health monitoring
- Service discovery for inter-service communication
- High availability support
- Dashboard UI

**Configuration:**
- Port: 8761
- Spring Boot 3.4.0
- Netflix Eureka Server 4.1.3

### 5. Superset Proxy (`superset-proxy/`)

Proxy service for Apache Superset integration with caching and data transformation.

**Key Components:**
- `SupersetController`: REST endpoints for Superset data
- `SupersetProxyService`: Business logic for Superset integration
- Redis-based caching (production)
- Custom key generation for cache optimization

**API Endpoints:**
- `GET /status`: Health check
- `GET /charts`: Retrieve available charts
- `GET /datasets`: Get datasets
- `GET /dimensions`: Get dimension definitions
- `GET /measures`: Get measure definitions
- `GET /filters`: Get filter definitions
- `GET /categories`: Get categories
- `GET /categories/{categoryType}`: Get specific category type
- `GET /stats`: Statistics endpoint
- `GET /warmUp`: Cache warm-up
- `GET /cacheEvict`: Cache invalidation

**Technologies:**
- Spring Boot 3.2.12
- Redis (production profile)
- Spring Cloud Netflix Eureka Client
- Spring Cache

## Technology Stack

### Core Frameworks
- **Java**: 21 (LTS)
- **Spring Boot**: 3.2.x - 3.4.0
- **Spring Cloud**: 2023.0.3 - 2024.0.0
- **Maven**: 3.8.9+ for build automation

### Data & Persistence
- **PostgreSQL**: Primary database
- **Liquibase**: Database version control (4.29.0)
- **Spring Data JPA**: Data access layer
- **QueryDSL**: Type-safe query construction (5.1.0)

### Microservices Infrastructure
- **Netflix Eureka**: Service registry and discovery (4.1.3)
- **Spring Cloud Gateway**: API gateway and routing
- **Spring Cloud Netflix**: Microservices patterns

### Caching
- **Caffeine**: In-memory caching
- **Redis**: Distributed caching (production)
- **Spring Cache**: Cache abstraction

### Security
- **Spring Security**: Authentication and authorization
- **JWT**: Token-based authentication

### Documentation & API
- **SpringDoc OpenAPI**: API documentation (2.6.0)
- **Swagger UI**: Interactive API explorer (1.8.0)

### Additional Libraries
- **OpenCSV**: CSV file processing (5.9)
- **Apache Commons Text**: String utilities (1.12.0)
- **Google Cloud Translate**: Translation services (26.43.0)
- **Jackson**: JSON processing

### Development Tools
- **Maven Compiler Plugin**: Java 21 compilation (3.11.0)
- **Spring Boot Maven Plugin**: Application packaging
- **Maven Checkstyle Plugin**: Code quality (3.5.0)

## Getting Started

### Prerequisites

- **Java Development Kit (JDK)**: 21 or higher
- **Maven**: 3.8.9 or higher
- **PostgreSQL**: 12 or higher
- **Redis**: 6 or higher (for production)
- **Docker** (optional): For containerized deployment

### Installation

1. **Clone the repository:**
   ```bash
   git clone git@github.com/devgateway/data-viz-api.git
   cd data-viz-api
   ```

2. **Update Configuration Files:**
   
   Update application properties with your database credentials:
   - `api-security/src/main/resources/application.properties`
   - `commons/src/main/resources/common.properties`

3. **Build the Project:**
   ```bash
   mvn clean install
   ```

### Running the Application

#### Start Services in Order:

1. **Start Eureka Registry:**
   ```bash
   cd registry
   mvn spring-boot:run
   ```
   Access at: http://localhost:8761

2. **Start API Security:**
   ```bash
   cd api-security
   mvn spring-boot:run
   ```

3. **Start Superset Proxy:**
   ```bash
   cd superset-proxy
   mvn spring-boot:run
   ```

4. **Start API Gateway:**
   ```bash
   cd api-gateway
   mvn spring-boot:run
   ```
   Access at: http://localhost:8080

#### Using Docker:

```bash
docker compose up -d
```

### Verifying Installation

1. Check Eureka Dashboard: http://localhost:8761
2. Verify all services are registered
3. Test gateway endpoint: http://localhost:8080/actuator/health

## API Documentation

### Base URLs

- **API Gateway**: `http://localhost:8080`
- **Eureka Dashboard**: `http://localhost:8761`

### Authentication

All API requests (except `/gateway/signin`) require JWT authentication:

```bash
# Login
curl -X POST http://localhost:8080/gateway/signin \
  -d "username=admin&password=admin123"

# Use returned token in subsequent requests
curl -H "Authorization: Bearer <token>" \
  http://localhost:8080/admin/datasets
```

### Common Endpoints

#### Admin - Dataset Management
- `GET /admin/datasets` - List all datasets
- `GET /admin/datasets/{code}` - Get dataset by code
- `POST /admin/datasets` - Create new dataset
- `PUT /admin/datasets/{code}` - Update dataset
- `DELETE /admin/datasets/{code}` - Delete dataset
- `GET /admin/datasets/{code}/download` - Download dataset file

#### Admin - Category Management
- `GET /admin/categories` - List categories
- `GET /admin/categories/{id}` - Get category by ID
- `PUT /admin/categories/{id}` - Update category
- `DELETE /admin/categories/{id}` - Delete category

#### Admin - Dimension Management
- `GET /admin/dimensions` - List dimensions
- `GET /admin/dimensions/{id}` - Get dimension by ID
- `PUT /admin/dimensions/{id}` - Update dimension

#### Admin - Measure Management
- `GET /admin/measures` - List measures
- `GET /admin/measures/{id}` - Get measure by ID
- `PUT /admin/measures/{id}` - Update measure

#### Admin - Jobs
- `GET /admin/jobs/{id}` - Get job status by ID
- `GET /admin/jobs/code/{code}` - Get job by code

#### Public Endpoints
- `GET /categories` - Get all categories
- `GET /stats` - Get statistics
- `GET /stats/**` - Get statistics with dimensions
- `GET /dimensions` - Get dimension definitions
- `GET /filters` - Get filter definitions
- `GET /measures` - Get measure definitions

#### Superset Proxy Endpoints
- `GET /charts` - Get Superset charts
- `GET /datasets` - Get Superset datasets
- `GET /dimensions?dvzProxyDatasetId={id}` - Get dimensions for dataset
- `GET /measures?dvzProxyDatasetId={id}` - Get measures for dataset
- `GET /filters?dvzProxyDatasetId={id}` - Get filters for dataset
- `GET /categories?dvzProxyDatasetId={id}` - Get categories for dataset

#### Cache Management
- `GET /cacheEvict` - Evict all caches
- `GET /warmUp` - Warm up caches

### OpenAPI/Swagger Documentation

Once the application is running, access interactive API documentation at:
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Configuration

### Environment Variables

#### API Gateway (`api-gateway/src/main/resources/application.yml`)
```yaml
spring:
  application:
    name: api-gateway
server:
  port: 8080
eureka:
  client:
    serviceUrl:
      defaultZone: http://eureka:8761/eureka
```

#### API Security (`api-security/src/main/resources/application.properties`)
```properties
spring.application.name=api-security
spring.datasource.url=jdbc:postgresql://localhost:5432/dataviz_security
spring.datasource.username=postgres
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

#### Commons Configuration
```properties
viz.initial.config=true          # Enable initial configuration setup
viz.startup.import=false         # Enable automatic import on startup
viz.import.directory=/data/import # Import directory path
viz.date.format=yyyy-MM-dd       # Date format for imports
```

### Profile Configuration

- **Development Profile**: Uses in-memory cache, verbose logging
  - Activate: `--spring.profiles.active=dev`
- **Production Profile**: Uses Redis cache, optimized logging
  - Activate: `--spring.profiles.active=prod`

## Development

### Project Structure

```
data-viz-api/
├── api-gateway/           # API Gateway module
│   ├── src/main/
│   │   ├── java/
│   │   └── resources/
│   └── pom.xml
├── api-security/          # Security module
│   ├── src/main/
│   └── pom.xml
├── commons/               # Shared business logic
│   ├── src/main/
│   │   ├── java/
│   │   │   └── org/devgateway/viz/commons/
│   │   │       ├── boot/          # Application bootstrap
│   │   │       ├── controllers/   # REST controllers
│   │   │       ├── domain/        # Domain models
│   │   │       ├── io/            # Import/Export
│   │   │       ├── observers/     # Event observers
│   │   │       ├── pojo/          # DTOs
│   │   │       ├── repositories/  # Data repositories
│   │   │       └── services/      # Business services
│   │   └── resources/
│   └── pom.xml
├── registry/              # Eureka service registry
│   ├── src/main/
│   └── pom.xml
├── superset-proxy/        # Superset integration
│   ├── src/main/
│   └── pom.xml
└── pom.xml               # Parent POM
```

### Building from Source

```bash
# Build entire project
mvn clean install

# Build specific module
cd commons
mvn clean install

# Skip tests
mvn clean install -DskipTests

# Run specific service
cd api-gateway
mvn spring-boot:run
```

### Code Quality

#### Checkstyle
Code quality is enforced using Maven Checkstyle Plugin (currently disabled in config):
```bash
mvn checkstyle:check
```

#### Testing
```bash
# Run all tests
mvn test

# Run tests for specific module
cd commons
mvn test
```

### Database Migrations

Liquibase is used for database version control. Changelog files are located in:
- `src/main/resources/db/changelog/`

To run migrations manually:
```bash
mvn liquibase:update
```

### Adding a New Microservice

1. Create new Maven module in parent POM
2. Add dependencies to Eureka Client
3. Implement `@SpringBootApplication` class with `@EnableDiscoveryClient`
4. Configure `application.yml` with service name and Eureka URL
5. Build and run

## Deployment

### Docker Deployment

Each module includes a `Dockerfile` for containerization.

#### Build Docker Images:
```bash
# Build all images
docker compose build

# Build specific service
docker build -t dataviz-api-gateway ./api-gateway
```

#### Docker Compose:
```yaml
version: '3.8'
services:
  eureka:
    build: ./registry
    ports:
      - "8761:8761"
  
  postgres:
    image: postgres:14
    environment:
      POSTGRES_DB: dataviz_main
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: password
    
  api-gateway:
    build: ./api-gateway
    depends_on:
      - eureka
    ports:
      - "8080:8080"
  
  api-security:
    build: ./api-security
    depends_on:
      - eureka
      - postgres
```

#### Run with Docker Compose:
```bash
docker compose up -d
```

### Production Deployment

1. **Environment Configuration:**
   - Set production database credentials
   - Configure Redis for caching
   - Enable HTTPS/TLS
   - Set appropriate log levels

2. **Health Checks:**
   All services expose Spring Actuator endpoints:
   - `/actuator/health` - Service health
   - `/actuator/info` - Service information
   - `/actuator/metrics` - Service metrics

3. **Monitoring:**
   - Use Spring Boot Admin for centralized monitoring
   - Configure Prometheus for metrics collection
   - Set up Grafana dashboards


## License

This project is developed by Development Gateway licensed under [Apache-2.0 license](/LICENSE)

## Contributing

For questions or contributions, please contact the [Development Gateway](mailto:info@developmentgateway.org) team.

---

