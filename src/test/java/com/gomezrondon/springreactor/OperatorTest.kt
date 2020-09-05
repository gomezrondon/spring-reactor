package com.gomezrondon.springreactor


import lombok.extern.slf4j.Slf4j
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.test.StepVerifier
import java.nio.file.Files
import java.nio.file.Path
import java.time.Duration

@Slf4j
class OperatorTest {

    companion object {
        private val log = LoggerFactory.getLogger(OperatorTest::class.java.name)
    }

    @Test
    @DisplayName("read a file in a background thread")
    fun test3() {
        val list: Mono<MutableList<String>> = Mono.fromCallable { Files.readAllLines(Path.of("text-file.txt")) }
                .log()
                .subscribeOn(Schedulers.boundedElastic()) // read from a background thread

       // list.subscribe { log.info("{}",it) }

        StepVerifier.create(list)
                .thenConsumeWhile{
                    Assertions.assertFalse(it.isEmpty())
                    log.info("size {}",it.size)
                    true
                }
                .verifyComplete()
    }


    @Test
    @DisplayName("flux with publishOn with Schedulers.single()")
    fun test2() {
        val flux = Flux.range(1, 4)
                .log() // to avoid the unbounded message it must be first
                .map { log.info("Map 1 - number {} on thread {}", it, Thread.currentThread().name)
                    it
                }.publishOn(Schedulers.single()) // affect only here and forward.
                .map { log.info("Map 2 - number {} on thread {}", it, Thread.currentThread().name)
                    it
                }


        StepVerifier.create(flux) // first connection
                .expectSubscription()
                .expectNext(1, 2, 3, 4 )
                .verifyComplete()
    }

    @Test
    @DisplayName("flux with subscribeOn with Schedulers.single()")
    fun test1() {
        val flux = Flux.range(1, 4)
                .log() // to avoid the unbounded message it must be first
                .map { log.info("Map 1 - number {} on thread {}", it, Thread.currentThread().name)
                    it
                }.subscribeOn(Schedulers.single()) // affect all the flux from begining to end
                .map { log.info("Map 2 - number {} on thread {}", it, Thread.currentThread().name)
                    it
                }


        StepVerifier.create(flux) // first connection
                .expectSubscription()
                .expectNext(1, 2, 3, 4 )
                .verifyComplete()
    }

}