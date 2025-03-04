package com.midas.crm.controller;


import com.midas.crm.entity.Role;
import com.midas.crm.entity.User;
import com.midas.crm.security.UserPrincipal;
import com.midas.crm.service.ExcelService;
import com.midas.crm.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequiredArgsConstructor
@RequestMapping("api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private ExcelService excelService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);


    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("crear-masivo")
    public ResponseEntity<?> createUsersFromExcel(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Debe subir un archivo Excel válido.");
        }

        try {
            List<User> users = excelService.leerUsuariosDesdeExcel(file);
            userService.saveUsers(users);
            return ResponseEntity.status(HttpStatus.CREATED).body("Usuarios cargados exitosamente.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al procesar el archivo Excel: " + e.getMessage());
        }
    }
    /*
    @GetMapping("listar")
    public List<User> listUsers() {
        return userService.findAllUsers();
    }
*/

    @GetMapping("listar")
    public Page<User> listUsers(Pageable pageable) {
        return userService.findAllUsers(pageable);
    }

    @PutMapping("change/{role}")
    public ResponseEntity<?> changeRole(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Role role) {
        User user = userService.findByUsername(userPrincipal.getUsername()).orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        if (role == Role.ADMIN && user.getRole() == Role.ADMIN) {
            // Actualiza el rol a SUPERADMIN si es un ADMIN
            userService.changeRole(Role.ADMIN, userPrincipal.getUsername());
            return ResponseEntity.ok(true);
        } else {
            // Maneja otros cambios de roles aquí si es necesario
            userService.changeRole(role, userPrincipal.getUsername());
            return ResponseEntity.ok(true);
        }
    }


    @GetMapping
    public ResponseEntity<User> getCurrentUser(@AuthenticationPrincipal UserPrincipal userPrincipal)
    {
        return new ResponseEntity<>(userService.findByUsernameReturnToken(userPrincipal.getUsername()), HttpStatus.OK);
    }

    // http://locahost:5555/gateway/usuario/{userId}
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUsuarioById(@PathVariable Long userId) {
        try {
            User usuario = userService.findUserById(userId);
            if (usuario != null) {
                return ResponseEntity.ok(usuario);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
