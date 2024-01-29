package com.colossus.updater;

import com.colossus.movie.Movie;
import com.colossus.movie.MovieRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@AllArgsConstructor

public class UpdaterService {

    @Value("${cron.apikey}")
    private static String apikey;

    private MovieRepository movieRepository;

    @Scheduled(cron = "${cron.time}")
    public void checkPremiers(){

        log.info("Updating premiers...{}", LocalDateTime.now());

        LocalDate localDate = LocalDate.now();
        int year = localDate.getYear();
        String month = String.valueOf(localDate.getMonth());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("https://kinopoiskapiunofficial.tech/api/v2.2/films/premieres?year=%d&month=%s", year, month)))
                .header("accept", "application/json")
                .header("X-API-KEY", apikey)
                .GET()
                .GET()
                .build();

        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(response.body());
            JsonNode itemsNode = jsonNode.get("items");
            TypeReference<List<Movie>> typeReference = new TypeReference<>() {};

            List<Movie> movieListFromAPI = mapper.readValue(itemsNode.toString(), typeReference);

            movieRepository.saveAll(movieListFromAPI);

        } catch (IOException | InterruptedException e) {
            log.error("Error while updating premiers...");
            throw new RuntimeException(e);
        }
    }
}
