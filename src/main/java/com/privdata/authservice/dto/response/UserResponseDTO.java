package com.privdata.authservice.dto.response;

import com.privdata.authservice.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {
    private UUID id;
    private String email;
    private UUID organizationId;
    private UUID personId;
    private UserStatus status;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}