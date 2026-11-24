# Réplica 2 en PC2:  docker build -t sso-monolito .   y luego el docker run del README
FROM eclipse-temurin:21-jre
# curl solo para probar desde el contenedor la comunicación con ofac-service (PC1)
RUN apt-get update && apt-get install -y --no-install-recommends curl && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY target/sso-monolito.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
