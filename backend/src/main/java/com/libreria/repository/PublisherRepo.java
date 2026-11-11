package com.libreria.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository Spring Data JPA per case editrici (Publisher Entity)
 */
@Repository
public interface PublisherRepo extends JpaRepository<com.libreria.model.entity.PublisherEntity, String> {

    /**
     * Cerca case editrici per nome con correlazioni complete
     */
    @Query("SELECT new com.libreria.model.dto.PublisherSummaryResponse(p.id, p.denominazione, p.sedeCitta, p.p_a, p.foundedYear) " +
           "FROM com.libreria.model.entity.PublisherEntity p " +
           "WHERE LOWER(p.denominazione) LIKE LOWER(%:query%) " +
           "ORDER BY p.denominazione")
    List<PublisherSummaryResponse> searchPublishers(@Param("query") String query);

    /**
     * Cerca editore per ID
     */
    Optional<PublisherEntity> findById(String id);

}
