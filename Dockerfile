FROM openjdk:12-jdk-alpine

WORKDIR /

ARG DATABASE_URL=mysql://127.0.0.1:5101/domus
ARG DATABASE_USERNAME=domus
ARG DATABASE_PASSWORD=domus

ENV DATABASE_URL ${DATABASE_URL}
ENV DATABASE_USERNAME ${DATABASE_USERNAME}
ENV DATABASE_PASSWORD ${DATABASE_PASSWORD}

RUN apk add pwgen

RUN addgroup application && \
	adduser --home /home/application --disabled-password --ingroup application application && \
    pwgen --secure 128 | passwd -u application

RUN chown -R application:application /home/application && \
	chmod -R a-rwx /home/application && \
	chmod -R g+r /home/application && \
	chmod -R u+rx /home/application

COPY ./build/libs/server.jar /home/application/server.jar

USER application

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/home/application/server.jar"]

EXPOSE 8080
