package com.example.Banco_Magalu.service;

import com.example.Banco_Magalu.entity.Auditoria;
import com.example.Banco_Magalu.repository.AuditoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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
    * Este método lista mensagens por conta do usuário.
    *
    * @param usuario - Usuário que deseja listar as mensagens de auditoria.
    * @return - Lista de mensagens de auditoria do usuário.
     */
    public List<String> buscarMensagensPorConta(String numero) {
        // Recupera as auditorias associadas à conta
        List<Auditoria> auditorias = auditoriaRepository.findByTransacao_ContaCorrente_Numero(numero);

        // Retorna uma lista de mensagens extraídas das auditorias
        return auditorias.stream()
                .map(Auditoria::getMensagem)
                .collect(Collectors.toList());
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
