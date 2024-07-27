package com.alkl1m.deal.config;

import com.alkl1m.deal.filter.JwtFilter;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@AllArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers("/swagger-ui/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/**").hasAnyAuthority("USER", "CREDIT_USER", "OVERDRAFT_USER", "DEAL_SUPERUSER", "SUPERUSER")
                                .requestMatchers(HttpMethod.PUT, "/**").hasAnyAuthority("DEAL_SUPERUSER", "SUPERUSER")
                                .requestMatchers(HttpMethod.PATCH, "/**").hasAnyAuthority("DEAL_SUPERUSER", "SUPERUSER")
                                .requestMatchers(HttpMethod.POST, "/deal/search").hasAnyAuthority("CREDIT_USER", "OVERDRAFT_USER", "DEAL_SUPERUSER", "SUPERUSER")
                                .requestMatchers(HttpMethod.DELETE, "/**").hasAnyAuthority("DEAL_SUPERUSER", "SUPERUSER")
                                .requestMatchers("/deal-contractor/**").hasAnyAuthority("DEAL_SUPERUSER", "SUPERUSER")
                                .requestMatchers("/contractor-to-role/**").hasAnyAuthority("DEAL_SUPERUSER", "SUPERUSER")
                                .anyRequest().authenticated());

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
