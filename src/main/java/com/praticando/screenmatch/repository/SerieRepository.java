package com.praticando.screenmatch.repository;

import com.praticando.screenmatch.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SerieRepository extends JpaRepository<Serie, Long> {
    // o nome do metodo ja realiza a query
    Optional<Serie> findByTituloContainingIgnoreCase(String titulo);
}
