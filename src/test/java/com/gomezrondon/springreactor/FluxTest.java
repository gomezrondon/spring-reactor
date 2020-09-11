package com.gomezrondon.springreactor;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class FluxTest {



    @Test
    @DisplayName("flux concat letters 2 by 2")
    void test11() {
        var flux = Flux.fromIterable(List.of("a", "b", "c", "d", "e", "f"))
                .window(2)
                .flatMap(stringFlux -> stringFlux.collect(Collectors.joining()))  // a -> list[a, new]
                ;

      //  flux.subscribe(System.out::println);
        StepVerifier.create(flux)
                .expectNext("ab", "cd","ef")
                //  .expectNextCount(9) //this works
                .verifyComplete();
    }

    @Test
    @DisplayName("flux with window")
    void test10() {
        var flux = Flux.fromIterable(List.of("a", "b", "c", "d", "e", "f"))
                .window(2)
                .flatMap(stringFlux -> stringFlux.concatWithValues("End"))  // a -> list[a, new]
                ;

        flux.subscribe(System.out::println);
        StepVerifier.create(flux)
                 .expectNext("a", "b","End", "c", "d","End", "e", "f","End")
              //  .expectNextCount(9) //this works
                .verifyComplete();
    }

    @Test
    @DisplayName("flux with flatmap")
    void test9() {
        var flux = Flux.fromIterable(List.of("a", "b", "c", "d", "e", "f"))
                .flatMap( s -> Flux.fromIterable(Arrays.asList(s)));

        StepVerifier.create(flux)
               .expectNext("a", "b", "c", "d", "e", "f")
               // .expectNext(Flux.just("A", "B"),Flux.just("C", "D"),Flux.just("E", "F"))
                .verifyComplete();
    }


    @Test
    @DisplayName("connectable Flux send data only with 2 subscribers") // aka a Hot Flux
    void test8() {
        var flux = Flux.range(1, 5)
                .log() // to avoid the unbounded message it must be first
                .delayElements(Duration.ofMillis(100))
                .publish()
                .autoConnect(2);

        StepVerifier.create(flux)// first connection
                .then(flux::subscribe) // second connection
                .expectNext(1, 2, 3, 4, 5 )
                .expectComplete()
                .verify();
    }


    @Test
    @DisplayName("connectable Flux") // aka a Hot Flux
    void test7() {
        var flux = Flux.range(1, 10)
                .log() // to avoid the unbounded message it must be first
                .delayElements(Duration.ofMillis(100))
                .publish();

        StepVerifier.create(flux)
                .then(flux::connect)
                .expectNext(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .expectComplete()
                .verify();
    }


    @Test
    @DisplayName("flux with pretty BackPressure")
    void test6() {


        var flux = Flux.range(1, 10)
                .log() // to avoid the unbounded message it must be first
                .limitRate(2);

//flux.limitRate(2).subscribe();

        StepVerifier.create(flux)
                .expectNext(1, 2, 3, 4, 5,6, 7, 8, 9, 10)
                .verifyComplete();
    }

    @Test
    @DisplayName("flux with Interval One")
    void test5() {
    //    Flux<Long> interval = getFluxInterval();
   //     interval.subscribe(i -> log.info("number {}", i));

        StepVerifier.withVirtualTime(this::getFluxInterval)
                .expectSubscription()
                //.expectNoEvent(Duration.ofHours(2))
                .thenAwait(Duration.ofDays(1))
                .expectNext(0L)
                .thenAwait(Duration.ofDays(1))
                .expectNext(1L)
                .thenCancel()
                .verify();
    }


    private Flux<Long> getFluxInterval() {
        return Flux.interval(Duration.ofDays(1))
                .log();
    }


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
                //.expectError(IndexOutOfBoundsException.class)
                .expectErrorMessage("index error")
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
