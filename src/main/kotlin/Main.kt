import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.Trees
import org.jetbrains.kotlin.spec.grammar.KotlinLexer
import org.jetbrains.kotlin.spec.grammar.KotlinParser

fun main(args: Array<String>) {
    println("Hello World!")

    // Try adding program arguments at Run/Debug configuration
    println("Program arguments: ${args.joinToString()}")

    val code = "val x = foo() + 10;"
    val lexer = KotlinLexer(CharStreams.fromString(code))
    val tokens = CommonTokenStream(lexer)
    val parser = KotlinParser(tokens)
    val tree = parser.kotlinFile()
    println(Trees.toStringTree(tree, parser))

}