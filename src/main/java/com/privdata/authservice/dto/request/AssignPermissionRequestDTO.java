package com.privdata.authservice.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignPermissionRequestDTO {

    @NotNull(message = "El id del permiso es obligatorio")
    private UUID permissionId;
}
