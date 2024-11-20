package com.example.Banco_Magalu.service;

import com.example.Banco_Magalu.entity.ContaCorrente;
import com.example.Banco_Magalu.entity.TipoTransacao;
import com.example.Banco_Magalu.entity.Transacao;
import com.example.Banco_Magalu.repository.TransacaoRepository;
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
    public Transacao realizarTransferencia(String numeroContaOrigem, String numeroContaDestino, BigDecimal valor) {
        // Validação do valor
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor da transferência deve ser maior que zero.");
        }

        // Busca as contas de origem e destino
        ContaCorrente contaOrigem = contaCorrenteService.buscarConta(numeroContaOrigem)
                .orElseThrow(() -> new IllegalArgumentException("Conta de origem não encontrada."));
        ContaCorrente contaDestino = contaCorrenteService.buscarConta(numeroContaDestino)
                .orElseThrow(() -> new IllegalArgumentException("Conta de destino não encontrada."));

        // Verifica saldo suficiente na conta de origem
        if (contaOrigem.getSaldo().compareTo(valor) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente na conta de origem.");
        }

        // Atualiza o saldo das contas
        contaOrigem.setSaldo(contaOrigem.getSaldo().subtract(valor));
        contaDestino.setSaldo(contaDestino.getSaldo().add(valor));

        // Salva as atualizações nas contas
        contaCorrenteService.atualizarConta(contaOrigem);
        contaCorrenteService.atualizarConta(contaDestino);

        // Cria a transação e salva
        Transacao transacao = new Transacao();
        transacao.setTipo(TipoTransacao.TRANSFERENCIA);  // Usando a constante diretamente
        transacao.setValor(valor);
        transacao.setData(java.time.LocalDate.now());
        transacao.setDescricao("Transferência de " + valor + " para conta " + numeroContaDestino);
        transacao.setContaCorrente(contaOrigem);  // Conta origem

        // Salva a transação no banco
        transacaoRepository.save(transacao);

        // Registra o log de auditoria
        String logMensagem = "Transferência de " + valor + " realizada de conta " + numeroContaOrigem + " para conta " + numeroContaDestino;
        auditoriaService.save(logMensagem);  // Salva a mensagem de auditoria

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
