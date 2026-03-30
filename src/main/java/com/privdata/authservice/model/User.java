package com.privdata.authservice.model;

import com.privdata.authservice.enums.UserStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "first_name", nullable = false)
    private String firstName;
    @Column(name = "last_name", nullable = false)
    private String lastName;
    @Column(name = "email", nullable = false)
    private String email;
    @Column(name = "password_Hash", nullable = false)
    private String passwordHash;
    @Column(name = "phone", nullable = false)
    private String phone;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserStatus status;

    private LocalDateTime lastLoginAt;//manejar desde la logica

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @OneToMany(mappedBy = "user_id", cascade = CascadeType.ALL)
    private List<User> users;

    @OneToMany(mappedBy = "user_id", cascade = CascadeType.ALL)
    private List<RefreshToken> tokens;
}
