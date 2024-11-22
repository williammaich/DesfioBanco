package com.example.Banco_Magalu.controller;

import com.example.Banco_Magalu.entity.ContaCorrente;
import com.example.Banco_Magalu.service.ContaCorrenteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/conta-corrente")
public class ContaCorrenteController {

    private final ContaCorrenteService contaCorrenteService;

    public ContaCorrenteController(ContaCorrenteService contaCorrenteService) {
        this.contaCorrenteService = contaCorrenteService;
        }

    @PostMapping
    public ResponseEntity<ContaCorrente> criarConta(@Valid @RequestBody ContaCorrente contaCorrente) {
        ContaCorrente novaConta = contaCorrenteService.criarConta(contaCorrente);
        return ResponseEntity.ok(novaConta);
    }

    @GetMapping("/{numero}")
    public ResponseEntity<ContaCorrente> buscarConta(@Valid@PathVariable("numero") String numero){
        return contaCorrenteService.buscarConta(numero)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PatchMapping("/{numero}/atualizar-saldo")
    public ResponseEntity<Void> atualizarSaldo(@PathVariable("numero") String numero, @RequestParam BigDecimal saldo) {
        contaCorrenteService.atualizarSaldo(numero, saldo);
        return ResponseEntity.ok().build();
    }

}
