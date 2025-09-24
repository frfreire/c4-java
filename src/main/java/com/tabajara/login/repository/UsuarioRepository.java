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

    // Renomeado para seguir a convenção do Spring Data JPA
    Optional<Usuario> findByUsername(String username);

    // Renomeado para seguir a convenção do Spring Data JPA
    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByUsernameOrEmail(String username, String email);

    // Renomeado para seguir a convenção do Spring Data JPA
    boolean existsByUsername(String username);

    // Renomeado para seguir a convenção do Spring Data JPA
    boolean existsByEmail(String email);

    @Modifying
    @Query("UPDATE Usuario u SET u.ultimoLogin = :ultimoLogin WHERE u.id = :userId")
    void updateLastLogin(@Param("userId") Long userId, @Param("ultimoLogin") LocalDateTime ultimoLogin);
}