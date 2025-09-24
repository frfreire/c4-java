package com.tabajara.login.service;

import com.tabajara.login.dto.LoginRequest;
import com.tabajara.login.dto.LoginResponse;
import com.tabajara.login.dto.RegisterRequest;
import com.tabajara.login.model.Usuario;

public interface IAutenticacaoService {

    LoginResponse login(LoginRequest loginRequest);
    Usuario register(RegisterRequest registerRequest);
    Usuario buscaPorUsername(String username);
    boolean verificaExistenciaUsername(String username);
    boolean verificaExistenciaEmail(String email);
}
