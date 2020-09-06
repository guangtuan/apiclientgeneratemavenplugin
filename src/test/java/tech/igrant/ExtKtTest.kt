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

    @Test
    fun classNameToVariableName() {
        val expected = "testClass"
        val canonicalName = "com.example.TestClass"
        assertEquals(expected, canonicalName.classNameToVariableName())
        val simpleName = "TestClass"
        assertEquals(expected, simpleName.classNameToVariableName())
    }

}