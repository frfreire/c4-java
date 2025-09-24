package com.tabajara.login.controller;

import com.tabajara.login.dto.LoginRequest;
import com.tabajara.login.dto.LoginResponse;
import com.tabajara.login.dto.RegisterRequest;
import com.tabajara.login.model.Usuario;
import com.tabajara.login.service.IAutenticacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
@Tag(name = "Authentication", description = "Endpoints para autenticação e registro")
public class AutenticacaoController {

    private final IAutenticacaoService autenticacaoService;

    @Autowired
    public AutenticacaoController(IAutenticacaoService autenticacaoService) {
        this.autenticacaoService = autenticacaoService;
    }

    @PostMapping("/login")
    @Operation(summary = "Login do usuário", description = "Autentica usuário e retorna JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login bem-sucedido",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponse.class)) }),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas",
                    content = @Content)
    })
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse loginResponse = autenticacaoService.login(loginRequest);
            return ResponseEntity.ok(loginResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Credenciais inválidas", "message", e.getMessage()));
        }
    }

    @PostMapping("/register")
    @Operation(summary = "Registro de usuário", description = "Cria uma nova conta de usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário registrado com sucesso",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Erro na validação dos dados ou usuário já existente",
                    content = @Content)
    })
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            Usuario usuario = autenticacaoService.register(registerRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Usuário registrado com sucesso", "username", usuario.getUsername()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Erro no registro", "message", e.getMessage()));
        }
    }

    @GetMapping("/profile")
    @PreAuthorize("hasAnyRole('USUARIO', 'ADMIN', 'MODERADOR')")
    @Operation(summary = "Perfil do usuário", description = "Retorna informações do usuário autenticado")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        try {
            Usuario usuario = autenticacaoService.buscaPorUsername(authentication.getName());
            return ResponseEntity.ok(Map.of(
                    "username", usuario.getUsername(),
                    "email", usuario.getEmail(),
                    "role", usuario.getRole(),
                    "criadoEm", usuario.getCriadoEm(),
                    "ultimoLogin", usuario.getUltimoLogin()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Usuário não encontrado", "message", e.getMessage()));
        }
    }

    @GetMapping("/health")
    @Operation(summary = "Health Check", description = "Verifica se o serviço está funcionando")
    @ApiResponse(responseCode = "200", description = "Serviço está operacional")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "Auth Service",
                "message", "Auth Service is running!",
                "timestamp", String.valueOf(System.currentTimeMillis())
        ));
    }

    @PostMapping("/logout")
    @PreAuthorize("hasAnyRole('USUARIO', 'ADMIN', 'MODERADOR')")
    @Operation(summary = "Logout", description = "Logout do usuário (invalidar token no frontend)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Instruções de logout retornadas com sucesso",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado - token JWT inválido ou não fornecido",
                    content = @Content)
    })
    public ResponseEntity<?> logout() {
        // Com JWT stateless, o logout é feito no frontend removendo o token
        return ResponseEntity.ok(Map.of(
                "message", "Logout realizado com sucesso",
                "instruction", "Remova o token JWT do armazenamento local"
        ));
    }
}
