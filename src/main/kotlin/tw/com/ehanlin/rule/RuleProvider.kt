package tw.com.ehanlin.rule

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.eclipse.microprofile.config.inject.ConfigProperty
import java.io.File
import javax.annotation.PostConstruct
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class RuleProvider {

    @Inject
    private lateinit var ruleParser: RuleParser

    @ConfigProperty(name = "rule.config.path")
    private lateinit var configPath: String

    private lateinit var ruleList: List<Rule>

    @PostConstruct
    fun init() {
        ruleList = configPath.split(",").flatMap {
            ruleParser.parse(File(it.trim()))
        }
    }

    fun rules(): List<Rule> = ruleList
}

@ApplicationScoped
class RuleParser {

    @Inject
    private lateinit var objectMapper: ObjectMapper

    fun parse(file: File): List<Rule> {
        return objectMapper.readValue(file, object : TypeReference<List<Rule>>() {})
    }

    fun parse(jsonString: String): List<Rule> {
        return objectMapper.readValue(jsonString, object : TypeReference<List<Rule>>() {})
    }
}

data class Rule(
    val path: String,
    val origin: String
)
