package com.privdata.authservice.controller;

import com.privdata.authservice.dto.request.LoginRequestDTO;
import com.privdata.authservice.dto.request.RegisterRequestDTO;
import com.privdata.authservice.dto.response.LoginResponseDTO;
import com.privdata.authservice.dto.response.RegisterResponseDTO;
import com.privdata.authservice.service.AuthService;
import com.privdata.authservice.shared.ApiResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponseDTO<RegisterResponseDTO>> register(@Valid @RequestBody RegisterRequestDTO request) {
        RegisterResponseDTO response = authService.register(request);

        ApiResponseDTO<RegisterResponseDTO> apiResponseDTO =
                new ApiResponseDTO<>(true, "Usuario registrado correctamente", response);

        return ResponseEntity.ok(apiResponseDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponseDTO<LoginResponseDTO>> login(@Valid @RequestBody LoginRequestDTO request) {
        LoginResponseDTO response = authService.login(request);

        ApiResponseDTO<LoginResponseDTO> apiResponseDTO =
                new ApiResponseDTO<>(true, "Inicio de sesión correcto", response);

        return ResponseEntity.ok(apiResponseDTO);
    }
}