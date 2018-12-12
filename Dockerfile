FROM ubuntu:trusty

ENV DEBIAN_FRONTEND noninteractive

MAINTAINER Adam Lounici <lounici@gmail.com>

RUN apt-get update && sh -c "echo Europe/Paris > /etc/timezone"

RUN apt-get -y upgrade

RUN apt-get -y -f --fix-missing install sudo nano locales wget nano build-essential

RUN locale-gen en_US en_US.UTF-8 fr_FR.UTF-8

RUN apt-get install -y -f openjdk-7-jdk fastjar unzip

RUN wget http://goo.gl/ca9oyo -O gatling.zip && \
 unzip gatling.zip && \
 rm gatling.zip && \
 mv gatling* /usr/local/gatling && \
 chmod -R 777 /usr/local/gatling

RUN sed -i -e "s/OLDDIR=/rm -rf \/usr\/local\/gatling\/results\/*\n\nOLDDIR=/g" /usr/local/gatling/bin/gatling.sh

RUN echo "mv /usr/local/gatling/results/*/* /usr/local/gatling/results" >> /usr/local/gatling/bin/gatling.sh

RUN apt-get -y -f install nginx-light

RUN echo "daemon off;" >> /etc/nginx/nginx.conf

RUN mkdir -p /var/www && ln -s /usr/local/gatling/results /var/www/gatling

RUN sed -i -e "s/http {/http {\n client_max_body_size 100M;/g" /etc/nginx/nginx.conf

ADD gatling /etc/nginx/sites-enabled/gatling

RUN chmod +x /etc/nginx/sites-enabled/gatling

RUN rm /etc/nginx/sites-enabled/default

ADD setup.sh /root/setup.sh

RUN chmod +x /root/setup.sh

VOLUME ["/usr/local/gatling", "/var/www"]

EXPOSE 80

ENTRYPOINT /bin/bash /root/setup.sh && /bin/bash
