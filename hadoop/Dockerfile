FROM sequenceiq/hadoop-docker:2.3.0

ADD core-site.xml.template $HADOOP_PREFIX/etc/hadoop/

ADD bootstrap2.sh /etc/bootstrap2.sh
RUN chown root:root /etc/bootstrap2.sh
RUN chmod 700 /etc/bootstrap2.sh

CMD ["/etc/bootstrap2.sh", "-d"]
