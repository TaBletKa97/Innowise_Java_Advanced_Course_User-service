FROM gradle:9-jdk25-alpine
WORKDIR /app
COPY . .
RUN gradle bootJar --no-daemon
ENTRYPOINT ["java", "-jar", "User-service-0.0.1-SNAPSHOT.jar" ,"--spring.profiles.active=prod"]
