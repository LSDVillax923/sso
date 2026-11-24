# PC2: docker build -t sen-service .   y luego el docker run del README
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/sen-service.jar app.jar
EXPOSE 8093
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
