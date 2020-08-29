FROM openjdk:8-jre

LABEL maintainer="esther.terrero@ericsson.com"

EXPOSE 8082

ADD target/tfmusermgtserver.jar /tfmusermgtserver

ENTRYPOINT ["java","-jar","/tfmusermgtserver"]