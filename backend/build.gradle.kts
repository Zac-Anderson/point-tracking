import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.10"
    id("org.jetbrains.kotlin.plugin.spring") version "1.4.10"
    id("org.springframework.boot") version "2.3.3.RELEASE"
}

apply(plugin = "org.jetbrains.kotlin.jvm")
apply(plugin = "io.spring.dependency-management")

repositories {
    jcenter()
    mavenCentral()
    maven(url = "https://repo.spring.io/milestone")
}

configure<DependencyManagementExtension> {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:Hoxton.RELEASE")
    }
}

dependencies {
    // Kotlin
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))

    // Spring
    implementation("org.springframework.boot:spring-boot-starter-web")

    // Tests
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.0")
    testImplementation("org.assertj:assertj-core:3.17.2")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")
    testImplementation("org.mockito:mockito-core:3.6.28")
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()

    testLogging {
        events("passed", "skipped", "failed")
    }

    reports {
        html.isEnabled = true
    }

    outputs.upToDateWhen { false }
}