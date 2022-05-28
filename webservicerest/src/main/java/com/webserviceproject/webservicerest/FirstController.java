package com.webserviceproject.webservicerest;


import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import org.json.JSONObject;

@RestController
public class FirstController {
  // Thread safe list of SSE emitters
  public List<SseEmitter> emitters = new CopyOnWriteArrayList<SseEmitter>();

  @PostMapping("/solicita_recurso_um")
  public Map<String, String> solicitaRecurso_1 (@RequestBody long id) {
    HashMap<String, String> map = new HashMap<>();
    map.put("key", "value");
    map.put("foo", "bar");
    map.put("aa", "bb");
    return map;
  }

  // Método para a inscrição do cliente
  @CrossOrigin
  @RequestMapping(value = "/subscribe", consumes = MediaType.ALL_VALUE)
  public SseEmitter subscribe() {
    SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);
    try {
      sseEmitter.send(SseEmitter.event().name("INIT"));
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    emitters.add(sseEmitter);
    sseEmitter.onCompletion(() -> {
      emitters.remove(sseEmitter);
    });
    return sseEmitter;
  }

  // Método para o dispatch do evento para todos os clientes
  @PostMapping(value = "/dispatchEvent")
  public void dispatchEventToClients(@RequestParam String title, @RequestParam String text) {
    String eventFormatted = new JSONObject()
    .put("title", title)
    .put("text", text).toString();
    
    for (SseEmitter emitter : emitters) {
      try {
        emitter.send(SseEmitter.event().name("Latest").data(eventFormatted));
      } catch (IOException e) {
        emitters.remove(emitter);
      }
    }
  }

  @PostMapping("/solicita_recurso_dois")
  public Map<String, String> solicitaRecurso_2 (@RequestBody long id) {
    HashMap<String, String> map = new HashMap<>();
    map.put("key", "value");
    map.put("foo", "bar");
    map.put("aa", "bb");
    return map;
  }

  @PostMapping("/libera_recurso_um")
  public Map<String, String> liberaRecurso_1 (@RequestBody long id) {
    HashMap<String, String> map = new HashMap<>();
    map.put("key", "value");
    map.put("foo", "bar");
    map.put("aa", "bb");
    return map;
  }

  @PostMapping("/libera_recurso_dois")
  public Map<String, String> liberaRecurso_2 (@RequestBody long id) {
    HashMap<String, String> map = new HashMap<>();
    map.put("key", "value");
    map.put("foo", "bar");
    map.put("aa", "bb");
    return map;
  }
}
