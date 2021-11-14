import XCTest
#if false
let Ꮻimports = ["kotlin.test.Test", "kotlin.test.assertEquals"]
#endif

class C {
    let x: Int
    init(x: Int) {
        self.x = x
    }
}

class Fig3Test: XCTestCase {
    func testFig3Value0() {
        let actual = C(x: 0).x
        XCTAssertEqual(actual, 0)
    }

    func testFig3ValueNegative() {
        let actual = C(x: -1).x
        XCTAssertEqual(actual, -1)
    }

    func testFig3ValueMax() {
        let actual = C(x: 2_147_483_647).x
        XCTAssertEqual(actual, 2_147_483_647)
    }
}