# ---------- STAGE 1: build ----------
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -B

COPY src src
RUN ./mvnw clean package -DskipTests -B

# ---------- STAGE 2: runtime ----------
FROM eclipse-temurin:21-jre AS runtime

WORKDIR /app

COPY --from=build /app/target/postgre-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]