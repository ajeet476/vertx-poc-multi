package io.ajeet.poc.common.tracing

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.coroutines.CoroutineContext

class TraceHelper {
  private companion object {
    val LOG: Logger = LoggerFactory.getLogger(TraceHelper::class.java)
  }

  suspend fun <T> withContextTraced(
    context: CoroutineContext,
    spanName: String?,
    block: suspend CoroutineScope.() -> T
  ): T {
    return coroutineScope {
      val spanElem = this.coroutineContext[SpanElement]

      if (spanElem == null) {
        LOG.warn ( "Calling 'withTracer', but no span found in context" )
        withContext(context, block)
      } else {
        val childSpan = if (isNewSpan(spanName)) {
          spanElem.tracer.buildSpan(spanName).asChildOf(spanElem.span).start()
        } else {
          spanElem.span
        }

        try {
          withContext(context + SpanElement(spanElem.tracer, childSpan), block)
        } finally {
          if (isNewSpan(spanName)) childSpan.finish()
        }
      }
    }
  }

  private fun isNewSpan(spanName: String?): Boolean {
    return spanName == null
  }
}