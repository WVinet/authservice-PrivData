package com.privdata.authservice.dto.request;

import com.privdata.authservice.model.Company;
import lombok.Data;

@Data
public class RegisterRequestDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Company companyId;
}
