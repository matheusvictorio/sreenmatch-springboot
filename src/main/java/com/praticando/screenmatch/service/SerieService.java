package com.praticando.screenmatch.service;

import com.praticando.screenmatch.dto.EpisodiosDTO;
import com.praticando.screenmatch.dto.SerieDTO;
import com.praticando.screenmatch.model.Serie;
import com.praticando.screenmatch.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Service
public class SerieService {
    @Autowired
    private SerieRepository serieRepository;

    public List<SerieDTO> obterTodasSeries() {
        return converterDados(serieRepository.findAll());
    }

    public List<SerieDTO> obterTop5Serie() {
        return converterDados(serieRepository.findTop5ByOrderByAvaliacaoDesc());

    }

    private List<SerieDTO> converterDados(List<Serie> series){
        return series.stream()
                .map(s -> new SerieDTO(s.getId(), s.getTitulo(), s.getTotalTemporadas(), s.getAvaliacao(), s.getGenero(), s.getAtores(), s.getPoster(), s.getSinopse()))
                .collect(Collectors.toList());
    }

    public List<SerieDTO> obterTop5Episodios() {
        return converterDados(serieRepository.encontrarEpisodiosMaisRecentes());
    }

    public SerieDTO obterSeriePorId(Long id) {
        Optional<Serie> serie = serieRepository.findById(id);

        if(serie.isPresent()){
            return new SerieDTO(serie.get().getId(), serie.get().getTitulo(), serie.get().getTotalTemporadas(), serie.get().getAvaliacao(), serie.get().getGenero(), serie.get().getAtores(), serie.get().getPoster(), serie.get().getSinopse());
        }
        return null;
    }

    public List<EpisodiosDTO> obterTemporadasTodas(Long id) {
        Optional<Serie> serie = serieRepository.findById(id);

        if(serie.isPresent()){

            Serie s = serie.get();
            return s.getEpisodios().stream()
                    .map(e -> new EpisodiosDTO(e.getTemporada(), e.getNumeroEpisodio(), e.getTitulo()))
                    .collect(Collectors.toList());
        }
        return null;
    }
}
