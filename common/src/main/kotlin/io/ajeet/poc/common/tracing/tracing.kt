package io.ajeet.poc.common.tracing

import io.jaegertracing.internal.JaegerTracer
import io.jaegertracing.internal.reporters.RemoteReporter
import io.jaegertracing.internal.samplers.ProbabilisticSampler
import io.jaegertracing.thrift.internal.senders.UdpSender
import io.opentracing.Tracer

fun createTracer(): Tracer {
    return JaegerTracer.Builder("TestApp")
        .withReporter(RemoteReporter.Builder().withSender(UdpSender()).build())
        .withSampler(ProbabilisticSampler(1.0))
        .build()
}
