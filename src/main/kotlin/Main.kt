import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.Trees
import org.jetbrains.kotlin.spec.grammar.KotlinLexer
import org.jetbrains.kotlin.spec.grammar.KotlinParser
import java.io.File

fun main(args: Array<String>) {
    preprocessKotlinJUnitToXCUnitFile(args[0], args[1])
}


fun preprocessKotlinJUnitToXCUnitFile(sourcePath: String, destPath: String) {
    val source = File(sourcePath)
    val dest   = File(destPath)
    preprocessKotlinJUnitToXCUnitFile(source, dest)
}


fun preprocessKotlinJUnitToXCUnitFile(source: File, dest: File) {
    val text = source.readText()
    val processed = preprocessKotlinJUnitToXCUnit(text)
    dest.writeText(processed)
}


/**
 * Returns a String which can be sent to SequalsK to produce an XCUnit test.
 */
fun preprocessKotlinJUnitToXCUnit(kotlinSource: String): String {
    val lexer = KotlinLexer(CharStreams.fromString(kotlinSource))
    val tokens = CommonTokenStream(lexer)
    val parser = KotlinParser(tokens)
    val tree = parser.kotlinFile()
    return Trees.toStringTree(tree, parser)
}