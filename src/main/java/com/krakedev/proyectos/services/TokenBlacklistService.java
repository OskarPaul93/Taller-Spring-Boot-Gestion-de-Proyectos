package com.krakedev.proyectos.services;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class TokenBlacklistService {

    private final Set<String> blacklist =
            ConcurrentHashMap.newKeySet();

    // Agregar token a la lista negra
    public void invalidarToken(String token) {
        blacklist.add(token);
    }

    // Verificar si el token esta invalidado
    public boolean estaInvalidado(String token) {
        return blacklist.contains(token);
    }

}
