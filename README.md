# TFM_Sw_CraftmanshipUPM

Este Trabajo de Fin de Master consiste en una aplicación basada en una arquitectura de microservicios usando las herramientas del proyecto Spring Cloud. 

La aplicación propuesta es un Bingo multijugador con dos microservicios principales:

-	Microservicio ‘TFMUserMgtServer’: Un microservicio dedicado al control de usuarios, el manejo de los grupos a los que pertenece cada usuario y las partidas correspondientes a cada uno de estos grupos. Los datos están almacenados en una base de datos MySQL.

-	Microservicio ‘TFMBingoGame’: Un microservicio dedicado al manejo de las distintas partidas de Bingo, de 75 o de 90 bolas, según petición del usuario. Los datos de este microservicio también están almacenados en una base de datos MySQL. 

Los dos microservicios se comunican mediante peticiones REST en ciertas situaciones (creación/borrado de partida, comienzo/finalización de partida o cuando se canta un premio). También es por medio de peticiones REST la comunicación entre los jugadores y el microservicio ‘TFMUserMgtServer’ para el manejo (creación/edición/borrado) de usuarios, grupos y partidas.

La comunicación entre los jugadores y el microservicio ‘TFMBingoGame’ es por medio de websockets. Los jugadores pueden suscribirse y generar eventos que se comunican por medio de websockets por el patrón publish-subscribe). Para ello se hace uso de RabbitMQ como message bróker (microservicio 'TFMRabbitMQ). 

Al ser una arquitectura microservicios toda comunicación entre los jugadores y los distintos microservicios se hace a través de un API Gw. Para ello está el microservicio ‘TFMAPIGateway’, que se encarga de enrutar las distintas peticiones al microservicio adecuado actuando como load balancer. Este microservicio está basado en el Spring Cloud Gateway y también simula una autenticación de los usuarios por medio de Spring Security. Esta simulación de autenticación ha sido la razón principal para utilizar el Spring Cloud Gateway en lugar del Ingress de Kubernetes.

Estos tres microservicios se registran en un registro de microservicios, el ‘TFMEurekaServer’ basado en el componente Spring Cloud Netflix Eureka.  Este registro hace disponible a sus clientes las distintas instancias del resto de los microservicios. Es verdad que dado que se está usando Kubernetes este registro puede parecer redundante, pero se ha decidido dejar para simular una arquitectura de microservicios completa y ver las distintas opciones de comunicación dentro del clúster. Además, el servidor Eureka sirve para monitorizar el estado de los microservicios gracias al dashboard que provee.

El directorio TFMSpecs contiene los Specs para el deployment mediante Kubernetes.

El fichero TFMApplicationBingo.zip contiene el modelo 4+1 vistas de Kruchten.
