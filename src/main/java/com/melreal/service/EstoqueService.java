package com.melreal.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Gerencia o estoque diário em memória.
 *
 * - O limite é configurado em application.properties (estoque.limite-diario).
 * - Todo dia à meia-noite o contador é resetado automaticamente.
 * - AtomicInteger garante que operações concorrentes (vários usuários ao mesmo
 *   tempo) não causem inconsistências — sem precisar de synchronized manual.
 */
@Service
public class EstoqueService {

    private static final Logger log = LoggerFactory.getLogger(EstoqueService.class);

    @Value("${estoque.limite-diario}")
    private int limiteDiario;

    // Quantidade já reservada no dia — começa em zero
    private final AtomicInteger reservados = new AtomicInteger(0);

    /**
     * Retorna quantas unidades ainda restam no dia.
     */
    public int getDisponivel() {
        return Math.max(0, limiteDiario - reservados.get());
    }

    /**
     * Retorna o limite total configurado.
     */
    public int getLimite() {
        return limiteDiario;
    }

    /**
     * Tenta reservar 'quantidade' unidades.
     *
     * @return true se havia estoque suficiente e a reserva foi feita,
     *         false se não havia estoque.
     */
    public boolean reservar(int quantidade) {
        // Loop CAS (Compare-And-Swap): tenta atualizar atomicamente.
        // Se outro thread mudou o valor entre a leitura e a escrita,
        // o AtomicInteger detecta e repete — zero risco de race condition.
        while (true) {
            int atual = reservados.get();
            int novoValor = atual + quantidade;

            if (novoValor > limiteDiario) {
                log.warn("Reserva de {} unidades recusada — disponível: {}", quantidade, getDisponivel());
                return false;
            }

            // compareAndSet só atualiza se o valor ainda for 'atual'
            if (reservados.compareAndSet(atual, novoValor)) {
                log.info("Reserva de {} unidades aceita — restam: {}", quantidade, getDisponivel());
                return true;
            }
            // Se falhou (outro thread atualizou antes), o while repete
        }
    }

    /**
     * Libera unidades reservadas (ex: cliente cancelou antes de enviar pro WhatsApp).
     */
    public void liberar(int quantidade) {
        reservados.updateAndGet(atual -> Math.max(0, atual - quantidade));
        log.info("{} unidades liberadas — disponível agora: {}", quantidade, getDisponivel());
    }

    /**
     * Reset automático todo dia à meia-noite (horário de Brasília).
     * Cron: segundo minuto hora dia mês dia-da-semana
     */
    @Scheduled(cron = "0 0 0 * * *", zone = "America/Sao_Paulo")
    public void resetarEstoque() {
        int antigo = reservados.getAndSet(0);
        log.info("Estoque resetado à meia-noite. Foram {} unidades reservadas no dia.", antigo);
    }

    /**
     * Reset manual — útil pra chamar via endpoint admin ou em testes.
     */
    public void resetarManualmente() {
        reservados.set(0);
        log.info("Estoque resetado manualmente.");
    }
}
