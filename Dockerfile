FROM maven:4.0.0-rc-4-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml /app
RUN mvn dependency:go-offline

COPY src /app/src
RUN mvn clean package


FROM openjdk:26-jdk-slim
WORKDIR /app
RUN useradd -m mah

COPY --from=build /app/target/*.jar app.jar
RUN chown mah:mah /app/app.jar
USER mah
ENTRYPOINT ["java", "-jar", "app.jar"]
