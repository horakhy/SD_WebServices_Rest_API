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
  // public List<SseEmitter> emitters = new CopyOnWriteArrayList<SseEmitter>();
  public Map<String, SseEmitter> emitters = new HashMap<String, SseEmitter>();

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
  public SseEmitter subscribe(@RequestParam String userId) {
    SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);
   
    sendInitEvent(sseEmitter);

    emitters.put(userId, sseEmitter);
    sseEmitter.onCompletion(() -> { emitters.remove(userId); });
    sseEmitter.onTimeout(() -> { emitters.remove(userId); });
    sseEmitter.onError((e) -> { emitters.remove(userId); });

    return sseEmitter;
  }


  // Método para o dispatch do evento para cliente específico
  @PostMapping(value = "/dispatchEvent")
  public void dispatchEventToClients(@RequestParam String title, @RequestParam String text, @RequestParam String userId) {
    String eventFormatted = new JSONObject()
    .put("title", title)
    .put("text", text).toString();
    
    SseEmitter sseEmitter = emitters.get(userId);
    if (sseEmitter != null) {
      try {
        sseEmitter.send(SseEmitter.event().name("Latest").data(eventFormatted));
      } catch (Exception e) {
        emitters.remove(userId);
      }
    }

    // for (SseEmitter emitter : emitters) {
    //   try {
    //     emitter.send(SseEmitter.event().name("Latest").data(eventFormatted));
    //   } catch (IOException e) {
    //     emitters.remove(emitter);
    //   }
    // }
  }

  private void sendInitEvent(SseEmitter sseEmitter) {
    try {
      sseEmitter.send(SseEmitter.event().name("INIT"));
    } catch (IOException e) {
      e.printStackTrace();
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
