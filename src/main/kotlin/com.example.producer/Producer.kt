package com.example.producer

import org.reactivestreams.Publisher
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

class Producer<T> : Publisher<T> {

    var list: List<T>? = emptyList();

    override fun subscribe(s: Subscriber<in T>?) {
        val listSubscription: ListSubscription = ListSubscription(s as Subscriber<T>)
        s.onSubscribe(listSubscription)
    }

    fun from(list: List<T>): Producer<T> {
        this.list = list;
        return this
    }

    inner class ListSubscription(val subscriber: Subscriber<T>) : Subscription {

        val isActive: AtomicBoolean = AtomicBoolean(true)
        val requestedNumber: AtomicLong = AtomicLong()

        override fun request(n: Long) {
            if (n <= 0 || !isActive.get()) {
                subscriber.onError(IllegalAccessError("Invalid requested number"))
                return
            }

            while (true) {
                var currentNumber: Long = requestedNumber.acquire
                if (currentNumber == Long.MAX_VALUE) {
                    return
                }

                var adjustedNumber: Long = currentNumber.plus(n)

                if (requestedNumber.compareAndSet(currentNumber, adjustedNumber)) {
                    break
                }
            }

            try {
                list!!
                        .stream()
                        .limit(requestedNumber.get())
                        .map { it.toString() }
                        .map { it.plus(" -||-") }
                        .forEach { subscriber.onNext(it as T) }

            } catch (t: Throwable) {
                subscriber.onError(t)
            }
            subscriber.onComplete()
        }

        override fun cancel() {
            isActive.set(false)
        }
    }

}