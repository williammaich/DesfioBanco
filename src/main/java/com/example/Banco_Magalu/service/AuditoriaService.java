package com.example.Banco_Magalu.service;

import com.example.Banco_Magalu.repository.AuditoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuditoriaService {

    @Autowired
    private AuditoriaRepository auditoriaRepository;

    public void save(String mensagem) {
        auditoriaRepository.save(mensagem);
    }

    public void deleteAll() {
        auditoriaRepository.deleteAll();
    }

    public Iterable<String> findAll() {
        return auditoriaRepository.findAll();
    }

}
