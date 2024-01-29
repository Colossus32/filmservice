package com.colossus.movie;

import com.colossus.user.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "movies")
public class Movie {

    public Movie() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    private String nameRu;

    @Column(nullable = false, unique = true)
    private String posterURL;

    @ManyToMany(mappedBy = "favoriteMovies")
    Set<User> favoritedByUsers = new HashSet<>();

}
