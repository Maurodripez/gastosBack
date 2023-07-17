package com.mauricio.gastos.controller;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class RolesController {

    @GetMapping("/prueba")
    @PreAuthorize("hasRole('ADMIN')")
    public String pruebaAdmin() {
        return "Eres admin";
    }
}
