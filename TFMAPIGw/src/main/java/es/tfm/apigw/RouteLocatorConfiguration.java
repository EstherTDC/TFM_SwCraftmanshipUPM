package es.tfm.apigw;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteLocatorConfiguration {
    @Bean
    public RouteLocator myRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
            .route(predicateSpec -> predicateSpec
                 .path("/usermgt/**")
                 .uri("lb://tfmusermgtserver"))            
            .route(predicateSpec -> predicateSpec
                 .path("/gamemgt/**")
                 .uri("lb://tfmusermgtserver"))
            .route(predicateSpec -> predicateSpec
                 .path("/groupmgt/**")
                 .uri("lb://tfmusermgtserver"))
            .route(predicateSpec -> predicateSpec
                 .path("/tfm-bingo-websocket/**")
                 .uri("lb://websocket-server"))
            .route(predicateSpec -> predicateSpec
                 .path("/onlineBingo/getPlayer/**")
                 .uri("lb://websocket-server"))
            .route(predicateSpec -> predicateSpec
                 .path("/onlineBingoView/**")
                 .uri("lb://websocket-server"))
            .route(predicateSpec -> predicateSpec
                 .path("/onlineBingo/**")
                 .uri("lb://websocket-server"))
            .route(predicateSpec -> predicateSpec
                 .path("/css/**")
                 .uri("lb://websocket-server"))
            .route(predicateSpec -> predicateSpec
                 .path("/webjars/**")
                 .uri("lb://websocket-server"))
            .route(predicateSpec -> predicateSpec
                 .path("/img/**")
                 .uri("lb://websocket-server"))
            .route(predicateSpec -> predicateSpec
                 .path("/**.js")
                 .uri("lb://websocket-server"))
            .build();
    }
}
