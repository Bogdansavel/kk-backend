plugins {
    java
    id("org.springframework.boot") version "3.2.4"
    id("io.spring.dependency-management") version "1.1.4"
    id("io.freefair.lombok") version "8.6"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation("org.springframework.boot:spring-boot-starter-web:3.2.0")
    implementation("org.springframework.boot:spring-boot-maven-plugin:3.2.4")
    implementation("org.springframework.boot:spring-boot-starter-validation:3.2.0")
    implementation("org.hibernate.orm:hibernate-core:6.4.4.Final")
    implementation("org.springframework.boot:spring-boot-starter-security:3.2.4")
    compileOnly("org.projectlombok:lombok:1.18.30")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.2.4")
    implementation("org.springframework.boot:spring-boot-starter-jdbc:3.2.4")
    implementation("org.postgresql:postgresql:42.7.3")
    implementation("com.google.api-client:google-api-client:2.2.0")
    implementation("com.google.oauth-client:google-oauth-client-jetty:1.34.1")
    implementation("com.google.apis:google-api-services-sheets:v4-rev612-1.25.0")
    compileOnly("com.google.cloud.sql:postgres-socket-factory:1.18.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<Jar> {
    manifest {
        attributes["KkbackendApplication"] = "com.example.kkbackend"
    }
}
