package com.praticando.screenmatch.principal;

import com.praticando.screenmatch.model.DadosEpisodio;
import com.praticando.screenmatch.model.DadosSerie;
import com.praticando.screenmatch.model.DadosTemporada;
import com.praticando.screenmatch.model.Episodio;
import com.praticando.screenmatch.service.ConsumoApi;
import com.praticando.screenmatch.service.ConverteDados;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


public class Principal {

    private Scanner sc = new Scanner(System.in);

    private ConsumoApi consumoApi = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();

    private final String ENDERECO= "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=6585022c";

    public void exibeMenu() {
        System.out.println("Digite o nome da série:");
        var nomeSerie = sc.nextLine();
        var json = consumoApi.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dadosSerie = conversor.obterDados(json, DadosSerie.class);
        System.out.println(dadosSerie);

        List<DadosTemporada> listaDadosTemporada = new ArrayList<>();

        for (int i = 1; i <= dadosSerie.totalTemporadas(); i++) {
            json = consumoApi.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + "&season=" + i + API_KEY);
            DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);

            listaDadosTemporada.add(dadosTemporada);
        }
        listaDadosTemporada.forEach(System.out::println);
        //lambda para imprimir os titulos dos episodios ao inves de usar for
        listaDadosTemporada.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));

        List<DadosEpisodio> dadosEpisodios = listaDadosTemporada.stream()
                .flatMap(t -> t.episodios().stream())
                .collect(Collectors.toList());

        dadosEpisodios.stream()
                .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
                .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
                .limit(5)
                .forEach(System.out::println);

        List<Episodio> episodios = listaDadosTemporada.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(d -> new Episodio(t.numero(), d))
                ).collect(Collectors.toList());

        episodios.forEach(System.out::println);

        System.out.println("Digite o trecho que deseja buscar no titulo do episodio:");
        var trechoTitulo = sc.nextLine();
        Optional<Episodio> episodioBuscado = episodios.stream()
                        .filter(e -> e.getTitulo().toLowerCase().contains(trechoTitulo.toLowerCase()))
                                .findFirst();
        if (episodioBuscado.isPresent()) {
            System.out.println("Episodio encontrado: " + episodioBuscado.get().getTitulo() + " na temporada " + episodioBuscado.get().getTemporada());
        }
        else {
            System.out.println("Episodio não encontrado");
        }

        System.out.println("A partir de que ano deseja buscar os episodios?");
        var ano = sc.nextInt();
        sc.nextLine();

        LocalDate dataBusca = LocalDate.of(ano, 1, 1);

        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        episodios.stream()
                .filter(e -> e.getDataLancamento() != null && e.getDataLancamento().isAfter(dataBusca))
                .forEach(e -> System.out.println(
                        "Temporada: " + e.getTemporada() +
                                ", Episodio: " + e.getTitulo() +
                                ", Data: " + e.getDataLancamento().format(formatador)

                        )
                );

    }
}
