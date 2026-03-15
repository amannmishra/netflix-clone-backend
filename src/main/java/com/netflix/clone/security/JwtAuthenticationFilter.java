package com.netflix.clone.security;

import java.io.IOException;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;


    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String jwt = extractJwtToken(request);

        if (jwt == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String username = jwtUtil.getUsernameFromToken(jwt);

            if (username != null &&
                SecurityContextHolder.getContext().getAuthentication() == null &&
                jwtUtil.validateToken(jwt, username)) {

                UserDetails userDetails = createUserDetailsFromToken(jwt, username);
                setAuthenticationInContext(request, userDetails);
            }

        } catch (Exception e) {
            // ❌ expired / invalid token → ignore & continue
        }

        filterChain.doFilter(request, response);
    }

    private String extractJwtToken(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        final String requestURI = request.getRequestURI();

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        // aise tha phle ab jo krunga vo chagtpt se
//       if ((requestURI.contains("/api/files/video/")
//         || requestURI.contains("/api/files/image"))
//         && request.getParameter("token") != null) {
//         return request.getParameter("token");
// }
if ((requestURI.startsWith("/api/files/video/")
    || requestURI.startsWith("/api/files/videos/")
    || requestURI.startsWith("/api/files/image/"))
    && request.getParameter("token") != null) {

    return request.getParameter("token");
}

        return null;
    }

    private UserDetails createUserDetailsFromToken(String jwt, String username) {
        String role = jwtUtil.getRoleFromToken(jwt);

        return User.builder()
                .username(username)
                .password("N/A")
                .authorities(Collections.singleton(
                        new SimpleGrantedAuthority("ROLE_" + role)))
                .build();
    }

    private void setAuthenticationInContext(
            HttpServletRequest request,
            UserDetails userDetails) {

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

        authToken.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}
