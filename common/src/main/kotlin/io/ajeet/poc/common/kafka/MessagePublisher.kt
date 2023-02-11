package io.ajeet.poc.common.kafka

interface MessagePublisher {
  fun publish(topic: String, data: String)
}