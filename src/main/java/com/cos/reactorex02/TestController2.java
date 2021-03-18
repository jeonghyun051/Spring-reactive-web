package com.cos.reactorex02;

import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@CrossOrigin
@RestController
public class TestController2 {

	// 프로세서이다. 역할은 지속적으로 응답, 구독을 Stirng으로
	Sinks.Many<String> sink;

	// mulitcast() 새로 들어온 데이터만 응답받음 hot 시퀀스 = 스트림
	// replay() 기존 데이터 + 새로운 데이터 응답 cold 시퀀스
	


	public TestController2() {
		this.sink = Sinks.many().multicast().onBackpressureBuffer(); // 싱크가 만들어짐, 리엑터는 내부적으로 만들어져 있음
	}
	
	@GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
	public Flux<Integer> findAll(){
		return Flux.just(1,2,3,4,5,6).log(); // 응답 데이터는 JSON데이터이다. 배열
	}

	@GetMapping("/send")
	public void send() {
		sink.tryEmitNext("Hello world");

	}

	// data : 실제값 \n\n
	@GetMapping(value = "/sse") // ServcerSentEvent의 ContentType은 text event stream 이다.
	public Flux<ServerSentEvent<String>> sse() { // Flux<ServerSentEvent<String>>자동으로 텍스트 제인스 스트림을 응답해줌

		return sink.asFlux().map(e -> ServerSentEvent.builder(e).build()).doOnCancel(()->{
			System.out.println("sse종료됨");
			sink.asFlux().blockLast();
		
		}); // 구독
	}

//	   // data = 실제값 \n\n
//	   @GetMapping(value = "/sse",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//	   public Flux<ServerSentEvent<String>> sse() { //ServerSendEvent의 ContentType은 text event stream
//	      return sink.asFlux().map(e->ServerSentEvent.builder(e).build()); //구독
//	   }

}
