package com.melreal.dto;

/**
 * Corpo da requisição POST /estoque/reservar.
 *
 * @param quantidade  número de unidades que o cliente quer reservar
 */
public record ReservaRequest(int quantidade) {}
