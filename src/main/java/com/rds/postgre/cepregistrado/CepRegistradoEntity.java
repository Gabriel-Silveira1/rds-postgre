package com.rds.postgre.cepregistrado;

import java.time.LocalDateTime;

public class CepRegistradoEntity {
    private Long id;
    private String cep;
    private LocalDateTime dataRegistro;

    public CepRegistradoEntity(Long id, String cep, LocalDateTime dataRegistro) {
        this.id = id;
        this.cep = cep;
        this.dataRegistro = dataRegistro;
    }

    public Long getId() { return id; }
    public String getCep() { return cep; }
    public LocalDateTime getDataRegistro() { return dataRegistro; }
}