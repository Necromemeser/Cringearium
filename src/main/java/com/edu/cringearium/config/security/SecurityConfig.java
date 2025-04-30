package com.edu.cringearium.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import com.edu.cringearium.services.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/courses/*/study").authenticated()
                        .requestMatchers("/styles/**", "/scripts/**", "/images/**", "/webjars/**").permitAll()
                        .requestMatchers("/", "/courses", "/courses/**", "/registration").permitAll()
                        .requestMatchers("/api/user/registration", "/api/payments/webhook",
                                "/api/courses",
                                "/api/courses/**",
                                "/api/courses/*/**").permitAll()
                        .requestMatchers("/info/**").permitAll()
                        .requestMatchers("/api/ollama", "/api/deepseek").authenticated()
                        .requestMatchers("/api/chats/**").authenticated()
                        .requestMatchers("/api/**").authenticated()
                        .requestMatchers("/**").authenticated()
                        .requestMatchers("/admin-panel").hasRole("Admin"))
                .formLogin(form -> form
                        .loginPage("/login")            // Кастомная страница логина
                        .loginProcessingUrl("/perform_login") // URL для обработки формы
//                        .defaultSuccessUrl("/profile", true)   // Перенаправление после успешного входа
                        .failureUrl("/login?error=true") // Перенаправление при ошибке
                        .permitAll()

                )
                .logout(logout -> logout
                .logoutUrl("/perform_logout")
                .logoutSuccessUrl("/login?logout=true")
                .permitAll()
                )
                .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
