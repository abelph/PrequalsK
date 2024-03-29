import kotlin.test.Test
import kotlin.test.assertEquals

class Fig4 {
    var s: String = ""
    fun greet(@argLabel("_") person: String, @argLabel("from") hometown: String) {
        s += "Hello ${person}!\n"
        s += "Glad you could visit from ${hometown}.\n"
    }
}

class Fig4Test: XCTestCase {
    fun testFig4(){
        val f = Fig4()
        val expected = "Hello Bill!\nGlad you could visit from Cupertino.\n"
        f.greet("Bill", "Cupertino")
        XCTAssertEqual(f.s, expected)
    }

    fun testFig4GreetAbel(){
        val f = Fig4()
        val expected = "Hello Abel!\nGlad you could visit from Austin.\n"
        f.greet("Abel", "Austin")
        XCTAssertEqual(f.s, expected)
    }
}