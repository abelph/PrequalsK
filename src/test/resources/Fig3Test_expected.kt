import kotlin.test.Test
import kotlin.test.assertEquals

class C(val x: Int) {
}

class Fig3Test: XCTestCase {
  fun testFig3Value0(){
   val actual = C(0).x
   XCTAssertEqual(actual, 0)
  }

  fun testFig3ValueNegative(){
   val actual = C(-1).x
   XCTAssertEqual(actual, -1)
  }

  fun testFig3ValueMax(){
   val actual = C(2_147_483_647).x
   XCTAssertEqual(actual, 2_147_483_647)
  }
}
