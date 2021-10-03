package com.intalio.intaliosyed.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collection;

@Data
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private Collection<String> roles;
}
