package com.gomezrondon.springreactor;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@Slf4j
public class FluxTest {

    @Test
    @DisplayName("flux with BackPressure")
    void test4() {


        var flux = Flux.range(1, 10)
                .log();


        flux.subscribe(new BaseSubscriber<Integer>() {
            private int count = 0;
            private final int requestCount = 2;

            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                request(requestCount); // first 2
            }

            @Override
            protected void hookOnNext(Integer value) {
                count++;
                if (count >= requestCount) {
                    count = 0;
                    request(requestCount); // get 2 by 2
                }

            }
        });
/*
 main] INFO reactor.Flux.Range.1 - | request(2)
[main] INFO reactor.Flux.Range.1 - | onNext(1)
[main] INFO reactor.Flux.Range.1 - | onNext(2)
[main] INFO reactor.Flux.Range.1 - | request(2)
[main] INFO reactor.Flux.Range.1 - | onNext(3)
[main] INFO reactor.Flux.Range.1 - | onNext(4)
        */

        StepVerifier.create(flux)
                .expectNext(1, 2, 3, 4, 5,6, 7, 8, 9, 10)
                .verifyComplete();
    }


    @Test
    @DisplayName("flux of numbers with error")
    void test3() {


        var flux = Flux.range(1, 5)
                .map(integer -> {
                    if (integer == 4) {
                        throw new IndexOutOfBoundsException("index error");
                    }
                    return integer;
                });

        StepVerifier.create(flux)
                .expectNext(1, 2, 3)
                .expectError(IndexOutOfBoundsException.class)
                .verify();
    }


    @Test
    @DisplayName("flux of numbers")
    void test2() {


        var flux = Flux.range(1, 5);

        StepVerifier.create(flux)
                .expectNext(1, 2, 3, 4, 5)
                .verifyComplete();
    }

    @Test
    @DisplayName("flux subscriber")
    void test1() {

        String[] strings = {"javier", "jose", "maria", "ana"};
        var flux = Flux.just(strings);

        StepVerifier.create(flux)
                .expectNext(strings)
                .verifyComplete();
    }

}
