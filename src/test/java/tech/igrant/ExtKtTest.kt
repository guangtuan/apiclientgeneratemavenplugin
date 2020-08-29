package tech.igrant

import org.junit.Assert.assertEquals
import org.junit.Test

class ExtKtTest {

    @Test
    fun slashToDot() {
        val str = "path/to/Demo.class"
        assertEquals(
                "path.to.Demo.class",
                str.slashToDot()
        )
    }

}