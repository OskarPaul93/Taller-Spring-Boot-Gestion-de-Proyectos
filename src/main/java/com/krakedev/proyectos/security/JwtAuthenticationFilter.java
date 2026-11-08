package com.krakedev.proyectos.security;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.krakedev.proyectos.services.TokenBlacklistService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final TokenBlacklistService blacklistService;

    public JwtAuthenticationFilter(
            JwtUtil jwtUtil,
            TokenBlacklistService blacklistService) {

        this.jwtUtil = jwtUtil;
        this.blacklistService = blacklistService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // Si no existe token, continuar con la cadena de filtros
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {

            // Comprobar si el token esta en la Blacklist
            if (blacklistService.estaInvalidado(token)) {
                response.sendError(
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "Token invalidado"
                );
                return;
            }

            // Validar firma y expiracion del JWT
            DecodedJWT decodedJWT = jwtUtil.validarToken(token);

            String username = decodedJWT.getSubject();
            String rol = decodedJWT.getClaim("rol").asString();

            // Crear autoridad de Spring Security
            SimpleGrantedAuthority authority =
                    new SimpleGrantedAuthority("ROLE_" + rol);

            // Crear objeto de autenticacion
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            List.of(authority)
                    );

            authentication.setDetails(
                    new WebAuthenticationDetailsSource()
                            .buildDetails(request)
            );

            // Guardar autenticacion en Spring Security
            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);

        } catch (Exception e) {

            SecurityContextHolder.clearContext();

            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Token invalido o expirado"
            );
            return;
        }

        filterChain.doFilter(request, response);
    }
}
