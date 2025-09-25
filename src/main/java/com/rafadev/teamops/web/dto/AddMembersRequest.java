package com.rafadev.teamops.web.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class AddMembersRequest {
    @NotEmpty
    private List<String> userIds;

    public List<String> getUserIds() {
        return userIds; }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds; }
}