package com.rds.postgre.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rds.postgre.dto.ViaCepResponse;
import com.rds.postgre.entity.ConsultaCep;
import com.rds.postgre.repository.ConsultaCepRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ConsultaCepService {

    private static final String BASE_URL = "https://viacep.com.br/ws/%s/json/";

    private final ConsultaCepRepository repository;
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public ConsultaCepService(ConsultaCepRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void init() {
        repository.criarTabelaSeNaoExistir();
    }

    // Faz a requisicao ao ViaCEP, mede o tempo de resposta e persiste tudo no banco
    public ConsultaCep consultarESalvar(String cepOriginal) {
        String cep = cepOriginal.replaceAll("\\D", "");
        if (cep.length() != 8) {
            throw new IllegalArgumentException("CEP inválido. Deve conter 8 dígitos.");
        }

        long inicio = System.currentTimeMillis();

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(String.format(BASE_URL, cep)))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            long tempoResposta = System.currentTimeMillis() - inicio;

            ViaCepResponse dados = mapper.readValue(response.body(), ViaCepResponse.class);

            ConsultaCep consulta = new ConsultaCep();
            consulta.setCep(cep);
            consulta.setLogradouro(dados.logradouro);
            consulta.setBairro(dados.bairro);
            consulta.setLocalidade(dados.localidade);
            consulta.setUf(dados.uf);
            consulta.setTempoRespostaMs(tempoResposta);
            consulta.setDataConsulta(LocalDateTime.now());

            return repository.salvar(consulta);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao consultar o CEP: " + e.getMessage(), e);
        }
    }

    public List<ConsultaCep> listarHistorico() {
        return repository.listarTodas();
    }

    public ConsultaCep buscarPorId(Long id) {
        ConsultaCep consulta = repository.buscarPorId(id);
        if (consulta == null) {
            throw new IllegalArgumentException("Consulta não encontrada: " + id);
        }
        return consulta;
    }

    public List<ConsultaCep> buscarHistoricoPorCep(String cep) {
        return repository.buscarPorCep(cep.replaceAll("\\D", ""));
    }
}