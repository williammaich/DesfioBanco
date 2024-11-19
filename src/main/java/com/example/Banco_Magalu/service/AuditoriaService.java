package com.example.Banco_Magalu.service;

import com.example.Banco_Magalu.entity.Auditoria;
import com.example.Banco_Magalu.repository.AuditoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;

    public AuditoriaService(@Autowired AuditoriaRepository auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
    }

    /*
     * Este método salva uma mensagem de auditoria no banco de dados.
     *
     * @param mensagem - Mensagem de auditoria a ser salva.
     */
    public void save(String mensagem) {
        Auditoria auditoria = new Auditoria();
        auditoria.setMensagem(mensagem);
        auditoriaRepository.save(auditoria);
    }


    /*
     * Este método exclui todas as mensagens de auditoria do banco de dados.
     */
    public void deleteAll() {
        auditoriaRepository.deleteAll();
    }

    /*
     * Este método retorna todas as mensagens de auditoria do banco de dados.
     *
     * @return - Lista de mensagens de auditoria.
     */
    public Iterable<String> findAll() {
        return auditoriaRepository.findAll()
                .stream()
                .map(Auditoria::getMensagem)
                .collect(Collectors.toList());
    }

}
