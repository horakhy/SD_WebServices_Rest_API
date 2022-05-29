package com.webserviceproject.webservicerest;

import java.io.IOException;
import java.util.*;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import org.json.JSONObject;

@RestController
public class FirstController {

  // Titulos
  private final String TITULO_PEGOU_RECURSO = "Recurso Adquirido";
  private final String TITULO_LIBEROU_RECURSO = "Recurso Liberado";
  private final String TITULO_LIVRE_RECURSO = "Recurso Livre";
  private final String TITULO_OCUPADO_RECURSO = "Recurso Ocupado";

  // Mensagens
  private final String PEGOU_RECURSO_UM = "Você pegou o recurso 1";
  private final String PEGOU_RECURSO_DOIS = "Você pegou o recurso 2";
  private final String RECURSO_UM_OCUPADO = "O recurso 1 está ocupado, você foi adicionado na fila de espera";
  private final String RECURSO_DOIS_OCUPADO = "O recurso 2 está ocupado, você foi adicionado na fila de espera";
  private final String RECURSO_JA_SOLICITADO = "Você já solicitou o recurso";

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
  private boolean recursoDisponível_1 = true;
  private boolean recursoDisponível_2 = true;

  // Cliente em espera para o recurso 1 e 2
  private ArrayList<String> clientesEmEspera1 = new ArrayList<String>();
  private ArrayList<String> clientesEmEspera2 = new ArrayList<String>();

  // Timeout
  private static final int TEMPO_LIMITE = 5000; // Tempo em milisegundos

  // Hashmap [ID cliente, Emissor]
  public Map<String, SseEmitter> emitters = new HashMap<String, SseEmitter>();

  @CrossOrigin
  @PostMapping("/solicita_recurso_um")
  public String solicitaRecurso_1(@RequestParam String client_id) {
    if (client_id.equals(clienteComRecurso_1)) {
      return RECURSO_JA_OBTIDO;
    }

    if (clientesEmEspera1.contains(client_id)) {
      return RECURSO_JA_SOLICITADO;
    }

    if (this.recursoDisponível_1) {

      dispatchEventToClient(TITULO_PEGOU_RECURSO, PEGOU_RECURSO_UM, client_id);

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
      this.recursoDisponível_1 = false;
      this.clienteComRecurso_1 = client_id;

      return PEGOU_RECURSO_UM;
    }
    clientesEmEspera1.add(client_id);

    dispatchEventToClient(TITULO_OCUPADO_RECURSO, RECURSO_UM_OCUPADO, client_id);

    return RECURSO_UM_OCUPADO;
  }

  @CrossOrigin
  @PostMapping("/solicita_recurso_dois")
  public String solicitaRecurso_2(@RequestParam String client_id) {
    if (client_id.equals(clienteComRecurso_2)) {
      return RECURSO_JA_OBTIDO;
    }

    if (clientesEmEspera2.contains(client_id)) {
      return RECURSO_JA_SOLICITADO;
    }

    if (this.recursoDisponível_2) {

      dispatchEventToClient(TITULO_PEGOU_RECURSO, PEGOU_RECURSO_DOIS, client_id);

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
      this.recursoDisponível_2 = false;
      this.clienteComRecurso_2 = client_id;

      return PEGOU_RECURSO_DOIS;
    }
    clientesEmEspera2.add(client_id);

    dispatchEventToClient(TITULO_OCUPADO_RECURSO, RECURSO_DOIS_OCUPADO, client_id);

    return RECURSO_DOIS_OCUPADO;
  }

  @CrossOrigin
  @PostMapping("/libera_recurso_um")
  public String liberaRecurso_1(@RequestParam String client_id) {
    System.out
        .println(
            "O cliente " + client_id + " efetuou a liberação do recurso 1");
    if (!client_id.equals(this.clienteComRecurso_1)) {
      return RECURSO_UM_INVALIDO;
    }

    dispatchEventToClient(TITULO_LIBEROU_RECURSO, RECURSO_UM_LIBERADO, client_id);

    if (clientesEmEspera1.size() > 0) {
      String proximo_cliente = clientesEmEspera1.get(0);
      this.clienteComRecurso_1 = proximo_cliente;
      clientesEmEspera1.remove(0);

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
      dispatchEventToClient(TITULO_LIVRE_RECURSO, RECURSO_UM_LIVRE, proximo_cliente);
      return RECURSO_UM_LIBERADO;
    }
    this.recursoDisponível_1 = true;
    this.clienteComRecurso_1 = null;
    return STRING_VAZIA;
  }

  @CrossOrigin
  @PostMapping("/libera_recurso_dois")
  public String liberaRecurso_2(@RequestParam String client_id) {
    System.out
        .println(
            "O cliente " + client_id + " efetuou a liberação do recurso 2");
    if (!client_id.equals(this.clienteComRecurso_2)) {
      return RECURSO_DOIS_INVALIDO;
    }

    dispatchEventToClient(TITULO_LIBEROU_RECURSO, RECURSO_DOIS_LIBERADO, client_id);

    if (clientesEmEspera2.size() > 0) {
      String proximo_cliente = clientesEmEspera2.get(0);
      this.clienteComRecurso_2 = proximo_cliente;
      clientesEmEspera2.remove(0);

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
      dispatchEventToClient(TITULO_LIVRE_RECURSO, RECURSO_DOIS_LIVRE, proximo_cliente);
      return RECURSO_DOIS_LIBERADO;
    }
    this.recursoDisponível_2 = true;
    this.clienteComRecurso_2 = null;
    return STRING_VAZIA;
  }

  public String liberarRecursoTimeout_1(String client_id) {
    // Testa se o cliente não liberou o recurso antes
    if (this.clienteComRecurso_1 != client_id)
      return STRING_VAZIA;

    if (this.clienteComRecurso_1 != null) {
      this.clienteComRecurso_1 = null;
      this.recursoDisponível_1 = true;

      dispatchEventToClient(TITULO_LIBEROU_RECURSO, RECURSO_UM_EXPIRADO, client_id);

      if (clientesEmEspera1.size() > 0) {
        this.recursoDisponível_1 = false;
        String proximo_cliente = clientesEmEspera1.get(0);
        this.clienteComRecurso_1 = proximo_cliente;
        clientesEmEspera1.remove(0);

        dispatchEventToClient(TITULO_LIVRE_RECURSO, RECURSO_UM_LIVRE, proximo_cliente);
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

      dispatchEventToClient(TITULO_LIBEROU_RECURSO, RECURSO_DOIS_EXPIRADO, client_id);

      if (clientesEmEspera2.size() > 0) {
        this.recursoDisponível_2 = false;
        String proximo_cliente = clientesEmEspera2.get(0);
        this.clienteComRecurso_2 = proximo_cliente;
        clientesEmEspera2.remove(0);

        dispatchEventToClient(TITULO_LIVRE_RECURSO, RECURSO_DOIS_LIVRE, proximo_cliente);
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
  public void dispatchEventToClient(String title, String text, String userId) {
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