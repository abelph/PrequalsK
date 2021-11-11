import kotlin.test.Test
import kotlin.test.assertEquals

class C(val x: Int) {
}

class Fig3Test: XCTestCase {

 fun testFig3Value0(){
  val actual = C(0).x
  XCTAssertEqual(actual, 0)
}

}
