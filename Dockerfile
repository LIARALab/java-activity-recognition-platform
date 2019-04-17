FROM library/fedora:27

WORKDIR /

ARG DATABASE_URL=mysql://127.0.0.1:5101/domus
ARG DATABASE_USERNAME=domus
ARG DATABASE_PASSWORD=domus

ENV DATABASE_URL ${DATABASE_URL}
ENV DATABASE_USERNAME ${DATABASE_USERNAME}
ENV DATABASE_PASSWORD ${DATABASE_PASSWORD}

RUN yum install wget pwgen findutils -y && \
    wget --no-cookies --no-check-certificate --header "Cookie: oraclelicense=accept-securebackup-cookie" http://download.oracle.com/otn/java/jdk/11.0.3+12/37f5e150db5247ab9333b11c1dddcd30/jdk-11.0.3_linux-x64_bin.rpm && \
    rpm -ivh jdk-11.0.3_linux-x64_bin.rpm && \
    rm -f jdk-11.0.3_linux-x64_bin.rpm

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