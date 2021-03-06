package tw.com.ehanlin.rule

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import io.quarkus.runtime.annotations.RegisterForReflection
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

/**
 * https://github.com/quarkusio/quarkus/issues/3954
 */
@RegisterForReflection
@JsonIgnoreProperties(ignoreUnknown = true)
data class Rule(
    @JsonProperty("path")
    var path: String = "",
    @JsonProperty("origin")
    var origin: String = ""
)
