FROM eclipse-temurin:17.0.16_8-jre-alpine

LABEL maintainer="Bertrik Sikken bertrik@gmail.com"
LABEL org.opencontainers.image.source="https://github.com/bertrik/stofradar"
LABEL org.opencontainers.image.description="Visualization of particulate matter data onto a map of the Netherlands"
LABEL org.opencontainers.image.licenses="MIT"

RUN apk add --no-cache imagemagick
RUN apk add --no-cache --virtual .ms-fonts msttcorefonts-installer && \
    update-ms-fonts 2>/dev/null && \
    fc-cache -f && \
    apk del .ms-fonts

ADD stofradar/build/distributions/stofradar.tar /opt/

WORKDIR /opt/stofradar
ENTRYPOINT ["/opt/stofradar/bin/stofradar"]

