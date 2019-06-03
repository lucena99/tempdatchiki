FROM 99taxis/mini-java8

USER root

RUN ln -sf /usr/share/zoneinfo/Europe/Moscow /etc/localtime && echo "Europe/Moscow" > /etc/timezone

RUN mkdir -p /app
COPY target/tempdatchiki.war /app/

RUN mkdir -p /log

WORKDIR /app

#EXPOSE 8181

CMD [ "java", \
    "-Dspring.profiles.active=prod", \
    "-Dport=${PORT}", \
    "-Ddburl=${DATABASE_URL}", \
    "-Xmx512m", \
    "-DlogPath=/log", \
    "-Dfile.encoding=UTF8", \
    "-jar", "/app/tempdatchiki.war"]

#windows
#docker run -p 8181:8181 -e PORT=8181 -e DATABASE_URL="sqlserver://username:password@host:1433/databaseName=sensorapp" td