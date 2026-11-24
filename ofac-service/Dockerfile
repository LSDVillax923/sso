# PC1:  docker build -t ofac-service .   y luego el docker run del README
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/ofac-service.jar app.jar
EXPOSE 8091
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
