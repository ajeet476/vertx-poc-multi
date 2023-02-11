package io.ajeet.poc.api

import io.ajeet.poc.common.kafka.KafkaPublisher
import io.ajeet.poc.common.tracing.SpanElement
import io.ajeet.poc.common.tracing.TraceHelper
import io.opentracing.Span
import io.opentracing.Tracer
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

class MainVerticle(private val tracer: Tracer) : CoroutineVerticle() {

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(MainVerticle::class.java)
    }

    override suspend fun start() {
        val router = Router.router(vertx)
        val kafkaPublisher = KafkaPublisher(vertx)

        router.post("/users")
            .coroutineHandler { ctx ->
                kafkaPublisher.publish("poc-topic", "...sending...")
                ctx.end("Hello from coroutine handler")
            }

        vertx.createHttpServer()
            .requestHandler(router)
            .listen(8888)
            .await()
    }

    private fun Route.coroutineHandler(handler: suspend (RoutingContext) -> (Unit)): Route = handler { ctx ->
        val span: Span = OpenTracingUtil.getSpan()
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