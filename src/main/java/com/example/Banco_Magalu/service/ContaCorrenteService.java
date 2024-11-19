package com.example.Banco_Magalu.service;

import com.example.Banco_Magalu.entity.ContaCorrente;
import com.example.Banco_Magalu.repository.ContaCorrenteRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class ContaCorrenteService {

    private final ContaCorrenteRepository contaCorrenteRepository;

    public ContaCorrenteService(ContaCorrenteRepository contaCorrenteRepository) {
        this.contaCorrenteRepository = contaCorrenteRepository;
    }


    /*
     * Método para criar uma nova conta corrente
     *
     * @param contaCorrente - objeto ContaCorrente com os dados da conta a ser criada
     * @return ContaCorrente - objeto ContaCorrente com os dados da conta criada
     */
    public ContaCorrente criarConta(ContaCorrente contaCorrente){
        if(contaCorrente == null){
           throw new IllegalArgumentException("A conta corrente não pode ser nula");
        }
        return contaCorrenteRepository.save(contaCorrente);
    }


    /*
     * Método para buscar uma conta corrente pelo número da conta
     *
     * @param numero - número da conta corrente a ser buscada
     * @return Optional<ContaCorrente> - objeto ContaCorrente com os dados da conta buscada ou null caso não encontre a conta
     */
    public Optional<ContaCorrente> buscarConta(String numero){
        return contaCorrenteRepository.findById(numero);
    }


    /*
     * Método para atualizar o saldo de uma conta corrente
     *
     * @param numero - número da conta corrente a ser atualizada
     * @param novoSaldo - novo saldo da conta corrente
     */
    public void atualizarSaldo(String numero, BigDecimal novoSaldo){
        ContaCorrente conta = contaCorrenteRepository.findById(numero).orElseThrow(
                ()-> new RuntimeException("Conta não encontrada"));
        conta.setSaldo(novoSaldo);
        contaCorrenteRepository.save(conta);
    }

    //Método para transferir fundos de uma conta para outra


    //Método para sacar fundos da conta

    //Método para depositar fundos na conta
}


