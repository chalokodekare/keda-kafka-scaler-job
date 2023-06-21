FROM openjdk:17-slim
LABEL maintainer="Ritu Bhandari"

ARG app
ARG version

COPY /build/libs/${app}-${version}.jar /opt/${app}/service.jar

WORKDIR /opt/${app}

#RUN jar xf service.jar BOOT-INF/classes/config.xml && \
#    sed -i -e 's|DIRECTORY Value="."|DIRECTORY Value="/tmp"|1' BOOT-INF/classes/config.xml && \
#    jar uf service.jar BOOT-INF/classes/config.xml && \
#    rm -rf BOOT-INF/

EXPOSE $8080

## Create a user; it must not be ROOT and its UID should be greater than 1000.
#RUN groupadd --gid 1100 "scaler" && \
#    useradd --create-home --no-log-init --shell "/bin/bash" --uid 1100 --gid 1100 "scaler"
#
#USER 1100

#FST serialization and snowflake compatibility with Java 17

ENTRYPOINT exec java $JAVA_OPTS \
           --add-opens=java.base/java.lang=ALL-UNNAMED \
           --add-opens=java.base/java.math=ALL-UNNAMED \
           --add-opens=java.base/java.util=ALL-UNNAMED \
           --add-opens=java.base/java.util.concurrent=ALL-UNNAMED \
           --add-opens=java.base/java.net=ALL-UNNAMED \
           --add-opens=java.base/java.text=ALL-UNNAMED \
           --add-opens=java.sql/java.sql=ALL-UNNAMED \
           --add-opens=java.base/java.nio=ALL-UNNAMED \
           -jar service.jar