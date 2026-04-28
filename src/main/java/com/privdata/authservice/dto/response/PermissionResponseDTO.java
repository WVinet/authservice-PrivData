package com.privdata.authservice.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PermissionResponseDTO {
    private UUID id;
    private String module;
    private String action;
    private String description;
    private boolean isActive;
    private LocalDateTime createdAt;
}