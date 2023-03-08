FROM adoptopenjdk/openjdk14:jre-14.0.2_12-alpine
MAINTAINER Bertrik Sikken bertrik@gmail.com

RUN apk add imagemagick
RUN apk add --virtual .ms-fonts msttcorefonts-installer && \
    update-ms-fonts 2>/dev/null && \
    fc-cache -f && \
    apk del .ms-fonts

ADD stofradar/build/distributions/stofradar.tar /opt/

WORKDIR /opt/stofradar
ENTRYPOINT /opt/stofradar/bin/stofradar

