package tw.com.ehanlin.rule

import io.quarkus.test.junit.QuarkusTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.File
import javax.inject.Inject

@QuarkusTest
class RuleProviderTest {

    @Inject
    lateinit var ruleProvider: RuleProvider

    @Test
    fun testRules() {
        val result = ruleProvider.rules()
        Assertions.assertEquals(2, result.size)
        with(result[0]) {
            Assertions.assertEquals("localhost:8080/api/test/(?<path>.*)", path)
            Assertions.assertEquals("127.0.0.1:8181/\${path}", origin)
        }
        with(result[1]) {
            Assertions.assertEquals("127.0.0.1:8080/api/web/(?<path>.*)", path)
            Assertions.assertEquals("localhost:8181/\${path}", origin)
        }
    }
}

@QuarkusTest
class RuleParserTest {
    @Inject
    lateinit var ruleParser: RuleParser

    @Test
    fun testParseJsonString() {
        val result = ruleParser.parse(
            """
[
    {
        "path" : "www.ehanlin.com.tw/api/test/(?<path>.*)",
        "origin" : "test-app/"
    },
    {
        "path" : "www.ehanlin.com.tw/api/web/(?<path>.*)",
        "origin" : "web-app/"
    }
]
        """.trimIndent()
        )
        Assertions.assertEquals(2, result.size)
        with(result[0]) {
            Assertions.assertEquals("www.ehanlin.com.tw/api/test/(?<path>.*)", path)
            Assertions.assertEquals("test-app/", origin)
        }
        with(result[1]) {
            Assertions.assertEquals("www.ehanlin.com.tw/api/web/(?<path>.*)", path)
            Assertions.assertEquals("web-app/", origin)
        }
    }

    @Test
    fun testParseFile() {
        val result = ruleParser.parse(File("src/main/resources/rule-config.json"))
        Assertions.assertEquals(2, result.size)
        with(result[0]) {
            Assertions.assertEquals("localhost:8080/api/test/(?<path>.*)", path)
            Assertions.assertEquals("127.0.0.1:8181/\${path}", origin)
        }
        with(result[1]) {
            Assertions.assertEquals("127.0.0.1:8080/api/web/(?<path>.*)", path)
            Assertions.assertEquals("localhost:8181/\${path}", origin)
        }
    }
}