package com.tabajara.login.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret:minha-chave-secreta-super-segura-deve-ter-pelo-menos-256-bits}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}")
    private Long jwtExpiracaoMs;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateToken(Authentication authentication) {
        UserDetails usuarioPrincipal = (UserDetails) authentication.getPrincipal();
        Date dataExpiracao = new Date(System.currentTimeMillis() + jwtExpiracaoMs);

        return Jwts.builder()
                .setSubject(usuarioPrincipal.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(dataExpiracao)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateToken(String username) {
        Date expiryDate = new Date(System.currentTimeMillis() + jwtExpiracaoMs);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUsernameToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public boolean validaToken(String authToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException e) {
            System.err.println("Token JWT inválido: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            System.err.println("Token JWT expirado: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.err.println("Token JWT não suportado: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("JWT claims string está vazio: " + e.getMessage());
        }
        return false;
    }

    public Long getTempoExpiracao() {
        return jwtExpiracaoMs;
    }
}
