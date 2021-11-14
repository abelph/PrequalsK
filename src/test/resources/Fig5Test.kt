import kotlin.test.Test
import kotlin.test.assertEquals

class C2 {
    var s: String = ""
    var x: Int = 0
        set(ᏫnewValue) {
            s = "old: ${field}\n"
            field = ᏫnewValue
            s += "new: ${field}"
        }
}

class Fig5Test {

    @Test
    fun testFig5Value42() {
        val c = C2()
        c.x = 42
        val expected = "old: 0\nnew: 42"
        assertEquals(expected, c.s)
    }

    @Test
    fun testFig5Value42() {
        val c = C2()
        c.x = 42
        c.x = 36
        val expected = "old: 42\nnew: 36"
        assertEquals(expected, c.s)
    }

}