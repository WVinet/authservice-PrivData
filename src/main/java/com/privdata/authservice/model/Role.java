package com.privdata.authservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "roles")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Role {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "description", nullable = false)
    private String description;
    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @OneToMany(mappedBy = "role_id", cascade = CascadeType.ALL)
    private List<Role> roles;
}
