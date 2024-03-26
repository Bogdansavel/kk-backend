FROM openjdk:17
LABEL authors="bogdansavelyev"
COPY build/libs/*SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]