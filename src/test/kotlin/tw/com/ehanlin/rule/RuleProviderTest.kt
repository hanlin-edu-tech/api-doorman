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
            Assertions.assertEquals("www.ehanlin.com.tw/api/test/(?<path>.*)", source)
            Assertions.assertEquals("test-app/", target)
        }
        with(result[1]) {
            Assertions.assertEquals("www.ehanlin.com.tw/api/web/(?<path>.*)", source)
            Assertions.assertEquals("web-app/", target)
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
        "source" : "www.ehanlin.com.tw/api/test/(?<path>.*)",
        "target" : "test-app/"
    },
    {
        "source" : "www.ehanlin.com.tw/api/web/(?<path>.*)",
        "target" : "web-app/"
    }
]
        """.trimIndent()
        )
        Assertions.assertEquals(2, result.size)
        with(result[0]) {
            Assertions.assertEquals("www.ehanlin.com.tw/api/test/(?<path>.*)", source)
            Assertions.assertEquals("test-app/", target)
        }
        with(result[1]) {
            Assertions.assertEquals("www.ehanlin.com.tw/api/web/(?<path>.*)", source)
            Assertions.assertEquals("web-app/", target)
        }
    }

    @Test
    fun testParseFile() {
        val result = ruleParser.parse(File("src/main/resources/rule-config.json"))
        Assertions.assertEquals(2, result.size)
        with(result[0]) {
            Assertions.assertEquals("www.ehanlin.com.tw/api/test/(?<path>.*)", source)
            Assertions.assertEquals("test-app/", target)
        }
        with(result[1]) {
            Assertions.assertEquals("www.ehanlin.com.tw/api/web/(?<path>.*)", source)
            Assertions.assertEquals("web-app/", target)
        }
    }
}