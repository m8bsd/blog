package com.example.blog;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/css/**", "/login", "/error").permitAll()
                // Admin-only pages and actions (must come before the public GET rules)
                .requestMatchers("/posts/new", "/posts/*/edit", "/posts/*/delete",
                        "/categories/**", "/comments/*/delete").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/posts", "/posts/*").hasRole("ADMIN")
                // Public: read posts, and leave comments
                .requestMatchers(HttpMethod.GET, "/", "/posts/*").permitAll()
                .requestMatchers(HttpMethod.POST, "/posts/*/comments").permitAll()
                .anyRequest().hasRole("ADMIN"))
            .formLogin(form -> form.loginPage("/login").permitAll())
            .logout(logout -> logout.logoutSuccessUrl("/"));
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService userDetailsService(@Value("${blog.admin.username}") String username,
                                          @Value("${blog.admin.password}") String password,
                                          PasswordEncoder encoder) {
        return new InMemoryUserDetailsManager(
                User.withUsername(username)
                        .password(encoder.encode(password))
                        .roles("ADMIN")
                        .build());
    }
}
