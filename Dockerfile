FROM openjdk:17
LABEL authors="bogdansavelyev"
COPY build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]