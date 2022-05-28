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

  // Mensagens
  private final String PEGOU_RECURSO_UM = "Você pegou o recurso 1";
  private final String PEGOU_RECURSO_DOIS = "Você pegou o recurso 2";
  private final String RECURSO_UM_OCUPADO = "O recurso 1 está ocupado, você foi adicionado na fila de espera";
  private final String RECURSO_DOIS_OCUPADO = "O recurso 2 está ocupado, você foi adicionado na fila de espera";

  private final String RECURSO_UM_INVALIDO = "Você não está utilizando o recurso 1";
  private final String RECURSO_DOIS_INVALIDO = "Você não está utilizando o recurso 2";
  private final String RECURSO_UM_LIBERADO = "Você liberou o recurso 1";
  private final String RECURSO_DOIS_LIBERADO = "Você liberou o recurso 2";
  private final String RECURSO_UM_EXPIRADO = "O recurso 1 expirou\n";
  private final String RECURSO_DOIS_EXPIRADO = "O recurso 2 expirou\n";

  private final String RECURSO_UM_LIVRE = "O recurso 1 está livre, você pode utilizá-lo";
  private final String RECURSO_DOIS_LIVRE = "O recurso 2 está livre, você pode utilizá-lo";

  private final String RECURSO_JA_OBTIDO = "Você já possui esse recurso";
  private final String STRING_VAZIA = "";

  // ID dos clientes com o recurso
  private String clienteComRecurso_1 = null;
  private String clienteComRecurso_2 = null;

  // Disponibilidade dos recursos
  private boolean recursoDisponível_1 = false;
  private boolean recursoDisponível_2 = false;

  // Cliente em espera para o recurso 1 e 2
  private ArrayList<String> clientesEmEspera1;
  private ArrayList<String> clientesEmEspera2;

  // Timeout
  private static final int TEMPO_LIMITE = 5000; // Tempo em milisegundos

  // Hashmap [ID cliente, Emissor]
  public Map<String, SseEmitter> emitters = new HashMap<String, SseEmitter>();

  @CrossOrigin
  @PostMapping(value="/solicita_recurso_um", consumes = MediaType.ALL_VALUE)
  public String solicitaRecurso_1(@RequestParam String clientId) {
    System.out.println("///////////////////////AS" + clientId);
    String Client_id = clientId.toString();
    String client_id_formatted = new JSONObject()
        .put("clientId", clientId).toString();
    System.out.println("///////////////////////AS" + client_id_formatted);
    if (clienteComRecurso_1 == clientId) {
      return RECURSO_JA_OBTIDO;
    }

    if (this.recursoDisponível_1) {
      // Tempo para expirar o recurso
      new Timer().schedule(new TimerTask() {
        @Override
        public void run() {
          try {
            liberarRecursoTimeout_1(Client_id);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }, TEMPO_LIMITE);
      this.recursoDisponível_1 = false;
      this.clienteComRecurso_1 = clientId;

      return PEGOU_RECURSO_UM;
    }
    clientesEmEspera1.add(Client_id);
    // client_id.notificar(RECURSO_UM_OCUPADO);
    return RECURSO_UM_OCUPADO;
  }
  @CrossOrigin
  @PostMapping("/solicita_recurso_dois")
  public String solicitaRecurso_2(@RequestBody String client_id) {
    if (clienteComRecurso_2 == client_id) {
      return RECURSO_JA_OBTIDO;
    }

    if (this.recursoDisponível_2) {
      // Tempo para expirar o recurso
      new Timer().schedule(new TimerTask() {
        @Override
        public void run() {
          try {
            // liberarRecursoTimeout_1(client_id);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }, TEMPO_LIMITE);
      this.recursoDisponível_2 = false;
      this.clienteComRecurso_2 = client_id;

      return PEGOU_RECURSO_DOIS;
    }
    clientesEmEspera2.add(client_id);
    return RECURSO_DOIS_OCUPADO;
  }

  public String liberarRecursoTimeout_1(String client_id) {
    // Testa se o cliente não liberou o recurso antes
    if (this.clienteComRecurso_1 != client_id)
      return STRING_VAZIA;

    if (this.clienteComRecurso_1 != null) {
      this.clienteComRecurso_1 = null;
      this.recursoDisponível_1 = true;
      // return RECURSO_UM_EXPIRADO;

      if (clientesEmEspera1.size() > 0) {
        this.recursoDisponível_1 = false;
        String proximo_cliente = clientesEmEspera1.get(0);
        this.clienteComRecurso_1 = proximo_cliente;
        clientesEmEspera1.remove(0);

        // proximo_cliente.notificar(RECURSO_UM_LIVRE);

        new Timer().schedule(new TimerTask() {
          @Override
          public void run() {
            try {
              liberarRecursoTimeout_1(client_id);
            } catch (Exception e) {
              e.printStackTrace();
            }
          }
        }, TEMPO_LIMITE);
      }
    }
    return RECURSO_UM_LIBERADO;
  }

  public String liberarRecursoTimeout_2(String client_id) {
    // Testa se o cliente não liberou o recurso antes
    if (this.clienteComRecurso_2 != client_id)
      return STRING_VAZIA;

    if (this.clienteComRecurso_2 != null) {
      this.clienteComRecurso_2 = null;
      this.recursoDisponível_2 = true;
      // return RECURSO_DOIS_EXPIRADO;

      if (clientesEmEspera2.size() > 0) {
        this.recursoDisponível_2 = false;
        String proximo_cliente = clientesEmEspera1.get(0);
        this.clienteComRecurso_2 = proximo_cliente;
        clientesEmEspera2.remove(0);

        // proximo_cliente.notificar(RECURSO_DOIS_LIVRE);

        new Timer().schedule(new TimerTask() {
          @Override
          public void run() {
            try {
              liberarRecursoTimeout_2(client_id);
            } catch (Exception e) {
              e.printStackTrace();
            }
          }
        }, TEMPO_LIMITE);
      }
    }
    return RECURSO_DOIS_LIBERADO;
  }

  private void sendInitEvent(SseEmitter sseEmitter) {
    try {
      sseEmitter.send(SseEmitter.event().name("INIT"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @PostMapping("/libera_recurso_um")
  public String liberaRecurso_1(@RequestBody String client_id) {
    System.out
        .println(
            "O cliente " + client_id + " efetuou a liberação do recurso 1");
    if (client_id != this.clienteComRecurso_1) {
      return RECURSO_UM_INVALIDO;
    }

    if (clientesEmEspera1.size() > 0) {
      String proximo_cliente = clientesEmEspera1.get(0);
      this.clienteComRecurso_1 = proximo_cliente;
      clientesEmEspera1.remove(0);
      dispatchEventToClients("Titulo yooo", RECURSO_UM_LIVRE, client_id);

      // proximo_cliente.notificar(RECURSO_UM_LIVRE);
      new Timer().schedule(new TimerTask() {
        @Override
        public void run() {
          try {
            liberarRecursoTimeout_1(proximo_cliente);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }, TEMPO_LIMITE);
      return RECURSO_UM_LIBERADO;
    }
    this.recursoDisponível_1 = true;
    this.clienteComRecurso_1 = null;
    return RECURSO_UM_LIBERADO;
  }

  @PostMapping("/libera_recurso_dois")
  public String liberaRecurso_2(@RequestBody String client_id) {
    System.out
        .println(
            "O cliente " + client_id + " efetuou a liberação do recurso 2");
    if (client_id != this.clienteComRecurso_2) {
      return RECURSO_DOIS_INVALIDO;
    }

    if (clientesEmEspera2.size() > 0) {
      String proximo_cliente = clientesEmEspera2.get(0);
      this.clienteComRecurso_2 = proximo_cliente;
      clientesEmEspera2.remove(0);
      dispatchEventToClients("Titulo yooo", RECURSO_DOIS_LIVRE, client_id);
      // proximo_cliente.notificar(RECURSO_DOIS_LIVRE);
      new Timer().schedule(new TimerTask() {
        @Override
        public void run() {
          try {
            liberarRecursoTimeout_2(proximo_cliente);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }, TEMPO_LIMITE);
      return RECURSO_DOIS_LIBERADO;
    }
    this.recursoDisponível_2 = true;
    this.clienteComRecurso_2 = null;
    return RECURSO_DOIS_LIBERADO;
  }

  // Método para a inscrição do cliente
  @CrossOrigin
  @RequestMapping(value = "/subscribe", consumes = MediaType.ALL_VALUE)
  public SseEmitter subscribe(@RequestParam String client_id) {
    SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);

    System.out.println("O cliente " + client_id + " se inscreveu");

    sendInitEvent(sseEmitter);

    emitters.put(client_id, sseEmitter);
    sseEmitter.onCompletion(() -> {
      emitters.remove(client_id);
    });
    sseEmitter.onTimeout(() -> {
      emitters.remove(client_id);
    });
    sseEmitter.onError((e) -> {
      emitters.remove(client_id);
    });

    return sseEmitter;
  }

  // Método para o dispatch do evento para cliente específico
  @PostMapping(value = "/dispatchEvent")
  public void dispatchEventToClients(@RequestParam String title, @RequestParam String text,
      @RequestParam String userId) {
    String eventFormatted = new JSONObject()
        .put("title", title)
        .put("text", text).toString();

    SseEmitter sseEmitter = emitters.get(userId);
    if (sseEmitter != null) {
      try {
        sseEmitter.send(SseEmitter.event().name("ServerEvent").data(eventFormatted));
      } catch (Exception e) {
        emitters.remove(userId);
      }
    }
  }

}
// if (client_id != this.clienteComRecurso_1) {
// System.out
// .println(
// "O cliente " + client_id + " registrou interesse no recurso " + numRecurso);

// if (numRecurso == 1)
// return solicitaRecurso_1(client_id);

// }
// if (client_id != this.clienteComRecurso_2) {
// if (numRecurso == 2)
// return solicitaRecurso_2(client_id);
// }
// return RECURSO_JA_OBTIDO;