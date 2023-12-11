FROM eclipse-temurin:11.0.21_9-jre-alpine
LABEL maintainer="Bertrik Sikken bertrik@gmail.com"

RUN apk add --no-cache imagemagick=7.1.1.13-r1
RUN apk add --no-cache --virtual .ms-fonts msttcorefonts-installer=3.8-r1 && \
    update-ms-fonts 2>/dev/null && \
    fc-cache -f && \
    apk del .ms-fonts

ADD stofradar/build/distributions/stofradar.tar /opt/

WORKDIR /opt/stofradar
ENTRYPOINT ["/opt/stofradar/bin/stofradar"]

