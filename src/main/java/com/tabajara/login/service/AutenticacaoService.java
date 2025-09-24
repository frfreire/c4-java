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

@Service
@Transactional
public class AutenticacaoService implements IAutenticacaoService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AutenticacaoService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider tokenProvider,
            AuthenticationManager authenticationManager) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.authenticationManager = authenticationManager;
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
        Usuario user = usuarioRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // O repositório não tem o método updateLastLogin, mas esta seria a forma correta
        // user.setUltimoLogin(LocalDateTime.now());
        // usuarioRepository.save(user);

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
        if (usuarioRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            throw new RuntimeException("Username já está em uso!");
        }

        if (usuarioRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
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
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + username));
    }

    @Override
    public boolean verificaExistenciaUsername(String username) {
        return usuarioRepository.findByUsername(username).isPresent();
    }

    @Override
    public boolean verificaExistenciaEmail(String email) {
        return usuarioRepository.findByEmail(email).isPresent();
    }
}