FROM openjdk:8

MAINTAINER Universitat Jaume I
LABEL Author="María Gómez"
LABEL E-mail="msirvent@uji.es"
LABEL version="0.0.1"

RUN mkdir /app
RUN mkdir /app/data

ADD ./target/linking-0.0.1-SNAPSHOT.jar seal-linking.jar
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -Dspring.config.location=file:/app/data/ -Dspring.profiles.active=docker -jar /seal-linking.jar" ]

EXPOSE 8090

