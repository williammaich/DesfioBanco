package com.example.Banco_Magalu.ServiceTest;

import com.example.Banco_Magalu.entity.ContaCorrente;
import com.example.Banco_Magalu.entity.TipoTransacao;
import com.example.Banco_Magalu.entity.Transacao;
import com.example.Banco_Magalu.repository.ContaCorrenteRepository;
import com.example.Banco_Magalu.repository.TransacaoRepository;
import com.example.Banco_Magalu.service.ContaCorrenteService;
import com.example.Banco_Magalu.service.TransacaoService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@Transactional
public class TransacaoServiceTest {

    @Autowired
    private TransacaoService transacaoService;

    @Autowired
    private TransacaoRepository transacaoRepository;

    @Autowired
    private ContaCorrenteService contaCorrenteService;

    @Autowired
    private ContaCorrenteRepository contaCorrenteRepository;

    /**
     * Teste para deposito em conta e no limite de credito
     */
    @Test
    public void testDepositoContaElimiteCredito() {
        ContaCorrente conta = new ContaCorrente(
                "12345",
                BigDecimal.valueOf(1000),//saldo inicial
                BigDecimal.valueOf(300),//limite atual
                LocalDate.now(),
                BigDecimal.valueOf(500), //limite maximo
                null);
        contaCorrenteRepository.save(conta);

        BigDecimal valorDeposito = BigDecimal.valueOf(400);
       Transacao tr =  transacaoService.realizarDeposito(conta.getNumero(), valorDeposito);
        ContaCorrente contaRecuperada = contaCorrenteRepository.findById(conta.getNumero()).orElseThrow();

        assertEquals(BigDecimal.valueOf(500), contaRecuperada.getLimiteCredito(), "O limite de credito deve ser atualizado com o valor restante do depósito.");
        assertEquals(BigDecimal.valueOf(1200), contaRecuperada.getSaldo(), "O saldo deve ser incrementado com o valor restante do depósito.");
        assertNotNull(tr, "A transação não deve ser nula.");
        assertEquals(TipoTransacao.DEPOSITO, tr.getTipo(), "O tipo da transação deve ser DEPOSITO.");
        assertEquals(BigDecimal.valueOf(500), contaRecuperada.getLimiteMaximo());

    }

    /**
     * Teste para saque em conta.
     */
    @Test
    public void testSaqueConta() {
        ContaCorrente conta = new ContaCorrente(
                "123456",
                BigDecimal.valueOf(1000), // Saldo inicial
                BigDecimal.valueOf(300),  // Limite de crédito usado
                LocalDate.now(),
                BigDecimal.valueOf(500),  // Limite de crédito total
                null);
        contaCorrenteRepository.save(conta);

        BigDecimal valorSaque = BigDecimal.valueOf(100); // Excede o saldo, mas é coberto pelo limite
        Transacao transacaoSaque = transacaoService.realizarSaque(
                conta.getNumero(),
                valorSaque);

        ContaCorrente contaAtualizada = contaCorrenteRepository.findById(conta.getNumero())
                .orElseThrow(() -> new RuntimeException("Conta não encontrada após o saque"));

        // Verificações
        assertEquals(0, contaAtualizada.getSaldo().compareTo(BigDecimal.valueOf(898)), "O saldo deve ser decrementado com o valor do saque.");
        assertEquals(BigDecimal.valueOf(300), contaAtualizada.getLimiteCredito(),
                "O limite de crédito não deve ser alterado.");
        assertNotNull(transacaoSaque, "A transação não deve ser nula.");
        assertEquals(TipoTransacao.SAQUE, transacaoSaque.getTipo(), "O tipo da transação deve ser SAQUE.");
        assertEquals(valorSaque, transacaoSaque.getValor(), "O valor da transação deve corresponder ao saque realizado.");
    }

    /**
     * Teste de saque de conta do saldo mais limite de credito.
     */
    @Test
    public void testSaqueContaSaldoLimiteCredito() {
        ContaCorrente conta = new ContaCorrente(
                "1234567",
                BigDecimal.valueOf(1000), // Saldo inicial
                BigDecimal.valueOf(100),  // Limite de crédito usado
                LocalDate.now(),
                BigDecimal.valueOf(600),  // Limite de crédito total
                null);
        contaCorrenteRepository.save(conta);

        BigDecimal valorSaque = BigDecimal.valueOf(1100); // Saque do saldo e limite de credito 1000 do saldo e 100 do limite
        Transacao transacaoSaque = transacaoService.realizarSaque(
                conta.getNumero(),valorSaque);

        ContaCorrente contaAtualizada = contaCorrenteRepository.findById(conta.getNumero())
                .orElseThrow(() -> new RuntimeException("Conta não encontrada após o saque"));

        assertEquals(BigDecimal.ZERO, contaAtualizada.getSaldo(), "O saldo deve ser zerado após o saque.");
        assertEquals(BigDecimal.valueOf(500.1),contaAtualizada.getLimiteCredito(),"O limite de crédito deve ser atualizado com o valor do saque.");
        assertNotNull(transacaoSaque, "A transação não deve ser nula.");
        assertEquals(TipoTransacao.SAQUE, transacaoSaque.getTipo(), "O tipo da transação deve ser SAQUE.");
        assertEquals(valorSaque, transacaoSaque.getValor(), "O valor da transação deve corresponder ao saque realizado.");
    }


}

