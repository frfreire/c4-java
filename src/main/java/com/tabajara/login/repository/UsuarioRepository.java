package com.tabajara.login.repository;

import com.tabajara.login.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> procurarPorUsername(String username);

    Optional<Usuario> procurarPorEmail(String email);

    Optional<Usuario> procurarPorUsernameOuEmail(String username, String email);

    boolean verificaUsernameExiste(String username);

    boolean verificaEmailExiste(String email);

    @Modifying
    @Query("UPDATE Usuario u SET u.ultimoLogin = :ultimoLogin WHERE u.id = :userId")
    void updateLastLogin(@Param("userId") Long userId, @Param("lastLogin") LocalDateTime lastLogin);
}
