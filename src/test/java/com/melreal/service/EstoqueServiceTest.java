package com.melreal.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class EstoqueServiceTest {

    private EstoqueService service;

    @BeforeEach
    void setUp() {
        service = new EstoqueService();
        ReflectionTestUtils.setField(service, "limiteDiario", 10);
    }

    @Test
    void deveRetornarLimiteCompleto_quandoNenhumaReserva() {
        assertThat(service.getDisponivel()).isEqualTo(10);
    }

    @Test
    void deveReservarComSucesso_quandoHaEstoque() {
        boolean ok = service.reservar(3);
        assertThat(ok).isTrue();
        assertThat(service.getDisponivel()).isEqualTo(7);
    }

    @Test
    void deveRecusarReserva_quandoEstoqueInsuficiente() {
        service.reservar(8);
        boolean ok = service.reservar(5); // só tem 2 sobrando
        assertThat(ok).isFalse();
        assertThat(service.getDisponivel()).isEqualTo(2);
    }

    @Test
    void deveLiberarUnidades_corretamente() {
        service.reservar(6);
        service.liberar(2);
        assertThat(service.getDisponivel()).isEqualTo(6);
    }

    @Test
    void deveResetarParaLimiteCompleto() {
        service.reservar(10);
        service.resetarManualmente();
        assertThat(service.getDisponivel()).isEqualTo(10);
    }

    @Test
    void naoDeveIrAbaixoDeZero_aoLiberar() {
        service.liberar(5); // libera sem ter reservado nada
        assertThat(service.getDisponivel()).isEqualTo(10); // não estoura
    }
}
