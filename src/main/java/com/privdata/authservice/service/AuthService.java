package com.privdata.authservice.service;

import com.privdata.authservice.dto.request.RegisterRequestDTO;
import com.privdata.authservice.dto.response.RegisterResponseDTO;
import com.privdata.authservice.enums.UserStatus;
import com.privdata.authservice.model.Role;
import com.privdata.authservice.model.User;
import com.privdata.authservice.repository.CompanyRepository;
import com.privdata.authservice.repository.RoleRepository;
import com.privdata.authservice.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;

//    public RegisterResponseDTO register(RegisterRequestDTO registerRequestDTO){
//        if (userRepository.existsByEmail(registerRequestDTO.getEmail())){
//            throw new RuntimeException("El email ingresado ya existe");
//        }
////        if (userRepository.existsByDocumentNumber(registerRequestDTO.getDocumentNumber())){
////            throw new RuntimeException("El rut ingresa ya existe")
////        }
//
//        User user = modelMapper.map(registerRequestDTO,User.class);
//
//        Role role = roleRepository.findByName("EMPLOYEE")
//                .orElseThrow(()-> new RuntimeException("Rol EMPLOYEE no existe"));
//        String hash = passwordEncoder.encode(registerRequestDTO.getPassword());
//
//        user.setPasswordHash(hash);
//        user.setRoleId(role);
//        user.setStatus(UserStatus.ACTIVE);
//
//        return modelMapper.map(userRepository.save(user), RegisterResponseDTO.class);
//    }
}
