FROM openjdk:8-jre

LABEL maintainer="esther.terrero@ericsson.com"

EXPOSE 8080

ADD target/tfmapigateway.jar /tfmapigateway

ENTRYPOINT ["java","-jar","/tfmapigateway"]