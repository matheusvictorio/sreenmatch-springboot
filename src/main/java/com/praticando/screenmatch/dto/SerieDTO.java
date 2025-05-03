package com.praticando.screenmatch.dto;

import com.praticando.screenmatch.model.Categoria;
import jakarta.persistence.Cache;

public record SerieDTO (Long id
                       , String titulo
                       , Integer totalTemporadas
                       , Double avaliacao
                       , Categoria genero
                       , String atores
                       , String poster
                       , String sinopse) {
}
