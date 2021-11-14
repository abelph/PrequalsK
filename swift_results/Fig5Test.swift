import XCTest
#if false
let Ꮻimports = ["kotlin.test.Test", "kotlin.test.assertEquals"]
#endif

class C2 {
    var s: String = ""
    var x: Int = 0 {
        willSet {
            s += "old: \(x)\n"
        }

        didSet {
            s += "new: \(x)\n"
        }
    }
}

class Fig5Test: XCTestCase {
    func testFig5Value42() {
        let c = C2()
        c.x = 42
        let expected = "old: 0\nnew: 42\n"
        XCTAssertEqual(c.s, expected)
    }

    func testFig5SetTwice() {
        let c = C2()
        c.x = 42
        c.x = 36
        let expected = "old: 0\nnew: 42\nold: 42\nnew: 36\n"
        XCTAssertEqual(c.s, expected)
    }
}