package es.tfm.apigw;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

	
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig{
	
	   @Autowired
	   PasswordEncoder passwordEncoder;

	   @Bean
	   public PasswordEncoder passwordEncoder(){
	     	return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	   }


	   @Bean
	    SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity http) throws Exception {
	        return http
	                .authorizeExchange()
	                .anyExchange().authenticated()
	                .and().authenticationManager(reactiveAuthenticationManager())
	                .httpBasic().and()
	                .csrf().disable()
	                .build();
	    }

	    @Bean
	    ReactiveAuthenticationManager reactiveAuthenticationManager(){
		    return new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsRepository());
	    }

	    @Bean
	    public MapReactiveUserDetailsService userDetailsRepository() {
	        User.UserBuilder userBuilder = User.builder().passwordEncoder(passwordEncoder::encode);
	        UserDetails user1 = userBuilder.username("Esther").password("Esther").roles("USER", "ADMIN").build();
	        UserDetails user2 = userBuilder.username("Luis").password("Lu1s").roles("USER").build();
	        UserDetails user3 = userBuilder.username("Mica").password("M1k").roles("USER").build();
	        UserDetails user4 = userBuilder.username("Patxi").password("Patx1").roles("USER").build();
	        return new MapReactiveUserDetailsService(user1, user2, user3, user4);
	    }
}

