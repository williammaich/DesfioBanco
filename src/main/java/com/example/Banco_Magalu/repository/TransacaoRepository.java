package com.example.Banco_Magalu.repository;

import com.example.Banco_Magalu.entity.ContaCorrente;
import com.example.Banco_Magalu.entity.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TransacaoRepository extends JpaRepository<Transacao, UUID> {
     // metodo para buscar transacoes por conta corrente
    List<Transacao> findByContaCorrente(ContaCorrente contaCorrente);
}
