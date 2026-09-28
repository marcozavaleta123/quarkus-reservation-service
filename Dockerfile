FROM eclipse-temurin:21-alpine
WORKDIR /workspace
COPY target/quarkus-reservation-service-*.jar app.jar
EXPOSE 8081
ENTRYPOINT [ "java", "-jar", "/workspace/app.jar" ]