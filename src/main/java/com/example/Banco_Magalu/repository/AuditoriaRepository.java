package com.example.Banco_Magalu.repository;

import com.example.Banco_Magalu.entity.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuditoriaRepository extends JpaRepository<Auditoria, UUID> {
}
