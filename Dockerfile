FROM openjdk:17
COPY target/bootcoin-service-0.0.1-SNAPSHOT.jar bootcoin-service.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/bootcoin-service.jar"]
