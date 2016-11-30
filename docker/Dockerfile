FROM java:8-jre
MAINTAINER Nikolche Mihajlovski

ENV RAPIDOID_JAR /opt/rapidoid.jar
ENV RAPIDOID_TMP /tmp/rapidoid

WORKDIR /opt
EXPOSE 8888

ENV RAPIDOID_VERSION 5.x.y-SNAPSHOT

COPY entrypoint.sh /opt/

COPY rapidoid.jar $RAPIDOID_JAR

RUN set -xe \
    && mkdir /app \
    && mkdir -p "$RAPIDOID_TMP"

ENTRYPOINT ["/opt/entrypoint.sh"]
