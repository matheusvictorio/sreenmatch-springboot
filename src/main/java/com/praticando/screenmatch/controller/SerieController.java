package com.praticando.screenmatch.controller;

import com.praticando.screenmatch.dto.EpisodiosDTO;
import com.praticando.screenmatch.dto.SerieDTO;
import com.praticando.screenmatch.service.SerieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/series")
public class SerieController {

    @Autowired
    private SerieService serieService;

    @GetMapping
    public List<SerieDTO> obterSeries(){
        return serieService.obterTodasSeries();
    }
    @GetMapping("/top5")
    public List<SerieDTO> obterTop5Serie(){
        return serieService.obterTop5Serie();
    }

    @GetMapping("/lancamentos")
    public List<SerieDTO> obterTop5Episodios(){
        return serieService.obterTop5Episodios();
    }

    @GetMapping("/{id}")
    public SerieDTO obterSeriePorId(@PathVariable Long id){
        return serieService.obterSeriePorId(id);
    }

    @GetMapping("/{id}/temporadas/todas")
    public List<EpisodiosDTO> obterTemporadasTodas(@PathVariable Long id){
        return serieService.obterTemporadasTodas(id);
    }
    @GetMapping("/{id}/temporadas/{numero}")
    public List<EpisodiosDTO> obterTemporadaPorNumero(@PathVariable Long id, @PathVariable Long numero){
        return serieService.obterTemporadaPorNumero(id, numero);
    }
}
