FROM bridg/java8

USER root

RUN ln -sf /usr/share/zoneinfo/Europe/Moscow /etc/localtime && echo "Europe/Moscow" > /etc/timezone

RUN mkdir -p /app
COPY target/tempdatchiki.war /app/

RUN mkdir -p /resources
COPY src/main/resources/log4j2.xml /resources/

RUN mkdir -p /log

WORKDIR /app

CMD [ "java", \
    "-Dlog4j.configurationFile=file:/resources/log4j2.xml", \
    "-Dport=${PORT}", \
    "-Ddburl=${DATABASE_URL}", \
    "-Xmx1G", \
    "-DlogPath=/log", \
    "-jar", "/app/tempdatchiki.war"]

#sudo docker run --net="host" -e PORT=8181 -e JDBC_DATABASE_URL="jdbc:postgresql://localhost:5432/td" -e JDBC_DATABASE_USERNAME=laba -e JDBC_DATABASE_PASSWORD=laba td