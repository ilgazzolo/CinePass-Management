package com.api.boleteria.dto.request;

import com.api.boleteria.model.Function;
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

    @NotNull(message = "this field is required")
    private List<Function> functionList;
}
