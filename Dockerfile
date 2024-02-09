FROM eclipse-temurin:17.0.10_7-jre-alpine

LABEL maintainer="Bertrik Sikken bertrik@gmail.com"

RUN apk add --no-cache imagemagick=7.1.1.26-r0
RUN apk add --no-cache --virtual .ms-fonts msttcorefonts-installer=3.8.1-r0 && \
    update-ms-fonts 2>/dev/null && \
    fc-cache -f && \
    apk del .ms-fonts

ADD stofradar/build/distributions/stofradar.tar /opt/

WORKDIR /opt/stofradar
ENTRYPOINT ["/opt/stofradar/bin/stofradar"]

