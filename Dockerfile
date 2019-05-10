FROM 99taxis/mini-java8

USER root

RUN ln -sf /usr/share/zoneinfo/Europe/Moscow /etc/localtime && echo "Europe/Moscow" > /etc/timezone

RUN mkdir -p /app
COPY target/tempdatchiki.war /app/

RUN mkdir -p /resources
COPY src/main/resources/log4j2.xml /resources/

RUN mkdir -p /log

WORKDIR /app

EXPOSE 8181

CMD [ "java", \
    "-Dlog4j.configurationFile=file:/resources/log4j2.xml", \
    "-Dport=${PORT}", \
    "-Ddburl=${DATABASE_URL}", \
    "-Xmx500m", \
    "-DlogPath=/log", \
    "-jar", "/app/tempdatchiki.war"]

#windows
#docker run -p 8181:8181 -e PORT=8181 -e DATABASE_URL="postgres://laba:laba@host.docker.internal:5432/td" td