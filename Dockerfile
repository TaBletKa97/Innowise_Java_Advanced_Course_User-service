FROM gradle:9.4.0-jdk25-alpine AS build
WORKDIR /home/gradle/src
COPY --chown=gradle:gradle . .
RUN gradle bootJar --no-daemon

FROM gradle:9-jdk25-alpine
WORKDIR /app
COPY ./build/libs/User-service-0.0.1-SNAPSHOT.jar /app/User-service.jar
ENTRYPOINT ["java", "-jar", "User-service.jar" ,"--spring.profiles.active=prod"]
