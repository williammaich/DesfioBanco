package com.example.Banco_Magalu.ServiceTest;

import com.example.Banco_Magalu.entity.ContaCorrente;
import com.example.Banco_Magalu.repository.ContaCorrenteRepository;
import com.example.Banco_Magalu.service.ContaCorrenteService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class ContaCorrenteServiceTest {

    @Autowired
    private ContaCorrenteService contaCorrenteService;

    @Autowired
    private ContaCorrenteRepository contaCorrenteRepository;

    /**
     * Cria uma conta corrente para testes.
     */
    @Test
    void testCriarContaValida() {
        ContaCorrente conta = new ContaCorrente(
                "12345",
                BigDecimal.valueOf(1000),//saldo inicial
                BigDecimal.valueOf(1000),//limite atual
                LocalDate.now(),
                BigDecimal.valueOf(1000), //limite maximo
                null);
        contaCorrenteRepository.save(conta);

        assertNotNull(conta.getNumero(), "A conta criada não deve ser nula.");
        assertEquals(0, conta.getSaldo().compareTo(BigDecimal.valueOf(1000.00)), "O saldo padrão deve ser 0.00.");
        assertEquals(0, conta.getLimiteCredito().compareTo(BigDecimal.valueOf(1000.00)), "O limite de crédito padrão deve ser 1000.00.");
        assertEquals(0,conta.getLimiteMaximo().compareTo(BigDecimal.valueOf(1000.00)), "O limite máximo padrão deve ser 1000.00.");
    }

    /**
     * Tenta criar uma conta com número duplicado.
     */
    @Test
    void testCriarContaDuplicada() {
        ContaCorrente conta = new ContaCorrente("12345", BigDecimal.valueOf(1000), BigDecimal.valueOf(500), LocalDate.now(), BigDecimal.valueOf(1000), null);

        // Lançamento da exceção ao tentar criar uma conta que já existe
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            contaCorrenteService.criarConta(conta);
        });

        // Verifica se a mensagem da exceção está correta
        assertEquals("Já existe uma conta com o número 12345", exception.getMessage());

    }

    /**
     * Teste Buscar Conta Existente.
     */
    @Test
    void testBuscarContaExistente() {
        ContaCorrente conta = new ContaCorrente("12345", BigDecimal.valueOf(500), null, null, null, null);
        contaCorrenteRepository.save(conta);

        Optional<ContaCorrente> resultado = contaCorrenteService.buscarConta("12345");

        assertTrue(resultado.isPresent(), "A conta deve estar presente.");
        assertEquals("12345", resultado.get().getNumero(), "O número da conta deve ser 12345.");
    }

    /**
     * Teste Buscar Conta Inexistente.
     */

    @Test
    void testBuscarContaInexistente() {
        Optional<ContaCorrente> resultado = contaCorrenteService.buscarConta("99999");

        assertTrue(resultado.isEmpty(), "A conta não deve estar presente.");
    }

    /**
     * Teste Atualizar Saldo.
     */
    @Test
    void testAtualizarSaldoValido() {
        ContaCorrente conta = new ContaCorrente("12345", BigDecimal.valueOf(500), null, null, null, null);
        contaCorrenteRepository.save(conta);

        contaCorrenteService.atualizarSaldo("12345", BigDecimal.valueOf(1000));

        ContaCorrente contaAtualizada = contaCorrenteRepository.findById("12345").orElseThrow();
        assertEquals(BigDecimal.valueOf(1000), contaAtualizada.getSaldo(), "O saldo deve ser atualizado para 1000.");
    }

    /**
     * Teste Atualizar Limite.
     */
    @Test
    void testAtualizarLimiteValido() {
        ContaCorrente conta = new ContaCorrente("12345", null, BigDecimal.valueOf(1000), null, null, null);
        contaCorrenteRepository.save(conta);

        contaCorrenteService.atualizarLimite("12345", BigDecimal.valueOf(2000));

        ContaCorrente contaAtualizada = contaCorrenteRepository.findById("12345").orElseThrow();
        assertEquals(BigDecimal.valueOf(2000), contaAtualizada.getLimiteCredito(), "O limite de crédito deve ser atualizado para 2000.");
    }

    /**
     * Teste Atualizar Limite Maximo.
     */
    @Test
    void testBuscarLimiteMaximo() {
        ContaCorrente conta = new ContaCorrente("12345", null, BigDecimal.valueOf(1000), null, BigDecimal.valueOf(2000), null);
        contaCorrenteRepository.save(conta);

        BigDecimal limiteMaximo = contaCorrenteService.buscarLimiteMaximo("12345");

        assertEquals(BigDecimal.valueOf(1000), limiteMaximo, "O limite máximo deve ser 2000.");
    }

    }

