package com.mohdsohel.CloudNest.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class ClerkJwtAuthFilter extends OncePerRequestFilter {

    @Value("${clerk.issuer}")
    private String clerkIssuer;

    private final ClerkJwksProvider clerkJwksProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if(request.getRequestURI().contains("/webhooks")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if(authHeader==null || !authHeader.startsWith("Bearer ")){
            response.sendError(HttpServletResponse.SC_FORBIDDEN,"Authorization header is invalid / missing");
            return;
        }

        try{
            String token = authHeader.substring(7);
            String[] chunks = token.split("\\.");

            if(chunks.length<3){
                response.sendError(HttpServletResponse.SC_FORBIDDEN,"Invalid JWT token format.");
                return;
            }

            String headerJson = new String(Base64.getUrlDecoder().decode(chunks[0]));
            ObjectMapper mapper = new ObjectMapper();
            JsonNode headerNode = mapper.readTree(headerJson);

            if(!headerNode.has("kid")){
                response.sendError(HttpServletResponse.SC_FORBIDDEN,"Token header is invalid / missing kid.");
                return;
            }

            String kid = headerNode.get("kid").asText();

            PublicKey publicKey = clerkJwksProvider.getPublicKey(kid);

//            Verify
            Claims claims = Jwts.parser()
                    .verifyWith(publicKey)
                    .clockSkewSeconds(60)
                    .requireIssuer(clerkIssuer)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String clerkUserId = claims.getSubject();

            UsernamePasswordAuthenticationToken authenticationToken = new  UsernamePasswordAuthenticationToken(clerkUserId,null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            filterChain.doFilter(request,response);

        }catch (Exception e){
            response.sendError(HttpServletResponse.SC_FORBIDDEN,"Invalid Jwt Token");
            return;
        }

    }
}
