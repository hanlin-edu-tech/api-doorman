package tw.com.ehanlin

import io.quarkus.test.junit.QuarkusTest
import io.vertx.ext.web.Router
import org.junit.jupiter.api.Test
import io.restassured.RestAssured.*
import io.vertx.core.Vertx
import org.hamcrest.CoreMatchers.*
import javax.inject.Inject

@QuarkusTest
class ApiDoormanTest {

    @Inject
    lateinit var vertx: Vertx

    @Test
    fun test() {
        val server = vertx.createHttpServer()
        val router = Router.router(vertx)

        (0..100).forEach {
            router.route("/${it}").handler { context ->
                if (it % 10 == 0) {
                    context.response().statusCode = 500
                    context.response().end()
                } else {
                    context.response().end("$it")
                }
            }
        }

        server.requestHandler(router).listen(8181)

        given().`when`().get("/api/test/1").then().statusCode(200).body(`is`("1"))
        given().`when`().get("/api/test/2").then().statusCode(200).body(`is`("2"))
        given().`when`().get("/api/test/10").then().statusCode(500)
        given().`when`().get("/api/test/20").then().statusCode(500)

        given().`when`().get("/api/web/1").then().statusCode(200).body(`is`("1"))
        given().`when`().get("/api/web/2").then().statusCode(200).body(`is`("2"))
        given().`when`().get("/api/web/10").then().statusCode(500)
        given().`when`().get("/api/web/20").then().statusCode(500)

        server.close()
    }

}