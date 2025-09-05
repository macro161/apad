# Weather Application

A Spring Boot weather application that provides weather information through both a web interface and REST API endpoints.

## Features

- **Web Interface**: Beautiful, responsive web UI for checking weather by city
- **REST API**: JSON endpoints for programmatic access to weather data
- **Sample Data**: Pre-loaded with weather data for major cities
- **Random Weather Generation**: Generates realistic weather data for unknown cities
- **Temperature Conversion**: Displays temperature in both Celsius and Fahrenheit
- **Weather Details**: Shows humidity, wind speed, weather description, and icons

## Technology Stack

- **Java 11**
- **Spring Boot 2.7.18**
- **Spring Web MVC**
- **Thymeleaf** (for web templates)
- **Maven** (build tool)

## Getting Started

### Prerequisites
- Java 11 or higher
- Maven 3.6+

### Running the Application

1. Clone the repository
2. Navigate to the project directory
3. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```
4. The application will start on port 8081

### Accessing the Application

- **Web Interface**: http://localhost:8081
- **API Base URL**: http://localhost:8081/api

## API Endpoints

### Get Weather by Query Parameter
```
GET /api/weather?city={cityName}
```
Example: `http://localhost:8081/api/weather?city=London`

### Get Weather by Path Parameter
```
GET /api/weather/{cityName}
```
Example: `http://localhost:8081/api/weather/Tokyo`

### Response Format
```json
{
  "city": "London",
  "country": "UK",
  "temperature": 288.15,
  "description": "Partly cloudy",
  "humidity": 65.0,
  "windSpeed": 12.5,
  "icon": "02d",
  "timestamp": 1757074892848,
  "temperatureCelsius": 15,
  "temperatureFahrenheit": 59
}
```

## Web Interface Features

- **Search Functionality**: Enter any city name to get weather information
- **Responsive Design**: Works on desktop and mobile devices
- **Weather Icons**: Visual representation of weather conditions
- **Temperature Display**: Shows temperature in both Celsius and Fahrenheit
- **Additional Details**: Humidity, wind speed, and last update time
- **API Information**: Built-in documentation of available API endpoints

## Sample Cities

The application comes pre-loaded with weather data for these cities:
- London, UK
- New York, US
- Tokyo, JP
- Paris, FR
- Sydney, AU
- Moscow, RU
- Mumbai, IN
- Berlin, DE

For any other city, the application will generate realistic random weather data.

## Configuration

The application can be configured through `src/main/resources/application.properties`:

```properties
# Server configuration
server.port=8081

# Application name
spring.application.name=weather-app

# Thymeleaf configuration
spring.thymeleaf.cache=false
spring.thymeleaf.enabled=true
spring.thymeleaf.prefix=classpath:/templates/
spring.thymeleaf.suffix=.html

# Logging configuration
logging.level.com.apad.weather=INFO
logging.level.org.springframework.web=DEBUG
```

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/apad/weather/
│   │       ├── WeatherApplication.java      # Main application class
│   │       ├── controller/
│   │       │   └── WeatherController.java   # REST and web controllers
│   │       ├── model/
│   │       │   └── Weather.java             # Weather data model
│   │       └── service/
│   │           └── WeatherService.java      # Business logic
│   └── resources/
│       ├── application.properties           # Configuration
│       └── templates/
│           └── index.html                   # Web interface template
└── test/
    └── java/
        └── com/apad/weather/
            └── WeatherApplicationTests.java
```

## Building for Production

To build a production-ready JAR file:

```bash
./mvnw clean package
```

The JAR file will be created in the `target/` directory and can be run with:

```bash
java -jar target/weather-0.0.1-SNAPSHOT.jar
```

## Testing

Run the tests with:

```bash
./mvnw test
```

## License

This project is created for demonstration purposes.