package com.privdata.authservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateRoleRequestDTO {

    @NotBlank(message = "El nombre del rol es obligatorio")
    private String name;

    @NotBlank(message = "La descripción es obligatoria")
    private String description;
}
