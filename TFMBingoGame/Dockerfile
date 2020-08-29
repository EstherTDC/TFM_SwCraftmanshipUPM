FROM openjdk:8-jre

LABEL maintainer="esther.terrero@ericsson.com"

EXPOSE 8081

ADD target/tfmbingogame.jar /tfmbingogame

ENTRYPOINT ["java","-jar","/tfmbingogame"]