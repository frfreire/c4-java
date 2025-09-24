package com.tabajara.login.service;

import com.tabajara.login.dto.LoginRequest;
import com.tabajara.login.dto.LoginResponse;
import com.tabajara.login.dto.RegisterRequest;
import com.tabajara.login.model.Role;
import com.tabajara.login.model.Usuario;
import com.tabajara.login.repository.UsuarioRepository;
import com.tabajara.login.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service
@Transactional
public class AutenticacaoService implements IAutenticacaoService{


    private final UsuarioRepository usuarioRepository;

    private PasswordEncoder passwordEncoder;

    private final JwtTokenProvider tokenProvider;

    private AuthenticationManager authenticationManager;

    @Autowired
    public AutenticacaoService(UsuarioRepository usuarioRepository, JwtTokenProvider tokenProvider) {
        this.usuarioRepository = usuarioRepository;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = tokenProvider.generateToken(authentication);

        Usuario user = usuarioRepository.procurarPorUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        usuarioRepository.updateLastLogin(user.getId(), LocalDateTime.now());

        return new LoginResponse(
                token,
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                tokenProvider.getTempoExpiracao()
        );
    }

    @Override
    public Usuario register(RegisterRequest registerRequest) {

        if (verificaExistenciaUsername(registerRequest.getUsername())) {
            throw new RuntimeException("Username já está em uso!");
        }

        if (verificaExistenciaEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email já está em uso!");
        }

        Usuario user = new Usuario(
                registerRequest.getUsername(),
                registerRequest.getEmail(),
                passwordEncoder.encode(registerRequest.getPassword()),
                Role.USUARIO
        );

        return usuarioRepository.save(user);
    }

    @Override
    public Usuario buscaPorUsername(String username) {
        return usuarioRepository.procurarPorUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + username));
    }

    @Override
    public boolean verificaExistenciaUsername(String username) {
        return usuarioRepository.verificaUsernameExiste(username);
    }

    @Override
    public boolean verificaExistenciaEmail(String email) {
        return usuarioRepository.verificaEmailExiste(email);
    }
}
