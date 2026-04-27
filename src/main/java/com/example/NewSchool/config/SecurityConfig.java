package com.example.NewSchool.config;

import com.example.NewSchool.security.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailsService uds;

    // CONSTRUCTOR A LAMEN (Ranplase @RequiredArgsConstructor)
    public SecurityConfig(CustomUserDetailsService uds) {
        this.uds = uds;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(c -> c.ignoringRequestMatchers("/logout"))
            .authenticationProvider(authProvider())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/css/**", "/js/**", "/images/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/prof/**").hasRole("PROF")
                .requestMatchers("/secretaire/**").hasRole("SECRETAIRE")
                .requestMatchers("/eleve/**").hasRole("ELEVE")
                .anyRequest().authenticated()
            )
            .formLogin(f -> f
                .loginPage("/")
                .loginProcessingUrl("/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .successHandler(successHandler())
                .failureUrl("/?error=true")
                .permitAll()
            )
            .logout(l -> l
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );
        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return (req, res, auth) -> {
            String role = auth.getAuthorities().iterator().next().getAuthority();
            switch (role) {
                case "ROLE_ADMIN"      -> res.sendRedirect("/admin/dashboard");
                case "ROLE_PROF"       -> res.sendRedirect("/prof/dashboard");
                case "ROLE_SECRETAIRE" -> res.sendRedirect("/secretaire/dashboard");
                case "ROLE_ELEVE"      -> res.sendRedirect("/eleve/dashboard");
                default                -> res.sendRedirect("/");
            }
        };
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        var p = new DaoAuthenticationProvider();
        p.setUserDetailsService(uds);
        p.setPasswordEncoder(passwordEncoder());
        return p;
    }

    @Bean
    public PasswordEncoder passwordEncoder() { 
        return new BCryptPasswordEncoder(); 
    }
}