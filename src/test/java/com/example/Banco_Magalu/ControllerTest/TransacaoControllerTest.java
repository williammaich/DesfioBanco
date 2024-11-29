package com.example.Banco_Magalu.ControllerTest;

import com.example.Banco_Magalu.controller.TransacaoController;
import com.example.Banco_Magalu.dto.DepositoDto;
import com.example.Banco_Magalu.dto.LoteTransferenciaDto;
import com.example.Banco_Magalu.dto.SaqueDto;
import com.example.Banco_Magalu.dto.TransferenciaDto;
import com.example.Banco_Magalu.entity.ContaCorrente;
import com.example.Banco_Magalu.entity.TipoTransacao;
import com.example.Banco_Magalu.entity.Transacao;
import com.example.Banco_Magalu.repository.ContaCorrenteRepository;
import com.example.Banco_Magalu.service.TransacaoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(TransacaoController.class)
public class TransacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ContaCorrenteRepository contaRepository;


    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    private TransacaoService transacaoService;



    /**
     * Teste para verificar a rota de deposito
     */
    @Test
    public void testDeposito() throws Exception {
        UUID id = UUID.randomUUID();
        TipoTransacao tipo = TipoTransacao.DEPOSITO;
        BigDecimal valor = BigDecimal.valueOf(100.00);
        LocalDate data = LocalDate.now();
        String descricao = "Depósito realizado com sucesso";
        ContaCorrente contaCorrente = new ContaCorrente();
        contaCorrente.setNumero("155");


        DepositoDto depositoDto = new DepositoDto("155", valor);
        Transacao transacao = new Transacao(id, tipo, valor, data, descricao, contaCorrente);


        Mockito.when(transacaoService.realizarDeposito(anyString(), any(BigDecimal.class))).thenReturn(transacao);


        mockMvc.perform(post("/transacao/deposito")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositoDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(transacao.getId().toString()))
                .andExpect(jsonPath("$.tipo").value(transacao.getTipo().toString()))
                .andExpect(jsonPath("$.valor").value(transacao.getValor().doubleValue()))
                .andExpect(jsonPath("$.descricao").value(transacao.getDescricao()));

    }

    /**
     * Teste para verificar a rota de saque
     */
    @Test
    public void testSaque() throws Exception {
        UUID id = UUID.randomUUID();
        TipoTransacao tipo = TipoTransacao.SAQUE;
        BigDecimal valor = BigDecimal.valueOf(100.00);
        LocalDate data = LocalDate.now();
        String descricao = "Saque realizado com sucesso";
        ContaCorrente contaCorrente = new ContaCorrente();
        contaCorrente.setNumero("155");

        Transacao transacao = new Transacao(id, tipo, valor, data, descricao, contaCorrente);

        SaqueDto saqueDto = new SaqueDto("155", valor);

        Mockito.when(transacaoService.realizarSaque(anyString(), any(BigDecimal.class))).thenReturn(transacao);

        mockMvc.perform(post("/transacao/saque")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(saqueDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(transacao.getId().toString()))
                .andExpect(jsonPath("$.tipo").value(transacao.getTipo().toString()))
                .andExpect(jsonPath("$.valor").value(transacao.getValor().doubleValue()))
                .andExpect(jsonPath("$.descricao").value(transacao.getDescricao()));
    }

    /**
     * Teste para verificar a rota de transferencia
     */
    @Test
    public void testTransferencia() throws Exception {
        UUID id = UUID.randomUUID();
        TipoTransacao tipo = TipoTransacao.TRANSFERENCIA;
        BigDecimal valor = BigDecimal.valueOf(100.00);
        LocalDate data = LocalDate.now();
        String descricao = "Transferência realizada com sucesso";

        ContaCorrente contaOrigem = new ContaCorrente();
        contaOrigem.setNumero("155");

        ContaCorrente contaDestino = new ContaCorrente();
        contaDestino.setNumero("54321");

        Transacao transacao = new Transacao(id, tipo, valor, data, descricao, contaOrigem);

        TransferenciaDto transferenciaDto = new TransferenciaDto("12345", "54321", valor);

        Mockito.when(transacaoService.realizarTransferencia(
                anyString(),
                anyString(),
                any(BigDecimal.class)
        )).thenReturn(transacao);

        mockMvc.perform(post("/transacao/transferencia")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferenciaDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(transacao.getId().toString()))
                .andExpect(jsonPath("$.tipo").value(transacao.getTipo().toString()))
                .andExpect(jsonPath("$.valor").value(transacao.getValor().doubleValue()))
                .andExpect(jsonPath("$.descricao").value(transacao.getDescricao()));
    }

    /**
     * Teste para verificar Transferencia em lote
     */
    @Test
    public void testTransferenciaLote() throws Exception {
        BigDecimal valor1 = BigDecimal.valueOf(100.00);
        BigDecimal valor2 = BigDecimal.valueOf(200.00);

        ContaCorrente contaOrigem1 = new ContaCorrente();
        contaOrigem1.setNumero("12345");

        ContaCorrente contaDestino1 = new ContaCorrente();
        contaDestino1.setNumero("54321");

        ContaCorrente contaOrigem2 = new ContaCorrente();
        contaOrigem2.setNumero("67890");

        ContaCorrente contaDestino2 = new ContaCorrente();
        contaDestino2.setNumero("09876");

        TransferenciaDto transferenciaDto1 = new TransferenciaDto("12345", "54321", valor1);
        TransferenciaDto transferenciaDto2 = new TransferenciaDto("67890", "09876", valor2);

        List<TransferenciaDto> transferencias = List.of(transferenciaDto1, transferenciaDto2);
        LoteTransferenciaDto loteTransferenciaDto = new LoteTransferenciaDto(transferencias);

        Mockito.when(transacaoService.realizarTransferencia(anyString(), anyString(), any(BigDecimal.class)))
                .thenReturn(null); // Aqui você pode mockar o comportamento da transferência, como sucesso ou falha.

        mockMvc.perform(post("/transacao/lote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loteTransferenciaDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sucesso").isArray())
                .andExpect(jsonPath("$.sucesso.length()").value(2)) // Espera que duas transferências tenham sido realizadas com sucesso
                .andExpect(jsonPath("$.falhas").isArray()) // Espera um array para as falhas
                .andExpect(jsonPath("$.falhas.length()").value(0)); // Se não houver falhas, o tamanho deve ser 0
    }
}



