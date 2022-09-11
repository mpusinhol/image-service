# Image Service API

Simple REST API designed to fetch optimized image sizes

## Technologies
- Java 17
- Spring Boot
- Maven
- Docker

## Running

This application depends on a AWS S3 emulator in order to be run in development mode, therefore there are a few steps to follow:

1. Make sure you have docker and docker-compose installed locally
2. In the root directory, run `docker-compose up s3`
3. On your browser, navigate to `http://localhost:9444/ui`
4. Copy the values defined for Access Key and Secret Key

### Terminal
1. Set the copied values as environment variables: 
- `export AWS_ACCESS_KEY_ID={COPIED_SECRET_KEY}`
- `export AWS_SECRET_ACCESS_KEY={COPIED_SECRET_KEY}`
2. run the command `mvn spring-boot:run -Dspring-boot.run.profiles=dev`


### Docker-compose
1. Just run `docker-compose up`

NOTE: Due to the fact that the images are currently being mocked from the resources folder,
for some reason they are not being loaded inside Docker. It's a bug I did not have time to look into as it's just part of the mocking,
 not the real application. The application will run normally, it will just not respond the request with any images.

## API Documentation
The documentation can be accessed on `http://localhost:8080/swagger-ui.html` after running the application.