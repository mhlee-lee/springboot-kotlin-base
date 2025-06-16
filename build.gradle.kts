import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    kotlin("jvm") version PluginVersions.KOTLIN
    kotlin("plugin.spring") version PluginVersions.KOTLIN
    kotlin("plugin.jpa")  version PluginVersions.KOTLIN
    kotlin("plugin.allopen") version PluginVersions.KOTLIN
    id("org.springframework.boot") version PluginVersions.SPRING_BOOT
    id("io.spring.dependency-management") version PluginVersions.SPRING_DEPENDENCY_MANAGEMENT
    id("com.epages.restdocs-api-spec") version "0.19.4"
}

group = "org.test"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:${DependencyVersions.REACTOR_KOTLIN_EXTENSIONS}")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${DependencyVersions.KOTLIN}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:${DependencyVersions.KOTLINX_COROUTINES_REACTOR}")

    runtimeOnly("com.mysql:mysql-connector-j")
    // https://mvnrepository.com/artifact/org.postgresql/postgresql
    runtimeOnly("org.postgresql:postgresql:42.7.6")

    // querydsl
    implementation("com.querydsl:querydsl-jpa:${DependencyVersions.QUERYDSL}")
    annotationProcessor("com.querydsl:querydsl-apt:${DependencyVersions.QUERYDSL}")
    annotationProcessor("jakarta.annotation:jakarta.annotation-api:${DependencyVersions.JAKARTA_ANNOTATION_API}")
    annotationProcessor("jakarta.persistence:jakarta.persistence-api:${DependencyVersions.JAKARTA_PERSISTENCE_API}")
    implementation("joda-time:joda-time:${DependencyVersions.JODA_TIME}")

    implementation("jakarta.validation:jakarta.validation-api:3.1.1")


    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:${DependencyVersions.KOTLIN}")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:${DependencyVersions.KOTLINX_COROUTINES_REACTOR}")
    testImplementation("org.mockito.kotlin:mockito-kotlin:${DependencyVersions.KOTLIN_MOCKITO}")
    testImplementation("io.mockk:mockk:1.14.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    testImplementation("com.epages:restdocs-api-spec-webtestclient:0.19.4")
    testImplementation("org.springframework.restdocs:spring-restdocs-webtestclient:3.0.3")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

fun kotlinCompilerOptions(): KotlinJvmCompile.() -> Unit = {
    compilerOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = JvmTarget.JVM_21
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

openapi3 {
    setServer("http://localhost:8080")
    title = "My API"
    description = "My API description"
    version = "0.1.0"
    format = "yaml"
}
