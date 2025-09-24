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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
    @PreAuthorize("hasAnyAuthority('ROLE_USUARIO', 'ROLE_ADMIN', 'ROLE_MODERADOR')")
    @Operation(summary = "Perfil do usuário", description = "Retorna informações do usuário autenticado")
    public ResponseEntity<?> getProfile(Authentication authentication) {

        System.out.println("=== DEBUG PROFILE ENDPOINT ===");

        try {
            // Debug do Authentication
            System.out.println("Authentication object: " + authentication);
            System.out.println("Authentication is null: " + (authentication == null));

            if (authentication != null) {
                System.out.println("Authentication.getName(): " + authentication.getName());
                System.out.println("Authentication.getPrincipal(): " + authentication.getPrincipal());
                System.out.println("Authentication.getAuthorities(): " + authentication.getAuthorities());
                System.out.println("Authentication.isAuthenticated(): " + authentication.isAuthenticated());
            }

            // Debug do SecurityContext
            Authentication contextAuth = SecurityContextHolder.getContext().getAuthentication();
            System.out.println("SecurityContext Authentication: " + contextAuth);
            if (contextAuth != null) {
                System.out.println("Context Authentication.getName(): " + contextAuth.getName());
                System.out.println("Context Authentication.getPrincipal(): " + contextAuth.getPrincipal());
            }

            // Verificar se temos um nome de usuário
            String username = null;
            if (authentication != null && authentication.getName() != null) {
                username = authentication.getName();
            } else if (contextAuth != null && contextAuth.getName() != null) {
                username = contextAuth.getName();
            }

            System.out.println("Username a ser usado: " + username);

            if (username == null || username.isEmpty() || "anonymousUser".equals(username)) {
                System.out.println("Username inválido ou usuário anônimo");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Usuário não autenticado", "username", String.valueOf(username)));
            }

            // Buscar usuário
            System.out.println("Buscando usuário: " + username);
            Usuario usuario = autenticacaoService.buscaPorUsername(username);
            System.out.println("Usuário encontrado: " + (usuario != null ? usuario.getUsername() : "null"));

            if (usuario == null) {
                System.out.println("Usuário não encontrado no banco");
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Usuário não encontrado", "username", username));
            }

            // Criar resposta
            Map<String, Object> response = new HashMap<>();
            response.put("username", usuario.getUsername());
            response.put("email", usuario.getEmail());
            response.put("role", usuario.getRole());
            response.put("criadoEm", usuario.getCriadoEm());
            response.put("ultimoLogin", usuario.getUltimoLogin());
            response.put("debug", "SUCCESS");

            System.out.println("Retornando resposta: " + response);
            System.out.println("=== FIM DEBUG PROFILE ===");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("ERRO no getProfile: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "Erro interno",
                            "message", e.getMessage(),
                            "type", e.getClass().getSimpleName()
                    ));
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
    @PreAuthorize("hasAnyAuthority('ROLE_USUARIO', 'ROLE_ADMIN', 'ROLE_MODERADOR')")
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
