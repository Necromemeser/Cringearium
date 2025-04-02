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
                        .requestMatchers("/styles/**", "/scripts/**", "/images/**", "/webjars/**").permitAll()
                        .requestMatchers("/api/courses/**", "/api/courses/{courseId}/data/**", "/api/orders/**").permitAll() // delete later
                        .requestMatchers("/api/**").permitAll() // delete later
                        .requestMatchers("/", "/courses", "/registration").permitAll()
//                        .requestMatchers("/api/ollama").authenticated()
//                        .requestMatchers("/api/chats/**").authenticated()
                        .requestMatchers("/api/**").authenticated()
                        .requestMatchers("/profile").authenticated()
                        .requestMatchers("/**").authenticated())
                .formLogin(form -> form
                        .loginPage("/login")            // Указываем кастомную страницу логина
                        .loginProcessingUrl("/perform_login") // URL для обработки формы
                        .defaultSuccessUrl("/profile")   // Перенаправление после успешного входа
                        .failureUrl("/login?error=true") // Перенаправление при ошибке
                        .permitAll()

                )
                .logout(logout -> logout
                .logoutUrl("/perform_logout")   // URL для выхода
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
