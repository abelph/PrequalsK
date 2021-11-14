import kotlin.test.Test
import kotlin.test.assertEquals

class C(val x: Int) {
}

class Fig3Test {

    @Test
    fun testFig3Value0() {
        val actual = C(0).x
        assertEquals(0, actual)
    }

    @Test
    fun testFig3ValueNegative() {
        val actual = C(-1).x
        assertEquals(-1, actual)
    }

    @Test
    fun testFig3ValueMax() {
        val actual = C(2_147_483_647).x
        assertEquals(2_147_483_647, actual)
    }

}
