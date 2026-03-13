FROM gradle:9-jdk25-alpine
WORKDIR /app
COPY ./build/libs/User-service-0.0.1-SNAPSHOT.jar /app/User-service.jar
ENTRYPOINT ["java", "-jar", "User-service.jar" ,"--spring.profiles.active=prod"]
