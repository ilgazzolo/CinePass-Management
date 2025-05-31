package com.api.boleteria.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class MovieRequestDTO {

    @NotNull(message = "this field is required")
    private String title;

    @NotNull(message = "this field is required")
    private String duration;

    @NotNull(message = "this field is required")
    private String genre;

    @NotNull(message = "this field is required")
    private String director;

    @NotNull(message = "this field is required")
    private String rating;

    @NotNull(message = "this field is required")
    private String synopsis;

    @NotNull(message = "this field is required")
    private Long functionId;
}
