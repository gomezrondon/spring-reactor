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
	@DisplayName("mono do ON Error Return")
	void test7() {

		String message = "NOTHING";
		Mono<Object> mono = Mono.error(new IllegalArgumentException("illegal argument exception"))
				.doOnError( e -> ApplicationTests.log.error("error message: {}",e.getMessage()))
				.onErrorReturn(message);


		StepVerifier.create(mono)
				.expectNext(message)
				.verifyComplete();
	}

	@Test
	@DisplayName("mono do ON Error Resume")
	void test6() {
		String name = "Javier Gomez";
		Mono<Object> mono = Mono.error(new IllegalArgumentException("illegal argument exception"))
				.doOnError( e -> ApplicationTests.log.error("error message: {}",e.getMessage()))
				.onErrorResume(s ->{
					log.info("inside on Error Resume");
							return Mono.just(name);
						})
				.log();

		mono.subscribe();

		log.info("-----------------------");
		StepVerifier.create(mono)
				.expectNext(name)
				.verifyComplete();
	}

	@Test
	@DisplayName("mono do ON Error")
	void test5() {

		Mono<Object> mono = Mono.error(new IllegalArgumentException("illegal argument exception"))
				.doOnError( e -> ApplicationTests.log.error("error message: {}",e.getMessage()))
				.log();

		mono.subscribe();

		log.info("-----------------------");
		StepVerifier.create(mono)
				.expectError(IllegalArgumentException.class)
				.verify();
	}

	@Test
	@DisplayName("mono do On Methods")
	void test4() {
		String name = "Javier Gomez";
		Mono<String> mono = Mono.just(name)
				.map(String::toUpperCase)
				.doOnSubscribe(subscription -> log.info("you are subcribed"))
				.doOnRequest(longnumber -> log.info("you are requesting {}", longnumber))
				.doOnNext(s -> log.info("the next value is: {}", s))
				.doOnSuccess(s -> log.info("do on Success Executed"));


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
