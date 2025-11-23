package com.capstone.vsl.security;

import com.capstone.vsl.util.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT Authentication Filter (Middleware)
 * 
 * This filter intercepts every HTTP request and performs the following:
 * 1. Checks for "Authorization" header with Bearer token
 * 2. Extracts the JWT token from the header
 * 3. Validates the token (signature and expiration)
 * 4. If valid, loads user details and sets authentication in SecurityContext
 * 5. Allows the request to proceed to the controller
 * 
 * This is the middleware that protects the API endpoints.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // Extract JWT token from Authorization header
            var jwt = parseJwt(request);
            
            if (jwt != null && jwtUtils.validateToken(jwt)) {
                // Extract username from token
                var username = jwtUtils.getUsernameFromToken(jwt);
                
                // Load user details from database
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                
                // Create authentication token
                var authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, 
                        null, 
                        userDetails.getAuthorities()
                );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // Set authentication in SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                log.debug("Set authentication for user: {}", username);
            }
        } catch (Exception e) {
            log.error("Cannot set user authentication: {}", e.getMessage());
        }

        // Continue with the filter chain
        filterChain.doFilter(request, response);
    }

    /**
     * Parses JWT token from Authorization header.
     * Expected format: "Bearer <token>"
     *
     * @param request HTTP request
     * @return JWT token string or null if not found
     */
    private String parseJwt(HttpServletRequest request) {
        var headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }
}

