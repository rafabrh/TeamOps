package com.rafadev.teamops.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AddMembersRequest {

    @NotEmpty
    private List<@NotBlank String> logins;

}
