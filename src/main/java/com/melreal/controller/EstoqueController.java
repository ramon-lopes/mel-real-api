package com.melreal.controller;

import com.melreal.dto.EstoqueResponse;
import com.melreal.dto.ReservaRequest;
import com.melreal.dto.ReservaResponse;
import com.melreal.service.EstoqueService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoints públicos consumidos pelo frontend.
 * <p>
 * GET  /estoque          → retorna disponível/limite/esgotado
 * POST /estoque/reservar → tenta reservar N unidades
 * POST /estoque/liberar  → devolve N unidades (cancelamento)
 */
@RestController
@RequestMapping("/estoque")
public class EstoqueController {

    private final EstoqueService estoqueService;

    public EstoqueController(EstoqueService estoqueService) {
        this.estoqueService = estoqueService;
    }

    // ── GET /estoque ──────────────────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<EstoqueResponse> consultar() {
        int disponivel = estoqueService.getDisponivel();
        int limite = estoqueService.getLimite();

        return ResponseEntity.ok(new EstoqueResponse(
                disponivel,
                limite,
                disponivel == 0
        ));
    }

    // ── POST /estoque/reservar ────────────────────────────────────────────────
    @PostMapping("/reservar")
    public ResponseEntity<ReservaResponse> reservar(@RequestBody ReservaRequest req) {
        if (req.quantidade() <= 0) {
            return ResponseEntity.badRequest().body(
                    new ReservaResponse(false, estoqueService.getDisponivel(),
                            "Quantidade inválida.")
            );
        }

        boolean ok = estoqueService.reservar(req.quantidade());

        if (ok) {
            return ResponseEntity.ok(new ReservaResponse(
                    true,
                    estoqueService.getDisponivel(),
                    "Reserva confirmada!"
            ));
        } else {
            return ResponseEntity.ok(new ReservaResponse(
                    false,
                    estoqueService.getDisponivel(),
                    "Estoque insuficiente. Restam apenas " + estoqueService.getDisponivel() + " unidade(s)."
            ));
        }
    }

    // ── POST /estoque/liberar ─────────────────────────────────────────────────
    @PostMapping("/liberar")
    public ResponseEntity<EstoqueResponse> liberar(@RequestBody ReservaRequest req) {
        if (req.quantidade() > 0) {
            estoqueService.liberar(req.quantidade());
        }
        int disponivel = estoqueService.getDisponivel();
        return ResponseEntity.ok(new EstoqueResponse(
                disponivel,
                estoqueService.getLimite(),
                disponivel == 0
        ));
    }

    // ── PUT /estoque/admin/limite ─────────────────────────────────────────────
    @PutMapping("/admin/limite")
    public ResponseEntity<EstoqueResponse> atualizarLimite(
            @RequestParam int valor,
            @RequestHeader("X-Admin-Key") String key) {
        if (!"sua-chave-secreta".equals(key)) return ResponseEntity.status(403).build();
        estoqueService.setLimite(valor);
        int d = estoqueService.getDisponivel();
        return ResponseEntity.ok(new EstoqueResponse(d, valor, d == 0));
    }

    // ── POST /estoque/admin/reset ─────────────────────────────────────────────
    @PostMapping("/admin/reset")
    public ResponseEntity<EstoqueResponse> reset(
            @RequestHeader("X-Admin-Key") String key) {
        if (!"sua-chave-secreta".equals(key)) return ResponseEntity.status(403).build();
        estoqueService.resetarManualmente();
        int d = estoqueService.getDisponivel();
        return ResponseEntity.ok(new EstoqueResponse(d, estoqueService.getLimite(), false));
    }
}
