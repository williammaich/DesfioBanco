package com.example.Banco_Magalu.service;

import com.example.Banco_Magalu.entity.ContaCorrente;
import com.example.Banco_Magalu.entity.Transacao;
import com.example.Banco_Magalu.repository.TransacaoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransacaoService {


    private TransacaoRepository transacaoRepository;

    public TransacaoService(TransacaoRepository transacaoRepository){
        this.transacaoRepository = transacaoRepository;
    }

    /*
     * Método para salvar uma transação
     *
     * @param transacao Objeto da transação a ser salva.
     * @return Objeto da transação salva.
     */
    public Transacao salvar(Transacao transacao) {
        if (transacao == null || transacao.getContaCorrente() == null) {
            throw new IllegalArgumentException("Transação ou conta corrente inválida.");
        }
        return transacaoRepository.save(transacao);
    }

    /*
     * Método para buscar todas as transações
     *
     * @return Lista de transações.
     */
      public List<Transacao> buscarPorConta(ContaCorrente contaCorrente) {

      if (contaCorrente == null || contaCorrente.getNumero() == null) {
          throw new IllegalArgumentException("Conta corrente invalida.");
      }
        return transacaoRepository.findByContaContacorrente(contaCorrente);
      }


}
