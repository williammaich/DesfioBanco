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
     * Realiza um depósito na conta.
     * @param numeroConta Número da conta.
     * @param valor Valor do depósito.
     * @return A transação realizada.
     */
     @Transactional
     public Transacao realizarDeposito(String numeroConta, BigDecimal valor) {
         //verifica se a conta existe
         ContaCorrente conta = contaCorrenteService.buscarConta(numeroConta)
                 .orElseThrow(() -> new ContaNaoEncontradaException("Conta não encontrada: " + numeroConta));

         //verifica se o valor do depósito é maior que zero
         if (valor.compareTo(BigDecimal.ZERO) <= 0) {
             throw new IllegalArgumentException("Valor do depósito deve ser maior que zero.");
         }

         // Atualiza o saldo da conta
         conta.setSaldo(conta.getSaldo().add(valor));

         // Salva a atualização do saldo na conta
         contaCorrenteService.atualizarSaldo(conta.getNumero(), conta.getSaldo());

         // Cria a transação e salva
         Transacao transacao = new Transacao();
         transacao.setTipo(TipoTransacao.DEPOSITO);
         transacao.setValor(valor);
         transacao.setData(java.time.LocalDate.now());
         transacao.setDescricao("Depósito de " + valor + " na conta " + numeroConta +" na data de "+ java.time.LocalDate.now());
         transacao.setContaCorrente(conta);
         transacaoRepository.save(transacao);

         // Salva registro do depósito na auditoria
         String logMensagem = "Depósito de "+ valor + " realizado na conta "+ numeroConta + ", na data de " + java.time.LocalDate.now();
          auditoriaService.save(logMensagem, transacao);
         return transacao;
     }
    /**
     * Realiza um saque na conta.
     * @param numeroConta Número da conta.
     * @param valor Valor do saque.
     * @return A transação realizada.
     */
    @Transactional
    public Transacao realizarSaque(String numeroConta, BigDecimal valor){
        //verifica se a conta existe
        ContaCorrente conta = contaCorrenteService.buscarConta(numeroConta)
               .orElseThrow(() -> new ContaNaoEncontradaException("Conta não encontrada: " + numeroConta));

        //verifica se o valor do saque é maior que zero
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor do saque deve ser maior que zero.");
        }
        //verifica se o saldo da conta é maior que o valor do saque
        if (conta.getSaldo().compareTo(valor) < 0) {
            throw new SaldoInsuficienteException("Saldo insuficiente na conta: " + numeroConta);
        }

        // Atualiza o saldo da conta
        conta.setSaldo(conta.getSaldo().subtract(valor));

        // Salva a atualização do saldo na conta
        contaCorrenteService.atualizarSaldo(conta.getNumero(), conta.getSaldo());

        // Cria a transação e salva
        Transacao transacao = new Transacao();
        transacao.setTipo(TipoTransacao.SAQUE);
        transacao.setValor(valor);
        transacao.setData(java.time.LocalDate.now());
        transacao.setDescricao("Saque de " + valor + " na conta " + numeroConta +" na data de "+ java.time.LocalDate.now());
        transacao.setContaCorrente(conta);
        transacaoRepository.save(transacao);

        // Salva registro do saque na auditoria
        String logMensagem = "Saque de "+ valor + " realizado na conta "+ numeroConta + ", na data de " + java.time.LocalDate.now();
        auditoriaService.save(logMensagem, transacao);

        return transacao;

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
