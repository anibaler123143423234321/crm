package com.midas.crm.controller;

import com.midas.crm.entity.ClientePromocionBody;
import com.midas.crm.entity.ClienteResidencial;
import com.midas.crm.entity.User;
import com.midas.crm.service.ClienteResidencialService;
import com.midas.crm.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ClientePromocionController {

    @Autowired
    private ClienteResidencialService clienteService;

    @Autowired
    private UserService userService;


    @PostMapping("/cliente-promocion")
    public ResponseEntity<?> crearClienteYPromocion(@RequestBody ClientePromocionBody body) {
        ClienteResidencial cliente = body.getClienteResidencial();
        Long usuarioId = body.getUsuarioId();

        if (cliente == null || usuarioId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Faltan datos de clienteResidencial o usuarioId");
        }

        User usuario = userService.findUserById(usuarioId);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado con id: " + usuarioId);
        }

        cliente.setUsuario(usuario);


        // Guardar Cliente con su usuario
        ClienteResidencial clienteGuardado = clienteService.guardar(cliente);

        var respuesta = new java.util.HashMap<String, Object>();
        respuesta.put("mensaje", "Datos guardados con éxito");
        respuesta.put("cliente", clienteGuardado);
        respuesta.put("usuario_creador", usuario.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

}
