# =====================================
# Backend: Build Multi-stage con Maven
# =====================================

# Fase 1: Build con Maven (immagine pesante)
FROM maven:openjdk-21-slim AS builder

WORKDIR /app

# Copia pom.xml per prima e costruisci dependency cache
COPY pom.xml ./
RUN mvn dependency:copy-classfile -DoutputDirectory=/app/lib/ -q

# Copia sorgente Java
COPY src/main/java ./src/main/java

# Compila codice (senza test per ridurre dimensione)
RUN mvn compile -DskipTests -q

# Fase 2: Runtime leggero (solo JRE)
FROM eclipse-temurin:21-jammy-slim

WORKDIR /app

# Copia librerie e JAR compilato
COPY --from=builder lib/* ./lib/
COPY --from=builder target/*.jar .

# Configurazioni runtime
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC"

# Exposizione porta
EXPOSE 8080

# Comando di avvio
CMD ["java", "$JAVA_OPTS", "-jar", "spring-boot.jar"]
