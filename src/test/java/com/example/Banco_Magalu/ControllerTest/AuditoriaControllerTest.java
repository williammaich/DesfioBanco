package com.example.Banco_Magalu.ControllerTest;

import com.example.Banco_Magalu.controller.AuditoriaController;
import com.example.Banco_Magalu.service.AuditoriaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(AuditoriaController.class)
public class AuditoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuditoriaService auditoriaService;

    @BeforeEach
    void setUp() {
        // Este método será executado antes de cada teste
    }

    /**
     * Teste para buscar mensagens de auditoria por conta
     * @throws Exception
     */
    @Test
    void testeBuscarMensagensAuditoriaConta() throws Exception {

        String numeroConta = "321";
        List<String> mensagens = Arrays.asList(
                "Transferência de R$ 100.00 realizada para conta 54321",
                "Saque de R$ 50.00 realizado na conta 321",
                "Depósito de R$ 200.00 realizado na conta 321"
        );

        when(auditoriaService.buscarMensagensPorConta(numeroConta)).thenReturn(mensagens);


        mockMvc.perform(get("/auditoria/conta/{numeroConta}", numeroConta)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())  // Espera um status 200 OK
                .andExpect(jsonPath("$.length()").value(3))  // Verifica se o retorno tem 3 mensagens
                .andExpect(jsonPath("$[0]").value("Transferência de R$ 100.00 realizada para conta 54321"))  // Verifica a primeira mensagem
                .andExpect(jsonPath("$[1]").value("Saque de R$ 50.00 realizado na conta 321"))  // Verifica a segunda mensagem
                .andExpect(jsonPath("$[2]").value("Depósito de R$ 200.00 realizado na conta 321"));  // Verifica a terceira mensagem
    }

    /**
     * Teste para buscar mensagens de auditoria por conta, mas com conta não encontrada
     * @throws Exception
     */
    @Test
    void testeBuscarMensagensAuditoriaContaNaoEncontrada() throws Exception {

        String numeroConta = "999";

        when(auditoriaService.buscarMensagensPorConta(numeroConta)).thenReturn(List.of());

        mockMvc.perform(get("/auditoria/conta/{numeroConta}", numeroConta)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())  // Espera um status 200 OK
                .andExpect(jsonPath("$.length()").value(0));  // Verifica que não há mensagens
    }
}
