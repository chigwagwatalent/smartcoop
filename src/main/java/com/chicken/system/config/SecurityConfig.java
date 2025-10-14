package com.chicken.system.config;

import com.chicken.system.services.UserServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserServices userServices,
                                                            BCryptPasswordEncoder encoder) {
        var provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userServices);
        provider.setPasswordEncoder(encoder);
        return provider;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        var cfg = new CorsConfiguration();
        cfg.setAllowedOriginPatterns(List.of("*"));   // any origin
        cfg.setAllowedMethods(List.of("*"));          // all methods
        cfg.setAllowedHeaders(List.of("*"));          // any header
        cfg.setExposedHeaders(List.of("Content-Disposition", "Authorization"));
        cfg.setAllowCredentials(true);
        cfg.setMaxAge(3600L);

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   DaoAuthenticationProvider authProvider) throws Exception {
        http
            .authenticationProvider(authProvider)
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf
                // allow cross-origin POST/PUT/DELETE to API without CSRF token
                .ignoringRequestMatchers("/v1/api/**", "/api/**")
            )
            .authorizeHttpRequests(auth -> auth
                // public endpoints (put these BEFORE anyRequest)
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()   // preflight
                .requestMatchers(
                    "/v1/api/**",        // your IoT + other APIs
                    "/api/**",           // any other APIs you created
                    "/auth/**",          // login/logout pages
                    "/css/**", "/js/**", "/images/**", "/webjars/**",
                    "/error"
                ).permitAll()
                // everything else (Thymeleaf pages) requires login
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/auth/login")
                .loginProcessingUrl("/auth/login")
                .defaultSuccessUrl("/dashboard", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/auth/logout")
                .logoutSuccessUrl("/auth/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }
}
