FROM java:8-jre
MAINTAINER Nikolche Mihajlovski

RUN groupadd -r rapidoid && useradd -r -g rapidoid rapidoid

ENV RAPIDOID_HOME /usr/local/rapidoid
ENV RAPIDOID_BASE /var/lib/rapidoid
ENV RAPIDOID_RUN /run/rapidoid
ENV TMPDIR /tmp/rapidoid

RUN mkdir -p "$RAPIDOID_HOME"
RUN mkdir -p "$RAPIDOID_BASE"

RUN set -xe \
	&& mkdir -p "$RAPIDOID_RUN" "$TMPDIR" \
        && chown -R rapidoid:rapidoid "$RAPIDOID_RUN" "$TMPDIR" "$RAPIDOID_BASE"

ADD rapidoid.jar /usr/local/rapidoid/rapidoid.jar

WORKDIR $RAPIDOID_BASE

EXPOSE 8080

USER rapidoid

CMD ["java", "-Djava.io.tmpdir=/tmp/rapidoid", "-cp", "/usr/local/rapidoid/rapidoid.jar", "org.rapidoid.main.Main", "path=/app", "mode=production"]
