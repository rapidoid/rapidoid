FROM openjdk:8-jre-slim
MAINTAINER Nikolche Mihajlovski

ENV RAPIDOID_JAR /opt/rapidoid.jar
ENV RAPIDOID_TMP /tmp/rapidoid

WORKDIR /opt
EXPOSE 8888

VOLUME ["/data"]

ENV RAPIDOID_VERSION 5.x.y-SNAPSHOT

COPY wrk /usr/local/bin/
COPY pipeline.lua /opt/

COPY entrypoint.sh /opt/
COPY rapidoid.jar $RAPIDOID_JAR

RUN set -xe \
    && mkdir /platform \
    && mkdir -p "$RAPIDOID_TMP"

ENTRYPOINT ["/opt/entrypoint.sh"]
