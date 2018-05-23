FROM alpine:latest

WORKDIR /

ARG DATABASE_URL=mysql://127.0.0.1:5101/domus
ARG DATABASE_USERNAME=domus
ARG DATABASE_PASSWORD=domus

ENV DATABASE_URL ${DATASOURCE_URL}
ENV DATABASE_USERNAME ${DATASOURCE_USERNAME}
ENV DATABASE_PASSWORD ${DATASOURCE_PASSWORD}

COPY ./build/libs/server.jar /home/application/server.jar

RUN apk add --no-cache openjdk8-jre pwgen && \
    addgroup application && \
	adduser -h /home/application -G application application << pwgen -S 20 && \
	chown -R application:application /home/application && \
	chmod -R a-rwx /home/application && \
	chmod -R g+r /home/application && \
	chmod -R u+rx /home/application
	
USER application

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/home/application/server.jar"]

EXPOSE 80