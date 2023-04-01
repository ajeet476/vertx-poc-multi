package io.ajeet.poc.api

import io.ajeet.poc.api.controller.TokenController
import io.ajeet.poc.api.controller.UserController
import io.ajeet.poc.api.service.TokenService
import io.ajeet.poc.api.service.UserService
import io.ajeet.poc.common.cassandra.CassandraDao
import io.ajeet.poc.common.config.CassandraConfigs
import io.ajeet.poc.common.config.KafkaConfigs
import io.ajeet.poc.common.kafka.KafkaPublisher
import io.ajeet.poc.common.kafka.MessagePublisher
import io.ajeet.poc.common.repository.UserRepository
import io.ajeet.poc.common.repository.UserTokenRepository
import io.ajeet.poc.common.repository.impl.CassandraUserRepository
import io.ajeet.poc.common.repository.impl.CassandraUserTokenRepository
import io.ajeet.poc.common.tracing.SpanElement
import io.ajeet.poc.common.tracing.TraceHelper
import io.opentracing.Span
import io.opentracing.util.GlobalTracer
import io.vertx.cassandra.CassandraClient
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Route
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import io.vertx.tracing.opentracing.OpenTracingUtil
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class MainVerticle : CoroutineVerticle() {

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(MainVerticle::class.java)
    }

    // configs : private mostly
    private val kafkaConfigs by lazy { KafkaConfigs() }
    private val cassandraConfigs by lazy { CassandraConfigs() }

    // database client
    private val cassandraClient: CassandraClient by lazy { CassandraDao(this.vertx, cassandraConfigs).getClient() }
    // repository
    private val userRepository: UserRepository by lazy { CassandraUserRepository(this.cassandraClient) }
    val userTokenRepository: UserTokenRepository by lazy { CassandraUserTokenRepository(this.cassandraClient) }

    // services : public (for custom IOC)
    val kafkaPublisher: MessagePublisher by lazy { KafkaPublisher(this.vertx, kafkaConfigs) }
    val tokenService by lazy { TokenService(this) }
    val userService by lazy { UserService(this.userRepository) }

    // controllers: always private
    private val tokenController by lazy { TokenController(this) }
    private val userController by lazy { UserController(this) }

    override suspend fun start() {
        vertx.createHttpServer()
            .requestHandler(router())
            .listen(8888)
            .await()
    }

    private fun router(): Router {
        val router = Router.router(vertx)

        router.post("/token")
            .coroutineHandler { ctx -> tokenController.generateToken(ctx) }

        router.get("/users/:id")
            .coroutineHandler { ctx -> userController.findUser(ctx) }

        router.get("/health")
            .coroutineHandler { ctx -> ctx.response().end(JsonObject.of().put("status", "up").toBuffer()) }

        return router
    }

    private fun Route.coroutineHandler(handler: suspend (RoutingContext) -> (Unit)): Route = handler { ctx ->
        val span: Span = OpenTracingUtil.getSpan()
        val tracer = GlobalTracer.get()
        launch(ctx.vertx().dispatcher() + SpanElement(tracer, span)) {
            TraceHelper().withContextTraced(coroutineContext, null) {
                try {
                    handler(ctx)
                } catch (t: Throwable) {
                    ctx.fail(t)
                }
            }
        }
    }
}