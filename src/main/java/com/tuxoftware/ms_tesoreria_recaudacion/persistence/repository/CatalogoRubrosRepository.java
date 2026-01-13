package com.tuxoftware.ms_tesoreria_recaudacion.persistence.repository;

import com.tuxoftware.ms_tesoreria_recaudacion.persistence.entity.CatalogoRubros;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CatalogoRubrosRepository extends JpaRepository<CatalogoRubros, String> {
    // Aquí irían consultas agrupadas por nivel CRI para reportes
}