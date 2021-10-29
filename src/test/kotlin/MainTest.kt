import java.net.URL
import kotlin.test.Test
import kotlin.test.assertEquals


class MainTest {

    private fun getResource(resourcePath: String): URL {
        return MainTest::class.java.getResource(resourcePath)!!
    }

    private fun readFileResourceString(resourcePath: String): String {
        return getResource(resourcePath).readText()
    }

    private fun getResourceFilePath(resourcePath: String): String {
        return getResource(resourcePath).path
    }

    private fun test(fileResourcePrefix: String) {
        val sourcePath = getResourceFilePath(fileResourcePrefix + ".kt")
        val actualResourcePath = fileResourcePrefix + "_actual.kt"
        val actualPath = getResourceFilePath(actualResourcePath)

        preprocessKotlinJUnitToXCUnitFile(sourcePath, actualPath)

        val result = readFileResourceString(actualResourcePath)
        val expected = readFileResourceString(fileResourcePrefix + "_expected.kt")
        assertEquals(expected, result)
    }

    @Test
    fun testConvertFig3Test() {
        test("Fig3Test")
    }

}