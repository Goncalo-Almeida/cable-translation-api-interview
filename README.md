# Translation API
## About this project
This project is a small MVP for a translation service. It is assumed that the service will be used by a single client, 
and that the client will be responsible for translating the text to be translated into the language of the translation service. 
The service will then return the translated text to the client.

### Requirements
- Java 21
- Maven 3.9.5 (any maven 3 version should be enough)
- Docker

### Running the project
1. Start a postgres DB with:
    ```
    docker run --name postgres_db -e POSTGRES_PASSWORD=password -p 5432:5432 -d postgres
    ```
2. Start the project with `mvn spring-boot:run` or `./mvnw spring-boot:run`. The project should start on port 8080.
3. Import the postman collection `Translation API.postman_collection.json` to postman and start testing the API.
4. Access the API documentation at [http://localhost:8080/cable/swagger-ui/index.html](http://localhost:8080/cable/swagger-ui/index.html)
5. Run the tests with `mvn test` or `./mvnw test`
6. Optionally, import this project into your favourite IDE and run it from there.

### What this project has
- REST API with the required endpoints to add translations and to translate single words/expression
- The `Languages` tables is populated with the ISO 639-1 language codes retrieved from 
[Wikipedia](https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes)
- A trace-id can be passed in the request header to trace the request through the different layers of the application, 
and through the different microservices in a distributed system
- Good dependency management avoiding adding unnecessary dependencies and delegating most of the dependency management to Spring Boot BOMs
- OpenAPI documentation using Swagger
- Dockerfile to build a docker image of the application
- docker-compose file to start the application and the DB with a single command

### What this project has not
- It can't translate sentences or long texts
- No proper commit history
- No integration tests, although it has a test class ready with Testcontainers to test from endpoint to DB
- No concurrency considerations other than the ones provided by the DB and the `@Transactional` annotation
- No transaction isolation and propagation considerations, using default values.
- Monitoring
- CI/CD pipeline
- Authentication and authorisation
- Logging

## Possible improvements for scalability and speed
Depending on the expected load of the service, there are several improvements that can be done to improve the scalability and speed of the service.
- Use a cache to avoid hitting the DB for every translation request. This can be done with Redis or Memcached.
- Configure a custom connection pool for the DB to avoid blocking the main thread pool with DB requests.
- Configure separate read and write data sources, thus only hitting the master DB for writes and the read copy (or slave DBs) for reads.
- Use CQRS to completely separate the read and write operations, thus allowing to scale the read operations independently of the write operations. 
Write operations can be stored in a NoSQL DB and read operations can be stored in a relational DB.
The data would have to be replicated from the write DB to the read DB using a CDC (Change Data Capture) pipeline. 
