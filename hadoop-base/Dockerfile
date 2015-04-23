FROM banno/druid-base:latest

MAINTAINER Zach Cox zach.cox@banno.com

ENV HADOOP_VERSION 2.3.0

RUN curl -s http://archive.apache.org/dist/hadoop/core/hadoop-${HADOOP_VERSION}/hadoop-${HADOOP_VERSION}.tar.gz | tar -xz -C /opt/
#ADD hadoop-${HADOOP_VERSION}.tar.gz /opt/
RUN cd /opt && ln -s ./hadoop-${HADOOP_VERSION} hadoop

ENV HADOOP_PREFIX /opt/hadoop
ENV HADOOP_COMMON_HOME /opt/hadoop
ENV HADOOP_HDFS_HOME /opt/hadoop
ENV HADOOP_MAPRED_HOME /opt/hadoop
ENV HADOOP_YARN_HOME /opt/hadoop
ENV HADOOP_CONF_DIR /opt/hadoop/etc/hadoop

#this is an ugly hack to implement Solution #1 from https://github.com/druid-io/druid/pull/1022
#this is the output of `/opt/hadoop/bin/hadoop classpath`
ENV DRUID_EXTRA_CLASSPATH :/opt/hadoop/etc/hadoop:/opt/hadoop/share/hadoop/common/lib/*:/opt/hadoop/share/hadoop/common/*:/opt/hadoop/share/hadoop/hdfs:/opt/hadoop/share/hadoop/hdfs/lib/*:/opt/hadoop/share/hadoop/hdfs/*:/opt/hadoop/share/hadoop/yarn/lib/*:/opt/hadoop/share/hadoop/yarn/*:/opt/hadoop/share/hadoop/mapreduce/lib/*:/opt/hadoop/share/hadoop/mapreduce/*

ADD mapred-site.xml yarn-site.xml core-site.xml /opt/hadoop/etc/hadoop/
