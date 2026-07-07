package com.rds.postgre.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// DTO so para desserializar o JSON retornado pela API do ViaCEP
@JsonIgnoreProperties(ignoreUnknown = true)
public class ViaCepResponse {

    public String cep;
    public String logradouro;
    public String bairro;
    public String localidade;
    public String uf;
}