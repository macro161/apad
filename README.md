# Weather Application Backend

A comprehensive weather application backend built with **Spring Boot 3**, **Java 21**, and modern best practices. This application provides RESTful APIs for retrieving current weather data with features like caching, validation, error handling, and comprehensive monitoring.

## 🚀 Features

- **Java 21 Features**: Utilizes records, pattern matching, switch expressions, and other modern Java features
- **Reactive Programming**: Built with Spring WebFlux for non-blocking, reactive operations
- **Comprehensive Validation**: Request validation with detailed error responses
- **Caching**: Intelligent caching with Caffeine for improved performance
- **Error Handling**: Global exception handling with consistent error responses
- **API Documentation**: OpenAPI/Swagger documentation with interactive UI
- **Monitoring**: Spring Boot Actuator with health checks and metrics
- **Testing**: Comprehensive unit and integration tests
- **Configuration**: Externalized configuration with validation

## 🛠 Technology Stack

- **Java 21** - Latest LTS version with modern language features
- **Spring Boot 3.5.5** - Latest Spring Boot with native compilation support
- **Spring WebFlux** - Reactive web framework
- **Spring Cache** - Caching abstraction with Caffeine
- **Spring Validation** - Bean validation with custom validators
- **OpenAPI 3** - API documentation and specification
- **JUnit 5** - Modern testing framework
- **Mockito** - Mocking framework for unit tests
- **Maven** - Build and dependency management

## 📋 Prerequisites

- Java 21 or higher
- Maven 3.6 or higher
- WeatherAPI.com API key (free tier available)

## 🔧 Configuration

### Environment Variables

Set the following environment variable:

```bash
export WEATHER_API_KEY=your-weatherapi-key-here
```

### Application Properties

Key configuration properties in `application.properties`:

```properties
# Weather API Configuration
weather.api.base-url=https://api.weatherapi.com/v1
weather.api.api-key=${WEATHER_API_KEY:your-api-key-here}
weather.api.timeout-seconds=10
weather.api.cache-ttl-minutes=10

# Server Configuration
server.port=8080

# Cache Configuration
spring.cache.type=caffeine
spring.cache.caffeine.spec=maximumSize=1000,expireAfterWrite=10m
```

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone <repository-url>
cd weather-application
```

### 2. Set API Key

Get a free API key from [WeatherAPI.com](https://www.weatherapi.com/) and set it:

```bash
export WEATHER_API_KEY=your-actual-api-key
```

### 3. Build the Application

```bash
./mvnw clean compile
```

### 4. Run Tests

```bash
./mvnw test
```

### 5. Start the Application

```bash
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`

## 📚 API Documentation

### Interactive Documentation

Once the application is running, visit:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI Spec**: http://localhost:8080/api-docs

### API Endpoints

#### Get Weather by Location Name

```http
GET /api/weather/current?location=London&temperatureUnit=celsius&windSpeedUnit=kph
```

#### Get Weather by Coordinates

```http
GET /api/weather/current/coordinates?latitude=51.5074&longitude=-0.1278
```

#### Get Weather (POST with Request Body)

```http
POST /api/weather/current
Content-Type: application/json

{
  "location": "London",
  "temperatureUnit": "celsius",
  "windSpeedUnit": "kph"
}
```

#### Health Check

```http
GET /api/weather/health
```

### Response Format

```json
{
  "location": {
    "name": "London",
    "country": "United Kingdom",
    "region": "City of London, Greater London",
    "latitude": 51.52,
    "longitude": -0.11,
    "timezone": "Europe/London"
  },
  "condition": {
    "text": "Partly cloudy",
    "icon": "//cdn.weatherapi.com/weather/64x64/day/116.png",
    "code": 1003
  },
  "temperature": 22.5,
  "temperatureUnit": "celsius",
  "feelsLike": 24.1,
  "humidity": 65,
  "windSpeed": 15.2,
  "windSpeedUnit": "kph",
  "windDirection": "W",
  "pressure": 1013.2,
  "precipitation": 0.0,
  "visibility": 10.0,
  "uvIndex": 5.2,
  "lastUpdated": "2024-01-15T14:30:00"
}
```

## 🏗 Architecture

### Package Structure

```
com.apad.weather/
├── client/          # External API clients
├── config/          # Configuration classes
├── controller/      # REST controllers
├── domain/          # Domain models (records)
├── dto/             # Data Transfer Objects
├── exception/       # Custom exceptions
└── service/         # Business logic services
```

### Key Components

- **WeatherController**: REST endpoints with validation
- **WeatherService**: Business logic and caching
- **WeatherApiClient**: External API integration
- **GlobalExceptionHandler**: Centralized error handling
- **WeatherConfiguration**: Application configuration

## 🧪 Testing

### Run All Tests

```bash
./mvnw test
```

### Test Coverage

- **Unit Tests**: Service layer logic with mocked dependencies
- **Integration Tests**: Full request/response flow with WebTestClient
- **Validation Tests**: Request validation and error handling

### Test Classes

- `WeatherControllerIntegrationTest`: API endpoint testing
- `WeatherServiceImplTest`: Service layer unit tests

## 📊 Monitoring

### Health Checks

```http
GET /actuator/health
```

### Metrics

```http
GET /actuator/metrics
```

### Prometheus Metrics

```http
GET /actuator/prometheus
```

## 🔒 Error Handling

The application provides consistent error responses:

```json
{
  "code": "LOCATION_NOT_FOUND",
  "message": "Location 'InvalidCity' not found",
  "details": "The specified location could not be found",
  "status": 404,
  "path": "/api/weather/current",
  "timestamp": "2024-01-15T14:30:00",
  "validationErrors": []
}
```

## 🚀 Performance Features

- **Caching**: Weather data cached for 10 minutes by default
- **Reactive**: Non-blocking I/O with WebFlux
- **Connection Pooling**: Optimized HTTP client configuration
- **Retry Logic**: Automatic retry for transient failures

## 🔧 Development

### Java 21 Features Used

- **Records**: Immutable data classes for domain models and DTOs
- **Pattern Matching**: Enhanced switch expressions for error handling
- **Text Blocks**: Improved string handling in tests
- **Sealed Classes**: Type-safe error handling (where applicable)

### Best Practices Implemented

- **Reactive Programming**: Non-blocking operations throughout
- **Validation**: Comprehensive input validation with custom messages
- **Error Handling**: Consistent error responses with proper HTTP status codes
- **Testing**: High test coverage with both unit and integration tests
- **Documentation**: OpenAPI specification with detailed examples
- **Configuration**: Externalized configuration with validation
- **Monitoring**: Health checks and metrics for production readiness

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Ensure all tests pass
6. Submit a pull request

## 📞 Support

For support and questions:
- Create an issue in the repository
- Check the API documentation at `/swagger-ui.html`
- Review the application logs for debugging information
