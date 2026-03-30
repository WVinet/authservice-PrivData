package com.privdata.authservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefreshToken {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "token", nullable = false)
    private String token;
    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;
    @Column(name = "revoked_at", nullable = false)
    private LocalDateTime revokedAt;
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
