package com.krakedev.proyectos.services;

import java.util.Optional;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.krakedev.proyectos.entidades.Usuario;
import com.krakedev.proyectos.repositories.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // Registrar usuario con contraseña hasheada
    public Usuario registrar(Usuario usuario) {

        Optional<Usuario> existente =
                usuarioRepository.findByUsername(usuario.getUsername());

        if (existente.isPresent()) {
            throw new IllegalArgumentException(
                    "El nombre de usuario ya existe");
        }

        String passwordHash = BCrypt.hashpw(
                usuario.getPassword(),
                BCrypt.gensalt(10)
        );

        usuario.setPassword(passwordHash);

        return usuarioRepository.save(usuario);
    }

    // Autenticar usuario
    public Optional<Usuario> autenticar(String username, String password) {

        Optional<Usuario> usuarioEncontrado =
                usuarioRepository.findByUsername(username);

        if (usuarioEncontrado.isPresent()) {

            Usuario usuario = usuarioEncontrado.get();

            boolean passwordCorrecta = BCrypt.checkpw(
                    password,
                    usuario.getPassword()
            );

            if (passwordCorrecta) {
                return Optional.of(usuario);
            }
        }

        return Optional.empty();
    }
}
