package com.gomezrondon.springreactor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class FluxTest {


    @Test
    @DisplayName("flux of numbers")
    void test2() {


        Flux<Integer> flux = Flux.range(1, 5);

        StepVerifier.create(flux)
                .expectNext(1, 2, 3, 4, 5)
                .verifyComplete();
    }

    @Test
    @DisplayName("flux subscriber")
    void test1() {

        String[] strings = {"javier", "jose", "maria", "ana"};
        Flux<String> flux = Flux.just(strings);

        StepVerifier.create(flux)
                .expectNext(strings)
                .verifyComplete();
    }

}
