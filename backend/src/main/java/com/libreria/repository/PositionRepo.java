package com.libreria.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository Spring Data JPA per posizione (Position Entity)
 */
@Repository
public interface PositionRepo extends JpaRepository<com.libreria.model.entity.PositionEntity, String> {

    /**
     * Cerca posizioni per nome con correlazioni complete
     */
    @Query("SELECT new com.libreria.model.dto.PositionSummaryResponse(p.id, p.nomeComune, p.paese, p.codicePa, p.natura) " +
           "FROM com.libreria.model.entity.PositionEntity p " +
           "WHERE LOWER(p.nomeComune) LIKE LOWER(%:query%) " +
           "ORDER BY p.nomeComune")
    List<PositionSummaryResponse> searchPositions(@Param("query") String query);

    /**
     * Cerca posizione per ID
     */
    PositionEntity findById(String id);

}
