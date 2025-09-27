package com.rafadev.teamops.web.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class LinkProjectsRequest {

    @NotEmpty
    private List<String> projectIds; // aceita UUID ou code (ex.: PRJ-7F3A2D)

}