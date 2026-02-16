package com.mtpm.auth;

import com.mtpm.security.JwtService;
import com.mtpm.security.JwtService;
import com.mtpm.security.Role;
import com.mtpm.tenant.Tenant;
import com.mtpm.tenant.TenantRepository;
import com.mtpm.user.User;
import com.mtpm.user.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final TenantRepository tenantRepo;
    private final UserRepository userRepo;
    private final JwtService jwtService;
    private final PasswordEncoder encoder;

    public AuthController(TenantRepository tenantRepo, UserRepository userRepo, JwtService jwtService, PasswordEncoder encoder) {
        this.tenantRepo = tenantRepo;
        this.userRepo = userRepo;
        this.jwtService = jwtService;
        this.encoder = encoder;
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignupRequest req) {
        UUID tenantId = UUID.randomUUID();
        Tenant tenant = new Tenant(tenantId, req.tenantSlug(), req.tenantName());
        tenantRepo.save(tenant);

        UUID userId = UUID.randomUUID();
        User user = new User(
                userId,
                tenantId,
                req.email(),
                req.displayName(),
                encoder.encode(req.password()),
                Role.OWNER
        );
        userRepo.save(user);

        String token = jwtService.createAccessToken(userId.toString(), tenantId.toString(), user.getRole().name(), user.getEmail());
        return ResponseEntity.ok(new AuthResponse(token, tenantId.toString(), user.getRole().name()));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        UUID tenantId = UUID.fromString(req.tenantId());

        var user = userRepo.findByTenantIdAndEmail(tenantId, req.email())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (user.getPasswordHash() == null || !encoder.matches(req.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        String token = jwtService.createAccessToken(user.getId().toString(), tenantId.toString(), user.getRole().name(), user.getEmail());
        return ResponseEntity.ok(new AuthResponse(token, tenantId.toString(), user.getRole().name()));
    }

    public record SignupRequest(
            @NotBlank String tenantSlug,
            @NotBlank String tenantName,
            @Email @NotBlank String email,
            @NotBlank String displayName,
            @NotBlank String password
    ) {}

    public record LoginRequest(
            @NotBlank String tenantId,
            @Email @NotBlank String email,
            @NotBlank String password
    ) {}

    public record AuthResponse(String accessToken, String tenantId, String role) {}
}
