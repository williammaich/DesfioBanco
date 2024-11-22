package com.example.Banco_Magalu.service;

import com.example.Banco_Magalu.entity.ContaCorrente;
import com.example.Banco_Magalu.entity.TipoTransacao;
import com.example.Banco_Magalu.entity.Transacao;
import com.example.Banco_Magalu.exception.ContaNaoEncontradaException;
import com.example.Banco_Magalu.exception.SaldoInsuficienteException;
import com.example.Banco_Magalu.repository.TransacaoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.util.List;

@Service
public class TransacaoService {

    private final TransacaoRepository transacaoRepository;
    private final ContaCorrenteService contaCorrenteService;
    private final AuditoriaService auditoriaService;

    // Injeção de dependências via construtor
    public TransacaoService(TransacaoRepository transacaoRepository, ContaCorrenteService contaCorrenteService, AuditoriaService auditoriaService) {
        this.transacaoRepository = transacaoRepository;
        this.contaCorrenteService = contaCorrenteService;
        this.auditoriaService = auditoriaService;
    }

    /**
     * Realiza a transferência de valores entre contas.
     *
     * @param numeroContaOrigem Número da conta de origem.
     * @param numeroContaDestino Número da conta de destino.
     * @param valor Valor a ser transferido.
     * @return Transação realizada.
     */
    @Transactional
    public Transacao realizarTransferencia(String numeroContaOrigem, String numeroContaDestino, BigDecimal valor) {

            if (valor.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Valor da transferência deve ser maior que zero.");
            }

            ContaCorrente contaOrigem = contaCorrenteService.buscarConta(numeroContaOrigem)
                    .orElseThrow(() -> new ContaNaoEncontradaException("Conta de origem não encontrada: " + numeroContaOrigem));

            ContaCorrente contaDestino = contaCorrenteService.buscarConta(numeroContaDestino)
                    .orElseThrow(() -> new ContaNaoEncontradaException("Conta de destino não encontrada: " + numeroContaDestino));

            if (contaOrigem.getSaldo().compareTo(valor) < 0) {
                throw new SaldoInsuficienteException("Saldo insuficiente na conta de origem: " + numeroContaOrigem);
            }

            // Atualiza os saldos das contas
            contaOrigem.setSaldo(contaOrigem.getSaldo().subtract(valor));
            contaDestino.setSaldo(contaDestino.getSaldo().add(valor));

            // Salva as atualizações nas contas
            contaCorrenteService.atualizarSaldo(contaOrigem.getNumero(), contaOrigem.getSaldo());
            contaCorrenteService.atualizarSaldo(contaDestino.getNumero(), contaDestino.getSaldo());

            // Cria a transação e salva
            Transacao transacao = new Transacao();
            transacao.setTipo(TipoTransacao.TRANSFERENCIA);
            transacao.setValor(valor);
            transacao.setData(java.time.LocalDate.now());
            transacao.setDescricao("Transferência de " + valor + " para conta " + numeroContaDestino);
            transacao.setContaCorrente(contaOrigem); // Conta origem
            transacaoRepository.save(transacao);

            // Registra a transferência na auditoria
            String logMensagem = "Transferência de " + valor + " realizada de conta " + numeroContaOrigem + " para conta " + numeroContaDestino;
            auditoriaService.save(logMensagem, transacao);

            return transacao;

    }



    /**
     * Busca todas as transações associadas a uma conta.
     *
     * @param contaCorrente A conta para filtrar as transações.
     * @return Lista de transações da conta.
     */
    public List<Transacao> buscarPorConta(ContaCorrente contaCorrente) {
        if (contaCorrente == null || contaCorrente.getNumero() == null) {
            throw new IllegalArgumentException("Conta corrente inválida.");
        }
        return transacaoRepository.findByContaCorrente(contaCorrente);
    }
}
