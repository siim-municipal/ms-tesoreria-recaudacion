package com.tuxoftware.ms_tesoreria_recaudacion.persistence.repository;

import com.tuxoftware.ms_tesoreria_recaudacion.persistence.entity.CatalogoRubros;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CatalogoRubrosRepository extends JpaRepository<CatalogoRubros, String> {
    Optional<CatalogoRubros> findByClaveConcepto(String claveConcepto);
}