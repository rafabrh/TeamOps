package com.rafadev.teamops.web;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
public class HelloController {

    @GetMapping("/health")
    public String health() {
        return  "OK"; }


    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('COLLAB')")
    @GetMapping("/me")
    public String me() {
        return "auth OK"; }
}
