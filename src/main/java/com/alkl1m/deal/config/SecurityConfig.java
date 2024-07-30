package com.alkl1m.deal.config;

import com.alkl1m.deal.domain.enums.ERole;
import com.alkl1m.deal.security.filter.JwtFilter;
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

/**
 * Конфигурация безопасности приложения, включая настройки авторизации и фильтры.
 */
@Configuration
@EnableMethodSecurity
@AllArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    /**
     * Создает менеджер аутентификации.
     *
     * @param authenticationConfiguration конфигурация аутентификации
     * @return менеджер аутентификации
     * @throws Exception если произошла ошибка при создании менеджера аутентификации
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Конфигурирует цепочку фильтров безопасности.
     *
     * @param http объект HttpSecurity для настройки безопасности
     * @return построенная цепочка фильтров безопасности
     * @throws Exception если произошла ошибка при настройке безопасности
     */
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
                                .requestMatchers("/**").hasAnyAuthority(ERole.USER.name(),
                                        ERole.CREDIT_USER.name(),
                                        ERole.OVERDRAFT_USER.name(),
                                        ERole.DEAL_SUPERUSER.name(),
                                        ERole.CONTRACTOR_RUS.name(),
                                        ERole.CONTRACTOR_SUPERUSER.name(),
                                        ERole.SUPERUSER.name())
                                .anyRequest().authenticated());

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
