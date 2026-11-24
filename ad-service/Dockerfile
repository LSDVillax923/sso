# PC2:  docker build -t ad-service .   y luego el docker run del README (reemplaza al contenedor OpenLDAP)
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY target/ad-service.jar app.jar
EXPOSE 389 636 8097
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
