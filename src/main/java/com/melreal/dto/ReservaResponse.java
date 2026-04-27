package com.melreal.dto;

/**
 * Resposta do POST /estoque/reservar.
 *
 * @param sucesso     true se a reserva foi aceita
 * @param disponivel  estoque restante após a operação
 * @param mensagem    texto amigável para exibir no frontend
 */
public record ReservaResponse(
        boolean sucesso,
        int disponivel,
        String mensagem
) {}
