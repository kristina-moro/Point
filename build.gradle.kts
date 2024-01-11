import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "org.point"
version = ""
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    maven {
        url = uri("https://plugins.gradle.org/m2/")
    }
    mavenCentral()
}

buildscript {
    val kotlinVersion = "1.7.22"
    repositories {
        gradlePluginPortal()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    }
}

plugins {
    val kotlinVersion = "1.7.22"
    id("org.springframework.boot") version "2.7.5"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("jvm") version "$kotlinVersion"
    kotlin("plugin.spring") version "$kotlinVersion"
    //id("nu.studer.jooq") version "8.0"
    id("org.openapi.generator") version "6.2.1"
    id("org.liquibase.gradle") version "2.2.0"
}

val bootVersion = "2.7.5"
val cloudStreamVersion = "3.2.6"
val openapiVersion = "1.7.0"
val testcontainersVersion = "1.17.6"

extra["testcontainersVersion"] = "1.17.6"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    //implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")

    implementation("org.springframework.cloud:spring-cloud-starter-bootstrap:3.1.5")
    implementation("commons-codec:commons-codec:1.15")

    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.1.7")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.6.4")

    implementation("net.logstash.logback:logstash-logback-encoder:4.11")
    implementation("org.springframework.boot:spring-boot-starter-logging")
    implementation("org.codehaus.janino:janino:3.1.9")
    implementation("org.zalando:problem-spring-webflux:0.27.0")

    // OPEN API
    implementation("org.springdoc:springdoc-openapi-webflux-core:$openapiVersion")
    implementation("org.springdoc:springdoc-openapi-webflux-ui:$openapiVersion")
    implementation("org.openapitools:jackson-databind-nullable:0.2.4")


    //implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.1.0")
    //implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:2.1.0")
    //implementation("org.springdoc:springdoc-openapi-starter-common:2.1.0")

 //   implementation("org.springdoc:springdoc-openapi-ui:1.7.0")
 //   implementation("org.springdoc:springdoc-openapi-common:1.7.0")

    // SWAGGER
    implementation("io.springfox:springfox-boot-starter:3.0.0")  // !!!
   // implementation("io.springfox:springfox-swagger2:3.0.0")
    //implementation("io.springfox:springfox-swagger-ui:3.0.0")
    implementation("io.swagger.core.v3:swagger-annotations:2.2.20")


    // DB
    //implementation("org.springframework.boot:spring-boot-starter-jooq:${bootVersion}")
    //implementation("io.r2dbc:r2dbc-spi:1.0.0.RELEASE")
    //implementation("io.r2dbc:r2dbc-pool:1.0.0.RELEASE")
    implementation("org.liquibase:liquibase-core:4.19.0")
    implementation("org.postgresql:postgresql:42.7.1")
    runtimeOnly("org.postgresql:postgresql:42.7.1")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.2.1")
    //runtimeOnly("io.r2dbc:r2dbc-postgresql:0.8.13.RELEASE")
    implementation("org.springframework.data:spring-data-r2dbc:3.0.0")


    // cache
    implementation("io.github.reactivecircus.cache4k:cache4k:0.11.0")

    // email templates
    implementation("org.springframework.boot:spring-boot-starter-mail:3.2.1")
    implementation("org.springframework.boot:spring-boot-starter-freemarker:3.2.1")
    implementation("javax.mail:mail:1.4.7")



    implementation("org.mockito.kotlin:mockito-kotlin:4.1.0")


    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.1")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.14.2")

    // macos m1 dns resolver
    //implementation("io.netty:netty-resolver-dns-native-macos:4.1.73.Final:osx-aarch_64")

    // TEST
    testImplementation("org.springframework.boot:spring-boot-starter-test:${bootVersion}")
    testImplementation("io.projectreactor:reactor-test:3.4.24")
    testImplementation("org.testcontainers:junit-jupiter:$testcontainersVersion")
    testImplementation("org.testcontainers:postgresql:$testcontainersVersion")

    testImplementation("org.springframework.cloud:spring-cloud-stream-test-support:${cloudStreamVersion}")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")

    testImplementation("io.mockk:mockk:1.13.2")
    testImplementation("org.assertj:assertj-core:3.23.1")
    testImplementation("com.ninja-squad:springmockk:3.1.1")

    testImplementation("org.jeasy:easy-random-core:5.0.0")

    // mocking web
    testImplementation("com.squareup.okhttp3:mockwebserver:4.10.0")
}

dependencyManagement {
    imports {
        mavenBom("org.testcontainers:testcontainers-bom:$testcontainersVersion")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

sourceSets.forEach { source ->
    source.resources.filter {
        it.extension == "yaml" && it.parentFile.name == "api"
    }.forEach {
        val fileName = it.nameWithoutExtension
        val generateTask = tasks.register(
            "generate_$fileName",
            org.openapitools.generator.gradle.plugin.tasks.GenerateTask::class.java
        ) {
            group = "openapi generation"

            inputSpec.set(it.absolutePath)
            outputDir.set("$buildDir/openapi")
            apiPackage.set("org.point.$fileName.controller.api")
            modelPackage.set("org.point.$fileName.controller.dto")
            validateSpec.set(true)
            generatorName.set("spring")

            additionalProperties.put("reactive", "true")
            additionalProperties.put("interfaceOnly", "true")
            additionalProperties.put("skipDefaultInterface", "true")
            additionalProperties.put("useTags", "true")
            additionalProperties.put(
                "additionalModelTypeAnnotations",
                "@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)"
            )
        }
        tasks.withType<KotlinCompile> {
            dependsOn(generateTask)
        }
    }
}

sourceSets {
    main {
        java {
            srcDir(file("${buildDir}/openapi/src/main/java"))
        }
    }
}
