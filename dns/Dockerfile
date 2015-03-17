FROM ubuntu:14.04

RUN apt-get update && apt-get -y install bind9

ADD named.conf.local /etc/bind/named.conf.local
ADD named.conf.options /etc/bind/named.conf.options

ADD db.dev.banno.com /etc/bind/db.dev.banno.com

CMD ["/usr/sbin/named", "-f", "-c", "/etc/bind/named.conf"]
