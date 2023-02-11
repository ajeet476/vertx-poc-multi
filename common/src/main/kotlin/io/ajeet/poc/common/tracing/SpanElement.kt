package io.ajeet.poc.common.tracing

import io.opentracing.Scope
import io.opentracing.Span
import io.opentracing.Tracer
import kotlinx.coroutines.ThreadContextElement
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

class SpanElement(val tracer: Tracer, val span: Span) : ThreadContextElement<Scope>, AbstractCoroutineContextElement(SpanElement) {

   companion object Key : CoroutineContext.Key<SpanElement>

   /**
   *  Will close current [Scope] after continuation's pause.
   */
   override fun restoreThreadContext(context: CoroutineContext, oldState: Scope) {
       oldState.close()
   }

   /**
   * Will create a new [Scope] after each continuation's resume, scope is activated with provided [span] instance.
   */
   override fun updateThreadContext(context: CoroutineContext): Scope {
       return tracer.activateSpan(span)
   }
}