import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

repositories {
    mavenCentral()
    jcenter()
}

plugins {
    kotlin("jvm") version Versions.kotlin
    id("com.github.ben-manes.versions") version Versions.Plugins.versions
    application
}

application {
    mainClassName = "com.ibood.appreciation.App"
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    fun ktor(suffix: String) = "io.ktor:ktor-$suffix:${Versions.ktor}"
    implementation(ktor("server-netty"))
    implementation(ktor("jackson"))
    implementation("org.kodein.di:kodein-di-framework-ktor-server-jvm:${Versions.kodein}")
    implementation("org.kodein.di:kodein-di-generic-jvm:${Versions.kodein}")
    implementation("io.github.microutils:kotlin-logging:${Versions.klogging}")
    implementation("ch.qos.logback:logback-classic:${Versions.logback}")

    testImplementation("org.testng:testng:${Versions.testng}")
    testImplementation("io.mockk:mockk:${Versions.mockk}")
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:${Versions.assertk}")
    testImplementation("org.skyscreamer:jsonassert:${Versions.jsonAssert}")
    testImplementation("com.jayway.jsonpath:json-path:${Versions.jsonPath}")
    testImplementation(ktor("server-test-host"))
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = Versions.jvm
            freeCompilerArgs = listOf("-Xjsr305=strict", "-Xuse-experimental=io.ktor.util.KtorExperimentalAPI")
        }
    }

    withType<Test> {
        useTestNG {}
    }

    withType<DependencyUpdatesTask> {
        val rejectPatterns = listOf("alpha", "beta", "eap", "rc").map { qualifier ->
            Regex("(?i).*[.-]$qualifier[.\\d-]*")
        }
        resolutionStrategy {
            componentSelection {
                all {
                    if (rejectPatterns.any { it.matches(candidate.version) }) {
                        reject("Release candidate")
                    }
                }
            }
        }
        checkForGradleUpdate = true
    }
}
