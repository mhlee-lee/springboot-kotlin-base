import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version PluginVersions.KOTLIN
    kotlin("plugin.spring") version PluginVersions.KOTLIN
    id("org.springframework.boot") version PluginVersions.SPRING_BOOT
    id("io.spring.dependency-management") version PluginVersions.SPRING_DEPENDENCY_MANAGEMENT
    id("org.jooq.jooq-codegen-gradle") version PluginVersions.JOOQ
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
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-http-client")
    implementation("org.springframework.boot:spring-boot-webclient")
    implementation("io.micrometer:context-propagation")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("tools.jackson.module:jackson-module-kotlin")

    runtimeOnly("com.h2database:h2")
    jooqCodegen("com.h2database:h2")
    jooqCodegen("org.jooq:jooq-meta-extensions")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")

    testImplementation("io.projectreactor:reactor-test")
    testImplementation("io.mockk:mockk:${DependencyVersions.MOCKK}")
}

val jooqGeneratedDir = layout.buildDirectory.dir("generated-src/jooq/main")

jooq {
    configuration {
        generator {
            name = "org.jooq.codegen.KotlinGenerator"
            database {
                name = "org.jooq.meta.extensions.ddl.DDLDatabase"
                properties {
                    property {
                        key = "scripts"
                        value = "src/main/resources/schema.sql"
                    }
                    property {
                        key = "defaultNameCase"
                        value = "lower"
                    }
                    property {
                        key = "unqualifiedSchema"
                        value = "none"
                    }
                }
            }
            generate {
                isDeprecated = false
                isRecords = true
                isPojos = false
                isDaos = false
                isKotlinNotNullPojoAttributes = true
                isKotlinNotNullRecordAttributes = false
            }
            target {
                packageName = "org.test.kotlin_base.jooq.generated"
                directory = jooqGeneratedDir.get().asFile.absolutePath
            }
        }
    }
}

kotlin {
    jvmToolchain(BuildVersions.JAVA.majorVersion.toInt())
    sourceSets.named("main") {
        kotlin.srcDir(jooqGeneratedDir)
    }
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

tasks.named("compileKotlin") {
    dependsOn(tasks.named("jooqCodegen"))
}

//openapi3 {
//    setServer("http://localhost:8080")
//    title = "My API"
//    description = "My API description"
//    version = "0.1.0"
//    format = "yaml"
//}
