package com.VisitPlanner.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public UserDetailsManager userDetailsManager(DataSource dataSource){

        JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);

        jdbcUserDetailsManager.setUsersByUsernameQuery(
                "select username, password, enabled from users where username=?"
        );

        jdbcUserDetailsManager.setAuthoritiesByUsernameQuery(
                "select username, role from user_roles where username=?"
        );


        return jdbcUserDetailsManager;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        http.authorizeHttpRequests(configurer -> configurer
                .requestMatchers("/visits/addForm").permitAll()
                .requestMatchers("/visits/create").permitAll()
                .requestMatchers("/visits/findForm/**").permitAll()
                .requestMatchers("/visits/visitNumber").permitAll()
                .requestMatchers("/visits/cancel").permitAll()

                .requestMatchers("/visits/user/**").hasRole("USER")
                .requestMatchers("/visits/logged").hasRole("USER")
                .requestMatchers("/login/**").hasRole("USER")
                .requestMatchers("/login/**").hasRole("ADMIN")
                .requestMatchers("/visits/logged").hasRole("ADMIN")
                .requestMatchers("/visits/admin/serviceDesk").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                    .loginPage("/login/showLoginPage")
                    .loginProcessingUrl("/authenticateTheUser")
                    .defaultSuccessUrl("/login/logged")
                    .permitAll()
            )
            .logout(logout -> logout
                    .permitAll()
                    .clearAuthentication(true)
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
                    .logoutSuccessUrl("/")
            )
            .exceptionHandling(configurer -> configurer
                    .accessDeniedPage("/login/accessDenied")
            );

        return http.build();
    }
}