package com.paypal.user_service.util;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    public JWTAuthenticationFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        String jwt = null;
        String username = null;

        if( authHeader != null && authHeader.startsWith("Bearer ")){
            jwt = authHeader.substring(7);
            try{
                username = jwtUtil.extractUsername(jwt);
            }catch(Exception ex){
                //log
            }
        }
        //below we are checking Authentication like username wise
        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
            if (jwtUtil.validateToken(jwt, username)){
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, null, null);
                //This below line attaches extra details about the current request (like IP address, session ID, etc.)
                // to the Authentication object before saving it into the SecurityContext.
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        //here we are checking authorization like the role e.g. Admin, user
        if( authHeader != null && authHeader.startsWith("Bearer ")){
            jwt = authHeader.substring(7);
            if (jwt == null || jwt.isBlank()){
                //This line tells the current filter (your JWT filter) to pass the request and response objects to the next filter in the chain,
                // like now continue with the normal Spring Security pipeline
                filterChain.doFilter(request, response);
                return;
            }
            try{
                username = jwtUtil.extractUsername(jwt);
                String role = jwtUtil.extractRole(jwt);
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, null, List.of(new SimpleGrantedAuthority(role)));
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                filterChain.doFilter(request, response);
            }catch(Exception ex){
                //log
            }
        }
        else{
            filterChain.doFilter(request, response);
        }
    }
}
