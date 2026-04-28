# Est?gio de Build
FROM maven:3.9.6-eclipse-temurin-21 AS build
COPY . .
RUN mvn clean package -DskipTests

# Est?gio de Execu??o
FROM eclipse-temurin:21-jre
COPY --from=build /target/mel-real-api-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
