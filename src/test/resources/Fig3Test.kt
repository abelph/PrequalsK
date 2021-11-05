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

}
