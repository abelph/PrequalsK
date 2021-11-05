import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.TokenStream
import org.antlr.v4.runtime.tree.ParseTreeWalker
import org.jetbrains.kotlin.spec.grammar.KotlinLexer
import org.jetbrains.kotlin.spec.grammar.KotlinParser
import org.jetbrains.kotlin.spec.grammar.KotlinParserBaseListener
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
    val preprocessor = PreprocessorListener(parser, tokens)
    val walker = ParseTreeWalker()
    walker.walk(preprocessor, tree)
    return preprocessor.getOutput()
}

class PreprocessorListener(private val parser: KotlinParser, private val tokens: TokenStream) : KotlinParserBaseListener() {
    private val outputTokens = ArrayList<String>()
    private var printedParent: ParserRuleContext? = null
    private var lastContext: ParserRuleContext? = null
    private var lastRuleStart = 0

    private var tryStartPrinting = false;

    fun getOutput(): String {
        return outputTokens.joinToString(separator = "")
    }

    fun stopPrinting() {
        if (printedParent == null) {
            printedParent = lastContext
        }
    }

    fun startPrinting() {
        tryStartPrinting = true // Handled in next call to "exitEveryRule"
    }

    fun undo() {
        while (outputTokens.size != lastRuleStart) {
            outputTokens.removeLast()
        }
    }

    fun ignore() {
        undo()
    }

//    override fun visitTerminal(p0: TerminalNode?) {
//    }

//    override fun visitErrorNode(p0: ErrorNode?) {
//    }

    override fun enterEveryRule(p0: ParserRuleContext?) {
        lastContext = p0
        lastRuleStart = outputTokens.size
        if (printedParent == null && p0 != null && p0.stop != null) {
            println(p0.toString(parser))
            for (i in p0.start.tokenIndex..p0.stop.tokenIndex) {
                outputTokens.add(tokens.get(i).text)
            }
        }
    }

    override fun exitEveryRule(ctx: ParserRuleContext?) {
        if (tryStartPrinting) {
            tryStartPrinting = false
            if (ctx == printedParent) {
                printedParent = null
            }
        }
    }

    // Ignore, instead print children
    override fun enterKotlinFile(p0: KotlinParser.KotlinFileContext?) {
        ignore()
    }

    // Do not print children of these
    override fun enterShebangLine(ctx: KotlinParser.ShebangLineContext?) { stopPrinting() }
    override fun exitShebangLine(ctx: KotlinParser.ShebangLineContext?) { startPrinting() }

    override fun enterFileAnnotation(ctx: KotlinParser.FileAnnotationContext?) { stopPrinting() }
    override fun exitFileAnnotation(ctx: KotlinParser.FileAnnotationContext?) { startPrinting() }

    override fun enterPackageHeader(ctx: KotlinParser.PackageHeaderContext?) { stopPrinting() }
    override fun exitPackageHeader(ctx: KotlinParser.PackageHeaderContext?) { startPrinting() }

    override fun enterImportList(ctx: KotlinParser.ImportListContext?) { stopPrinting() }
    override fun exitImportList(ctx: KotlinParser.ImportListContext?) { startPrinting() }

    override fun enterTopLevelObject(ctx: KotlinParser.TopLevelObjectContext?) {
        ignore()
    }

    override fun enterDeclaration(ctx: KotlinParser.DeclarationContext?) {
        ignore()
    }

    override fun enterClassDeclaration(ctx: KotlinParser.ClassDeclarationContext?) { stopPrinting() }
    override fun exitClassDeclaration(ctx: KotlinParser.ClassDeclarationContext?) { startPrinting() }

    override fun enterObjectDeclaration(ctx: KotlinParser.ObjectDeclarationContext?) { stopPrinting() }
    override fun exitObjectDeclaration(ctx: KotlinParser.ObjectDeclarationContext?) { startPrinting() }

    override fun enterFunctionDeclaration(ctx: KotlinParser.FunctionDeclarationContext?) { stopPrinting() }
    override fun exitFunctionDeclaration(ctx: KotlinParser.FunctionDeclarationContext?) { startPrinting() }

    override fun enterPropertyDeclaration(ctx: KotlinParser.PropertyDeclarationContext?) { stopPrinting() }
    override fun exitPropertyDeclaration(ctx: KotlinParser.PropertyDeclarationContext?) { startPrinting() }

    override fun enterTypeAlias(ctx: KotlinParser.TypeAliasContext?) { stopPrinting() }
    override fun exitTypeAlias(ctx: KotlinParser.TypeAliasContext?) { startPrinting() }

//    override fun enterClassDeclaration(p0: KotlinParser.ClassDeclarationContext?) {
//        stopPrinting()
//    }
//
//    override fun exitClassDeclaration(p0: KotlinParser.ClassDeclarationContext?) {
//        startPrinting()
//    }

}