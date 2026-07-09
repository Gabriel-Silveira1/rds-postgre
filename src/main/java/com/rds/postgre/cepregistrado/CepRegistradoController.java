package com.rds.postgre.cepregistrado;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ceps-registrados")
public class CepRegistradoController {

    private final CepRegistradoRepository repository;

    public CepRegistradoController(CepRegistradoRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<CepRegistradoEntity> listarTodos() {
        return repository.listarTodos();
    }

    @GetMapping("/{cep}")
    public List<CepRegistradoEntity> buscarPorCep(@PathVariable String cep) {
        return repository.buscarPorCep(cep);
    }
}