import org.jetbrains.kotlin.spec.grammar.tools.parseKotlinCode
import org.jetbrains.kotlin.spec.grammar.tools.tokenizeKotlinCode

fun main(args: Array<String>) {
    println("Hello World!")

    // Try adding program arguments at Run/Debug configuration
    println("Program arguments: ${args.joinToString()}")

    val tokens = tokenizeKotlinCode("val x = foo() + 10;")
    val parseTree = parseKotlinCode(tokens)
    // or just `val parseTree = parseKotlinCode("val x = foo() + 10;")`

    println(parseTree)

}