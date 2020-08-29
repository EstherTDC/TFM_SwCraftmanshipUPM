FROM openjdk:8-jre

LABEL maintainer="esther.terrero@ericsson.com"

EXPOSE 8761

ADD target/tfmeurekaserver.jar /tfmeurekaserver

ENTRYPOINT ["java","-jar","/tfmeurekaserver"]