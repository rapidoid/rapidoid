FROM java:8-jre
MAINTAINER Nikolche Mihajlovski

ENV RAPIDOID_HOME=/usr/local/rapidoid RAPIDOID_BASE=/var/lib/rapidoid RAPIDOID_RUN=/run/rapidoid TMPDIR=/tmp/rapidoid

RUN set -xe \
 && mkdir -p "$RAPIDOID_HOME" "$RAPIDOID_BASE" "$RAPIDOID_RUN" "$TMPDIR"

RUN echo '#!/usr/bin/env bash' > /usr/local/bin/rapidoid \
 && echo 'java -Djava.io.tmpdir=/tmp/rapidoid -cp /usr/local/rapidoid/rapidoid.jar:/app/app.jar org.rapidoid.standalone.Main app.jar=/app/app.jar root=/app $@' >> /usr/local/bin/rapidoid \
 && chmod ugo+x /usr/local/bin/rapidoid

ADD rapidoid.jar /usr/local/rapidoid/rapidoid.jar
RUN chmod ugo+r /usr/local/rapidoid/rapidoid.jar

RUN mkdir -p /app
VOLUME /app

WORKDIR $RAPIDOID_BASE
EXPOSE 8888
CMD ["rapidoid"]
