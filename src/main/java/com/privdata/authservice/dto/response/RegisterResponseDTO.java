package com.privdata.authservice.dto.response;

import com.privdata.authservice.enums.UserStatus;
import com.privdata.authservice.model.Company;
import lombok.Data;

import java.util.UUID;

@Data
public class RegisterResponseDTO {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private UserStatus status;
    private Company companyId;
}
