package com.intalio.intaliosyed.controller;

import com.intalio.intaliosyed.dto.BaseResponse;
import com.intalio.intaliosyed.entity.Role;
import com.intalio.intaliosyed.entity.User;
import com.intalio.intaliosyed.exception.RegisterFailedException;
import com.intalio.intaliosyed.model.ERole;
import com.intalio.intaliosyed.model.JwtResponse;
import com.intalio.intaliosyed.model.LoginRequest;
import com.intalio.intaliosyed.model.RegistrationResponse;
import com.intalio.intaliosyed.repository.RoleRepository;
import com.intalio.intaliosyed.repository.UserRepository;
import com.intalio.intaliosyed.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(AuthController.PATH)
public class AuthController {

    public static final String PATH = "/auth/user";

    private final AuthService authService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthService authService, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void createUser() throws IOException {

        final Optional<User> optionalUser = userRepository.findByUsername("root");

        if (!optionalUser.isPresent()) {

            final Role migrate = new Role(ERole.ROLE_MIGRATE);
            final Role read = new Role(ERole.ROLE_READ);

            List<Role> roles = Arrays.asList(migrate, read);
//        List<Role> roles = Collections.singletonList(read);

            User user = new User("root", passwordEncoder.encode("root"), roleRepository.saveAll(roles));

            userRepository.save(user);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<JwtResponse>> login(@RequestBody LoginRequest loginRequest) {
        JwtResponse response = authService.login(loginRequest).orElseThrow(() -> new RuntimeException("ERROR"));
        BaseResponse<JwtResponse> r = new BaseResponse<>(HttpStatus.ACCEPTED.value(), response, true);
        return new ResponseEntity<>(r, HttpStatus.ACCEPTED);
    }

    @PostMapping("/register")
    public ResponseEntity<BaseResponse<RegistrationResponse>> register(@RequestBody LoginRequest registerRequest) throws RegisterFailedException {
        RegistrationResponse response = authService.register(registerRequest).orElseThrow(() -> new RuntimeException("ERROR"));
        BaseResponse<RegistrationResponse> r = new BaseResponse<>(HttpStatus.CREATED.value(), response, true);
        return new ResponseEntity<>(r, HttpStatus.CREATED);
    }
}
