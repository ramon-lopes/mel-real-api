package com.melreal.controller;

import com.melreal.dto.EstoqueResponse;
import com.melreal.service.EstoqueService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoints de administração — protegidos por token simples via header.
 *
 * Header obrigatório: X-Admin-Token: <valor configurado em application.properties>
 *
 * POST /admin/reset   → zera o contador (novo dia antes da meia-noite)
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Value("${admin.token:mel-real-admin-2026}")
    private String adminToken;

    private final EstoqueService estoqueService;

    public AdminController(EstoqueService estoqueService) {
        this.estoqueService = estoqueService;
    }

    @PostMapping("/reset")
    public ResponseEntity<EstoqueResponse> reset(
            @RequestHeader(value = "X-Admin-Token", required = false) String token) {

        if (!adminToken.equals(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        estoqueService.resetarManualmente();
        int disponivel = estoqueService.getDisponivel();

        return ResponseEntity.ok(new EstoqueResponse(
                disponivel,
                estoqueService.getLimite(),
                disponivel == 0
        ));
    }
}
