import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version PluginVersions.KOTLIN
    kotlin("plugin.spring") version PluginVersions.KOTLIN
    id("org.springframework.boot") version PluginVersions.SPRING_BOOT
    id("io.spring.dependency-management") version PluginVersions.SPRING_DEPENDENCY_MANAGEMENT
//    id("com.epages.restdocs-api-spec") version PluginVersions.RESTDOCS_API_SPEC
}

group = "org.test"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(BuildVersions.JAVA.majorVersion.toInt())
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
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-http-client")
    implementation("org.springframework.boot:spring-boot-webclient")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("tools.jackson.module:jackson-module-kotlin")

    implementation("io.r2dbc:r2dbc-pool")
    runtimeOnly("io.r2dbc:r2dbc-h2")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")

    testImplementation("io.projectreactor:reactor-test")
    testImplementation("io.mockk:mockk:${DependencyVersions.MOCKK}")
}

kotlin {
    jvmToolchain(BuildVersions.JAVA.majorVersion.toInt())
    compilerOptions {
        freeCompilerArgs.addAll(
            "-Xjsr305=strict",
            "-Xannotation-default-target=param-property",
        )
        jvmTarget = JvmTarget.fromTarget(BuildVersions.JAVA.majorVersion)
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

//openapi3 {
//    setServer("http://localhost:8080")
//    title = "My API"
//    description = "My API description"
//    version = "0.1.0"
//    format = "yaml"
//}
