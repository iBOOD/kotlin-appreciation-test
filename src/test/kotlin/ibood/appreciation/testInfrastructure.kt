package ibood.appreciation

import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.withTestApplication
import org.kodein.di.Kodein

fun withKtor(testCode: TestApplicationEngine.() -> Unit)= withTestApplication({
    val kodein = Kodein {
        extend(applicationKodein())
    }
    mainKodeined(kodein)
}) {
    testCode()
}
