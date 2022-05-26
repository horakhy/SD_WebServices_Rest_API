// package com.webserviceproject;

// public class FirstControllerServ {

//   private final String PEGOU_RECURSO_UM = "Você pegou o recurso 1";
//   private final String PEGOU_RECURSO_DOIS = "Você pegou o recurso 2";
//   private final String RECURSO_UM_OCUPADO = "O recurso 1 está ocupado, você foi adicionado na fila de espera";
//   private final String RECURSO_DOIS_OCUPADO = "O recurso 2 está ocupado, você foi adicionado na fila de espera";

//   private final String RECURSO_UM_INVALIDO = "Você não está utilizando o recurso 1";
//   private final String RECURSO_DOIS_INVALIDO = "Você não está utilizando o recurso 2";
//   private final String RECURSO_UM_LIBERADO = "Você liberou o recurso 1";
//   private final String RECURSO_DOIS_LIBERADO = "Você liberou o recurso 2";
//   private final String RECURSO_UM_EXPIRADO = "O recurso 1 expirou\n";
//   private final String RECURSO_DOIS_EXPIRADO = "O recurso 2 expirou\n";

//   private final String RECURSO_UM_LIVRE = "O recurso 1 está livre, você pode utilizá-lo";
//   private final String RECURSO_DOIS_LIVRE = "O recurso 2 está livre, você pode utilizá-lo";

//   private final String RECURSO_JA_OBTIDO = "Você já possui esse recurso";
//   private final String STRING_VAZIA = "";

//   private long clienteComRecurso_1;
//   private long clienteComRecurso_2;
//   private ArrayList<long> clientesEmEspera1;
//   private ArrayList<long> clientesEmEspera2;
//   private ArrayList<long> clientesQueJáPediramRecurso;
//   private boolean recursoDisponível_1 = true;
//   private boolean recursoDisponível_2 = true;

//   private static final int TEMPO_LIMITE = 5000; // Tempo em milisegundos

// public ServImpl() throws RemoteException {
//   super();
//   this.clientesEmEspera1 = new ArrayList<InterfaceCli>();
//   this.clientesEmEspera2 = new ArrayList<InterfaceCli>();
//   this.clientesQueJáPediramRecurso = new ArrayList<InterfaceCli>();
//   this.clienteComRecurso_1 = -1;
//   this.clienteComRecurso_2 = -1;

// }

//   public void liberarRecursoTimeout_1(InterfaceCli referenciaCliente)
//       throws InvalidKeyException, NoSuchAlgorithmException, SignatureException, RemoteException {
//     // Testa se o cliente não liberou o recurso antes
//     if (this.clienteComRecurso_1 != referenciaCliente.getId())
//       return;

//     byte[] assinatura = this.geraAssinatura(RECURSO_UM_EXPIRADO);
//     if (this.clienteComRecurso_1 != -1) {
//       this.clienteComRecurso_1 = -1;
//       this.recursoDisponível_1 = true;
//       referenciaCliente.notificar(RECURSO_UM_EXPIRADO, assinatura);

//       if (clientesEmEspera1.size() > 0) {
//         this.recursoDisponível_1 = false;
//         byte[] assinatura_2 = this.geraAssinatura(RECURSO_UM_LIVRE);
//         InterfaceCli cliente = clientesEmEspera1.get(0);
//         this.clienteComRecurso_1 = cliente.getId();
//         clientesEmEspera1.remove(0);
//         cliente.notificar(RECURSO_UM_LIVRE, assinatura_2);

//         new Timer().schedule(new TimerTask() {
//           @Override
//           public void run() {
//             try {
//               liberarRecursoTimeout_1(cliente);
//             } catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException
//                 | RemoteException e) {
//               e.printStackTrace();
//             }
//           }
//         }, TEMPO_LIMITE);
//       }
//     }
//   }

//   public void liberarRecursoTimeout_2(long referenciaCliente) {
//     // Testa se o cliente não liberou o recurso antes
//     if (this.clienteComRecurso_2 != referenciaCliente.getId())
//       return;

//     byte[] assinatura = this.geraAssinatura(RECURSO_DOIS_EXPIRADO);

//     if (this.clienteComRecurso_2 != -1) {
//       this.clienteComRecurso_2 = -1;
//       this.recursoDisponível_2 = true;
//       // referenciaCliente.notificar(RECURSO_DOIS_EXPIRADO, assinatura);

//       if (clientesEmEspera2.size() > 0) {
//         this.recursoDisponível_2 = false;
//         byte[] assinatura_2 = this.geraAssinatura(RECURSO_DOIS_LIVRE);
//         InterfaceCli cliente = clientesEmEspera2.get(0);
//         this.clienteComRecurso_2 = cliente.getId();
//         clientesEmEspera2.remove(0);
//         cliente.notificar(RECURSO_DOIS_LIVRE, assinatura_2);
//         new Timer().schedule(new TimerTask() {
//           @Override
//           public void run() {
//             try {
//               liberarRecursoTimeout_2(cliente);
//             } catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException
//                 | RemoteException e) {
//               e.printStackTrace();
//             }
//           }
//         }, TEMPO_LIMITE);
//       }
//     }
//   }

//   public String processaPedido_1(String text, InterfaceCli referenciaCliente)
//       throws RemoteException, InvalidKeyException, NoSuchAlgorithmException, SignatureException {
//     if (this.recursoDisponível_1) {
//       // Tempo para expirar o recurso
//       new Timer().schedule(new TimerTask() {
//         @Override
//         public void run() {
//           try {
//             liberarRecursoTimeout_1(referenciaCliente);
//           } catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException | RemoteException e) {
//             e.printStackTrace();
//           }
//         }
//       }, TEMPO_LIMITE);
//       this.recursoDisponível_1 = false;
//       this.clienteComRecurso_1 = referenciaCliente.getId();

//       // Cliente pediu recurso pela primeira vez
//       if (!clientesQueJáPediramRecurso.contains(referenciaCliente)) {
//         clientesQueJáPediramRecurso.add(referenciaCliente);
//         referenciaCliente.setChavePublicaServidor(this.chavePublica);
//       }

//       // Cliente pediu recurso mais de uma vez
//       return PEGOU_RECURSO_UM;
//     }
//     /*
//      * Cliente não pegou o recurso, então deve ser notificado que o recurso está
//      * ocupado
//      */

//     byte[] assinatura = this.geraAssinatura(RECURSO_UM_OCUPADO);

//     // Cliente pediu recurso pela primeira vez
//     if (!clientesQueJáPediramRecurso.contains(referenciaCliente)) {
//       clientesQueJáPediramRecurso.add(referenciaCliente);
//       referenciaCliente.setChavePublicaServidor(this.chavePublica);
//     }

//     // Cliente pediu recurso mais de uma vez
//     // Adiciona na fila de espera
//     clientesEmEspera1.add(referenciaCliente);
//     referenciaCliente.notificar(RECURSO_UM_OCUPADO, assinatura);
//     return STRING_VAZIA;
//   }

//   public String processaPedido_2(String text, InterfaceCli referenciaCliente)
//       throws RemoteException, InvalidKeyException, NoSuchAlgorithmException, SignatureException {
//     if (this.recursoDisponível_2) {
//       // Tempo para expirar o recurso
//       new Timer().schedule(new TimerTask() {
//         @Override
//         public void run() {
//           try {
//             liberarRecursoTimeout_2(referenciaCliente);
//           } catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException | RemoteException e) {
//             e.printStackTrace();
//           }
//         }
//       }, TEMPO_LIMITE);
//       this.recursoDisponível_2 = false;
//       this.clienteComRecurso_2 = referenciaCliente.getId();

//       // Cliente pediu recurso pela primeira vez
//       if (!clientesQueJáPediramRecurso.contains(referenciaCliente)) {
//         clientesQueJáPediramRecurso.add(referenciaCliente);
//         referenciaCliente.setChavePublicaServidor(this.chavePublica);
//       }
//       // Cliente pediu recurso mais de uma vez
//       return PEGOU_RECURSO_DOIS;
//     }

//     /*
//      * Cliente não pegou o recurso, então deve ser notificado que o recurso está
//      * ocupado
//      */

//     byte[] assinatura = this.geraAssinatura(RECURSO_DOIS_OCUPADO);

//     // Cliente pediu recurso pela primeira vez
//     if (!clientesQueJáPediramRecurso.contains(referenciaCliente)) {
//       clientesQueJáPediramRecurso.add(referenciaCliente);
//       referenciaCliente.setChavePublicaServidor(this.chavePublica);
//     }

//     // Cliente pediu recurso mais de uma vez
//     // Adiciona na fila de espera
//     clientesEmEspera2.add(referenciaCliente);
//     referenciaCliente.notificar(RECURSO_DOIS_OCUPADO, assinatura);
//     return STRING_VAZIA;
//   }

//   @Override
//   public String registrarInteresse(String text, InterfaceCli referenciaCliente, int numRecurso)
//       throws RemoteException, InvalidKeyException, NoSuchAlgorithmException, SignatureException {

//     if (referenciaCliente.getId() != this.clienteComRecurso_1) {
//       System.out
//           .println(
//               "O cliente " + referenciaCliente.getId() + " registrou interesse no recurso " + numRecurso);

//       if (numRecurso == 1)
//         return processaPedido_1(text, referenciaCliente);

//     }
//     if (referenciaCliente.getId() != this.clienteComRecurso_2) {
//       if (numRecurso == 2)
//         return processaPedido_2(text, referenciaCliente);
//     }
//     return RECURSO_JA_OBTIDO;
//   }

//   @Override
//   public String registrarLiberacao(String text, long idCliente, int numRecurso)
//       throws RemoteException, RemoteException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {

//     System.out
//         .println(
//             "O cliente " + idCliente + " efetuou a liberação do recurso " + numRecurso);
//     if (numRecurso == 1) {
//       if (idCliente != this.clienteComRecurso_1) {
//         return RECURSO_UM_INVALIDO;
//       }

//       if (clientesEmEspera1.size() > 0) {
//         byte[] assinatura = this.geraAssinatura(RECURSO_UM_LIVRE);
//         InterfaceCli cliente = clientesEmEspera1.get(0);
//         this.clienteComRecurso_1 = cliente.getId();
//         clientesEmEspera1.remove(0);
//         cliente.notificar(RECURSO_UM_LIVRE, assinatura);
//         new Timer().schedule(new TimerTask() {
//           @Override
//           public void run() {
//             try {
//               liberarRecursoTimeout_1(cliente);
//             } catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException
//                 | RemoteException e) {
//               e.printStackTrace();
//             }
//           }
//         }, TEMPO_LIMITE);
//         return RECURSO_UM_LIBERADO;
//       }
//       this.recursoDisponível_1 = true;
//       this.clienteComRecurso_1 = -1;
//       return RECURSO_UM_LIBERADO;
//     }

//     if (numRecurso == 2) {
//       if (idCliente != this.clienteComRecurso_2) {
//         return RECURSO_DOIS_INVALIDO;
//       }

//       if (clientesEmEspera2.size() > 0) {
//         byte[] assinatura = this.geraAssinatura(RECURSO_DOIS_LIVRE);
//         InterfaceCli cliente = clientesEmEspera2.get(0);
//         this.clienteComRecurso_2 = cliente.getId();
//         clientesEmEspera2.remove(0);
//         cliente.notificar(RECURSO_DOIS_LIVRE, assinatura);
//         new Timer().schedule(new TimerTask() {
//           @Override
//           public void run() {
//             try {
//               liberarRecursoTimeout_2(cliente);
//             } catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException
//                 | RemoteException e) {
//               e.printStackTrace();
//             }
//           }
//         }, TEMPO_LIMITE);
//         return RECURSO_DOIS_LIBERADO;
//       }
//       this.recursoDisponível_2 = true;
//       this.clienteComRecurso_2 = -1;
//       return RECURSO_DOIS_LIBERADO;
//     }
//     return STRING_VAZIA;
//   }
// }
