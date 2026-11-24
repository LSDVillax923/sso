# PC2: docker build -t sin-service .   y luego el docker run del README
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/sin-service.jar app.jar
EXPOSE 8092
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
