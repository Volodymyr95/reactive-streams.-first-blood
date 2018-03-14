package com.example.part1.producer;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import static java.util.List.of;

public class TestMain {

    public static void main(String[] args) {

        new UpperCaseStringProducer<String>()
                .from(of("sd", "ii", "cdscsd", "sdcsdcsdcds"))
                .subscribe(
                        new Subscriber<String>() {

                            @Override
                            public void onSubscribe(Subscription s) {
                                s.request(2);
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                s.request(2);
                            }

                            @Override
                            public void onNext(String s) {
                                System.out.println(s);
                            }

                            @Override
                            public void onError(Throwable t) {
                                System.out.println(t);
                            }

                            @Override
                            public void onComplete() {
                                System.out.println("Complete");
                            }
                        }
                );
    }


}
