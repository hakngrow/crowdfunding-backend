package com.aestus.api.profile.config;

import com.aestus.api.common.filter.JwtTokenAuthenticationFilter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class AestusMicroserviceTestConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager jwtAuthenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Bean
    public JwtTokenAuthenticationFilter jwtTokenAuthenticationFilter() {
        return new JwtTokenAuthenticationFilter();
    }

    protected void configure(HttpSecurity http) throws Exception {

        // Enable cors but disable csrf as apis are stateless and using jwt
        http.cors()
                .and()
                .csrf()
                .disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // Request filtering
        http.addFilterBefore(jwtTokenAuthenticationFilter(), BasicAuthenticationFilter.class);
        // .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()

        http.authorizeRequests()
                // Permitted requests
                .antMatchers("/**/public", "/**/ping", "/swagger/**", "/swagger-ui/**", "/v3/api-docs/**", "/api/v1/authenticate", "/api/v1/any/message")
                .permitAll()
                // Authenticated requests
                .anyRequest()
                .authenticated();
    }
}
