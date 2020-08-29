package es.tfm.bingo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import es.tfm.bingo.websocket.CustomHandshakeHandler;

@Configuration
@PropertySource("classpath:application.properties")
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${broker.relay.host}")
	private String brokerRelayHost;
	  
	@Value("${broker.relay.port}")
	private int brokerRelayPort;

	
	@Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
 
      registry.enableStompBrokerRelay("/topic/","/queue/")
	        .setRelayHost(brokerRelayHost)
	        .setRelayPort(brokerRelayPort);

      registry.setApplicationDestinationPrefixes("/app/");
      registry.setUserDestinationPrefix("/user/");

    }
  
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
      registry.addEndpoint("/tfm-bingo-websocket")
              .setAllowedOrigins("*")
              .setHandshakeHandler(new CustomHandshakeHandler())
              .withSockJS();
    }
    
    @EventListener
    public void handleSubscribeEvent(SessionSubscribeEvent event) {
        System.out.println("<==> handleSubscribeEvent: username="+event.getUser().getName()+", event="+event);
    }

    @EventListener
    public void handleConnectEvent(SessionConnectEvent event) {
    	System.out.println("===> handleConnectEvent: username="+event.getUser().getName()+", event="+event);
    }

    @EventListener
    public void handleDisconnectEvent(SessionDisconnectEvent event) {
    	System.out.println("<=== handleDisconnectEvent: username="+event.getUser().getName()+", event="+event);
    }
}