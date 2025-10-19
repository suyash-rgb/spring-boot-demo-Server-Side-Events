package com.example.springboot.Server_Side_Events_Demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/sse")
public class SseController {
  private final SseEmitter emitter = new SseEmitter();
  private final ExecutorService executorService = Executors.newSingleThreadExecutor();

  @GetMapping("/events")
  public SseEmitter handleSse(){
      executorService.execute(()->{
          try {
              for (int i = 0; i < 100; i++) {
                  emitter.send(SseEmitter.event().id(String.valueOf(i)).data("Event " + i));

              }
              emitter.complete();
          } catch(Exception e){
              emitter.completeWithError(e);
          }
      });
      return emitter;
  }

}
