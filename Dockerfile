FROM adoptopenjdk/openjdk14:jre-14.0.2_12-alpine
MAINTAINER Bertrik Sikken bertrik@gmail.com

ADD stofradar/build/distributions/stofradar.tar /opt/

WORKDIR /opt/stofradar
ENTRYPOINT /opt/stofradar/bin/stofradar

