package com.rds.postgre.sqs.controller;

import com.rds.postgre.sqs.service.SqsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sqs")
public class SqsController {

    private final SqsService sqsService;

    public SqsController(SqsService sqsService) {
        this.sqsService = sqsService;
    }

    @PostMapping("/enviar")
    public ResponseEntity<String> enviar(@RequestBody String mensagem) {
        String messageId = sqsService.enviarMensagem(mensagem);
        return ResponseEntity.ok("Enviado direto pra fila! MessageId: " + messageId);
    }

    @GetMapping("/visualizar")
    public ResponseEntity<List<String>> visualizar() {
        return ResponseEntity.ok(sqsService.visualizarMensagens());
    }

    @GetMapping("/contagem")
    public ResponseEntity<Map<String, Integer>> contagem() {
        return ResponseEntity.ok(Map.of("mensagensNaFila", sqsService.contarMensagens()));
    }

    @DeleteMapping("/limpar")
    public ResponseEntity<String> limpar() {
        sqsService.limparFila();
        return ResponseEntity.ok("Fila limpa com sucesso");
    }

    @GetMapping("/visualizar-detalhado")
    public ResponseEntity<List<Map<String, String>>> visualizarDetalhado() {
        return ResponseEntity.ok(sqsService.visualizarComHandle());
    }

    @DeleteMapping("/mensagem")
    public ResponseEntity<String> removerMensagem(@RequestBody String receiptHandle) {
        sqsService.removerMensagem(receiptHandle);
        return ResponseEntity.ok("Mensagem removida");
    }

    @PostMapping("/chamar-proximo")
    public ResponseEntity<Map<String, String>> chamarProximo() {
        return ResponseEntity.ok(sqsService.chamarProximo());
    }
}