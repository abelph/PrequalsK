import XCTest
#if false
let Ꮻimports = ["kotlin.test.Test", "kotlin.test.assertEquals"]
#endif

class Fig4 {
    var s: String = ""
    func greet(_ person: String, from hometown: String) {
        s += "Hello \(person)!\n"
        s += "Glad you could visit from \(hometown).\n"
    }
}

class Fig4Test: XCTestCase {
    func testFig4() {
        let f = Fig4()
        let expected = "Hello Bill!\nGlad you could visit from Cupertino.\n"
        f.greet("Bill", from: "Cupertino")
        XCTAssertEqual(f.s, expected)
    }

    func testFig4GreetAbel() {
        let f = Fig4()
        let expected = "Hello Abel!\nGlad you could visit from Austin.\n"
        f.greet("Abel", from: "Austin")
        XCTAssertEqual(f.s, expected)
    }
}