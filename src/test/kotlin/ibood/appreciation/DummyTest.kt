package ibood.appreciation

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.testng.annotations.Test

@Test
class DummyTest {
    fun `just some dummy test`() {
        assertThat(1 + 1).isEqualTo(2)
    }
}
