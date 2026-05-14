package com.leads.sandbox.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration

@SecurityScheme(
        name = "Bearer Authentication",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class SwaggerConfig {

  @Bean
  OpenAPI apiInfo() {
    return new OpenAPI()
            .info(
                    new Info()
                            .title("Service Provider API")
                            .description("""
                                        Service Provider API Documentation
                                        
                                        ## Authentication
                                        This API uses JWT Bearer token authentication.
                                        
                                        ### How to use:
                                        1. Obtain a token from the authentication endpoint
                                        2. Click the 'Authorize' button above
                                        3. Enter: **Bearer your_token_here**
                                        4. All requests will automatically include the Authorization header
                                        """)
                            .version("1.0.0")
            )
            // Add security requirement for all endpoints
            .addSecurityItem(new SecurityRequirement()
                    .addList("Bearer Authentication"))
            .components(new Components()
                    // Security scheme is automatically added by @SecurityScheme annotation
            );
  }
}