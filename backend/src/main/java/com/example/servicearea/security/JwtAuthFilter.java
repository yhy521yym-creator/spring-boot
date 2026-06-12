package com.example.servicearea.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import com.example.servicearea.common.ApiResponse;
import com.example.servicearea.entity.SysUser;
import com.example.servicearea.service.UserService;
import com.example.servicearea.support.MerchantAccessHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final UserService userService;
    private final MerchantAccessHelper merchantAccessHelper;
    private final ObjectMapper objectMapper;

    public JwtAuthFilter(JwtService jwtService, CustomUserDetailsService userDetailsService,
                         UserService userService, MerchantAccessHelper merchantAccessHelper,
                         ObjectMapper objectMapper) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.userService = userService;
        this.merchantAccessHelper = merchantAccessHelper;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();
        if (path != null && (path.startsWith("/auth/login") || path.startsWith("/auth/register"))) {
            filterChain.doFilter(request, response);
            return;
        }

        String auth = request.getHeader("Authorization");
        System.out.println("=== Authorization header: " + (auth != null ? "Bearer ****" : "null"));
        
        if (auth == null || !auth.startsWith("Bearer ")) {
            System.out.println("=== No valid Authorization header");
            filterChain.doFilter(request, response);
            return;
        }

        String token = auth.substring(7).trim();
        System.out.println("=== Token length: " + token.length());
        
        if (token.isEmpty()) {
            System.out.println("=== Token is empty");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            Claims claims = jwtService.parseClaims(token);
            String username = claims.getSubject();
            System.out.println("=== JWT parsed, username: " + username);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                System.out.println("=== UserDetails loaded: " + userDetails.getUsername());
                var authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                System.out.println("=== Authentication set successfully");

                if (userDetails instanceof UserPrincipal principal && "MERCHANT".equals(principal.getRole())) {
                    SysUser user = userService.findByUsername(principal.getUsername());
                    try {
                        merchantAccessHelper.assertMerchantUserCanLogin(user);
                    } catch (IllegalStateException ex) {
                        writeForbidden(response, ex.getMessage());
                        return;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("=== JWT parse error: " + e.getMessage());
            e.printStackTrace();
        }

        filterChain.doFilter(request, response);
    }

    private void writeForbidden(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.fail(403, message)));
    }
}

