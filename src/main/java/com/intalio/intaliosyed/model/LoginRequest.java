package com.intalio.intaliosyed.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@NoArgsConstructor
public class LoginRequest {
    private String username;
    private String password;
    private Collection<String> roles;
}
