package com.krakedev.proyectos.controllers;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.krakedev.proyectos.entidades.Usuario;
import com.krakedev.proyectos.security.JwtUtil;
import com.krakedev.proyectos.services.TokenBlacklistService;
import com.krakedev.proyectos.services.UsuarioService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioService usuarioService;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService blacklistService;

    public AuthController(UsuarioService usuarioService,
    		JwtUtil jwtUtil,
            TokenBlacklistService blacklistService) {
        this.usuarioService = usuarioService;
        this.jwtUtil = jwtUtil;
        this.blacklistService = blacklistService;
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
                    Map.of("error",  "Error al registrar usuario")
            );
        }
    }

    // INICIAR SESIÓN (AUTENTICACIÓN TEMPORAL) Parte 1
    // LOGIN Y GENERACION DE JWT parte 2
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
                
                String token = jwtUtil.generarToken(encontrado);

                return ResponseEntity.ok(
                        Map.of(
                                "mensaje", "Autenticación correcta",
                                "token", token,
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
                    Map.of("error", "Error al autenticar usuario")
            );
        }
    }
    
 // LOGOUT E INVALIDACION DEL TOKEN
    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @RequestHeader("Authorization") String authHeader) {

        try {

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                        Map.of("error", "Token no proporcionado correctamente")
                );
            }

            String token = authHeader.substring(7);

            // Verificar si el token ya fue invalidado
            if (blacklistService.estaInvalidado(token)) {

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                        Map.of("error", "El token ya fue invalidado")
                );
            }

            // Verificar la firma y expiracion del token
            jwtUtil.validarToken(token);

            // Agregar token a la lista negra
            blacklistService.invalidarToken(token);

            return ResponseEntity.ok(
                    Map.of("mensaje", "Sesion cerrada correctamente")
            );

        } catch (JWTVerificationException e) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Map.of("error", "Token invalido o expirado")
            );

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("error", "Error al cerrar sesion")
            );
        }
    }
}
