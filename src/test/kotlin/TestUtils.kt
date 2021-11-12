import java.net.URL
import kotlin.test.assertEquals

fun getResource(resourcePath: String): URL {
    return MainTest::class.java.getResource(resourcePath)!!
}

fun readFileResourceString(resourcePath: String): String {
    return getResource(resourcePath).readText()
}

fun getResourceFilePath(resourcePath: String): String {
    return getResource(resourcePath).path
}

fun integrationTest(fileResourcePrefix: String) {
    val sourcePath = getResourceFilePath("$fileResourcePrefix.kt")
    val outputPath = getResourceFilePath(fileResourcePrefix + ".swift")
    transpileKotlinJUnitToXCUnitFile(sourcePath, outputPath)
}

fun testPreprocess(fileResourcePrefix: String) {
    val source = readFileResourceString("$fileResourcePrefix.kt")
    val result = preprocess(source)
    val expected = readFileResourceString(fileResourcePrefix + "_expected.kt")
    assertStringEquals(expected, result)
}

fun assertStringEquals(expected: String, actual: String) {
    assertEquals(cleanStringForComparison(expected),
        cleanStringForComparison(actual))
}

fun cleanStringForComparison(s: String): String {
    return s.replace(Regex("\\s+"), " ").trim()
}

