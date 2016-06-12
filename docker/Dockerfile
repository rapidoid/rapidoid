FROM java:8-jre
MAINTAINER Nikolche Mihajlovski

ENV RAPIDOID_HOME=/usr/local/rapidoid
ENV RAPIDOID_JAR=$RAPIDOID_HOME/rapidoid.jar
ENV RAPIDOID_BASE=/var/lib/rapidoid
ENV RAPIDOID_RUN=/run/rapidoid
ENV RAPIDOID_TMP=/tmp/rapidoid

RUN set -xe \
 && mkdir -p "$RAPIDOID_HOME" "$RAPIDOID_BASE" "$RAPIDOID_RUN" "$RAPIDOID_TMP"

RUN echo '#!/usr/bin/env bash' > /usr/local/bin/rapidoid \
 && echo 'java -Djava.io.tmpdir=/tmp/rapidoid -cp /usr/local/rapidoid/rapidoid.jar:/app/app.jar:/app/*.jar org.rapidoid.standalone.Main app.jar=/app/app.jar root=/app $@' >> /usr/local/bin/rapidoid \
 && chmod ugo+x /usr/local/bin/rapidoid

ADD rapidoid.jar $RAPIDOID_JAR

RUN chmod ugo+r $RAPIDOID_JAR

RUN mkdir -p /app
VOLUME /app

WORKDIR $RAPIDOID_BASE
EXPOSE 8888

ENTRYPOINT ["rapidoid"]
CMD ["production"]
