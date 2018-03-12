package com.example.part1.producer;

import lombok.AllArgsConstructor;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class UpperCaseStringProducer<T> implements Publisher<T> {

    private List<T> list;

    @Override
    public void subscribe(Subscriber<? super T> s) {
        StringSubscription stringSubscription = new StringSubscription(s);
        s.onSubscribe(stringSubscription);
    }

    public UpperCaseStringProducer from(List<T> list) {
        this.list = list;
        return this;
    }

    @AllArgsConstructor
    class StringSubscription implements Subscription {

        private Subscriber subscriber;

        private final AtomicLong requestedNumber = new AtomicLong();
        private final AtomicBoolean isActive = new AtomicBoolean(true);


        @Override
        public void request(long n) {

            while (true) {

                if (n <= 0 || !isActive.get()) {
                    subscriber.onError(new RuntimeException("Invalid request"));
                    return;
                }

                long currentNumer = requestedNumber.getAcquire();

                if (currentNumer == Long.MAX_VALUE) {
                    return;
                }

                long adjustedNumber = currentNumer + n;

                if (requestedNumber.compareAndSet(currentNumer, adjustedNumber)) {
                    break;
                }
            }

            try {
                list.stream()
                        .limit(requestedNumber.get())
                        .map(Object::toString)
                        .map(String::toUpperCase)
                        .forEach(a -> subscriber.onNext((T) a));


            } catch (Throwable throwable) {
                subscriber.onError(throwable);
            }
            subscriber.onComplete();
        }

        @Override
        public void cancel() {
            isActive.getAndSet(false);
        }
    }

}
