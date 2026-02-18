package com.mtpm.me;

import com.mtpm.security.JwtService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/me")
public class MeController {

    @GetMapping
    public Map<String, Object> me(Authentication auth) {
        var claims = (JwtService.JwtClaims) auth.getPrincipal();
        return Map.of(
                "userId", claims.userId(),
                "email", claims.email(),
                "tenantId", claims.tenantId(),
                "role", claims.role()
        );
    }
}
