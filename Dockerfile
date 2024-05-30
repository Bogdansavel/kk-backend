FROM openjdk:17
LABEL authors="bogdansavelyev"
CMD ["./gradlew", "clean", "bootJar"]
COPY build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]