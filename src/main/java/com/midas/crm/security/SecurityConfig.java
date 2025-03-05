package com.midas.crm.security;

import com.midas.crm.security.jwt.JwtAuthorizationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Autowired
    @Lazy
    private CustomUserDetailsService customUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Deshabilita CSRF para APIs sin estado (JWT)
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Usa la configuración de CORS
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        // Endpoints de autenticación y registro
                        .requestMatchers(
                                "/api/authentication/sign-in",
                                "/api/authentication/sign-up"
                        ).permitAll()
                        // Endpoints públicos adicionales (por ejemplo, promociones y carga masiva)
                        .requestMatchers(
                                "/api/cliente-promocion",
                                "/api/user/crear-masivo"
                        ).permitAll()
                        // Endpoints de usuarios (listar, cambiar rol, obtener por id, búsqueda, soft delete, etc.)
                        .requestMatchers(
                                "/api/user/listar",
                                "/api/user/change/**",   // Permite /api/user/change/{role} o /api/user/change/{role}/{userId}
                                "/api/user",             // Por ejemplo, para obtener el usuario actual
                                "/api/user/**",          // Cubre rutas como /api/user/{userId}, /api/user/soft/{userId} y /api/user/buscar
                                "/api/user/buscar",
                                "/api/user/soft/**"
                        ).permitAll()
                        // Cualquier otra solicitud requiere autenticación
                        .anyRequest().authenticated()
                );


        http.addFilterBefore(jwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ✅ CONFIGURACIÓN CORRECTA DE CORS
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        return request -> {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowedOrigins(List.of(
                    "http://localhost:5200",
                    "https://seguimiento-egresado.web.app",
                    "https://apisozarusac.com",
                    "https://apisozarusac.com/BackendJava",
                    "https://www.api.midassolutiongroup.com",
                    "https://project-a16f1.web.app"
            ));
            config.setAllowedMethods(List.of("GET", "POST", "DELETE", "PUT", "OPTIONS"));
            config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
            config.setExposedHeaders(List.of("Authorization"));
            config.setAllowCredentials(true);
            return config;
        };
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(
                                "http://localhost:5200",
                                "https://apisozarusac.com/BackendJava",
                                "https://seguimiento-egresado.web.app",
                                "https://apisozarusac.com",
                                "https://www.api.midassolutiongroup.com",
                                "https://project-a16f1.web.app"
                        )
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("Authorization", "Content-Type")
                        .allowCredentials(true);
            }
        };
    }
}
