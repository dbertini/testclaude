package com.libreria.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository Spring Data JPA per utente (User Entity) con permessi di accesso
 */
@Repository
public interface UserRepo extends JpaRepository<com.libreria.model.entity.UserEntity, String> {

    /**
     * Cerca utente per username o email (caso insensitivo)
     */
    @Query("SELECT u FROM com.libreria.model.entity.UserEntity u " +
           "WHERE LOWER(u.username) LIKE LOWER(:username) OR LOWER(u.email) LIKE LOWER(:email)")
    Optional<com.libreria.model.entity.UserEntity> findUserByUsernameOrEmail(@Param("username") String username, @Param("email") String email);

    /**
     * Verifica se utente esiste per username o email
     */
    boolean existsByUsername(String username) || existsByEmail(String email);

    /**
     * Cerca utente per ID
     */
    Optional<com.libreria.model.entity.UserEntity> findById(String id);

}
