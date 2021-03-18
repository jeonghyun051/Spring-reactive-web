package com.cos.reactorex02;

import java.time.Duration;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

// Netty 서버가 돈다. 비동기이다, Tomcat 서버는 스레드 서버이다.
// 요청할 때마다 이벤트 루프가 돈다.
// Flux N개 이상의 데이터를 응답 할 때
// Mono 0~1개의 데이터를 응답할 때

@RestController
public class TestController1 {

	@GetMapping("/flux1")
	public Flux<Integer> flux1() {
		return Flux.just(1,2,3,4).log(); // request(unbounded), onComplete 후 response한다.
	}
	
	
	// 프로토콜이 스트림이로 바뀌니까 브라우저가 받자마자 뿌린다. 원래 messageConvert는 모아놨다가 한번에 브라우저에 뿌린다.
	@GetMapping(value="/flux2", produces=MediaType.APPLICATION_STREAM_JSON_VALUE) // 원래는 value 하지만 한개일 때는 생략가능 // 미니어타입 org 스프링꺼
	public Flux<Integer> flux2() {
		return Flux.just(1,2,3,4).delayElements(Duration.ofSeconds(1)).log(); // request(unbounded), onComplete 후 response한다.
	}
	
	@GetMapping(value="/flux3", produces=MediaType.APPLICATION_STREAM_JSON_VALUE)
	public Flux<Long> flux3() {
		return Flux.interval(Duration.ofSeconds(1)).log();
	}
	
	
	@GetMapping(value="/mono1") // 데이터를 한개만 응답해주니까 딜레이도 못주고, 스트림을 쓸 필요도 없다. 
	public Mono<Integer> mono1() {
		return Mono.just(1).log(); 
	}
}
