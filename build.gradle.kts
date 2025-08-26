plugins {
    java
    id("org.springframework.boot") version "3.2.4"
    id("io.spring.dependency-management") version "1.1.4"
    id("io.freefair.lombok") version "8.14.2"
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
    implementation("org.springframework.boot:spring-boot-starter-web:3.4.2")
    implementation("org.springframework.boot:spring-boot-maven-plugin:3.2.4")
    implementation("org.springframework.boot:spring-boot-starter-validation:3.2.0")
    implementation("org.hibernate.orm:hibernate-core:6.6.13.Final")
    implementation("org.springframework.boot:spring-boot-starter-security:3.2.12")
    compileOnly("org.projectlombok:lombok:1.18.38")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.2.4")
    implementation("org.springframework.boot:spring-boot-starter-jdbc:3.2.4")
    implementation("org.postgresql:postgresql:42.7.3")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<Jar> {
    manifest {
        attributes["KkbackendApplication"] = "com.example.kkbackend"
    }
}