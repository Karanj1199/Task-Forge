package com.mtpm.user;

import com.mtpm.security.Role;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(nullable = false)
    private String email;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(name = "password_hash")
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public User() {}

    public User(UUID id, UUID tenantId, String email, String displayName, String passwordHash, Role role) {
        this.id = id;
        this.tenantId = tenantId;
        this.email = email;
        this.displayName = displayName;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public UUID getId() { return id; }
    public UUID getTenantId() { return tenantId; }
    public String getEmail() { return email; }
    public String getDisplayName() { return displayName; }
    public String getPasswordHash() { return passwordHash; }
    public Role getRole() { return role; }
}
