package com.prismhealth.security;

import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
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
                .cors().and().authorizeRequests().antMatchers("/auth/token").permitAll().antMatchers("/auth/signUp")
                .permitAll().antMatchers(HttpMethod.POST, "/auth/forgotpassword").permitAll()
                .antMatchers(HttpMethod.POST, "/car/**").authenticated().antMatchers("/notification/**").authenticated()
                .antMatchers("/auth/**").authenticated().antMatchers("/owner/**").authenticated()
                .antMatchers("/payment/**").authenticated().antMatchers(HttpMethod.GET, "/payment/auth")
                .hasAnyAuthority("ROLE_ADMIN").antMatchers("/transaction/**").hasAuthority("ROLE_ADMIN")
                .antMatchers("/help").authenticated().antMatchers(HttpMethod.POST, "/help/issues/addreply")
                .hasAnyAuthority("ROLE_ADMIN", "ROLE_HELP_SUPPORT").antMatchers(HttpMethod.POST, "/policy")
                .hasAnyAuthority("ROLE_ADMIN", "ROLE_SITE_CONTENT_UPDATER").antMatchers(HttpMethod.DELETE, "/policy/**")
                .hasAnyAuthority("ROLE_ADMIN", "ROLE_SITE_CONTENT_UPDATER").antMatchers(HttpMethod.GET, "/help/issues")
                .hasAnyAuthority("ROLE_ADMIN", "ROLE_HELP_SUPPORT").antMatchers("/order/**").authenticated()
                .antMatchers("/user/**").authenticated().antMatchers("/admin/car/**")
                .hasAnyAuthority("ROLE_ADMIN", "ROLE_CAR_USER_VERIFIER").antMatchers("/admin/user/**")
                .hasAnyAuthority("ROLE_ADMIN", "ROLE_CAR_USER_VERIFIER").antMatchers("/admin/metrics/user")
                .hasAnyAuthority("ROLE_ADMIN", "ROLE_CAR_USER_VERIFIER").antMatchers("/admin/metrics/car")
                .hasAnyAuthority("ROLE_ADMIN", "ROLE_CAR_USER_VERIFIER").antMatchers("/admin/**")
                .hasAuthority("ROLE_ADMIN").and().addFilter(new JWTAuthenticationFilter(authenticationManager()))
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