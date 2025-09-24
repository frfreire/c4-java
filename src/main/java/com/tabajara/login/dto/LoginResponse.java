package com.tabajara.login.dto;

import com.tabajara.login.model.Role;

public class LoginResponse {
    private String token;
    private String tipo = "Bearer";
    private String username;
    private String email;
    private Role role;
    private Long expiraEm;

    public LoginResponse() {}

    public LoginResponse(String token, String username, String email, Role role, Long expiraEm) {
        this.token = token;
        this.username = username;
        this.email = email;
        this.role = role;
        this.expiraEm = expiraEm;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public Long getExpiraEm() { return expiraEm; }
    public void setExpiraEm(Long expiraEm) { this.expiraEm = expiraEm; }
}
