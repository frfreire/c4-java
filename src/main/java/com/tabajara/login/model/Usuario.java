package com.tabajara.login.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 50)
    @Column(unique = true)
    private String username;

    @NotBlank
    @Email
    @Size(max = 100)
    @Column(unique = true)
    private String email;

    @NotBlank
    @Size(max = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USUARIO;

    private boolean ativo = true;

    @Column(name = "conta_nao_expirada")
    private boolean contaNaoExpirada = true;

    @Column(name = "conta_nao_bloqueada")
    private boolean contaNaoBloqueada = true;

    @Column(name = "credencial_nao_expirada")
    private boolean credencialNaoExpirada = true;

    @Column(name = "criado_em")
    private LocalDateTime criadoEm = LocalDateTime.now();

    @Column(name = "ultimo_login")
    private LocalDateTime ultimoLogin;

    public Usuario() {}

    public Usuario(String username, String email, String password, Role role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return List.of(new SimpleGrantedAuthority("ROLE_" + this.role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return credencialNaoExpirada;
    }

    @Override
    public boolean isAccountNonLocked() {
        return contaNaoBloqueada;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return contaNaoExpirada;
    }

    @Override
    public boolean isEnabled() {
        return ativo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime createdAt) { this.criadoEm = createdAt; }

    public LocalDateTime getUltimoLogin() { return ultimoLogin; }
    public void setUltimoLogin(LocalDateTime lastLogin) { this.ultimoLogin = lastLogin; }
}
