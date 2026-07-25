package com.ejada.project.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@OpenAPIDefinition(
    info = @Info(
        title = "E-Commerce API",
        version = "1.0",
        description = """
Spring Boot E-Commerce Backend

Authentication:
- Register using /api/auth/register
- Login using /api/auth/login
- Click Authorize and paste the JWT
- Endpoints marked with 🔒 require authentication.
"""
    )
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT"
)
public class OpenApiConfig {
}