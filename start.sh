#!/bin/bash

/usr/local/firebird/bin/fbguard -daemon

/jre8/bin/java \
-Dlog4j.configurationFile=file:/resources/log4j2.xml \
-Dport=${PORT} \
-Xmx1G \
-DlogPath=/log \
-jar /app/tempdatchiki.war