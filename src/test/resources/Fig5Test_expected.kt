import kotlin.test.Test
import kotlin.test.assertEquals

class C2 {
    var s: String = ""
    var x: Int = 0
        set(newValue) {
            s = "old: ${field}\n"
            field = newValue
            s += "new: ${field}"
        }
}

class Fig5Test: XCTestCase {

    fun testFig5Value42() {
        val c = C2()
        c.x = 42
        val expected = "old: 0\nnew: 42"
        XCTAssertEqual(c.s, expected)
    }

}