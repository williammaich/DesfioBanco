package com.example.Banco_Magalu.ControllerTest;

import com.example.Banco_Magalu.controller.ContaCorrenteController;
import com.example.Banco_Magalu.entity.ContaCorrente;
import com.example.Banco_Magalu.service.ContaCorrenteService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
public class ContaCorrenteControllerTest {

    @Autowired
    private ContaCorrenteController contaCorrenteController;

    @Autowired
    private ContaCorrenteService contaCorrenteService;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(contaCorrenteController).build();
    }

    @Test
    void testCriarConta() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/conta-corrente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"numero\": \"155\", \"saldo\": 1000, \"limiteCredito\": 500, \"dataDeCriacao\": \"" + LocalDate.now() + "\", \"limiteMaximo\": 1000, \"transacoes\": null}")
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.numero").value("155"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.saldo").value(1000.00))
                .andExpect(MockMvcResultMatchers.jsonPath("$.limiteCredito").value(500.00))
                .andExpect(MockMvcResultMatchers.jsonPath("$.limiteMaximo").value(1000.00));


        Optional<ContaCorrente> contaSalva = contaCorrenteService.buscarConta("155");


        assertTrue(contaSalva.isPresent());
        assertEquals("155", contaSalva.get().getNumero());
        assertEquals(0,contaSalva.get().getSaldo().compareTo(BigDecimal.valueOf(1000)));
        assertEquals(0,contaSalva.get().getLimiteCredito().compareTo(BigDecimal.valueOf(500)));
        assertEquals(0,contaSalva.get().getLimiteMaximo().compareTo(BigDecimal.valueOf(1000)));
        assertNotNull(contaSalva.get().getDataDeCriacao());  // Verifica que a data de criação não é nula
    }

    @Test
    void testeConsultarContaNaoExistente() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/conta-corrente/171")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Transactional
    @Test
    void testeConsultarContaExistente() throws Exception {
        ContaCorrente conta = new ContaCorrente("321", BigDecimal.valueOf(1000), BigDecimal.valueOf(500), LocalDate.now(), BigDecimal.valueOf(1000), null);
        contaCorrenteService.criarConta(conta);
        if (conta.getTransacoes() != null) {
            conta.getTransacoes().size();
        } else {
            conta.setTransacoes(new ArrayList<>());
        }

        mockMvc.perform(MockMvcRequestBuilders.get("/conta-corrente/321")  // Alterado para 321
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testeAtualizarConta() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.patch("/conta-corrente/155/atualizar-saldo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"numero\": \"155\", \"saldo\": 2000.0, \"limiteCredito\": 1000.0, \"dataDeCriacao\": \"" + LocalDate.now() + "\", \"limiteMaximo\": 2000.0, \"transacoes\": null}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.numero").value("155"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.saldo").value(2000.0));
        Optional<ContaCorrente> contaSalva = contaCorrenteService.buscarConta("155");
        assertTrue(contaSalva.isPresent());
    }
}




