package com.assistant.api_gateway.filter;

import com.assistant.api_gateway.config.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Autowired
    private RouteValidator validator;

    //    @Autowired
//    private RestTemplate template;
    @Autowired
    private JwtUtil jwtUtil;

    public AuthenticationFilter() {
        super(Config.class);
    }

//    @Override
//    public GatewayFilter apply(Config config) {
//        return ((exchange, chain) -> {
//            if (validator.isSecured.test(exchange.getRequest())) {
//                //header contains token or not
//                System.out.println("Gateway filter invoked for: " + exchange.getRequest().getURI());
//
//                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
//                    throw new RuntimeException("missing authorization header");
//                }
//
//                String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
//                if (authHeader != null && authHeader.startsWith("Bearer ")) {
//                    authHeader = authHeader.substring(7);
//                }
//                try {
////                    //REST call to AUTH service
////                    template.getForObject("http://IDENTITY-SERVICE//validate?token" + authHeader, String.class);
//                    System.out.println("Validating JWT: " + authHeader);
//
//                    jwtUtil.validateToken(authHeader);
//                    System.out.println("Validated JWT: " + authHeader);
//
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    throw new RuntimeException("unauthorized access to application: " + e.getMessage());
//                }
//
//            }
//            return chain.filter(exchange);
//        });
//    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            if (validator.isSecured.test(exchange.getRequest())) {
//
//                log.info("Gateway filter invoked for: " + exchange.getRequest().getURI());
                log.info("Path: {}", exchange.getRequest().getURI());
                log.info("Query: {}", exchange.getRequest().getQueryParams());


                String token = null;

                // 1. Check query parameter first
                String queryToken = exchange.getRequest().getQueryParams().getFirst("token");
                if (queryToken != null && !queryToken.isEmpty()) {
                    token = queryToken;
                    log.info("JWT found in query param");
                }

                // 2. If not found, check Authorization header
                if (token == null) {
                    String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
                    if (authHeader == null) {
                        throw new RuntimeException("Missing Authorization header or query param");
                    }
                    if (authHeader.startsWith("Bearer ")) {
                        token = authHeader.substring(7);
                    } else {
                        throw new RuntimeException("Invalid Authorization header format");
                    }
                    log.info("JWT found in Authorization header");
                }

                try {
                    // Validate token (via your JWT util or Auth service)
                    jwtUtil.validateToken(token);
                    log.info("Validated JWT: " + token);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("Unauthorized access: " + e.getMessage());
                }
            }

            return chain.filter(exchange);
        };
    }



//@Override
//public GatewayFilter apply(Config config) {
//    return ((exchange, chain) -> {
//        if (validator.isSecured.test(exchange.getRequest())) {
//            System.out.println("Gateway filter invoked for: " + exchange.getRequest().getURI());
//
//            if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
//                throw new RuntimeException("Missing authorization header");
//            }
//
//            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
//            if (authHeader != null && authHeader.startsWith("Bearer ")) {
//                authHeader = authHeader.substring(7);
//            }
//
//            try {
//                // Validate and extract claims
//                Claims claims = jwtUtil.extractAllClaims(authHeader);
//
//                String username = claims.getSubject();
//                Object roles = claims.get("roles");
//
//                System.out.println("Validated JWT for user: " + username + " with roles: " + roles);
//
//                // Add claims as headers for downstream services
//                exchange = exchange.mutate()
//                        .request(r -> r.headers(headers -> {
//                            headers.add("X-Username", username);
//                            headers.add("X-Roles", roles.toString());
//                        }))
//                        .build();
//
//            } catch (Exception e) {
//                System.out.println("Invalid access...!");
//                throw new RuntimeException("Unauthorized access to application");
//            }
//        }
//        return chain.filter(exchange);
//    });
//}


    public static class Config {

    }
}