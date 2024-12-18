package com.example.Banco_Magalu.service;

import com.example.Banco_Magalu.entity.ContaCorrente;
import com.example.Banco_Magalu.repository.ContaCorrenteRepository;
import org.hibernate.Hibernate;
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

        boolean contaJaExiste = contaCorrenteRepository.existsById(contaCorrente.getNumero());
        if(contaJaExiste){
            throw new IllegalArgumentException("Já existe uma conta com o número " + contaCorrente.getNumero());
        }

        if(contaCorrente.getSaldo() == null){
            contaCorrente.setSaldo(BigDecimal.valueOf(0.00));
        }
        if(contaCorrente.getLimiteCredito() == null){
            contaCorrente.setLimiteCredito(BigDecimal.valueOf(0.00));
        }
        if(contaCorrente.getLimiteMaximo() == null){
            contaCorrente.setLimiteMaximo(BigDecimal.valueOf(1000.00));
        }

        return contaCorrenteRepository.save(contaCorrente);
    }

    /*
     * Método para buscar uma conta corrente pelo número da conta
     *
     * @param numero - número da conta corrente a ser buscada
     * @return Optional<ContaCorrente> - objeto ContaCorrente com os dados da conta buscada ou null caso não encontre a conta
     */
    public Optional<ContaCorrente> buscarConta(String numero)
    {
        Optional<ContaCorrente> conta = contaCorrenteRepository.findById(numero);
        conta.ifPresent(c -> Hibernate.initialize(c.getTransacoes()));
        return conta;
    }

    /*
     * Método para atualizar o saldo de uma conta corrente
     *
     * @param numero - número da conta corrente a ser atualizada
     * @param novoSaldo - novo saldo da conta corrente
     */
    public void atualizarSaldo(String numero, BigDecimal novoSaldo){
       if(novoSaldo.compareTo(BigDecimal.ZERO) < 0 ){
           throw new IllegalArgumentException("O saldo não pode ser negativo");
       }
       ContaCorrente conta = contaCorrenteRepository.findById(numero)
               .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada" + numero));

       conta.setSaldo(novoSaldo);
       contaCorrenteRepository.save(conta);
    }

    /*
     * Método para atualizar o limite de uma conta corrente
     *
     * @param numero - número da conta corrente a ser atualizada
     * @param novoLimite - novo limite da conta corrente
     */
    public void atualizarLimite(String numero, BigDecimal novoLimite){
        if(novoLimite.compareTo(BigDecimal.ZERO) < 0){
            throw new IllegalArgumentException("O limite não pode ser negativo");
        }
        ContaCorrente conta = contaCorrenteRepository.findById(numero)
                .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada" + numero));

        conta.setLimiteCredito(novoLimite);
        contaCorrenteRepository.save(conta);
    }

    /**
     * Método para buscar o limite máximo de uma conta corrente
     * @param numero
     * @return
     */
    public BigDecimal buscarLimiteMaximo(String numero) {
        ContaCorrente conta = buscarConta(numero)
                .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada" + numero));
         return conta.getLimiteCredito();
    }

    /**
     * Método para atualizar o saldo de uma conta corrente
     * @param conta
     */
    public void atualizarSaldo(ContaCorrente conta) {
        contaCorrenteRepository.save(conta);
    }

    /**
     * Método para deletar uma conta corrente
     * @param numero
     */
     public void deletarConta(String numero) {
         ContaCorrente conta = contaCorrenteRepository.findById(numero)
                 .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada " + numero));
          contaCorrenteRepository.delete(conta);
     }
}


