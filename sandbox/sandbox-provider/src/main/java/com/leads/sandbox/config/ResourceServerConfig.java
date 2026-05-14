package com.leads.sandbox.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.Collections;
import java.util.List;


@EnableWebSecurity
@Configuration(proxyBeanMethods = false)
@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "security.enabled", havingValue = "true")
public class ResourceServerConfig {
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
            .cors(Customizer.withDefaults())
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/v3/**", "/swagger-ui/**").permitAll()
                    .anyRequest().authenticated()
            )
            .csrf(AbstractHttpConfigurer::disable)
            .oauth2ResourceServer(oauth2 -> oauth2
                    .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            );
    return http.build();
  }

  private JwtAuthenticationConverter jwtAuthenticationConverter() {
    JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

    converter.setJwtGrantedAuthoritiesConverter(
            jwt -> {
              List<String> userRoleAuthorities = jwt.getClaimAsStringList("authorities");

              if (userRoleAuthorities == null) {
                userRoleAuthorities = Collections.emptyList();
              }

              JwtGrantedAuthoritiesConverter scopesConverter = new JwtGrantedAuthoritiesConverter();

              Collection<GrantedAuthority> scopeAuthorities = scopesConverter.convert(jwt);

              scopeAuthorities
                      .addAll(userRoleAuthorities.stream()
                              .map(SimpleGrantedAuthority::new)
                              .toList());
              return scopeAuthorities;
            }
    );
    return converter;
  }

}
