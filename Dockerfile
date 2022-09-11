FROM maven:3.8.3-openjdk-17 as builder

ENV SERVICE_NAME=image-service

WORKDIR /$SERVICE_NAME
COPY . /$SERVICE_NAME

RUN mvn clean package

FROM openjdk:17-slim

ENV SERVICE_NAME=image-service

WORKDIR /$SERVICE_NAME

COPY --from=builder /$SERVICE_NAME/target/$SERVICE_NAME-*.jar /$SERVICE_NAME/$SERVICE_NAME.jar

EXPOSE 8080

CMD java -jar $SERVICE_NAME.jar