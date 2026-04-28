package com.privdata.authservice.service;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.privdata.authservice.dto.response.PermissionResponseDTO;
import com.privdata.authservice.dto.response.RoleResponseDTO;
import com.privdata.authservice.dto.response.UserResponseDTO;
import com.privdata.authservice.model.Permission;
import com.privdata.authservice.model.Role;
import com.privdata.authservice.model.RolePermissions;
import com.privdata.authservice.model.User;
import com.privdata.authservice.repository.PermissionRepository;
import com.privdata.authservice.repository.RolePermissionRepository;
import com.privdata.authservice.repository.RoleRepository;
import com.privdata.authservice.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final UserRepository userRepository;

    // ── Roles ─────────────────────────────────────────────────────────────────

    public List<RoleResponseDTO> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(this::toRoleDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public RoleResponseDTO createRole(String name, String description) {
        String normalized = name.trim().toUpperCase().replace(" ", "_");

        if (roleRepository.existsByName(normalized)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya existe un rol con el nombre: " + normalized);
        }

        Role role = new Role();
        role.setName(normalized);
        role.setDescription(description);
        role.setIsActive(true);

        Role saved = roleRepository.save(role);
        return toRoleDTO(saved);
    }

    // ── Permisos de un rol ────────────────────────────────────────────────────

    @Transactional
    public void assignPermissionToRole(UUID roleId, UUID permissionId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rol no encontrado"));

        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Permiso no encontrado"));

        if (rolePermissionRepository.existsByRoleAndPermission(role, permission)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El rol ya tiene ese permiso asignado");
        }

        RolePermissions rp = new RolePermissions();
        rp.setRole(role);
        rp.setPermission(permission);
        rp.setActive(true);
        rolePermissionRepository.save(rp);
    }

    @Transactional
    public void removePermissionFromRole(UUID roleId, UUID permissionId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rol no encontrado"));

        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Permiso no encontrado"));

        List<RolePermissions> matches = rolePermissionRepository
                .findByRoleAndIsActiveTrue(role)
                .stream()
                .filter(rp -> rp.getPermission().getId().equals(permission.getId()))
                .collect(Collectors.toList());

        if (matches.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "El rol no tiene ese permiso asignado");
        }

        rolePermissionRepository.deleteAll(matches);
    }

    // ── Permisos ──────────────────────────────────────────────────────────────

    public List<PermissionResponseDTO> getAllPermissions() {
        return permissionRepository.findAll().stream()
                .map(this::toPermissionDTO)
                .collect(Collectors.toList());
    }

    // ── Usuarios ──────────────────────────────────────────────────────────────

    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toUserDTO)
                .collect(Collectors.toList());
    }

    // ── Mappers privados ──────────────────────────────────────────────────────

    private RoleResponseDTO toRoleDTO(Role role) {
        List<PermissionResponseDTO> perms = rolePermissionRepository
                .findByRoleAndIsActiveTrue(role)
                .stream()
                .map(rp -> toPermissionDTO(rp.getPermission()))
                .collect(Collectors.toList());

        return new RoleResponseDTO(
                role.getId(),
                role.getName(),
                role.getDescription(),
                role.getIsActive(),
                role.getCreatedAt(),
                role.getUpdatedAt(),
                perms
        );
    }

    private PermissionResponseDTO toPermissionDTO(Permission p) {
        return new PermissionResponseDTO(
                p.getId(),
                p.getModule(),
                p.getAction(),
                p.getDescription(),
                p.isActive(),
                p.getCreatedAt()
        );
    }

    private UserResponseDTO toUserDTO(User u) {
        return new UserResponseDTO(
                u.getId(),
                u.getEmail(),
                u.getOrganizationId(),
                u.getPersonId(),
                u.getStatus(),
                u.isActive(),
                u.getCreatedAt(),
                u.getUpdatedAt()
        );
    }
}
