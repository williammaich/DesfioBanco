package com.example.Banco_Magalu.repository;

import com.example.Banco_Magalu.entity.ContaCorrente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContaCorrenteRepository extends JpaRepository<ContaCorrente, String> {

    ContaCorrente findByNumero(String numero);
}
