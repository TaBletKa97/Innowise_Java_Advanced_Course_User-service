FROM gradle:9-jdk25-alpine AS build
WORKDIR /app
COPY . .
RUN gradle bootJar --no-daemon

FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar userservice.jar
ENTRYPOINT ["java", "-jar", "userservice.jar" ,"--spring.profiles.active=prod"]