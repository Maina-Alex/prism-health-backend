package com.prismhealth.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {
    private final UserDetailsService userDetailsService;

    public JWTAuthorizationFilter(UserDetailsService userDetailsService, AuthenticationManager authManager) {
        super(authManager);
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        String header = req.getHeader(SecurityConstants.HEADER_STRING);

        if (header == null || !header.startsWith(SecurityConstants.TOKEN_PREFIX)) {

            chain.doFilter(req, res);
            return;
        }
        try {
            UsernamePasswordAuthenticationToken authentication = getAuthentication(req);

            SecurityContextHolder.getContext().setAuthentication(authentication);

            chain.doFilter(req, res);

        } catch (Exception e) {
            res.sendError(400, e.getMessage());
            return;

        }

    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {

        String token = request.getHeader(SecurityConstants.HEADER_STRING);

        String user = JWT.require(Algorithm.HMAC512(SecurityConstants.SECRET.getBytes())).build()
                .verify(token.replace(SecurityConstants.TOKEN_PREFIX, "")).getSubject();

        UserDetails details = this.userDetailsService.loadUserByUsername(user);
        return new UsernamePasswordAuthenticationToken(details, "", details.getAuthorities());

    }
}
