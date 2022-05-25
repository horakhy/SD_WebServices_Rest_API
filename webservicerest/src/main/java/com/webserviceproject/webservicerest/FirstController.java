package com.webserviceproject.webservicerest;


import java.util.*;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FirstController {
  
  @PostMapping("/solicita_recurso_um")
  public Map<String, String> solicitaRecurso_1 (@RequestBody long id) {
    HashMap<String, String> map = new HashMap<>();
    map.put("key", "value");
    map.put("foo", "bar");
    map.put("aa", "bb");
    return map;
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
  public Map<String, String> lberaRecurso_2 (@RequestBody long id) {
    HashMap<String, String> map = new HashMap<>();
    map.put("key", "value");
    map.put("foo", "bar");
    map.put("aa", "bb");
    return map;
  }
}
