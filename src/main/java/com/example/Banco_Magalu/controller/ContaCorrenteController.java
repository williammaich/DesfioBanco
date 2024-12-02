package com.example.Banco_Magalu.controller;

import com.example.Banco_Magalu.dto.ContaCorrenteDto;
import com.example.Banco_Magalu.entity.ContaCorrente;
import com.example.Banco_Magalu.service.ContaCorrenteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping(value="/conta-corrente", produces = "application/json")
@Tag(name= "Conta Corrente")
public class ContaCorrenteController {

    private final ContaCorrenteService contaCorrenteService;

    public ContaCorrenteController(ContaCorrenteService contaCorrenteService) {
        this.contaCorrenteService = contaCorrenteService;
        }


    @Operation(summary = "Criar uma nova conta corrente", description = "Endpoint para criar uma conta corrente com saldo inicial e limite de crédito.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conta criada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ContaCorrente.class))),
            @ApiResponse(responseCode = "400", description = "Erro nos dados da solicitação")
    })
    @PostMapping
    public ResponseEntity<ContaCorrente> criarConta(@Valid @RequestBody ContaCorrente contaCorrente) {
        ContaCorrente novaConta = contaCorrenteService.criarConta(contaCorrente);
        return ResponseEntity.ok(novaConta);
    }


    @Operation(summary = "Consultar uma conta corrente", description = "Busca os detalhes de uma conta corrente pelo número da conta.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conta encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ContaCorrente.class))),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada")
    })
    @GetMapping("/{numero}")
    public ResponseEntity<ContaCorrente> buscarConta(@PathVariable("numero") String numero){
        return contaCorrenteService.buscarConta(numero)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @Operation(
            summary = "Atualiza o saldo de uma conta corrente",
            description = "Permite atualizar o saldo de uma conta corrente identificada pelo número da conta."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Saldo atualizado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ContaCorrenteDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Conta não encontrada",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PatchMapping("/{numero}/atualizar-saldo")
    public ResponseEntity<ContaCorrenteDto> atualizarSaldo(@PathVariable("numero") String numero, @RequestBody ContaCorrente contaAtualizada) {
        ContaCorrente conta = contaCorrenteService.buscarConta(numero)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conta não encontrada"));

        conta.setSaldo(contaAtualizada.getSaldo());
        contaCorrenteService.atualizarSaldo(conta);

        ContaCorrenteDto dto = new ContaCorrenteDto(conta.getNumero(), conta.getSaldo());
        return ResponseEntity.ok(dto);
    }

    @Operation(
            summary = "Deletar uma conta  corrente",
            description = "Permite deletar uma conta corrente identificada pelo número da conta."
            )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Conta deletada com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Conta não encontrada",
                    content = @Content(mediaType = "application/json")
            )

    })
    @DeleteMapping("/Deletar/{numero}")
    public ResponseEntity<Void> deletarConta(@PathVariable("numero") String numero) {
        contaCorrenteService.deletarConta(numero);
        return ResponseEntity.noContent().build();
    }

}
