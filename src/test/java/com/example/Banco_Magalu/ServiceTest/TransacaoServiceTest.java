package com.example.Banco_Magalu.ServiceTest;

import com.example.Banco_Magalu.entity.ContaCorrente;
import com.example.Banco_Magalu.entity.TipoTransacao;
import com.example.Banco_Magalu.entity.Transacao;
import com.example.Banco_Magalu.exception.SaldoInsuficienteException;
import com.example.Banco_Magalu.repository.ContaCorrenteRepository;
import com.example.Banco_Magalu.repository.TransacaoRepository;
import com.example.Banco_Magalu.service.TransacaoService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
public class TransacaoServiceTest {

    @Autowired
    private TransacaoService transacaoService;

    @Autowired
    private TransacaoRepository transacaoRepository;

    @Autowired
    private ContaCorrenteRepository contaCorrenteRepository;

    /**
     * Teste para deposito em conta e no limite de credito
     */
    @Test
    public void testDepositoContaElimiteCredito() {
        ContaCorrente conta = new ContaCorrente(
                "12345",
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(300),
                LocalDate.now(),
                BigDecimal.valueOf(500),
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
     * Teste para deposito valor nulo
     */
    @Test
    public void testDepositoValorNulo() {
        ContaCorrente conta = new ContaCorrente(
                "12345",
                BigDecimal.valueOf(10),
                BigDecimal.valueOf(300),
                LocalDate.now(),
                BigDecimal.valueOf(500),
                null);
        contaCorrenteRepository.save(conta);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> transacaoService.realizarDeposito(conta.getNumero(), null),
                "Deveria lançar IllegalArgumentException para valor nulo"
        );

        assertEquals("O valor do depósito não pode ser nulo ou negativo", exception.getMessage());

        ContaCorrente contaRecuperada = contaCorrenteRepository.findById(conta.getNumero())
                .orElseThrow();
        assertEquals(BigDecimal.valueOf(10), contaRecuperada.getSaldo(), "O saldo não deve ser alterado");
        assertEquals(BigDecimal.valueOf(300), contaRecuperada.getLimiteCredito(), "O limite de crédito deve ser zero.");

    }

    /**
     * Teste para deposito valor negativo
     */
    @Test
    public void testDepositoValorNegativo() {
        ContaCorrente conta = new ContaCorrente(
                "12345",
                BigDecimal.valueOf(10),
                BigDecimal.valueOf(300),
                LocalDate.now(),
                BigDecimal.valueOf(500),
                null);
        contaCorrenteRepository.save(conta);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> transacaoService.realizarDeposito(conta.getNumero(), BigDecimal.valueOf(-100)),
                "Deveria lançar IllegalArgumentException para valor negativo"
        );

        assertEquals("O valor do depósito não pode ser nulo ou negativo", exception.getMessage());

        ContaCorrente contaRecuperada = contaCorrenteRepository.findById(conta.getNumero())
                .orElseThrow();
        assertEquals(BigDecimal.valueOf(10), contaRecuperada.getSaldo(), "O saldo não deve ser alterado");
        assertEquals(BigDecimal.valueOf(300), contaRecuperada.getLimiteCredito(), "O limite de crédito deve ser zero.");

    }

    /**
     * Teste para saque em conta.
     */
    @Test
    public void testSaqueConta() {
        ContaCorrente conta = new ContaCorrente(
                "123456",
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(300),
                LocalDate.now(),
                BigDecimal.valueOf(500),
                null);
        contaCorrenteRepository.save(conta);

        BigDecimal valorSaque = BigDecimal.valueOf(100);
        Transacao transacaoSaque = transacaoService.realizarSaque(
                conta.getNumero(),
                valorSaque);

        ContaCorrente contaAtualizada = contaCorrenteRepository.findById(conta.getNumero())
                .orElseThrow(() -> new RuntimeException("Conta não encontrada para o saque"));

        // Verificações
        assertEquals(1, contaAtualizada.getSaldo().compareTo(BigDecimal.valueOf(898)), "O saldo deve ser decrementado com o valor do saque.");
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
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(600),
                LocalDate.now(),
                BigDecimal.valueOf(600),
                null);
        contaCorrenteRepository.save(conta);

        BigDecimal valorSaque = BigDecimal.valueOf(1100);
        Transacao transacaoSaque = transacaoService.realizarSaque(
                conta.getNumero(),valorSaque);

        ContaCorrente contaAtualizada = contaCorrenteRepository.findById(conta.getNumero())
                .orElseThrow(() -> new RuntimeException("Conta não encontrada para o saque"));

        assertEquals(BigDecimal.ZERO, contaAtualizada.getSaldo(), "O saldo deve ser zerado após o saque.");
        assertEquals(-1, contaAtualizada.getSaldo().compareTo(BigDecimal.valueOf(389)),"O limite de crédito deve ser atualizado com o valor do saque.");
        assertNotNull(transacaoSaque, "A transação não deve ser nula.");
        assertEquals(TipoTransacao.SAQUE, transacaoSaque.getTipo(), "O tipo da transação deve ser SAQUE.");
        assertEquals(valorSaque, transacaoSaque.getValor(), "O valor da transação deve corresponder ao saque realizado.");
    }

    /**
     * Teste de saque de conta com valor total do saldo e limite de credito.
     */
    @Test
    public void testSaqueContaValorTotalSaldoECredito() {
        ContaCorrente conta = new ContaCorrente(
                "12345678",
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(515),
                LocalDate.now(),
                BigDecimal.valueOf(600),
                null);
        contaCorrenteRepository.save(conta);


        BigDecimal valorSaque = BigDecimal.valueOf(1500);
        Transacao transacaoSaque = transacaoService.realizarSaque(
                conta.getNumero(),
                valorSaque);
        ContaCorrente contaAtualizada = contaCorrenteRepository.findById(conta.getNumero())
                .orElseThrow(() -> new RuntimeException("Conta não encontrada para o saque"));

        assertEquals(BigDecimal.ZERO, contaAtualizada.getSaldo(), "O saldo deve ser zerado após o saque.");
        assertEquals(0, contaAtualizada.getSaldo().compareTo(BigDecimal.ZERO), "O limite de crédito deve ser zerado após o saque.");
        assertNotNull(transacaoSaque, "A transação não deve ser nula.");
        assertEquals(TipoTransacao.SAQUE, transacaoSaque.getTipo(), "O tipo da transação deve ser SAQUE.");
        assertEquals(valorSaque, transacaoSaque.getValor(), "O valor da transação deve corresponder ao saque realizado.");
    }

    /**
    * Teste de saque de conta com valor negativo.
     */
    @Test
    public void testSaqueContaValorNegativo() {
    ContaCorrente conta = new ContaCorrente(
            "12345",
            BigDecimal.valueOf(10),
            BigDecimal.valueOf(300),
            LocalDate.now(),
            BigDecimal.valueOf(500),
            null);
        contaCorrenteRepository.save(conta);


    IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> transacaoService.realizarSaque(conta.getNumero(), BigDecimal.valueOf(-100)),
            "Deveria lançar IllegalArgumentException para valor negativo"
    );


    assertEquals("O valor do saque não pode ser nulo ou negativo", exception.getMessage());


    ContaCorrente contaRecuperada = contaCorrenteRepository.findById(conta.getNumero())
            .orElseThrow();
    assertEquals(BigDecimal.valueOf(10), contaRecuperada.getSaldo(), "O saldo não deve ser alterado");
    assertEquals(BigDecimal.valueOf(300), contaRecuperada.getLimiteCredito(), "O limite de crédito deve ser zero.");

}

/**
 * Teste de saque com valor nulo
 */
@Test
public void testSaqueContaValorNulo() {
    ContaCorrente conta = new ContaCorrente(
            "12345",
            BigDecimal.valueOf(10),
            BigDecimal.valueOf(300),
            LocalDate.now(),
            BigDecimal.valueOf(500),
            null);
    contaCorrenteRepository.save(conta);

    IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> transacaoService.realizarDeposito(conta.getNumero(), null),
            "Deveria lançar IllegalArgumentException para valor nulo"
    );


    assertEquals("O valor do depósito não pode ser nulo ou negativo", exception.getMessage());


    ContaCorrente contaRecuperada = contaCorrenteRepository.findById(conta.getNumero())
            .orElseThrow();
    assertEquals(BigDecimal.valueOf(10), contaRecuperada.getSaldo(), "O saldo não deve ser alterado");
    assertEquals(BigDecimal.valueOf(300), contaRecuperada.getLimiteCredito(), "O limite de crédito deve ser zero.");

}

    /**
     * Teste de saque de conta com valor maior que o saldo e limite de credito.
     */

    @Test
    public void testSaqueContaValorMaiorSaldoECredito() {
        ContaCorrente conta = new ContaCorrente(
                "123456789",
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(400),
                LocalDate.now(),
                BigDecimal.valueOf(600),
                null);
        contaCorrenteRepository.save(conta);

        BigDecimal valorSaque = BigDecimal.valueOf(1500);

        SaldoInsuficienteException exception = assertThrows(
                SaldoInsuficienteException.class,
                () -> transacaoService.realizarSaque(conta.getNumero(), valorSaque),
                "Deveria lançar SaldoInsuficienteException para saque acima do limite"
        );


        Assertions.assertEquals(
                "Saldo insuficiente na conta: " + conta.getNumero(),
                exception.getMessage(),
                "A mensagem da exceção deve corresponder"
        );


        ContaCorrente contaRecuperada = contaCorrenteRepository.findById(conta.getNumero())
                .orElseThrow(() -> new RuntimeException("Conta não encontrada para o saque"));


        Assertions.assertEquals(
                BigDecimal.valueOf(1000),
                contaRecuperada.getSaldo(),
                "O saldo deve permanecer inalterado após o saque inválido"
        );
        Assertions.assertEquals(
                BigDecimal.valueOf(400),
                contaRecuperada.getLimiteCredito(),
                "O limite de crédito deve permanecer inalterado após o saque inválido"
        );


        Assertions.assertFalse(
                transacaoRepository.findAll().isEmpty(),
                "Nenhuma transação deve ser registrada para saque inválido"
        );
    }

    /**
     * Teste de transferencia de conta.
     */
    @Test
    public void testTransferenciaConta() {
        ContaCorrente conta1 = new ContaCorrente(
                "123456",
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(500),
                LocalDate.now(),
                BigDecimal.valueOf(600),
                null);
        contaCorrenteRepository.save(conta1);

        ContaCorrente conta2 = new ContaCorrente(
                "654321",
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(500),
                LocalDate.now(),
                BigDecimal.valueOf(600),
                null);
        contaCorrenteRepository.save(conta2);

        BigDecimal valorTransferencia = BigDecimal.valueOf(500);
        Transacao transacaoTransferencia = transacaoService.realizarTransferencia(
                conta1.getNumero(),
                conta2.getNumero(),
                valorTransferencia);

        ContaCorrente conta1Atualizada = contaCorrenteRepository.findById(conta1.getNumero())
                .orElseThrow(() -> new RuntimeException("Conta não encontrada para a transferencia"));
    ContaCorrente conta2Atualizada = contaCorrenteRepository.findById(conta2.getNumero())
                .orElseThrow(() -> new RuntimeException("Conta não encontrada para a transferencia"));


        assertEquals(0, conta1Atualizada.getSaldo().compareTo(BigDecimal.valueOf(490)), "O saldo da conta 1 deve ser decrementado com o valor da transferencia.");
        assertEquals(BigDecimal.valueOf(1500), conta2Atualizada.getSaldo(), "O saldo da conta 2 deve ser incrementado com o valor da transferencia.");
        assertEquals(BigDecimal.valueOf(500), conta1Atualizada.getLimiteCredito(), "O limite de crédito da conta 1 deve permanecer inalterado.");
        assertEquals(BigDecimal.valueOf(500), conta2Atualizada.getLimiteCredito(), "O limite de crédito da conta 2 deve permanecer inalterado.");
        assertNotNull(transacaoTransferencia, "A transação não deve ser nula.");
        assertEquals(TipoTransacao.TRANSFERENCIA, transacaoTransferencia.getTipo(), "O tipo da transação deve ser TRANSFERENCIA.");
        assertEquals(valorTransferencia, transacaoTransferencia.getValor(), "O valor da transação deve corresponder à transferencia realizada.");

    }

    /**
     * Teste de transferencia de conta com valor maior que o saldo e usando o limite de credito.
     */
    @Test
    public void testTransferenciaContaValorMaiorSaldoECredito() {
        ContaCorrente conta1 = new ContaCorrente(
                "1234567",
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(500),
                LocalDate.now(),
                BigDecimal.valueOf(600),
                null);
        contaCorrenteRepository.save(conta1);

        ContaCorrente conta2 = new ContaCorrente(
                "654321",
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(500),
                LocalDate.now(),
                BigDecimal.valueOf(600),
                null);
        contaCorrenteRepository.save(conta2);
        BigDecimal valorTransferencia = BigDecimal.valueOf(1100);
        Transacao transacaoTransferencia = transacaoService.realizarTransferencia(
                conta1.getNumero(),
                conta2.getNumero(),
                valorTransferencia);

        ContaCorrente conta1Atualizada = contaCorrenteRepository.findById(conta1.getNumero())
                .orElseThrow(() -> new RuntimeException("Conta não encontrada para a transferencia"));
        ContaCorrente conta2Atualizada = contaCorrenteRepository.findById(conta2.getNumero())
                .orElseThrow(() -> new RuntimeException("Conta não encontrada para a transferencia"));


        assertEquals(BigDecimal.ZERO, conta1Atualizada.getSaldo(), "O saldo da conta 1 deve ser decrementado com o valor da transferencia e ficando Zero.");
        assertEquals(BigDecimal.valueOf(2100), conta2Atualizada.getSaldo(), "O saldo da conta 2 deve ser incrementado com o valor da transferencia.");
        assertEquals(0,conta1Atualizada.getLimiteCredito().compareTo(BigDecimal.valueOf(378)),  "O limite de crédito foi alterado com o valor da transferencia.");
        assertEquals(BigDecimal.valueOf(500), conta2Atualizada.getLimiteCredito(), "O limite de crédito da conta 2 deve permanecer inalterado.");
        assertNotNull(transacaoTransferencia, "A transação não deve ser nula.");
        assertEquals(TipoTransacao.TRANSFERENCIA, transacaoTransferencia.getTipo(), "O tipo da transação deve ser TRANSFERENCIA.");
        assertEquals(valorTransferencia, transacaoTransferencia.getValor(), "O valor da transação deve corresponder à transferencia realizada.");
    }

    /**
     * Teste de transferencia de conta com valor maior que o saldo e limite de credito.
     */
    @Test
    public void testTransferenciaContaValorTotalSaldoECredito() {
        ContaCorrente conta1 = new ContaCorrente(
                "12345678",
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(500),
                LocalDate.now(),
                BigDecimal.valueOf(600),
                null);
        contaCorrenteRepository.save(conta1);

        ContaCorrente conta2 = new ContaCorrente(
                "654321",
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(500),
                LocalDate.now(),
                BigDecimal.valueOf(600),
                null);
        contaCorrenteRepository.save(conta2);



        BigDecimal valorTransferencia = BigDecimal.valueOf(2500);


        SaldoInsuficienteException exception = assertThrows(
                SaldoInsuficienteException.class,
                () -> transacaoService.realizarTransferencia(conta1.getNumero(), conta2.getNumero(), valorTransferencia),
                "Deveria lançar SaldoInsuficienteException para transferência acima do saldo e limite"
        );


        Assertions.assertEquals(
                "Saldo insuficiente na conta de origem: " + conta1.getNumero(),
                exception.getMessage(),
                "A mensagem da exceção deve corresponder"
        );


        ContaCorrente contaOrigemAtualizada = contaCorrenteRepository.findById(conta1.getNumero())
                .orElseThrow(() -> new RuntimeException("Conta origem não encontrada após a transferência"));
        ContaCorrente contaDestinoAtualizada = contaCorrenteRepository.findById(conta2.getNumero())
                .orElseThrow(() -> new RuntimeException("Conta destino não encontrada após a transferência"));


        Assertions.assertEquals(
                BigDecimal.valueOf(1000),
                contaOrigemAtualizada.getSaldo(),
                "O saldo da conta origem deve permanecer inalterado após transferência inválida"
        );
        Assertions.assertEquals(
                BigDecimal.valueOf(500),
                contaOrigemAtualizada.getLimiteCredito(),
                "O limite de crédito da conta origem deve permanecer inalterado após transferência inválida"
        );


        Assertions.assertEquals(
                BigDecimal.valueOf(1000),
                contaDestinoAtualizada.getSaldo(),
                "O saldo da conta destino deve permanecer inalterado após transferência inválida"
        );


        Assertions.assertFalse(
                transacaoRepository.findAll().isEmpty(),
                "Nenhuma transação deve ser registrada para transferência inválida"
        );
    }

    /**
     * Teste de transferencia de conta com conta origem e destino iguais.
     */
    @Test
    public void testTransferenciaContaContaOrigemIgualDestino() {
        ContaCorrente conta1 = new ContaCorrente(
                "12345",
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(500),
                LocalDate.now(),
                BigDecimal.valueOf(600),
                null);
        contaCorrenteRepository.save(conta1);

        ContaCorrente conta2 = new ContaCorrente(
                "12345",
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(500),
                LocalDate.now(),
                BigDecimal.valueOf(600),
                null);
        contaCorrenteRepository.save(conta2);

        BigDecimal valorTransferencia = BigDecimal.valueOf(500);


        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> transacaoService.realizarTransferencia(conta1.getNumero(), conta2.getNumero(), valorTransferencia),
                "Deveria lançar IllegalArgumentException para transferência de conta origem e destino iguais"
        );


        Assertions.assertEquals(
                "Conta de origem e destino não podem ser iguais.",
                exception.getMessage(),
                "A mensagem da exceção deve corresponder"
        );


        ContaCorrente contaOrigemAtualizada = contaCorrenteRepository.findById(conta1.getNumero())
                .orElseThrow(() -> new RuntimeException("Conta origem não encontrada após a transferência"));
        ContaCorrente contaDestinoAtualizada = contaCorrenteRepository.findById(conta2.getNumero())
                .orElseThrow(() -> new RuntimeException("Conta destino não encontrada após a transferência"));


        Assertions.assertEquals(
                BigDecimal.valueOf(1000),
                contaOrigemAtualizada.getSaldo(),
                "O saldo da conta origem deve permanecer inalterado após transferência inválida"
        );
        Assertions.assertEquals(
                BigDecimal.valueOf(500),
                contaOrigemAtualizada.getLimiteCredito(),
                "O limite de crédito da conta origem deve permanecer inalterado após transferência inválida"
        );


        Assertions.assertEquals(
                BigDecimal.valueOf(1000),
                contaDestinoAtualizada.getSaldo(),
                "O saldo da conta destino deve permanecer inalterado após transferência inválida"
        );

    }

    /**
     * Teste transferencia de conta com o valor negativo.
     */
    @Test
    public void testTransferenciaContaValorNegativo() {
        ContaCorrente conta1 = new ContaCorrente(
                "123456789",
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(500),
                LocalDate.now(),
                BigDecimal.valueOf(600),
                null);
        contaCorrenteRepository.save(conta1);

        ContaCorrente conta2 = new ContaCorrente(
                "54321",
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(500),
                LocalDate.now(),
                BigDecimal.valueOf(600),
                null);
        contaCorrenteRepository.save(conta2);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> transacaoService.realizarTransferencia(conta1.getNumero(),conta2.getNumero(), BigDecimal.valueOf(-100)),
                "Deveria lançar IllegalArgumentException para valor negativo"
        );


        assertEquals("O valor da transferência não pode ser nulo ou negativo", exception.getMessage());


        ContaCorrente contaRecuperada = contaCorrenteRepository.findById(conta1.getNumero())
                .orElseThrow();
        ContaCorrente contaDestinoAtualizada = contaCorrenteRepository.findById(conta2.getNumero()).orElseThrow();
        assertEquals(BigDecimal.valueOf(1000), contaRecuperada.getSaldo(), "O saldo não deve ser alterado");
        assertEquals(BigDecimal.valueOf(500), contaRecuperada.getLimiteCredito(), "O limite de crédito deve ser zero.");
        assertEquals(BigDecimal.valueOf(1000), contaDestinoAtualizada.getSaldo(), "O saldo não deve ser alterado");
        assertEquals(BigDecimal.valueOf(500), contaDestinoAtualizada.getLimiteCredito(), "O limite de crédito deve ser zero.");
    }

    /**
     * Teste de Transferencia com valor nulo.
     */
    @Test
    public void testTransferenciaContaValorNulo() {
        ContaCorrente conta1 = new ContaCorrente(
                "123456789",
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(500),
                LocalDate.now(),
                BigDecimal.valueOf(600),
                null);
        contaCorrenteRepository.save(conta1);

        ContaCorrente conta2 = new ContaCorrente(
                "54321",
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(500),
                LocalDate.now(),
                BigDecimal.valueOf(600),
                null);
        contaCorrenteRepository.save(conta2);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> transacaoService.realizarTransferencia(conta1.getNumero(),conta2.getNumero(), null),
                "Deveria lançar IllegalArgumentException para valor negativo"
        );


        assertEquals("O valor da transferência não pode ser nulo ou negativo", exception.getMessage());


        ContaCorrente contaRecuperada = contaCorrenteRepository.findById(conta1.getNumero())
                .orElseThrow();
        ContaCorrente contaDestinoAtualizada = contaCorrenteRepository.findById(conta2.getNumero()).orElseThrow();
        assertEquals(BigDecimal.valueOf(1000), contaRecuperada.getSaldo(), "O saldo não deve ser alterado");
        assertEquals(BigDecimal.valueOf(500), contaRecuperada.getLimiteCredito(), "O limite de crédito deve ser zero.");
        assertEquals(BigDecimal.valueOf(1000), contaDestinoAtualizada.getSaldo(), "O saldo não deve ser alterado");
        assertEquals(BigDecimal.valueOf(500), contaDestinoAtualizada.getLimiteCredito(), "O limite de crédito deve ser zero.");
    }
}


