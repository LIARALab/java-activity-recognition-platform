FROM library/fedora:27

WORKDIR /

ARG DATABASE_URL=mysql://127.0.0.1:5101/domus
ARG DATABASE_USERNAME=domus
ARG DATABASE_PASSWORD=domus

ENV DATABASE_URL ${DATABASE_URL}
ENV DATABASE_USERNAME ${DATABASE_USERNAME}
ENV DATABASE_PASSWORD ${DATABASE_PASSWORD}

RUN yum install wget pwgen findutils -y && \
    wget --no-cookies --no-check-certificate --header "Cookie: oraclelicense=accept-securebackup-cookie" http://download.oracle.com/otn-pub/java/jdk/12.0.1+12/69cfe15208a647278a19ef0990eea691/jdk-12.0.1_linux-x64_bin.rpm && \
    rpm -ivh jdk-12.0.1_linux-x64_bin.rpm && \
    rm -f jdk-12.0.1_linux-x64_bin.rpm

RUN groupadd application && \
	useradd -d /home/application -m -g application -p `pwgen -s 20` application && \
	chown -R application:application /home/application && \
	chmod -R a-rwx /home/application && \
	chmod -R g+r /home/application && \
	chmod -R u+rx /home/application

COPY ./build/libs/server.jar /home/application/server.jar

USER application

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/home/application/server.jar"]

EXPOSE 8080
