package com.example.Banco_Magalu.repository;

import com.example.Banco_Magalu.entity.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AuditoriaRepository extends JpaRepository<Auditoria, UUID> {

    List<Auditoria> findByTransacao_ContaCorrente_Numero(String numero);

}
