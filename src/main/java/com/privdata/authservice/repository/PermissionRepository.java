package com.privdata.authservice.repository;

import com.privdata.authservice.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PermissionRepository extends JpaRepository<UUID, Permission> {
}
