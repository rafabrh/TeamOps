package com.rafadev.teamops.web.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class LinkProjectsRequest {
    @NotEmpty
    private List<String> projectIds; // aceita UUID ou code (ex.: PRJ-7F3A2D)

    public List<String> getProjectIds() {
        return projectIds; }

    public void setProjectIds(List<String> projectIds) {
        this.projectIds = projectIds; }
}