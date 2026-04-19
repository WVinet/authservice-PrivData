package com.privdata.authservice.repository;

import com.privdata.authservice.model.RolePermissions;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RolePermissionRepository extends JpaRepository<UUID, RolePermissions> {
}
