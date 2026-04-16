package com.privdata.authservice.security;

import com.privdata.authservice.dto.request.LoginRequestDTO;
import com.privdata.authservice.dto.request.RegisterRequestDTO;
import com.privdata.authservice.dto.response.LoginResponseDTO;
import com.privdata.authservice.dto.response.RegisterResponseDTO;
import com.privdata.authservice.enums.UserStatus;
import com.privdata.authservice.model.Company;
import com.privdata.authservice.model.Role;
import com.privdata.authservice.model.SecurityUser;
import com.privdata.authservice.model.User;
import com.privdata.authservice.model.UserRole;
import com.privdata.authservice.repository.CompanyRepository;
import com.privdata.authservice.repository.RoleRepository;
import com.privdata.authservice.repository.UserRepository;
import com.privdata.authservice.repository.UserRoleRepository;
import com.privdata.authservice.service.AuthService;
import com.privdata.authservice.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RoleService roleService;

    @Override
    public RegisterResponseDTO register(RegisterRequestDTO request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El correo ya se encuentra registrado"
            );
        }

        //cambiar por metodo que valide id de la empresa
        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Empresa no encontrada"
                ));
        /////////////////////

        Role defaultRole = roleService.findByName("USER");

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setStatus(UserStatus.PENDING);
        user.setCompany(company);

        User savedUser = userRepository.save(user);

        UserRole userRole = new UserRole();
        userRole.setUser(savedUser);
        userRole.setRole(defaultRole);

        userRoleRepository.save(userRole);

        return new RegisterResponseDTO(
                savedUser.getId(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getEmail(),
                savedUser.getStatus(),
                savedUser.getCompany().getId()
        );
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Credenciales inválidas"
                ));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Credenciales inválidas"
            );
        }

        if (user.getStatus() != UserStatus.ACTIVE && user.getStatus() != UserStatus.PENDING) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Usuario no habilitado para iniciar sesión"
            );
        }

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        SecurityUser securityUser = new SecurityUser(user);
        String jwtToken = jwtService.generateToken(securityUser);

        return new LoginResponseDTO(jwtToken);
    }
}