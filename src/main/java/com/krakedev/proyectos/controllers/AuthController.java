package com.krakedev.proyectos.controllers;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.krakedev.proyectos.entidades.Usuario;
import com.krakedev.proyectos.services.UsuarioService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // REGISTRAR USUARIO
    @PostMapping("/registrar")
    public ResponseEntity<?> registrar(@RequestBody Usuario usuario) {

        try {

            Usuario nuevoUsuario = usuarioService.registrar(usuario);

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    Map.of(
                            "mensaje", "Usuario registrado correctamente",
                            "id", nuevoUsuario.getId(),
                            "username", nuevoUsuario.getUsername(),
                            "rol", nuevoUsuario.getRol()
                    )
            );

        } catch (IllegalArgumentException e) {

            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    Map.of("error", e.getMessage())
            );

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("error", e.getMessage())
            );
        }
    }

    // INICIAR SESIÓN (AUTENTICACIÓN TEMPORAL)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Usuario usuario) {

        try {

            Optional<Usuario> usuarioAutenticado =
                    usuarioService.autenticar(
                            usuario.getUsername(),
                            usuario.getPassword()
                    );

            if (usuarioAutenticado.isPresent()) {

                Usuario encontrado = usuarioAutenticado.get();

                return ResponseEntity.ok(
                        Map.of(
                                "mensaje", "Autenticación correcta",
                                "id", encontrado.getId(),
                                "username", encontrado.getUsername(),
                                "rol", encontrado.getRol()
                        )
                );
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Map.of("error", "Usuario o contraseña incorrectos")
            );

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("error", e.getMessage())
            );
        }
    }
}
