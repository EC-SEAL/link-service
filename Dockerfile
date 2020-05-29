FROM openjdk:8

MAINTAINER Universitat Jaume I
LABEL Author="María Gómez"
LABEL E-mail="msirvent@uji.es"
LABEL version="0.0.1"

VOLUME /tmp

RUN mkdir /etc/seal
RUN mkdir /etc/seal/testKeys
RUN mkdir /etc/seal/resources
COPY ./target/classes/testKeys/keystore.jks ./etc/seal/testKeys/keystore.jks
COPY ./target/classes/users-cm.json ./etc/seal/resources/users-cm.json

ADD ./target/linking-0.0.1-SNAPSHOT.jar seal-linking.jar
#RUN sh -c 'touch /seal-linking.jar'
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=docker -jar /seal-linking.jar" ]

EXPOSE 8090

