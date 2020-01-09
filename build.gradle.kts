import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

repositories {
    mavenCentral()
    jcenter()
}

plugins {
    kotlin("jvm") version Versions.kotlin
    application
}

application {
    mainClassName = "ibood.appreciation.App"
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    fun ktor(suffix: String) = "io.ktor:ktor-$suffix:${Versions.ktor}"
    implementation(ktor("server-netty"))
    implementation("org.kodein.di:kodein-di-framework-ktor-server-jvm:${Versions.kodein}")
    implementation("org.kodein.di:kodein-di-generic-jvm:${Versions.kodein}")
    implementation("io.github.microutils:kotlin-logging:${Versions.klogging}")
    implementation("ch.qos.logback:logback-classic:${Versions.logback}")

    testImplementation("org.testng:testng:${Versions.testng}")
    testImplementation("io.mockk:mockk:${Versions.mockk}")
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:${Versions.assertk}")
    testImplementation("org.skyscreamer:jsonassert:${Versions.jsonAssert}")
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
}
