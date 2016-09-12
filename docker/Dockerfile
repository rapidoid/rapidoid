FROM java:8-jre
MAINTAINER Nikolche Mihajlovski

ENV RAPIDOID_VERSION 5.2.2
ENV RAPIDOID_JAR /opt/rapidoid.jar
ENV RAPIDOID_TMP /tmp/rapidoid

VOLUME /app
WORKDIR /opt
EXPOSE 8888

COPY entrypoint.sh /opt/

ADD rapidoid.jar $RAPIDOID_JAR

RUN set -xe \
    && mkdir -p "$RAPIDOID_TMP" \
	&& chmod 444 $RAPIDOID_JAR \
	&& chmod 555 /opt/entrypoint.sh

ENTRYPOINT ["/opt/entrypoint.sh"]