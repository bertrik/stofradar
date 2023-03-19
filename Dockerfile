FROM adoptopenjdk/openjdk14:jre-14.0.2_12-alpine
LABEL maintainer="Bertrik Sikken bertrik@gmail.com"

RUN apk add --no-cache imagemagick=7.0.10.48-r0
RUN apk add --no-cache --virtual .ms-fonts msttcorefonts-installer=3.6-r2 && \
    update-ms-fonts 2>/dev/null && \
    fc-cache -f && \
    apk del .ms-fonts

ADD stofradar/build/distributions/stofradar.tar /opt/

WORKDIR /opt/stofradar
ENTRYPOINT ["/opt/stofradar/bin/stofradar"]

