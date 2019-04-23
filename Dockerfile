#FROM maven:3.5.2 AS builder
#COPY . /app
#WORKDIR /app
#RUN mvn clean install

FROM java:8-jre

RUN ln -sf /usr/share/zoneinfo/Europe/Moscow /etc/localtime && \
    echo "Europe/Moscow" > /etc/timezone

RUN mkdir -p /app
WORKDIR /app
#COPY --from=builder /app/target/tempdatchiki.war /app/
COPY target/tempdatchiki.war /app/

RUN mkdir -p /resources
COPY src/main/resources/log4j2.xml /resources/

RUN mkdir -p /log

CMD [ "java", \
    "-Dlog4j.configurationFile=file:/resources/log4j2.xml", \
    "-Dserver.port=${PORT}", \
    "-Xmx1G", \
    "-DlogPath=/log", \
    "-jar", "/app/tempdatchiki.war"]

# EXPOSE ${PORT}

# -p 9998:9998 --mount type=bind,source=/log,target=/log