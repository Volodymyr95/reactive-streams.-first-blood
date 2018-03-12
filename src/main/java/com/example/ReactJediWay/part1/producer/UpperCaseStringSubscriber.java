package com.example.ReactJediWay.part1.producer;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class UpperCaseStringSubscriber<T> implements Subscriber<T> {

    @Override
    public void onSubscribe(Subscription s) {
        System.out.println("subcri");
    }

    @Override
    public void onNext(T t) {
        if (t instanceof String) {
            ((String) t).toUpperCase();
        }
    }

    @Override
    public void onError(Throwable t) {
        System.out.println();
    }

    @Override
    public void onComplete() {

    }


}
