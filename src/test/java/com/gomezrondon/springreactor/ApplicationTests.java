package com.gomezrondon.springreactor;

import jdk.jfr.StackTrace;
import jdk.jshell.JShell;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


@Slf4j
class ApplicationTests {


	@Test
	@DisplayName("mono subscriber with Complete")
	void test3() {
		String name = "Javier Gomez";
		Mono<String> mono = Mono.just(name)
				.log()
				.map(String::toUpperCase);


		mono.subscribe(n -> log.info("Name {}", n)
				,Throwable::printStackTrace
				,()-> log.info("Finished")
		, Subscription::cancel);

		log.info("-----------------------");
		StepVerifier.create(mono)
				.expectNext(name.toUpperCase())
				.verifyComplete();
	}

	@Test
	@DisplayName("mono subscriber with Error")
	void test2() {
		String name = "Javier Gomez";
		Mono<String> mono = Mono.just(name)
				.map(n -> {throw new RuntimeException("this is an error");});

		mono.subscribe(n -> log.info("Name {}", n), s -> log.error("An error Ocurred"));
		mono.subscribe(n -> log.info("Name {}", n),   Throwable::printStackTrace);

		log.info("-----------------------");
		StepVerifier.create(mono)
				.expectError(RuntimeException.class)
				.verify();
	}

	@Test
	@DisplayName("mono subscriber")
	void test1() {
		String name = "Javier Gomez";
		Mono<String> mono = Mono.just(name)
				.log();

		mono.subscribe();

		log.info("-----------------------");
		StepVerifier.create(mono)
				.expectNext(name)
				.verifyComplete();
	}

}
