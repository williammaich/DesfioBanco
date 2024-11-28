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
import java.text.DecimalFormat;
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
    DecimalFormat df = new DecimalFormat("0.00");

    /**
     * Realiza um depósito na conta.
     * @param numeroConta Número da conta.
     * @param valor Valor do depósito.
     * @return A transação realizada.
     */
    @Transactional
    public Transacao realizarDeposito(String numeroConta, BigDecimal valor) {
        // Verifica se a conta existe
        ContaCorrente conta = contaCorrenteService.buscarConta(numeroConta)
                .orElseThrow(() -> new ContaNaoEncontradaException("Conta não encontrada: " + numeroConta));

        // Verifica se o valor do depósito é maior que zero
        if (valor == null ||valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor do depósito não pode ser nulo ou negativo");
        }

        // Recupera o limite máximo da conta
        BigDecimal limiteMaximo = conta.getLimiteMaximo(); // Limite máximo definido
        BigDecimal limiteCreditoAtual = conta.getLimiteCredito(); // Limite de crédito atual
        BigDecimal saldoAtual = conta.getSaldo(); // Saldo da conta

        // Verifica quanto do limite ainda está disponível
        BigDecimal limiteDisponivel = limiteMaximo.subtract(limiteCreditoAtual);

        // Inicializa as variáveis para o limite e saldo que serão atualizadas
        BigDecimal valorParaLimite = BigDecimal.ZERO;
        BigDecimal valorParaSaldo = valor;

        // Se o limite disponível for maior que zero, aloca o depósito no limite
        if (limiteDisponivel.compareTo(BigDecimal.ZERO) > 0) {
            valorParaLimite = valor.min(limiteDisponivel); // Aloca o mínimo entre o valor do depósito e o limite disponível
            valorParaSaldo = valor.subtract(valorParaLimite); // O restante vai para o saldo

            // Atualiza o limite de crédito
            conta.setLimiteCredito(limiteCreditoAtual.add(valorParaLimite)); // Atualiza o limite de crédito
            contaCorrenteService.atualizarLimite(conta.getNumero(), conta.getLimiteCredito());
        }

        // Agora, aloca o restante do valor diretamente no saldo, se houver valor restante
        if (valorParaSaldo.compareTo(BigDecimal.ZERO) > 0) {
            conta.setSaldo(saldoAtual.add(valorParaSaldo)); // Atualiza o saldo com o valor restante
            contaCorrenteService.atualizarSaldo(conta.getNumero(), conta.getSaldo());
        }

        // Cria a transação e salva
        Transacao transacao = new Transacao();
        transacao.setTipo(TipoTransacao.DEPOSITO);
        transacao.setValor(valor);
        transacao.setData(java.time.LocalDate.now());
        transacao.setDescricao("Depósito na conta " + numeroConta + " na data de " + java.time.LocalDate.now() +
                ", Restituição de limite de crédito: R$ " + df.format(valorParaLimite) +
                ", Saldo atual: R$ " + df.format(conta.getSaldo()) +
                ". Limite disponível: R$ " + df.format(conta.getLimiteCredito()));
        transacao.setContaCorrente(conta);
        transacaoRepository.save(transacao);

        // Salva o registro do depósito na auditoria
        String logMensagem = "Depósito na conta " + numeroConta + " na data de " + java.time.LocalDate.now() +
                ", Restituição de limite de crédito: R$ " + df.format(valorParaLimite) +
                ", Saldo atual: R$ " + df.format(conta.getSaldo()) +
                ". Limite disponível: R$ " + df.format(conta.getLimiteCredito());
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
        if (valor == null ||valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor do saque não pode ser nulo ou negativo");
        }

        BigDecimal taxaSaque = valor.multiply(BigDecimal.valueOf(0.01));
        BigDecimal valorComTaxa = valor.add(taxaSaque);

        BigDecimal limiteDisponivel = conta.getSaldo().add(conta.getLimiteCredito());

        if (limiteDisponivel.compareTo(valorComTaxa) < 0) {
            throw new SaldoInsuficienteException("Saldo insuficiente na conta: " + numeroConta);
        }

        BigDecimal saldoDisponivel   = conta.getSaldo();
        if(saldoDisponivel.compareTo(valorComTaxa) >= 0){
            conta.setSaldo(saldoDisponivel.subtract(valorComTaxa));
        }else{

            BigDecimal saldoRestante = valorComTaxa.subtract(conta.getSaldo());
            conta.setSaldo(BigDecimal.ZERO);
            if(saldoRestante.compareTo(conta.getLimiteCredito()) > 0){
                throw new SaldoInsuficienteException("O Saque excede o limite de crédito da conta: " + numeroConta);
            }
            conta.setLimiteCredito(conta.getLimiteCredito().subtract(saldoRestante));

        }

        // Salva a atualização do saldo na conta
        contaCorrenteService.atualizarSaldo(conta.getNumero(), conta.getSaldo());
        contaCorrenteService.atualizarLimite(conta.getNumero(), conta.getLimiteCredito());


        // Cria a transação e salva
        Transacao transacao = new Transacao();
        transacao.setTipo(TipoTransacao.SAQUE);
        transacao.setValor(valor);
        transacao.setData(java.time.LocalDate.now());
        transacao.setDescricao("Saque de R$ " + valor + " na conta " + numeroConta +" na data de "+ java.time.LocalDate.now()
                + ". Taxa de saque: R$ " +df.format( taxaSaque)+ ". Limite disponível: R$ "+ df.format( conta.getLimiteCredito()));

        transacao.setContaCorrente(conta);
        transacaoRepository.save(transacao);

        // Salva registro do saque na auditoria
        String logMensagem = "Saque de R$ "+ valor + " realizado na conta "+ numeroConta + ", na data de " + java.time.LocalDate.now()
                + ". Taxa de saque: R$ " +df.format( taxaSaque)+ ". Limite disponível: R$ "+ df.format( conta.getLimiteCredito());
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

            if (valor == null ||valor.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("O valor da transferência não pode ser nulo ou negativo");
            }

            if (numeroContaOrigem.equals(numeroContaDestino)){
                throw new IllegalArgumentException("Conta de origem e destino não podem ser iguais.");
                }

            ContaCorrente contaOrigem = contaCorrenteService.buscarConta(numeroContaOrigem)
                    .orElseThrow(() -> new ContaNaoEncontradaException("Conta de origem não encontrada: " + numeroContaOrigem));

            ContaCorrente contaDestino = contaCorrenteService.buscarConta(numeroContaDestino)
                    .orElseThrow(() -> new ContaNaoEncontradaException("Conta de destino não encontrada: " + numeroContaDestino));


            BigDecimal taxaTransferencia = valor.multiply(BigDecimal.valueOf(0.01));
            BigDecimal valorComTaxa = valor.add(taxaTransferencia);

            BigDecimal limiteDisponivel = contaOrigem.getSaldo().add(contaOrigem.getLimiteCredito());

            if(limiteDisponivel.compareTo(valorComTaxa) < 0){
                throw new SaldoInsuficienteException("Saldo insuficiente na conta de origem: " + numeroContaOrigem);
            }

            // recalcula a taxa caso o limite seja usado
            if(contaOrigem.getLimiteCredito().compareTo(BigDecimal.ZERO) > 0){
                taxaTransferencia = valor.multiply(BigDecimal.valueOf(0.02));
                valorComTaxa = valor.add(taxaTransferencia);
            }

            // Atualiza o saldo e limite da conta de origem
            BigDecimal saldoDisponivel = contaOrigem.getSaldo();
            if(saldoDisponivel.compareTo(valorComTaxa) >= 0){
                contaOrigem.setSaldo(saldoDisponivel.subtract(valorComTaxa));
            }else{
                BigDecimal saldoRestante = valorComTaxa.subtract(saldoDisponivel);
                contaOrigem.setSaldo(BigDecimal.ZERO);
                contaOrigem.setLimiteCredito(contaOrigem.getLimiteCredito().subtract(saldoRestante));
            }

            // Atualiza o saldo da conta de origem e limite se for usado

            contaCorrenteService.atualizarSaldo(contaOrigem.getNumero(), contaOrigem.getSaldo());
            contaCorrenteService.atualizarLimite(contaOrigem.getNumero(), contaOrigem.getLimiteCredito());
            // Atualiza o saldo da conta de destino
           contaDestino.setSaldo(contaDestino.getSaldo().add(valor));
            contaCorrenteService.atualizarSaldo(contaDestino.getNumero(), contaDestino.getSaldo());

            // Cria a transação e salva
            Transacao transacao = new Transacao();
            transacao.setTipo(TipoTransacao.TRANSFERENCIA);
            transacao.setValor(valor);
            transacao.setData(java.time.LocalDate.now());
            transacao.setDescricao("Transferência de R$ " + valor +" para conta " + numeroContaDestino +" na data de "+ java.time.LocalDate.now()+ ". Taxa de transferência: R$ "+ df.format( taxaTransferencia)+" Limite disponível: R$ "+ df.format( contaOrigem.getLimiteCredito()));
            transacao.setContaCorrente(contaOrigem); // Conta origem
            transacaoRepository.save(transacao);

            // Registra a transferência na auditoria
            String logMensagem = "Transferência de R$ " + valor + " para conta " + numeroContaDestino +" na data de "+ java.time.LocalDate.now()+ ". Taxa de transferência: R$ "+ df.format( taxaTransferencia)+"  Limite disponível: R$ "+ df.format( contaOrigem.getLimiteCredito());
            auditoriaService.save(logMensagem, transacao);

            return transacao;
    }

}
