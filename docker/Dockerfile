FROM java:8-jre
MAINTAINER Nikolche Mihajlovski

RUN groupadd -r rapidoid && useradd -r -g rapidoid rapidoid

ENV RAPIDOID_HOME=/usr/local/rapidoid RAPIDOID_BASE=/var/lib/rapidoid RAPIDOID_RUN=/run/rapidoid TMPDIR=/tmp/rapidoid

RUN set -xe \
 && mkdir -p "$RAPIDOID_HOME" "$RAPIDOID_BASE" "$RAPIDOID_RUN" "$TMPDIR" \
 && chown -R rapidoid:rapidoid "$RAPIDOID_RUN" "$TMPDIR" "$RAPIDOID_BASE"

RUN echo '#!/usr/bin/env bash' > /usr/local/bin/rapidoid \
 && echo 'java -Djava.io.tmpdir=/tmp/rapidoid -cp /usr/local/rapidoid/rapidoid.jar:/app:/app/* org.rapidoid.main.Main path=/app $@' >> /usr/local/bin/rapidoid \
 && chmod ugo+x /usr/local/bin/rapidoid

ADD rapidoid.jar /usr/local/rapidoid/rapidoid.jar

WORKDIR $RAPIDOID_BASE

EXPOSE 8080

USER rapidoid

CMD ["rapidoid"]