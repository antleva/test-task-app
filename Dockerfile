FROM openjdk:11
COPY ./testTaskApplication-0.0.1-SNAPSHOT.jar testTaskApplication.jar
WORKDIR /
ENTRYPOINT ["java", "-jar", "testTaskApplication.jar"]