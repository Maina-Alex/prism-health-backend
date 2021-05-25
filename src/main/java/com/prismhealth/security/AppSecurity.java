package com.prismhealth.security;

import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@EnableWebSecurity
public class AppSecurity extends WebSecurityConfigurerAdapter {
    private final UserDetailsServiceImpl userDetailsService;
    private final BCryptPasswordEncoder passEncoder;

    public AppSecurity(UserDetailsServiceImpl userDetailsService, BCryptPasswordEncoder passEncoder) {
        this.userDetailsService = userDetailsService;
        this.passEncoder = passEncoder;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // disable caching
        http.headers().cacheControl().disable();

        /* ROUTING SECURITY */
        http.csrf().disable() // disable csrf for our requests.
                .cors().and().authorizeRequests().antMatchers("/auth/token").permitAll().antMatchers("/accounts/signUp")
                .permitAll().antMatchers(HttpMethod.POST, "/auth/forgotpassword").permitAll()
                .antMatchers(HttpMethod.POST, "/product/**").authenticated().antMatchers("/notification/**")
                .authenticated().antMatchers("/auth/**").authenticated().antMatchers("/help").authenticated()
                .antMatchers(HttpMethod.POST, "/services/users/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_PROVIDER")
                .antMatchers(HttpMethod.PUT, "/services/users/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_PROVIDER")
                .antMatchers(HttpMethod.POST, "/services/providers/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_PROVIDER")
                .antMatchers(HttpMethod.PUT, "/services/providers/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_PROVIDER")
                .antMatchers("/admin/**").hasAuthority("ROLE_ADMIN").and()
                .addFilter(new JWTAuthenticationFilter(authenticationManager()))
                .addFilter(new JWTAuthorizationFilter(userDetailsService, authenticationManager())).sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        LoggerFactory.getLogger(this.getClass()).info(userDetailsService.toString());
        auth.userDetailsService(userDetailsService).passwordEncoder(passEncoder);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {

        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.addExposedHeader("Authorization");
        source.registerCorsConfiguration("/**", config.applyPermitDefaultValues());

        return source;
    }

}