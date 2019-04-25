#!/bin/bash

whoami

/usr/local/firebird/bin/fbguard -daemon

java \
-Dlog4j.configurationFile=file:/resources/log4j2.xml \
-Dport=${PORT} \
-Xmx1G \
-DlogPath=/log \
-jar /app/tempdatchiki.war