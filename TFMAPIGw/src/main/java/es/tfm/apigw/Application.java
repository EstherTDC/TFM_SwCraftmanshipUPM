package es.tfm.apigw;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@EnableDiscoveryClient
@SpringBootApplication
public class Application {
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}

@RestController
class ServiceInstanceRestController{

	@Autowired
	private DiscoveryClient discoveryClient;
	
	@RequestMapping("/registered-services")
	public List<String> services(){
		return this.discoveryClient.getServices();
	}
	
	@RequestMapping("/registered-instances/{applicationName}")
	public List<ServiceInstance> serviceInstances(@PathVariable String applicationName){
		return this.discoveryClient.getInstances(applicationName);
	}
}