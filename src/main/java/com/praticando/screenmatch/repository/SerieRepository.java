package com.praticando.screenmatch.repository;

import com.praticando.screenmatch.model.Categoria;
import com.praticando.screenmatch.model.Episodio;
import com.praticando.screenmatch.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SerieRepository extends JpaRepository<Serie, Long> {
    // o nome do metodo ja realiza a query
    Optional<Serie> findByTituloContainingIgnoreCase(String titulo);


    List<Serie> findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(String nomeAtor, Double avaliacao);

    List<Serie> findTop5ByOrderByAvaliacaoDesc();

    List<Serie> findByGenero(Categoria categoria);

    // Derived query
    //List<Serie> findByTotalTemporadasLessThanEqualAndAvaliacaoGreaterThanEqual(Integer totalTemporadas, Double avaliacao);

    //JPQL
    @Query("select s from Serie s where s.totalTemporadas <= :totalTemporadas and s.avaliacao >= :avaliacao")
    List<Serie> seriesPorTemporadasEAvaliacao(Integer totalTemporadas, Double avaliacao);

    @Query("select e from Serie s join s.episodios e where e.titulo ILIKE %:nomeEpisodio%")
    List<Episodio> episodiosPorTrecho(String nomeEpisodio);


    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE s = :serie ORDER BY e.avaliacao DESC LIMIT 5")
    List<Episodio> topEpisodiosPorSerie(Serie serie);

    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE s = :serie AND YEAR(e.dataLancamento) >= :anoLimite")
    List<Episodio> episodiosPorSerieEAno(Serie serie, int anoLimite);

    @Query("SELECT s FROM Serie s " +
            "JOIN s.episodios e " +
            "GROUP BY s " +
            "ORDER BY MAX(e.dataLancamento) DESC LIMIT 5")
    List<Serie> encontrarEpisodiosMaisRecentes();

    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE s.id = :id AND e.temporada = :numero")
    List<Episodio> episodiosPorTemporada(Long id, Long numero);
}
