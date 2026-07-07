package com.rds.postgre.controller;

import com.rds.postgre.entity.ConsultaCep;
import com.rds.postgre.service.ConsultaCepService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ceps")
public class ConsultaCepController {

    private final ConsultaCepService service;

    public ConsultaCepController(ConsultaCepService service) {
        this.service = service;
    }

    // Dispara uma nova consulta ao ViaCEP e salva o resultado no banco
    @PostMapping
    public ResponseEntity<ConsultaCep> consultar(@RequestBody Map<String, String> body) {
        ConsultaCep resultado = service.consultarESalvar(body.get("cep"));
        return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
    }

    // Historico completo de todas as consultas ja feitas
    @GetMapping
    public ResponseEntity<List<ConsultaCep>> listarHistorico() {
        return ResponseEntity.ok(service.listarHistorico());
    }

    // Busca uma consulta especifica pelo id do registro
    @GetMapping("/{id}")
    public ResponseEntity<ConsultaCep> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    // Historico de consultas feitas para um CEP especifico
    @GetMapping("/historico/{cep}")
    public ResponseEntity<List<ConsultaCep>> buscarHistoricoPorCep(@PathVariable String cep) {
        return ResponseEntity.ok(service.buscarHistoricoPorCep(cep));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleErro(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}