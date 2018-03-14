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



    class StringSubscription implements Subscription {

        private Subscriber subscriber;

        private final AtomicLong requestedNumber = new AtomicLong();
        private final AtomicBoolean isActive = new AtomicBoolean(true);
        private long position = 0;


        public StringSubscription(Subscriber subscriber) {
            this.subscriber = subscriber;
        }


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
                        .skip(position)
                        .takeWhile( t -> requestedNumber.get() > 0)
                        .peek(t -> requestedNumber.decrementAndGet())
                        .map(Object::toString)
                        .map(String::toUpperCase)
                        .forEach(a -> subscriber.onNext((T) a));


            } catch (Throwable throwable) {
                subscriber.onError(throwable);
            }

            position = n;

            subscriber.onComplete();
        }

        @Override
        public void cancel() {
            isActive.getAndSet(false);
        }
    }

}
