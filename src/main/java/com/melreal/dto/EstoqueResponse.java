package com.melreal.dto;

/**
 * Resposta padrão com o status do estoque.
 *
 * @param disponivel  quantas unidades ainda restam hoje
 * @param limite      limite total configurado para o dia
 * @param esgotado    true quando disponivel == 0
 */
public record EstoqueResponse(
        int disponivel,
        int limite,
        boolean esgotado
) {}
