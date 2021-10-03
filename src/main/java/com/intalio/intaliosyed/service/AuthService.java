package com.intalio.intaliosyed.service;

import com.intalio.intaliosyed.config.security.UserDetailsImpl;
import com.intalio.intaliosyed.entity.Role;
import com.intalio.intaliosyed.entity.User;
import com.intalio.intaliosyed.exception.RegisterFailedException;
import com.intalio.intaliosyed.model.ERole;
import com.intalio.intaliosyed.model.JwtResponse;
import com.intalio.intaliosyed.model.LoginRequest;
import com.intalio.intaliosyed.model.RegistrationResponse;
import com.intalio.intaliosyed.repository.RoleRepository;
import com.intalio.intaliosyed.repository.UserRepository;
import com.intalio.intaliosyed.util.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthenticationManager authenticationManager, JwtUtils jwtUtils, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<JwtResponse> login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        final UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        final List<String> roles = userDetails
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        final String jwt = jwtUtils.generateJwtToken(authentication);

        return Optional.of(
                new JwtResponse(
                        jwt,
                        roles
                )
        );
    }

    public Optional<RegistrationResponse> register(LoginRequest registerRequest) throws RegisterFailedException {
        final String username = registerRequest.getUsername();
        final String password = registerRequest.getPassword();
        final Collection<String> roles = registerRequest.getRoles();

        if ((null == username || username.trim().isEmpty()) || (null == password || password.trim().isEmpty()) || (null == roles || roles.isEmpty())) {
            throw new RuntimeException("Invalid Request. Username, Password and Roles should not be blank");
        }

        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            throw new RegisterFailedException("Username already exists", HttpStatus.BAD_REQUEST);
        }

        Collection<Role> r = roleRepository.findByNameIn(roles.stream().map(ERole::valueOf).collect(Collectors.toList()));

        final User user = new User(
                username,
                passwordEncoder.encode(password),
                r
        );

        log.info("saving user...");
        return Optional.of(RegistrationResponse.builder().userId(userRepository.save(user).getId()).build());
    }
}
