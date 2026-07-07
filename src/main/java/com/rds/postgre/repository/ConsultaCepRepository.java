package com.rds.postgre.repository;

import com.rds.postgre.entity.ConsultaCep;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class ConsultaCepRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<ConsultaCep> rowMapper = (rs, rowNum) -> {
        ConsultaCep c = new ConsultaCep();
        c.setId(rs.getLong("id"));
        c.setCep(rs.getString("cep"));
        c.setLogradouro(rs.getString("logradouro"));
        c.setBairro(rs.getString("bairro"));
        c.setLocalidade(rs.getString("localidade"));
        c.setUf(rs.getString("uf"));
        c.setTempoRespostaMs(rs.getLong("tempo_resposta_ms"));
        c.setDataConsulta(rs.getTimestamp("data_consulta").toLocalDateTime());
        return c;
    };

    public ConsultaCepRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void criarTabelaSeNaoExistir() {
        String sql = """
                CREATE TABLE IF NOT EXISTS consultas_cep (
                    id SERIAL PRIMARY KEY,
                    cep VARCHAR(8) NOT NULL,
                    logradouro VARCHAR(255),
                    bairro VARCHAR(255),
                    localidade VARCHAR(255),
                    uf VARCHAR(2),
                    tempo_resposta_ms BIGINT NOT NULL,
                    data_consulta TIMESTAMP NOT NULL
                )
                """;
        jdbcTemplate.execute(sql);
    }

    // CREATE - salva o resultado de uma consulta (requisicao + resposta + tempo)
    public ConsultaCep salvar(ConsultaCep consulta) {
        String sql = """
                INSERT INTO consultas_cep
                (cep, logradouro, bairro, localidade, uf, tempo_resposta_ms, data_consulta)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, consulta.getCep());
            ps.setString(2, consulta.getLogradouro());
            ps.setString(3, consulta.getBairro());
            ps.setString(4, consulta.getLocalidade());
            ps.setString(5, consulta.getUf());
            ps.setLong(6, consulta.getTempoRespostaMs());
            ps.setTimestamp(7, Timestamp.valueOf(consulta.getDataConsulta()));
            return ps;
        }, keyHolder);

        consulta.setId(keyHolder.getKey().longValue());
        return consulta;
    }

    // READ - historico completo, mais recentes primeiro
    public List<ConsultaCep> listarTodas() {
        String sql = "SELECT * FROM consultas_cep ORDER BY data_consulta DESC";
        return jdbcTemplate.query(sql, rowMapper);
    }

    // READ - busca por id
    public ConsultaCep buscarPorId(Long id) {
        String sql = "SELECT * FROM consultas_cep WHERE id = ?";
        List<ConsultaCep> resultado = jdbcTemplate.query(sql, rowMapper, id);
        return resultado.isEmpty() ? null : resultado.get(0);
    }

    // READ - historico de consultas de um CEP especifico
    public List<ConsultaCep> buscarPorCep(String cep) {
        String sql = "SELECT * FROM consultas_cep WHERE cep = ? ORDER BY data_consulta DESC";
        return jdbcTemplate.query(sql, rowMapper, cep);
    }
}