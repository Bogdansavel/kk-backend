FROM openjdk:17
LABEL authors="bogdansavelyev"
CMD ["./gradlew", "clean", "bootJar"]
COPY *.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]