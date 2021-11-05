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
    private var savedTokenSize = 0
    private var printedParent: ParserRuleContext? = null
    private var lastContext: ParserRuleContext? = null
    private var lastRuleStart = 0

    private var tryStartPrinting = false;

    private var skipToClassBody = false;

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

    fun removeTokensStartingAtIndex(i: Int) {
        while (outputTokens.size != i) {
            outputTokens.removeLast()
        }
    }

    fun undo() {
        removeTokensStartingAtIndex(lastRuleStart)
    }

    fun ignore() {
        undo()
    }

    fun saveCheckpoint() {
        savedTokenSize = outputTokens.size
    }

    fun restoreCheckpoint() {
        removeTokensStartingAtIndex(savedTokenSize)
    }

//    override fun visitTerminal(p0: TerminalNode?) {
//    }

//    override fun visitErrorNode(p0: ErrorNode?) {
//    }

    fun saveTokens(context: ParserRuleContext) {
        saveTokens(context, context)
    }

    fun saveTokens(startContext: ParserRuleContext, stopContext: ParserRuleContext) {
        for (i in startContext.start.tokenIndex..stopContext.stop.tokenIndex) {
            outputTokens.add(tokens.get(i).text)
        }
    }

    override fun enterEveryRule(p0: ParserRuleContext?) {
        lastContext = p0
        lastRuleStart = outputTokens.size
        if (printedParent == null && p0 != null && p0.stop != null) {
            println(p0.toString(parser))
            saveTokens(p0, p0)
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

    override fun enterClassDeclaration(ctx: KotlinParser.ClassDeclarationContext?) {
        if (ctx != null) {
            if (ctx.simpleIdentifier().text.endsWith("Test")) {
                ignore()
                var startContext: ParserRuleContext = ctx.simpleIdentifier()
                var stopContext: ParserRuleContext = startContext
                if (ctx.modifiers() != null) {
                    startContext = ctx.modifiers()
                } else {
                    if (ctx.CLASS() != null) {
                        outputTokens.add(ctx.CLASS().text + " ")
                    }
                    if (ctx.FUN() != null) {
                        outputTokens.add(ctx.FUN().text + " ")
                    }
                    if (ctx.INTERFACE() != null) {
                        outputTokens.add(ctx.INTERFACE().text + " ")
                    }
                }
                if (ctx.typeParameters() != null) {
                    stopContext = ctx.typeParameters()
                }
                if (ctx.primaryConstructor() != null) {
                    stopContext = ctx.primaryConstructor()
                }
                saveTokens(startContext, stopContext)

                if (ctx.delegationSpecifiers() != null) {
                    outputTokens.add(": ")
                    saveTokens(ctx.delegationSpecifiers())
                    outputTokens.add(", XCTestCase ")
                } else {
                    outputTokens.add(": XCTestCase ")
                }
                if (ctx.typeConstraints() != null) {
                    saveTokens(ctx.typeConstraints())
                }
                saveCheckpoint()
                skipToClassBody = true
            } else {
                stopPrinting()
            }
        }
    }
    override fun exitClassDeclaration(ctx: KotlinParser.ClassDeclarationContext?) { startPrinting() }

    override fun enterModifiers(ctx: KotlinParser.ModifiersContext?) { stopPrinting() }
    override fun exitModifiers(ctx: KotlinParser.ModifiersContext?) { startPrinting() }

    override fun enterSimpleIdentifier(ctx: KotlinParser.SimpleIdentifierContext?) { stopPrinting() }
    override fun exitSimpleIdentifier(ctx: KotlinParser.SimpleIdentifierContext?) { startPrinting() }

    override fun enterTypeParameters(ctx: KotlinParser.TypeParametersContext?) { stopPrinting() }
    override fun exitTypeParameters(ctx: KotlinParser.TypeParametersContext?) { startPrinting() }

    override fun enterPrimaryConstructor(ctx: KotlinParser.PrimaryConstructorContext?) { stopPrinting() }
    override fun exitPrimaryConstructor(ctx: KotlinParser.PrimaryConstructorContext?) { startPrinting() }

    override fun enterDelegationSpecifiers(ctx: KotlinParser.DelegationSpecifiersContext?) { stopPrinting() }
    override fun exitDelegationSpecifiers(ctx: KotlinParser.DelegationSpecifiersContext?) { startPrinting() }

    override fun enterTypeConstraints(ctx: KotlinParser.TypeConstraintsContext?) { stopPrinting() }
    override fun exitTypeConstraints(ctx: KotlinParser.TypeConstraintsContext?) { startPrinting() }

    override fun enterClassBody(ctx: KotlinParser.ClassBodyContext?) {
        if (ctx != null) {
            if (skipToClassBody) {
                restoreCheckpoint()
                saveTokens(ctx)
                skipToClassBody = false
            }
            stopPrinting()
        }
    }
    override fun exitClassBody(ctx: KotlinParser.ClassBodyContext?) { startPrinting() }

    override fun enterEnumClassBody(ctx: KotlinParser.EnumClassBodyContext?) {
        if (ctx != null) {
            if (skipToClassBody) {
                restoreCheckpoint()
                saveTokens(ctx)
                skipToClassBody = false
            }
            stopPrinting()
        }
    }
    override fun exitEnumClassBody(ctx: KotlinParser.EnumClassBodyContext?) { startPrinting() }


    override fun enterObjectDeclaration(ctx: KotlinParser.ObjectDeclarationContext?) { stopPrinting() }
    override fun exitObjectDeclaration(ctx: KotlinParser.ObjectDeclarationContext?) { startPrinting() }

    override fun enterFunctionDeclaration(ctx: KotlinParser.FunctionDeclarationContext?) { stopPrinting() }
    override fun exitFunctionDeclaration(ctx: KotlinParser.FunctionDeclarationContext?) { startPrinting() }

    override fun enterPropertyDeclaration(ctx: KotlinParser.PropertyDeclarationContext?) { stopPrinting() }
    override fun exitPropertyDeclaration(ctx: KotlinParser.PropertyDeclarationContext?) { startPrinting() }

    override fun enterTypeAlias(ctx: KotlinParser.TypeAliasContext?) { stopPrinting() }
    override fun exitTypeAlias(ctx: KotlinParser.TypeAliasContext?) { startPrinting() }

}