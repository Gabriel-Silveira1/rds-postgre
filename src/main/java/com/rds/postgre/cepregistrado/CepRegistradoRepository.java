package com.rds.postgre.cepregistrado;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class CepRegistradoRepository {

    private final JdbcTemplate jdbcTemplate;

    public CepRegistradoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void salvar(String cep) {
        String sql = "INSERT INTO cep_registrado (cep, data_registro) VALUES (?, ?)";
        jdbcTemplate.update(sql, cep, LocalDateTime.now());
    }

    public List<CepRegistradoEntity> listarTodos() {
        String sql = "SELECT id, cep, data_registro FROM cep_registrado ORDER BY data_registro DESC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new CepRegistradoEntity(
                rs.getLong("id"),
                rs.getString("cep"),
                rs.getTimestamp("data_registro").toLocalDateTime()
        ));
    }

    public List<CepRegistradoEntity> buscarPorCep(String cep) {
        String sql = "SELECT id, cep, data_registro FROM cep_registrado WHERE cep = ? ORDER BY data_registro DESC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new CepRegistradoEntity(
                rs.getLong("id"),
                rs.getString("cep"),
                rs.getTimestamp("data_registro").toLocalDateTime()
        ), cep);
    }
}