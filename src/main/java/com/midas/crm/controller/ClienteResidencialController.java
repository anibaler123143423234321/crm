package com.midas.crm.controller;

import com.midas.crm.entity.ClienteConUsuarioDTO;
import com.midas.crm.service.ClienteResidencialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteResidencialController {

    @Autowired
    private ClienteResidencialService clienteResidencialService;

    @GetMapping("/con-usuario")
    public ResponseEntity<List<ClienteConUsuarioDTO>> obtenerClientesConUsuario() {
        List<ClienteConUsuarioDTO> clientes = clienteResidencialService.obtenerClientesConUsuario();
        return ResponseEntity.ok(clientes);
    }
}
