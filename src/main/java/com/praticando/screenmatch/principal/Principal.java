package com.praticando.screenmatch.principal;

import com.praticando.screenmatch.model.*;
import com.praticando.screenmatch.repository.SerieRepository;
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
    private List<DadosSerie> dadosSerie = new ArrayList<>();

    private SerieRepository serieRepository;

    List<Serie> series = new ArrayList<>();

    public Principal(SerieRepository serieRepository) {
        this.serieRepository = serieRepository;
    }


    public void exibeMenu() {
        var opcao = -1;
        while (opcao != 0) {
        var menu = """
                1 - Buscar séries
                2 - Buscar episódios
                3 - Listar séries buscadas
                4 - Buscar série por titulo
                5 - Buscar sério por ator
                6 - Buscar top 5 séries
                7 - Buscar série por categoria
                8 - Buscar série por numero de temporadas e avaliação
                9 - Buscar pelo título do episódio
                
                0 - Sair
                """;

        System.out.println(menu);
        opcao = sc.nextInt();
        sc.nextLine();

        switch (opcao) {
            case 1:
                buscarSerieWeb();
                break;
            case 2:
                buscarEpisodioPorSerie();
                break;
            case 3:
                listarSeriesBuscadas();
                break;
            case 4:
                buscarSeriePorTitulo();
                break;
            case 5:
                buscarSeriesPorAtor();
                break;
            case 6:
                buscarTop5Serie();
                break;
            case 7:
                buscarSeriePorCategoria();
                break;
            case 8:
                buscarSeriePorNumeroTemporadaAvaliacao();
                break;
            case 9:
                buscarEpisodioPorTitulo();
                break;
            case 0:
                System.out.println("Saindo...");
                break;
            default:
                System.out.println("Opção inválida");
        }
        }
    }


    private void buscarSerieWeb() {
        DadosSerie dados = getDadosSerie();
        Serie serie = new Serie(dados);
        serieRepository.save(serie);
        System.out.println(dados);
    }

    private DadosSerie getDadosSerie() {
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = sc.nextLine();
        var json = consumoApi.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        return dados;
    }

    private void buscarEpisodioPorSerie(){
        listarSeriesBuscadas();
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = sc.nextLine();

        Optional<Serie> serie = serieRepository.findByTituloContainingIgnoreCase(nomeSerie);
        if(serie.isPresent()){
            var serieEncontrada = serie.get();

            List<DadosTemporada> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumoApi.obterDados(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }
            temporadas.forEach(System.out::println);

            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d -> d.episodios().stream()
                            .map(e -> new Episodio(d.numero(), e)))
                    .collect(Collectors.toList());

            serieEncontrada.setEpisodios(episodios);
            serieRepository.save(serieEncontrada);
        } else {
            System.out.println("Série não encontrada");
        }


    }
    private void listarSeriesBuscadas() {
        series = serieRepository.findAll();
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }

    private void buscarSeriePorTitulo() {
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = sc.nextLine();

        Optional<Serie> serieBuscada = serieRepository.findByTituloContainingIgnoreCase(nomeSerie);

        if(serieBuscada.isPresent()){

            System.out.println("Séries encontradas: " + serieBuscada.get());

        } else {
            System.out.println("Série não encontrada");
        }
    }

    private void buscarSeriesPorAtor() {
        System.out.println("Digite o nome do ator para busca");
        var nomeAtor = sc.nextLine();
        System.out.println("Avaliação mínima para a busca");
        var avaliacao = sc.nextDouble();
        List<Serie> seriesPorAtor = serieRepository.findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(nomeAtor, avaliacao);
        System.out.println("Séries em que o ator '" + nomeAtor + "' aparece: ");
        seriesPorAtor.forEach(s ->
                System.out.println(s.getTitulo() + " avaliação: " + s.getAvaliacao()));
    }

    private void buscarTop5Serie() {
        List<Serie> serieTop = serieRepository.findTop5ByOrderByAvaliacaoDesc();
        System.out.println("Séries com maior avaliação: ");
        serieTop.forEach(s ->
                System.out.println(s.getTitulo() + " avaliação: " + s.getAvaliacao()));
    }
    private void buscarSeriePorCategoria() {
        System.out.println("Digite a categoria para busca");
        var nomeCategoria = sc.nextLine();
        Categoria categoria = Categoria.fromPortugues(nomeCategoria);

        List<Serie> seriesPorCategoria = serieRepository.findByGenero(categoria);
        System.out.println("Séries em que a categoria '" + categoria + "' aparece: ");
        seriesPorCategoria.forEach(s ->
                System.out.println(s.getTitulo() + " avaliação: " + s.getAvaliacao()));
    }

    private void buscarSeriePorNumeroTemporadaAvaliacao() {
        System.out.println("Digite o número de temporadas para busca: ");
        var numeroTemporada = sc.nextInt();
        System.out.println("Digite a avaliação mínima para a busca: ");
        var avaliacao = sc.nextDouble();

        List<Serie> seriesNichadas = serieRepository.seriesPorTemporadasEAvaliacao(numeroTemporada, avaliacao);
        System.out.println("Séries em que a quantidade de temporadas é igual ou menor que " + numeroTemporada + " e a avaliação é igual ou maior que " + avaliacao);
        seriesNichadas.forEach(s->
                System.out.println(s.getTitulo() + ", temporadas: " + s.getTotalTemporadas() + ",avaliação: " + s.getAvaliacao()));
    }

    private void buscarEpisodioPorTitulo() {
        System.out.println("Digite o nome do episódio para busca");
        var nomeEpisodio = sc.nextLine();

        List<Episodio> episodios = serieRepository.episodiosPorTrecho(nomeEpisodio);
        System.out.println("Episódios com o nome '" + nomeEpisodio + "': ");
        episodios.forEach(e -> System.out.printf("Série: %s, Temporada: %d, Título: %s\n", e.getSerie().getTitulo(), e.getSerie().getTotalTemporadas(), e.getTitulo()));
    }
}

