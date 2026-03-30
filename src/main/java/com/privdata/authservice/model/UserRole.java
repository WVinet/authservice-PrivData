package com.privdata.authservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "user_roles")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRole {
    @Id
    @GeneratedValue
    private UUID id;


    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;


    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
