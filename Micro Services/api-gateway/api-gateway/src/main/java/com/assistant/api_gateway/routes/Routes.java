//package com.assistant.api_gateway.routes;
//
//import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
//import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.function.*;
//
//@Configuration
//public class Routes {
//
//    @Bean
//    public RouterFunction<ServerResponse> messageServiceRoute(){
//        return GatewayRouterFunctions.route("message_service")
//                .route(RequestPredicates.path("/api/message"), HandlerFunctions.http("http://localhost:8080"))
//                .build();
//    }
//
//    @Bean
//    public RouterFunction<ServerResponse> sessionServiceRoute(){
//        return GatewayRouterFunctions.route("session_service")
//                .route(RequestPredicates.path("/api/sessions"), HandlerFunctions.http("http://localhost:8080"))
//                .build();
//    }
//
//    @Bean
//    public RouterFunction<ServerResponse> userServiceRoute(){
//        return GatewayRouterFunctions.route("user_service")
//                .route(RequestPredicates.path("/api/users"), HandlerFunctions.http("http://localhost:8080"))
//                .build();
//    }
//}
