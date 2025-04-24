package com.praticando.screenmatch.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
// O @JsonAlias é utilizado para mapear campos do JSON para atributos da classe, permitindo que o Jackson reconheça diferentes nomes que podem ser usados na resposta JSON.
// Neste caso, vamos usar @JsonAlias para que o campo "Title" do JSON seja mapeado para o atributo "titulo" da classe DadosSerie.

// O @JsonIgnoreProperties é usado para ignorar campos específicos do JSON que não são necessários na classe.
@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosSerie(@JsonAlias("Title") String titulo,
                         @JsonAlias("totalSeasons") Integer totalTemporadas,
                         @JsonAlias("imdbRating") String avaliacao
// O @JsonAlias é usado apenas para leitura de dados do JSON, permitindo que múltiplos nomes sejam reconhecidos ao desserializar.
// Já o @JsonProperty pode ser utilizado tanto para leitura quanto para escrita, ou seja, ele define como o atributo será nomeado tanto ao ler do JSON quanto ao gerar um JSON a partir da classe.
            ) {
}
