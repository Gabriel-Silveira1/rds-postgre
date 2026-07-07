package com.rds.postgre.entity;

import java.time.LocalDateTime;

// POJO simples, sem anotacoes JPA - mapeamento manual via JDBC
public class ConsultaCep {

    private Long id;
    private String cep;
    private String logradouro;
    private String bairro;
    private String localidade;
    private String uf;
    private long tempoRespostaMs;
    private LocalDateTime dataConsulta;

    public ConsultaCep() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getLocalidade() {
        return localidade;
    }

    public void setLocalidade(String localidade) {
        this.localidade = localidade;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public long getTempoRespostaMs() {
        return tempoRespostaMs;
    }

    public void setTempoRespostaMs(long tempoRespostaMs) {
        this.tempoRespostaMs = tempoRespostaMs;
    }

    public LocalDateTime getDataConsulta() {
        return dataConsulta;
    }

    public void setDataConsulta(LocalDateTime dataConsulta) {
        this.dataConsulta = dataConsulta;
    }
}