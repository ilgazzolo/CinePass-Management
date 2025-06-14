package com.api.boleteria.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class MovieRequestDTO {

    @NotBlank(message = "this field is required")
    private String title;

    @NotNull(message = "this field is required")
    private Integer duration;

    @NotBlank(message = "this field is required")
    private String genre;

    @NotBlank(message = "this field is required")
    private String director;

    @NotBlank(message = "this field is required")
    private String rating;

    @NotBlank(message = "this field is required")
    private String synopsis;

    private List<Long> functionListId;
}
