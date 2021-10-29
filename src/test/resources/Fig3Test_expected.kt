import XCTest

class Fig3Test: XCTestCase {

    @Test
    fun testFig3Value0() {
        val actual = C(0).x
        XCTAssertEqual(actual, 0)
    }

}
