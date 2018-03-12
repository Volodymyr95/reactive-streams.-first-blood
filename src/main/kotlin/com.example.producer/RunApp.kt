package com.example.producer

import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription

fun main(args: Array<String>) {
    val producer: Producer<String> = Producer()
    producer.from(mutableListOf("one", "two,", "three")).subscribe(
            object : Subscriber<String> {

                override fun onSubscribe(s: Subscription) {
                    s.request(1)
                }

                override fun onNext(s: String) {
                    println(s)
                }

                override fun onError(t: Throwable) {
                    println(t)
                }

                override fun onComplete() {
                    println("Complete")
                }
            }
    )
}